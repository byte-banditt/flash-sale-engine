"""Print exact stored metrics, resource extrema, and accounting checks."""
import json,pathlib,sys,re,collections
root=pathlib.Path(sys.argv[1])
for path in sorted(root.glob('*/reconciliation.json')):
 d=path.parent;s=json.loads((d/'summary.json').read_text())['metrics'];r=json.loads(path.read_text())
 assert s.get('accepted_orders',{}).get('count',0)==r['http_202'],(d,'ledger count mismatch')
 lat=s['http_req_duration']
 print(json.dumps({'run':d.name,'req_per_s':s['http_reqs']['rate'],'p50_ms':lat['med'],'p95_ms':lat['p(95)'],'p99_ms':lat['p(99)'],'http_error_rate':s['http_req_failed']['value'],**r}))
 stats=collections.defaultdict(list)
 for line in (d/'stats.log').read_text().splitlines():
  if not line.startswith('{'):continue
  try:p=json.loads(line)
  except ValueError:continue
  stats[p['Name']].append(p)
 for name,points in stats.items():
  cpu=max(points,key=lambda p:float(p['CPUPerc'].removesuffix('%')))
  # Docker displays MiB/GiB; retain original display strings (no added rounding).
  def bytesused(p):
   part=p['MemUsage'].split(' / ')[0];m=re.match(r'([0-9.]+)(\w+)',part)
   return float(m[1])*{'B':1,'KiB':1024,'MiB':1024**2,'GiB':1024**3}[m[2]]
  mem=max(points,key=bytesused)
  print(json.dumps({'run':d.name,'container':name,'max_observed_cpu':cpu['CPUPerc'],'max_observed_memory':mem['MemUsage']}))
