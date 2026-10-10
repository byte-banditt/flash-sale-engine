"""Extract actual trace events; never substitute row creation time for commit time."""
import pathlib,json,re,sys
root=pathlib.Path(sys.argv[1]);audit=json.loads((root/'invariants.json').read_text());lines=(root/'app.log').read_text().splitlines();result=[]
for order in audit['i2_keys']:
 selected=[l for l in lines if 'ORDER_TRACE ' in l and ('orderId='+order['order_id']+' ') in l]
 events=[]
 for line in selected:
  event=re.search(r'event=(\w+)',line);epoch=re.search(r'epochMs=(\d+)',line)
  events.append({'event':event[1], 'epoch_ms':int(epoch[1]),'raw':line})
 failures=[e for e in events if e['event']=='CONFIRM_FAILURE'];starts=[e['epoch_ms'] for e in events if e['event']=='REFUND_START'];ends=[e['epoch_ms'] for e in events if e['event']=='REFUND_END' and 'applied=true' in e['raw']];commits=[e['epoch_ms'] for e in events if e['event']=='PERSIST_COMMIT']
 relation='unobserved'
 if starts and ends and commits:
  relation='persist_before_refund' if min(commits)<min(starts) else 'refund_before_persist' if min(ends)<min(commits) else 'overlap_or_same_millisecond'
 result.append({'key':order['key'],'order_id':order['order_id'],'confirm_failure_types':[re.search(r'type=(\w+)',e['raw'])[1] for e in failures],'relation':relation,'events':events})
(root/'timeline.json').write_text(json.dumps(result,indent=2)+'\n');print(json.dumps(result),flush=True)
