"""One PostgreSQL snapshot plus the raw HTTP-202 ledger; retain every violating key."""
import collections, json, os, pathlib, re, subprocess, sys
root=pathlib.Path(sys.argv[1])
pg=os.environ.get('BENCH_POSTGRES','flash_postgres')
sql="""begin isolation level repeatable read read only;
select jsonb_build_object('kind','products','row',to_jsonb(p)) from products p;
select jsonb_build_object('kind','orders','row',to_jsonb(o)) from orders o;
select jsonb_build_object('kind','events','row',to_jsonb(e)) from outbox_events e;
commit;"""
cmd=['docker','exec',pg,'psql','-U','postgres','-d','flashsale_db','-qAt','-v','ON_ERROR_STOP=1','-c',sql]
# Stream rows inside one snapshot. jsonb_agg of the full sale exceeded the capped
# PostgreSQL backend's memory in the retained first attempt.
with (root/'postgres-snapshot.jsonl').open('w') as f:
 p=subprocess.run(cmd,text=True,stdout=f,stderr=subprocess.STDOUT)
(root/'postgres-snapshot.log').write_text('$ '+' '.join(cmd)+'\nstdout=postgres-snapshot.jsonl\nexit='+str(p.returncode)+'\n')
if p.returncode: raise RuntimeError('Snapshot failed; see postgres-snapshot.jsonl')
data={'products':[],'orders':[],'events':[]}
with (root/'postgres-snapshot.jsonl').open() as f:
 for line in f:
  if line.strip():
   item=json.loads(line);data[item['kind']].append(item['row'])
orders=data['orders']; events=data['events']; product=next(p for p in data['products'] if p['id']==101)
accepted=re.findall(r'ACCEPTED (bench-[^\s"\\]+)',(root/'k6.log').read_text()) if (root/'k6.log').exists() else []
confirmed={o['idempotency_key']:o for o in orders if o['status']=='CONFIRMED'}
pending=[o for o in orders if o['status']=='PENDING']
qty=sum(o['quantity'] for o in confirmed.values()); held=sum(o['quantity'] for o in pending)
event_by_order={e['order_id']:e for e in events}
missing=[]
for o in orders:
 e=event_by_order.get(o['order_id'])
 if e is None or any(e['payload'].get(k)!=o[col] for k,col in [('orderId','order_id'),('productId','product_id'),('quantity','quantity'),('idempotencyKey','idempotency_key')]):
  missing.append(o['idempotency_key'])
duplicates=sorted(k for k,n in collections.Counter(o['idempotency_key'] for o in orders).items() if n>1)
result={'initial_stock':product['total_stock'],'postgres_stock':product['available_stock'],
 'http_202':len(accepted),'confirmed':len(confirmed),'confirmed_qty':qty,
 'pending_orders':len(pending),'pending_qty':held,'pending_events':sum(e['status']=='PENDING' for e in events),
 'cancelled_keys':sorted(o['idempotency_key'] for o in orders if o['status']=='CANCELLED'),
 'stock_ledger_delta':qty+held+product['available_stock']-product['total_stock'],
 'settled_stock_delta':qty+product['available_stock']-product['total_stock'],
 'lost_keys':sorted(set(accepted)-set(confirmed)), 'duplicate_keys':duplicates,
 'stuck_pending_keys':sorted(o['idempotency_key'] for o in pending), 'missing_or_mismatched_intent_keys':sorted(missing)}
result['pass']=not any([result['stock_ledger_delta'],result['settled_stock_delta'],result['lost_keys'],duplicates,
 result['stuck_pending_keys'],missing,result['pending_events'],result['cancelled_keys']])
if os.environ.get('CI_BENCH')=='true':
 result['oversold_count']=max(0,qty-product['total_stock'])
 result['pass']=result['pass'] and result['oversold_count']==0
(root/'outbox-invariants.json').write_text(json.dumps(result,indent=2)+'\n');print(json.dumps(result),flush=True)
sys.exit(0 if result['pass'] else 1)
