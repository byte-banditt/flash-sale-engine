import asyncio, subprocess, json, pathlib, time, collections, sys, os, urllib.request, urllib.parse
root=pathlib.Path(sys.argv[1] if len(sys.argv)>1 else 'bench/results/replicas/http');root.mkdir(parents=True,exist_ok=True)
outbox=os.environ.get('OUTBOX_MODE')=='true'
base=os.environ.get('BASE_URL','http://localhost:8080')
target=urllib.parse.urlparse(base)
aliases={name:os.environ.get(env,name) for name,env in [('flash_postgres','BENCH_POSTGRES'),('flash_redis','BENCH_REDIS'),('flash_rabbitmq','BENCH_RABBITMQ')]}
def docker(container,*args): return subprocess.check_output(['docker','exec',aliases.get(container,container),*args],text=True).strip()
async def requests(count,same):
    gate=asyncio.Event()
    async def one(i):
        await gate.wait()
        key='same-key' if same else f'oversell-{i}'
        body=json.dumps({'productId':101,'quantity':1,'idempotencyKey':key}).encode()
        try:
            reader,writer=await asyncio.open_connection(target.hostname,target.port or 80)
            writer.write(b'POST /api/v1/orders HTTP/1.1\r\nHost: localhost\r\nContent-Type: application/json\r\nConnection: close\r\nContent-Length: '+str(len(body)).encode()+b'\r\n\r\n'+body)
            await writer.drain()
            response=await asyncio.wait_for(reader.read(),120)
            writer.close();await writer.wait_closed()
            headers=response.split(b'\r\n\r\n')[0].decode()
            return {'key':key,'status':int(headers.split()[1]),'headers':headers}
        except Exception as e: return {'key':key,'error':repr(e)}
    tasks=[asyncio.create_task(one(i)) for i in range(count)]
    gate.set();return await asyncio.gather(*tasks)
for label,count,stock,same in [('oversell',2000,100,False),('same-key',40,10,True)]:
    docker('flash_postgres','psql','-U','postgres','-d','flashsale_db','-c','TRUNCATE orders CASCADE' if outbox else 'TRUNCATE orders')
    docker('flash_redis','redis-cli','FLUSHDB')
    if outbox: urllib.request.urlopen(urllib.request.Request(base+f'/api/v1/orders/init?productId=101&stock={stock}',method='POST')).read()
    else: docker('flash_redis','redis-cli','SET','product:101:stock',str(stock))
    responses=asyncio.run(requests(count,same)); (root/f'{label}-http.json').write_text(json.dumps(responses,indent=2))
    expected=1 if same else stock
    for _ in range(120):
        pg=docker('flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select count(*),count(distinct idempotency_key),count(*) filter(where status='CONFIRMED') from orders")
        if int(pg.split('|')[2])==expected:break
        time.sleep(1)
    remaining=int(docker('flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc','select available_stock from products where id=101')) if outbox else int(docker('flash_redis','redis-cli','GET','product:101:stock'))
    counts=collections.Counter(r.get('status','error') for r in responses)
    upstreams=collections.Counter(r['headers'].split('X-Replica: ')[-1] for r in responses if 'headers' in r)
    for _ in range(120):
        queues=docker('flash_rabbitmq','rabbitmqctl','list_queues','name','messages_ready','messages_unacknowledged')
        rows=[line.split() for line in queues.splitlines() if line.startswith('order.')]
        if rows and all(row[1:]==['0','0'] for row in rows): break
        time.sleep(1)
    (root/f'{label}-queues.log').write_text(queues+'\n')
    if outbox:
        intents=docker('flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select count(*) from orders o left join outbox_events e on e.order_id=o.order_id where e.event_id is null or e.payload->>'idempotencyKey'<>o.idempotency_key")
        (root/f'{label}-missing-intents.txt').write_text(intents+'\n')
        assert intents=='0',intents
    summary={'requests':count,'initial_stock':stock,'statuses':dict(counts),'upstreams':dict(upstreams),'postgres':pg,'redis_stock':None if outbox else remaining}
    if outbox: summary.update(postgres_stock=remaining,stock_source='PostgreSQL')
    (root/f'{label}-reconciliation.json').write_text(json.dumps(summary,indent=2))
    print(summary,flush=True)
    assert counts[202]==expected,summary
    assert set(counts)<=({200,202} if same else {202,409}),summary
    assert pg==f'{expected}|{expected}|{expected}',summary
    assert remaining==stock-expected,summary
    assert len(upstreams)>=2,summary
    assert rows and all(row[1:]==['0','0'] for row in rows),queues
print('HTTP assertions passed',flush=True)
