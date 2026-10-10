"""Exact JSON/Markdown from executed experiments. No interpolation or rounding."""
import collections,json,pathlib,re
base=pathlib.Path('bench/results/decision-fix')
rows=[];resources=[]
for p in sorted(base.glob('*/*/invariants.json')):
 d=p.parent;a=json.loads(p.read_text());m=json.loads((d/'summary.json').read_text())['metrics'];lat=m['http_req_duration']
 assert m.get('accepted_orders',{}).get('count',0)==a['http_202'],d
 row={'group':d.parent.name,'run':d.name,'http_202':a['http_202'],'confirmed':a['confirmed'],'confirmed_quantity':a['confirmed_quantity'],'redis_stock':a['redis_stock'],'i1_delta_units':a['i1_delta_units'],'i1_violation_units':a['i1_violation_units'],'i2_violations':a['i2_violations'],'lost':a['lost'],'duplicates':a['duplicates'],'stuck':a['stuck'],'stuck_decisions':a['stuck_decisions'],'http_error_rate':m['http_req_failed']['value'],'req_per_s':m['http_reqs']['rate'],'p50_ms':lat['med'],'p95_ms':lat['p(95)'],'p99_ms':lat['p(99)'],'k6_exit':json.loads((d/'reconciliation.json').read_text())['k6_exit']}
 rows.append(row)
 stats=collections.defaultdict(list)
 for l in (d/'stats.log').read_text().splitlines():
  if l.startswith('{'):
   try:v=json.loads(l);stats[v['Name']].append(v)
   except (ValueError,KeyError):pass
 def memory(v):
  match=re.match(r'([0-9.]+)(\w+)',v['MemUsage']);return float(match[1])*{'B':1,'KiB':1024,'MiB':1024**2,'GiB':1024**3}[match[2]]
 for name,points in stats.items():
  resources.append({'group':d.parent.name,'run':d.name,'container':name,'max_observed_cpu':max(points,key=lambda x:float(x['CPUPerc'].strip('%')))['CPUPerc'],'max_observed_memory':max(points,key=memory)['MemUsage']})
aggregate=[]
for group in sorted({r['group'] for r in rows}):
 rr=[r for r in rows if r['group']==group];violation_runs=sum(any(r[k] for k in ['i1_violation_units','i2_violations','lost','duplicates','stuck','stuck_decisions']) for r in rr)
 aggregate.append({'group':group,'runs':len(rr),'violating_runs':violation_runs,'i1_excess_units':sum(r['i1_delta_units'] for r in rr),'i2_violations':sum(r['i2_violations'] for r in rr),'lost':sum(r['lost'] for r in rr),'duplicates':sum(r['duplicates'] for r in rr),'stuck':sum(r['stuck'] for r in rr),'stuck_decisions':sum(r['stuck_decisions'] for r in rr),'rule_of_three_upper_bound':3/len(rr) if violation_runs==0 else None})
comparisons=[]
for idx in range(1,4):
 before=next((r for r in rows if r['group']=='before-normal' and r['run']==f'normal-{idx}'),None);after=next((r for r in rows if r['group']=='after-normal' and r['run']==f'normal-{idx}'),None)
 if before and after:comparisons.append({'pair':idx,**{k+'_delta_after_minus_before':after[k]-before[k] for k in ['req_per_s','p50_ms','p95_ms','p99_ms','http_error_rate']}})
(base/'metrics.json').write_text(json.dumps({'runs':rows,'groups':aggregate,'resources':resources,'normal_comparison':comparisons},indent=2)+'\n')
columns=['group','run','http_202','confirmed','redis_stock','i1_delta_units','i2_violations','lost','duplicates','stuck','stuck_decisions','http_error_rate','req_per_s','p50_ms','p95_ms','p99_ms','k6_exit']
lines=['| '+' | '.join(columns)+' |','| '+' | '.join('---' for _ in columns)+' |']
for r in rows:lines.append('| '+' | '.join(str(r[k]) for k in columns)+' |')
(base/'run-table.md').write_text('\n'.join(lines)+'\n')
print(json.dumps(aggregate))
