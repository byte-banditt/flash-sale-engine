"""Audit unit and quantity invariants against raw Postgres and Redis snapshots."""
import subprocess,json,pathlib,sys,re,collections
root=pathlib.Path(sys.argv[1])
def raw(args):
 p=subprocess.run(args,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
 if p.returncode:raise RuntimeError(p.stdout)
 return p.stdout.strip()
pg=raw(['docker','exec','flash_postgres','psql','-U','postgres','-d','flashsale_db','-tAc',"select coalesce(json_agg(row_to_json(t)), '[]'::json) from (select idempotency_key,order_id,quantity,status,created_at from orders order by idempotency_key) t"])
(root/'postgres-details.json').write_text(pg+'\n');orders=json.loads(pg)
lua="local out={} for _,pattern in ipairs({'idempotency:*','order:*:decision','order:*:rolled-back'}) do for _,key in ipairs(redis.call('KEYS',pattern)) do table.insert(out,key) table.insert(out,redis.call('GET',key)) end end return out"
redis=raw(['docker','exec','flash_redis','redis-cli','--json','EVAL',lua,'0'])
(root/'redis-state.json').write_text(redis+'\n');flat=json.loads(redis);state=dict(zip(flat[::2],flat[1::2]))
stock=raw(['docker','exec','flash_redis','redis-cli','--raw','GET','product:101:stock']);(root/'stock-audit.txt').write_text(stock+'\n');stock=int(stock) if stock else None
accepted=re.findall(r'ACCEPTED (bench-[^\s"\\]+)',(root/'k6.log').read_text());confirmed=[o for o in orders if o['status']=='CONFIRMED'];keys={o['idempotency_key'] for o in confirmed};ids={o['order_id'] for o in confirmed}
qty=sum(o['quantity'] for o in confirmed)
i2=[]
for o in confirmed:
 key=o['idempotency_key'];oid=o['order_id'];reservation=state.get('idempotency:'+key);decision=state.get('order:'+oid+':decision')
 if reservation!=oid and decision!='CONSUMER':i2.append({'key':key,'order_id':oid,'quantity':o['quantity'],'reservation':reservation,'decision':decision,'rolled_back':state.get('order:'+oid+':rolled-back'),'created_at':o['created_at']})
matches={(o['idempotency_key'],o['order_id']) for o in confirmed}
stuck=sorted(k.removeprefix('idempotency:') for k,v in state.items() if k.startswith('idempotency:') and (k.removeprefix('idempotency:'),v) not in matches)
stuck_decisions=sorted(k for k,v in state.items() if k.endswith(':decision') and v=='CONSUMER' and k.split(':')[1] not in ids)
lost=sorted(set(accepted)-keys);duplicates=sorted(k for k,v in collections.Counter(o['idempotency_key'] for o in confirmed).items() if v>1)
delta=None if stock is None else qty+stock-100000
result={'initial_stock':100000,'http_202':len(accepted),'confirmed':len(confirmed),'confirmed_quantity':qty,'redis_stock':stock,'i1_delta_units':delta,'i1_violation':delta!=0,'i1_violation_units':None if delta is None else abs(delta),'i1_related_keys':[o['key'] for o in i2] if delta else [],'i2_violations':len(i2),'i2_keys':i2,'lost':len(lost),'lost_keys':lost,'duplicates':len(confirmed)-len(keys),'duplicate_keys':duplicates,'stuck':len(stuck),'stuck_keys':stuck,'stuck_decisions':len(stuck_decisions),'stuck_decision_keys':stuck_decisions,'confirmed_without_http_202':sorted(keys-set(accepted))}
(root/'invariants.json').write_text(json.dumps(result,indent=2)+'\n');print(json.dumps(result),flush=True)

# Signal the campaign to stop, without modifying source, if the requested repro is null.
if root.parent.name=="repro" and root.name=="rabbit-10":
    runs=[json.loads(p.read_text()) for p in root.parent.glob("*/invariants.json")]
    if len(runs)==10 and all(not r["i1_violation"] and r["i2_violations"]==0 for r in runs):
        print("UNREPRODUCED: all requested repro runs have no I1/I2 violation; stop before source edits",flush=True)
        sys.exit(3)
