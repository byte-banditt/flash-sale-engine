"""Exact chronological run-pair deltas; no rounding or missing-run substitution."""
from pathlib import Path
import json
rows=[]
for n in [1,2,3]:
 before=json.loads(Path(f'bench/results/replicas/r2-run-{n}/summary.json').read_text())['metrics']
 after=json.loads(Path(f'bench/results/confirms/normal-run-{n}/summary.json').read_text())['metrics']
 measures={'req_per_s':('http_reqs','rate'),'p50_ms':('http_req_duration','med'),'p95_ms':('http_req_duration','p(95)'),'p99_ms':('http_req_duration','p(99)')}
 for measure,(metric,field) in measures.items():
  b=before[metric][field];a=after[metric][field]
  rows.append({'pair':n,'measure':measure,'before':b,'after':a,'after_minus_before':a-b})
for fault in ['kill','rabbit']:
 for n in [1,2,3]:
  before=json.loads(Path(f'bench/results/faults-before/{fault}-run-{n}/reconciliation.json').read_text())
  after=json.loads(Path(f'bench/results/confirms/{fault}-run-{n}/reconciliation.json').read_text())
  rows.append({'fault':fault,'pair':n,'before_lost':before['lost_orders'],'after_lost':after['lost_orders'],'before_stuck':before['stuck_reservations'],'after_stuck':after['stuck_reservations'],'before_oversold':before['oversold_count'],'after_oversold':after['oversold_count'],'before_stock_accounting_excess':before['stock_accounting_excess'],'after_stock_accounting_excess':after['stock_accounting_excess']})
result=json.dumps(rows,indent=2)+'\n';print(result,end='')
Path('bench/results/confirms/comparison.json').write_text(result)
