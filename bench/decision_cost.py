"""Isolated cached decision-Lua RPC timings, distinct from HTTP path deltas.
Uses own keys only; does not clear any Redis database. Run with k6 stopped.
"""
import socket,time,json,pathlib,uuid,sys
root=pathlib.Path(sys.argv[1]);root.mkdir(parents=True,exist_ok=False)
sock=socket.create_connection(('localhost',6379));reader=sock.makefile('rb')
def response():
 line=reader.readline();kind=line[:1];body=line[1:-2]
 if kind==b'+':return body.decode()
 if kind==b':':return int(body)
 if kind==b'-':raise RuntimeError(body.decode())
 if kind==b'$':
  n=int(body)
  if n<0:return None
  value=reader.read(n);assert reader.read(2)==b'\r\n';return value.decode()
 raise RuntimeError('Unsupported RESP response: '+repr(line))
def command(*args):
 parts=[str(x).encode() if not isinstance(x,bytes) else x for x in args]
 sock.sendall(b'*'+str(len(parts)).encode()+b'\r\n'+b''.join(b'$'+str(len(x)).encode()+b'\r\n'+x+b'\r\n' for x in parts));return response()
script=pathlib.Path('src/main/resources/scripts/claim_persistence.lua').read_bytes();sha=command('SCRIPT','LOAD',script)
prefix='bench-decision-cost:'+str(uuid.uuid4());owned=[];summaries=[]
def percentile(values,q):
 v=sorted(values);pos=(len(v)-1)*q;lo=int(pos);hi=min(lo+1,len(v)-1)
 return (v[lo]+(v[hi]-v[lo])*(pos-lo))/1_000_000
try:
 for batch in range(1,4):
  for case in ['new_claim','resume_claim','refund_blocks']:
   calls=[];orders=[]
   for i in range(1000):
    oid=f'{prefix}:{batch}:{case}:{i}';keys=[oid+':decision',oid+':rolled-back',oid+':reservation'];owned.extend(keys);command('SET',keys[2],oid)
    if case!='new_claim':command('SET',keys[0],'CONSUMER' if case=='resume_claim' else 'REFUND')
    orders.append((oid,keys))
   start=time.perf_counter_ns()
   for oid,keys in orders:
    began=time.perf_counter_ns()
    try:value=command('EVALSHA',sha,3,*keys,oid)
    except Exception as e:
     calls.append({'order_id':oid,'elapsed_ns':time.perf_counter_ns()-began,'error':repr(e)})
     (root/f'{batch}-{case}-raw.json').write_text(json.dumps(calls,indent=2)+'\n');raise
    elapsed=time.perf_counter_ns()-began
    calls.append({'order_id':oid,'elapsed_ns':elapsed,'result':value})
   elapsed=time.perf_counter_ns()-start
   (root/f'{batch}-{case}-raw.json').write_text(json.dumps(calls,indent=2)+'\n')
   expected=0 if case=='refund_blocks' else 1
   assert all(c['result']==expected for c in calls),case
   values=[c['elapsed_ns'] for c in calls]
   summaries.append({'batch':batch,'case':case,'samples':len(calls),'elapsed_ns':elapsed,'calls_per_s':len(calls)*1_000_000_000/elapsed,'p50_ms':percentile(values,.5),'p95_ms':percentile(values,.95),'p99_ms':percentile(values,.99),'errors':0})
except Exception as e:
 (root/'failure.txt').write_text(repr(e)+'\n');raise
finally:
 for key in owned:command('DEL',key)
 sock.close()
(root/'summary.json').write_text(json.dumps({'script_sha':sha,'prefix':prefix,'runs':summaries},indent=2)+'\n');print(json.dumps(summaries))
