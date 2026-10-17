"""Raw evidence plus key-level reconciliation; sequential isolated experiments."""
import subprocess, pathlib, time, json, re, threading, urllib.request, sys, datetime, os
root=pathlib.Path(sys.argv[1]); root.mkdir(parents=True,exist_ok=True)
fault=sys.argv[2] if len(sys.argv)>2 else 'none'
outbox_mode=os.environ.get('OUTBOX_MODE')=='true'
base_url=os.environ.get('BASE_URL','http://localhost:8080')
app_override=os.environ.get('BENCH_APP_CONTAINERS','').split()
aliases={name:os.environ.get(env,name) for name,env in [('flash_postgres','BENCH_POSTGRES'),('flash_redis','BENCH_REDIS'),('flash_rabbitmq','BENCH_RABBITMQ')]}
def resolve(args): return [aliases.get(arg,arg) for arg in args]

def cmd(args,name,check=True):
    args=resolve(args)
    began=datetime.datetime.now(datetime.timezone.utc).isoformat()
    p=subprocess.run(args,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    ended=datetime.datetime.now(datetime.timezone.utc).isoformat()
    (root/name).write_text('$ '+' '.join(args)+'\nstarted='+began+'\n'+p.stdout+f'\nexit_code={p.returncode}\nended='+ended+'\n')
    if check and p.returncode: raise RuntimeError(f'{args}: {p.stdout}')
    return p.stdout

def raw(args): return subprocess.check_output(resolve(args),text=True).strip()
apps=app_override or raw(['docker','compose','--profile','distributed','ps','-q','app']).splitlines()
containers=apps+[aliases[name] for name in ['flash_postgres','flash_redis','flash_rabbitmq']]
cmd(['git','rev-parse','HEAD'],'git.txt')
cmd(['git','diff','--','src','docker-compose.yml','deploy','Dockerfile'],'working-source.diff')
cmd(['sha256sum','bench/load.js','bench/experiment.py'],'harness-sha256.txt')
cmd(['docker','compose','--profile','distributed','config'],'compose.txt')
if outbox_mode:
    cmd(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-c','TRUNCATE orders CASCADE'],'reset-pg.log')
for _ in range(120):
    try:
        urllib.request.urlopen(urllib.request.Request(base_url+'/api/v1/orders/init?productId=101&stock=100000',method='POST'));break
    except Exception: time.sleep(1)
else: raise RuntimeError('Proxy not ready')
if not outbox_mode:
    cmd(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-c','TRUNCATE orders'],'reset-pg.log')
cmd(['docker','exec','flash_redis','redis-cli','FLUSHDB'],'reset-redis.log')

def resources(label):
    for i,c in enumerate(containers):
        cmd(['docker','exec',c,'sh','-c','cat /sys/fs/cgroup/cpu.stat; cat /sys/fs/cgroup/memory.events'],f'{label}-cgroup-{i}.txt',False)
resources('before')
stop=threading.Event()
def sample():
    with (root/'stats.log').open('w') as f:
        while not stop.is_set():
            f.write(datetime.datetime.now(datetime.timezone.utc).isoformat()+'\n');f.flush()
            subprocess.run(['docker','stats','--no-stream','--format','{{json .}}'],stdout=f,stderr=f)
            subprocess.run(['free','-b'],stdout=f,stderr=f)
            stop.wait(5)
thread=threading.Thread(target=sample);thread.start()
start=time.monotonic();event_since=str(int(time.time()))
(root/'run-started.txt').write_text(event_since+'\n')
with (root/'k6.log').open('w') as f:
    args=['k6','run','--quiet','--summary-export',str(root/'summary.json'),'-e','BASE_URL='+base_url,'-e',f'RUN_ID={root.name}','bench/load.js']
    if os.environ.get('CI_BENCH')=='true': args[2:2]=['-e','CI_BENCH=true']
    if fault!='none': args[2:2]=['-e','FAULT_LOAD=true']
    f.write('$ '+' '.join(args)+'\n');f.flush()
    proc=subprocess.Popen(args,stdout=f,stderr=f)
    if fault!='none':
        time.sleep(max(0,45-(time.monotonic()-start)))
        if fault=='kill':
            cmd(['docker','kill','-s','KILL',apps[0]],'fault-kill.log')
            time.sleep(20)
            cmd(['docker','start',apps[0]],'fault-start.log')
        elif fault=='rabbit': cmd(['docker','restart','flash_rabbitmq'],'fault-restart.log')
        elif fault=='redis': cmd(['docker','restart','flash_redis'],'fault-restart.log')
        else: raise ValueError(fault)
    code=proc.wait(); f.write(f'\nexit_code={code}\n')
stop.set();thread.join()
# Require ready + unacked zero, continuously, before reconciliation. Keep every poll.
quiet=0
with (root/'drain.log').open('w') as f:
    for _ in range(120):
        p=subprocess.run(resolve(['docker','exec','flash_rabbitmq','rabbitmqctl','list_queues','name','messages_ready','messages_unacknowledged']),text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
        f.write(p.stdout);f.flush()
        rows=[l.split() for l in p.stdout.splitlines() if l.startswith('order.')]
        queue_observed=p.returncode==0
        if not queue_observed and outbox_mode:
            # Docker restart can leave exec diagnostics broken while AMQP works.
            # Observe the actual management counters through a surviving app's
            # network, retaining both the failed CLI and unmodified API response.
            args=['docker','exec',apps[-1],'wget','-T','5','-qO-',
                  '--header','Authorization: Basic Z3Vlc3Q6Z3Vlc3Q=',
                  'http://rabbitmq:15672/api/queues/%2F']
            try:
                api=subprocess.run(args,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,timeout=10)
                with (root/'management-queue-polls.jsonl').open('a') as evidence:
                    evidence.write(json.dumps({'at':datetime.datetime.now(datetime.timezone.utc).isoformat(),'command':args,'exit':api.returncode,'response':api.stdout})+'\n')
                if api.returncode==0:
                    queues=json.loads(api.stdout)
                    rows=[[q['name'],str(q['messages_ready']),str(q['messages_unacknowledged'])]
                          for q in queues if q['name'].startswith('order.')]
                    queue_observed=True
                    f.write('Observed management counters: '+json.dumps(rows)+'\n');f.flush()
            except (subprocess.TimeoutExpired,ValueError,KeyError) as error:
                f.write('Management observation failed: '+repr(error)+'\n');f.flush()
        settled=True
        if outbox_mode:
            state=raw(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',
                "select (select count(*) from orders where status='PENDING')+(select count(*) from outbox_events where status='PENDING')"])
            f.write('pending orders+events='+state+'\n');f.flush();settled=state=='0'
        quiet=quiet+1 if settled and queue_observed and rows and all(r[1:]==['0','0'] for r in rows) else 0
        if quiet>=3: break
        time.sleep(2)
    else: f.write('DRAIN TIMEOUT\n')
pg=raw(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select idempotency_key || ',' || status || ',' || quantity from orders order by idempotency_key"])
(root/'orders.csv').write_text(pg+'\n')
redis=raw(['docker','exec','flash_redis','redis-cli','--scan','--pattern','idempotency:*'])
(root/'reservations.txt').write_text(redis+'\n')
stockraw=raw(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',
              'select available_stock from products where id=101']) if outbox_mode else raw(['docker','exec','flash_redis','redis-cli','--raw','GET','product:101:stock'])
(root/'stock.txt').write_text(stockraw+'\n')
accepted=re.findall(r'ACCEPTED (bench-[^\s"\\]+)',(root/'k6.log').read_text())
confirmed=[line.split(',')[0] for line in pg.splitlines() if ',CONFIRMED,' in line]
reservations={key.removeprefix('idempotency:') for key in redis.splitlines()}
stock=int(stockraw) if stockraw else None
lost=sorted(set(accepted)-set(confirmed));stuck=sorted(reservations-set(confirmed))
(root/'lost-keys.json').write_text(json.dumps(lost,indent=2))
(root/'stuck-keys.json').write_text(json.dumps(stuck,indent=2))
summary={'http_202':len(accepted),'confirmed':len(confirmed),'redis_stock':stock,'oversold_count':max(0,len(confirmed)-100000),'duplicate_orders':len(confirmed)-len(set(confirmed)),'lost_orders':len(lost),'stuck_reservations':len(stuck),'unacknowledged_confirmed':len(set(confirmed)-set(accepted)),'stock_accounting_excess':None if stock is None else max(0,len(confirmed)+stock-100000),'drained':quiet>=3,'k6_exit':code}
if outbox_mode:
    summary.update(redis_stock=None,postgres_stock=stock,stock_source='PostgreSQL',
        pending_orders=int(raw(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select count(*) from orders where status='PENDING'"])),
        cancelled_orders=int(raw(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select count(*) from orders where status='CANCELLED'"])))
(root/'reconciliation.json').write_text(json.dumps(summary,indent=2)+'\n')
resources('after')
cmd(['docker','inspect',*containers],'inspect.log')
cmd(['docker','events','--since',event_since,'--until',str(int(time.time())),'--filter','event=oom','--filter','event=die','--format','{{json .}}'],'docker-events.log',False)
if app_override:
    for index,app in enumerate(apps): cmd(['docker','logs','--since',event_since,app],f'app-{index}.log',False)
else:
    cmd(['docker','compose','--profile','distributed','logs','--no-color','app'],'app.log',False)
print(json.dumps(summary),flush=True)
