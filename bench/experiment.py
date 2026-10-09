"""Raw evidence plus key-level reconciliation; sequential isolated experiments."""
import subprocess, pathlib, time, json, re, threading, urllib.request, sys, datetime
root=pathlib.Path(sys.argv[1]); root.mkdir(parents=True,exist_ok=True)
fault=sys.argv[2] if len(sys.argv)>2 else 'none'

def cmd(args,name,check=True):
    p=subprocess.run(args,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    (root/name).write_text('$ '+' '.join(args)+'\n'+p.stdout+f'\nexit_code={p.returncode}\n')
    if check and p.returncode: raise RuntimeError(f'{args}: {p.stdout}')
    return p.stdout

def raw(args): return subprocess.check_output(args,text=True).strip()
apps=raw(['docker','compose','--profile','distributed','ps','-q','app']).splitlines()
containers=apps+['flash_postgres','flash_redis','flash_rabbitmq']
cmd(['git','rev-parse','HEAD'],'git.txt')
cmd(['docker','compose','--profile','distributed','config'],'compose.txt')
for _ in range(120):
    try:
        urllib.request.urlopen(urllib.request.Request('http://localhost:8080/api/v1/orders/init?productId=101&stock=100000',method='POST'));break
    except Exception: time.sleep(1)
else: raise RuntimeError('Proxy not ready')
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
start=time.monotonic()
with (root/'k6.log').open('w') as f:
    args=['k6','run','--quiet','--summary-export',str(root/'summary.json'),'-e','BASE_URL=http://localhost:8080','-e',f'RUN_ID={root.name}','bench/load.js']
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
        p=subprocess.run(['docker','exec','flash_rabbitmq','rabbitmqctl','list_queues','name','messages_ready','messages_unacknowledged'],text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
        f.write(p.stdout);f.flush()
        rows=[l.split() for l in p.stdout.splitlines() if l.startswith('order.')]
        quiet=quiet+1 if p.returncode==0 and rows and all(r[1:]==['0','0'] for r in rows) else 0
        if quiet>=3: break
        time.sleep(2)
    else: f.write('DRAIN TIMEOUT\n')
pg=raw(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select idempotency_key || ',' || status || ',' || quantity from orders order by idempotency_key"])
(root/'orders.csv').write_text(pg+'\n')
redis=raw(['docker','exec','flash_redis','redis-cli','--scan','--pattern','idempotency:*'])
(root/'reservations.txt').write_text(redis+'\n')
stockraw=raw(['docker','exec','flash_redis','redis-cli','--raw','GET','product:101:stock'])
(root/'stock.txt').write_text(stockraw+'\n')
accepted=re.findall(r'ACCEPTED (bench-[^\s"\\]+)',(root/'k6.log').read_text())
confirmed=[line.split(',')[0] for line in pg.splitlines() if ',CONFIRMED,' in line]
reservations={key.removeprefix('idempotency:') for key in redis.splitlines()}
stock=int(stockraw) if stockraw else None
lost=sorted(set(accepted)-set(confirmed));stuck=sorted(reservations-set(confirmed))
(root/'lost-keys.json').write_text(json.dumps(lost,indent=2))
(root/'stuck-keys.json').write_text(json.dumps(stuck,indent=2))
summary={'http_202':len(accepted),'confirmed':len(confirmed),'redis_stock':stock,'oversold_count':max(0,len(confirmed)-100000),'duplicate_orders':len(confirmed)-len(set(confirmed)),'lost_orders':len(lost),'stuck_reservations':len(stuck),'unacknowledged_confirmed':len(set(confirmed)-set(accepted)),'stock_accounting_excess':None if stock is None else max(0,len(confirmed)+stock-100000),'drained':quiet>=3,'k6_exit':code}
(root/'reconciliation.json').write_text(json.dumps(summary,indent=2)+'\n')
resources('after')
cmd(['docker','inspect',*containers],'inspect.log')
cmd(['docker','compose','--profile','distributed','logs','--no-color','app'],'app.log',False)
print(json.dumps(summary),flush=True)
