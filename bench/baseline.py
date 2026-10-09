import subprocess, time, json, pathlib, urllib.request
root = pathlib.Path('bench/results/baseline')
def cmd(args, dest):
    p = subprocess.run(args, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    dest.write_text('$ ' + ' '.join(args) + '\n' + p.stdout + '\nexit_code=' + str(p.returncode) + '\n')
    return p.stdout
for _ in range(120):
    try:
        urllib.request.urlopen(urllib.request.Request('http://localhost:8080/api/v1/orders/init?productId=101&stock=100000',method='POST'))
        break
    except Exception: time.sleep(1)
else: raise RuntimeError('App never ready')
for run in range(1,4):
    d = root / f'run-{run}'; d.mkdir(exist_ok=True)
    cmd(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-c','TRUNCATE orders'],d/'reset-postgres.log')
    cmd(['docker','exec','flash_redis','redis-cli','FLUSHDB'],d/'reset-redis.log')
    cmd(['git','rev-parse','HEAD'],d/'git.txt')
    cmd(['k6','run','--summary-export',str(d/'summary.json'),'--summary-trend-stats','avg,min,med,max,p(90),p(95),p(99)','scripts/load_test.js'],d/'k6.log')
    for attempt in range(120):
        queues = cmd(['docker','exec','flash_rabbitmq','rabbitmqctl','list_queues','name','messages_ready','messages_unacknowledged'],d/'queues.log')
        rows = [l.split() for l in queues.splitlines() if l.startswith('order.')]
        if rows and all(r[1:] == ['0','0'] for r in rows): break
        time.sleep(1)
    cmd(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'"],d/'postgres.log')
    cmd(['docker','exec','flash_redis','redis-cli','GET','product:101:stock'],d/'redis.log')
    cmd(['docker','stats','--no-stream','--format','{{json .}}'],d/'stats.log')
    cmd(['docker','inspect','flash_postgres','flash_redis','flash_rabbitmq'],d/'inspect.json.log')
    print('completed',run,flush=True)
