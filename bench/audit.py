"""Audit all completed ledgers and preserve confirmed-without-202 keys."""
from pathlib import Path
import json,re
rows=[]
for group in ['replicas','faults-before','confirms']:
 for p in sorted(Path('bench/results',group).glob('*/reconciliation.json')):
  d=p.parent;r=json.loads(p.read_text());metrics=json.loads((d/'summary.json').read_text())['metrics']
  accepted=re.findall(r'ACCEPTED (bench-[^\s"\\]+)',(d/'k6.log').read_text())
  pg=[l.split(',') for l in (d/'orders.csv').read_text().splitlines() if l]
  confirmed=[key for key,status,qty in pg if status=='CONFIRMED']
  extras=sorted(set(confirmed)-set(accepted))
  (d/'confirmed-without-202.json').write_text(json.dumps(extras,indent=2)+'\n')
  assert len(accepted)==len(set(accepted))==metrics.get('accepted_orders',{}).get('count',0)==r['http_202'],d
  assert len(confirmed)==r['confirmed'],d
  assert len(extras)==r['unacknowledged_confirmed'],d
  assert len(set(accepted)-set(confirmed))==r['lost_orders'],d
  assert len(confirmed)-len(set(confirmed))==r['duplicate_orders'],d
  rows.append({'group':group,'run':d.name,'http_202':r['http_202'],'confirmed':r['confirmed'],'confirmed_without_http_202':len(extras),'lost':r['lost_orders'],'stuck':r['stuck_reservations'],'stock_accounting_excess':r['stock_accounting_excess']})
print(json.dumps(rows,indent=2))
Path('bench/results/ledger-audit.json').write_text(json.dumps(rows,indent=2)+'\n')
