import subprocess, time, json, pathlib, urllib.request
from urllib.error import HTTPError
root = pathlib.Path('bench/results/baseline')
def cmd(args, dest):
    p = subprocess.run(args, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    dest.write_text('$ ' + ' '.join(args) + '\n' + p.stdout + '\nexit_code=' + str(p.returncode) + '\n')
    return p.stdout
for _ in range(120):
    try:
        urllib.request.urlopen(urllib.request.Request('http://localhost:8080/api/v1/orders/init?productId=101&stock=100000',method='POST'))
        break
    except HTTPError as error:
        if error.code == 409:
            break
        time.sleep(1)
    except Exception: time.sleep(1)
else: raise RuntimeError('App never ready')
for run in range(1,4):
    d = root / f'run-{run}'; d.mkdir(exist_ok=True)
    cmd(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-c','TRUNCATE orders CASCADE'],d/'reset-postgres.log')
    cmd(['docker','exec','flash_redis','redis-cli','FLUSHDB'],d/'reset-redis.log')
    cmd(['git','rev-parse','HEAD'],d/'git.txt')
    cmd(['k6','run','--summary-export',str(d/'summary.json'),'--summary-trend-stats','avg,min,med,max,p(90),p(95),p(99)','scripts/load_test.js'],d/'k6.log')
    for attempt in range(120):
        pending = cmd(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',
                       "select (select count(*) from orders where status='PENDING')+(select count(*) from outbox_events where status='PENDING')"],d/'drain.log')
        if pending.strip() == '0': break
        time.sleep(1)
    cmd(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'"],d/'postgres.log')
    cmd(['docker','exec','flash_redis','redis-cli','GET','product:101:stock'],d/'redis.log')
    cmd(['docker','stats','--no-stream','--format','{{json .}}'],d/'stats.log')
    cmd(['docker','inspect','flash_postgres','flash_redis','flash_kafka'],d/'inspect.json.log')
    print('completed',run,flush=True)
