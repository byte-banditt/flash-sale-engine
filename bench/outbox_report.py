"""Append only measured outbox-session evidence; never replace historical sections."""
import datetime, decimal, hashlib, json, pathlib, re
R=pathlib.Path('bench/results/outbox-session')
F=R/'testcontainers-final'
def read(p): return json.loads(p.read_text())
def link(p): return f'[{p.name}]({p.as_posix()})'
def metrics(p):
 m=read(p/'summary.json')['metrics']
 return {k:m[a][b] for k,a,b in [('req/s','http_reqs','rate'),('p50 ms','http_req_duration','med'),
   ('p95 ms','http_req_duration','p(95)'),('p99 ms','http_req_duration','p(99)'),('HTTP error fraction','http_req_failed','value')]}
def table(headers,rows):
 return '\n'.join(['| '+' | '.join(headers)+' |','| '+' | '.join(['---']*len(headers))+' |']+
  ['| '+' | '.join(str(v) for v in row)+' |' for row in rows])+'\n'
def summaries(p):
 s=p.read_text()
 matches=re.findall(r'Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)',s)
 return list(map(int,matches[-1])) if matches else None

rabbits=[F/f'rabbit-{i}' for i in range(1,11)]
kills=[F/f'kill-{i}' for i in range(1,4)]
assert all((p/'outbox-invariants.json').exists() for p in rabbits+kills), 'Do not fill unexecuted runs'
before=R/'before-comparison'; after=R/'after-comparison'
assert (after/'summary.json').exists(), 'After load has not completed'
bm=metrics(before); am=metrics(after)
full=R/'full-fault-tests-attempt-3.log'
targeted=R/'poller-and-core-tests.log'; tests=summaries(targeted)
crash_root=R/'testcontainers-poller-retry/poller-crash'
crash=read(crash_root/'result.json')
all_pass=all(read(p/'outbox-invariants.json')['pass'] and read(p/'reconciliation.json')['drained'] for p in rabbits+kills)
all_pass=all_pass and tests is not None and tests[1:]==[0,0,0] and 'BUILD SUCCESS' in targeted.read_text() and crash['pass']
all_pass=all_pass and read(after/'outbox-invariants.json')['pass'] and read(after/'reconciliation.json')['drained']
rows=[]
for p in rabbits+kills:
 a=read(p/'outbox-invariants.json'); m=metrics(p)
 rows.append([p.name,'PASS' if a['pass'] and read(p/'reconciliation.json')['drained'] else 'FAIL',a['http_202'],a['confirmed'],a['confirmed_qty'],a['postgres_stock'],
   a['settled_stock_delta'],len(a['missing_or_mismatched_intent_keys']),len(a['lost_keys']),len(a['duplicate_keys']),
   a['pending_orders'],a['pending_events'],len(a['cancelled_keys']),read(p/'reconciliation.json')['drained'],m['HTTP error fraction'],m['req/s'],m['p50 ms'],m['p95 ms'],m['p99 ms'],read(p/'reconciliation.json')['k6_exit']])
headers=['run','result','202','CONFIRMED','qty','PG stock','stock delta','bad intent','lost','duplicate','PENDING orders','PENDING events','cancelled','queue drained','HTTP error fraction','req/s','p50 ms','p95 ms','p99 ms','k6 exit']
normal=[]
for p in [R/'before-normal-1',R/'before-normal-clean-1',R/'before-final-1',before,after]:
 a=read(p/'reconciliation.json'); m=metrics(p)
 normal.append([p.name,a['http_202'],a['confirmed'],a.get('postgres_stock',a['redis_stock']),a['lost_orders'],a['drained'],
   m['req/s'],m['p50 ms'],m['p95 ms'],m['p99 ms'],m['HTTP error fraction'],a['k6_exit']])
delta={k:str(decimal.Decimal(str(am[k]))-decimal.Decimal(str(bm[k]))) for k in bm}
resource=[]
for p in rabbits+kills+[after]:
 for i in range(5):
  def counters(file):
   result={k:int(v) for k,v in re.findall(r'^(\w+) (\d+)$',file.read_text(),re.M)}
   return result or None
  a=counters(p/f'before-cgroup-{i}.txt'); b=counters(p/f'post-oracle-cgroup-{i}.log')
  resource.append({'run':p.name,'container_index':i,'before':a,'after_oracle':b})
(R/'resource-audit.json').write_text(json.dumps(resource,indent=2)+'\n')
date=datetime.datetime.now(datetime.timezone.utc).date().isoformat()
text=f'''\n<!-- outbox-session -->
## {date} — transactional outbox session

Verdict: {'the ambiguous-confirm refund defect is fixed in the executed recovery cases' if all_pass else 'NOT all executed cases pass; the fix is not established'}. The table below is this session's evidence, not a consistency proof.
The load generator, nginx, apps and infrastructure share one laptop. PostgreSQL
is now the stock source; Redis reservations are no longer authoritative.
During Testcontainers faults the original compose apps/proxy were stopped, while
the original infrastructure containers remained running alongside the isolated
fixture. The HTTP before/after comparison uses the original compose stack.

The old timeout path threw AmqpException and invoked controller rollback. The
existing HEAD already contained Redis REFUND/CONSUMER arbitration; this session
replaces that boundary rather than claiming the committed arbiter was absent.
Stock decrement, PENDING order and delivery intent now share one PostgreSQL
transaction. Consumer and sweep lock the same order before selecting its terminal
status. Only sweep cancellation refunds. Relay failure leaves the event PENDING.
Publication remains at least once; a DB/broker atomic transaction is not claimed.

### Test output and retained failures

'''
logs=[R/'baseline/tests.log']+sorted(R.glob('compile-attempt-*.log'))+sorted(R.glob('tests-attempt-*.log'))+sorted(R.glob('full-fault-tests-attempt-*.log'))+[targeted]
testrows=[]
for p in logs:
 s=p.read_text(); count=summaries(p)
 status='BUILD SUCCESS' if 'BUILD SUCCESS' in s else 'BUILD FAILURE' if 'BUILD FAILURE' in s else 'INTERRUPTED / no completed build verdict'
 if status.startswith('INTERRUPTED'): count=None
 testrows.append([p.name,count if count is not None else 'null',status,link(p)])
text+=table(['log','[run,fail,error,skip]','build verdict','raw'],testrows)
text+='\nRaw final targeted Maven summary:\n```text\n'+ '\n'.join(line for line in targeted.read_text().splitlines() if '[INFO] Tests run:' in line or '[INFO] BUILD ' in line)+'\n```\n'
text+='''
The final long campaign completed the HTTP harness, every standard broker restart
and every replica kill, with per-case PASS logs and complete oracles. Its poller
test then exhausted the test JVM heap while reading the fixture's entire log
history; Maven reported BUILD FAILURE and its zero-test aggregate is incomplete,
not a claim that no cases executed. The dump identifies ContainerState.getLogs.
The replacement streams only the already-captured current-case log files. A
targeted rerun executes the poller crash and all core/unit tests; the unchanged
completed fault cases are not substituted with invented results or a fictitious
single green full-suite summary.

'''
text+='Retained heap failure: '+link(R/'poller-log-oom.dump')+', '+link(R/'poller-log-oom.dumpstream')+'.\n'
text+='''
The first container campaign's full-table JSON aggregation killed a capped
PostgreSQL backend. Its first run's basic reconciliation passed, but its full
oracle failed; the next run was interrupted to repair the collector. Neither
attempt is discarded. The replacement streams individual rows inside one
repeatable-read snapshot. Production caps were not increased.

'''
old=R/'testcontainers/rabbit-1'; partial=R/'testcontainers/rabbit-2'
text+='First failed attempt raw reconciliation:\n```json\n'+(old/'reconciliation.json').read_text()+'```\n'
text+='Collector failure:\n```text\n'+(old/'invariants-command.log').read_text()+'```\n'
text+='Observed counters after the failed collector:\n```text\n'+(partial/'interruption-memory-events.log').read_text()+'```\n'
text+='Interrupted next run: '+link(partial/'interrupted.txt')+', '+link(partial/'interruption-counts.log')+'. No final invariant counts are invented for it.\n'
text+='''
The next campaign was also interrupted after Docker exec failed against a broker
that Docker still reported Running with OOMKilled=false. Its management host port
refused connections, while a management query through the app network succeeded.
The collector now retains failed CLI output, falls back to actual management queue
counters through the surviving app, and records unavailable cgroup data as null.
Missing observations cannot establish zero OOM or zero throttling. Management
counters have the broker's statistics refresh delay; the same repeated quiet-poll
condition and settled PostgreSQL intent are required before the oracle.

'''
middle=R/'testcontainers-rerun'; middle_rows=[]
for p in sorted(middle.glob('rabbit-*'),key=lambda p:int(p.name.split('-')[1])):
 a=read(p/'outbox-invariants.json') if (p/'outbox-invariants.json').exists() else None
 b=read(p/'reconciliation.json') if (p/'reconciliation.json').exists() else None
 middle_rows.append([p.name, a['pass'] if a else 'null',b['drained'] if b else 'null',
   a['http_202'] if a else 'null',a['confirmed'] if a else 'null',a['postgres_stock'] if a else 'null',
   'extra diagnostic restart during load' if p.name=='rabbit-6' else 'queue/exec observations failed' if p.name=='rabbit-5' else 'retained interrupted campaign'])
text+=table(['retained run','stock/intent oracle','drained','202','CONFIRMED','stock','qualification'],middle_rows)
text+='Diagnostic restart was recorded under rabbit-5 but occurred during rabbit-6, as the actual timestamps show. Neither is substituted for a standard run. Raw: '+link(middle/'rabbit-5/additional-diagnostic-restart.log')+', '+link(middle/'rabbit-5/drain.log')+', '+link(middle/'internal-management-probe.log')+', '+link(middle/'interrupted-campaign.txt')+'.\n'
text+='\n### Per-run recovery results\n\n'+table(headers,rows)
text+='\nEach row links to a raw HTTP ledger, queue polls, orders, a streamed PostgreSQL snapshot, every violating-key list, and post-oracle resource counters in its run directory.\n'
for p in rabbits+kills:
 text+=f'- {p.name}: '+link(p/'outbox-invariants.json')+', '+link(p/'summary.json')+', '+link(p/'postgres-snapshot.jsonl')+'\n'
text+='\nFailed-key lists remain in each oracle JSON even when empty. Zero observed violations does not establish universal consistency.\n'
n=len(rabbits)
if all(read(p/'outbox-invariants.json')['pass'] for p in rabbits):
 text+=f'For these {n} completed broker-restart reruns: rule of three, approximate 95% per-run upper bound = 3/{n} = {3/n}, assuming independent comparable trials. Shared warm fixtures do not establish that independence.\n'
http=F/'http'
text+='\n### Existing HTTP scenarios and deterministic poller crash\n\n'
for name in ['oversell','same-key']:
 text+='```json\n'+(http/f'{name}-reconciliation.json').read_text()+'\n```\n'
text+='Poller crash result:\n```json\n'+json.dumps(crash,indent=2)+'\n```\n'
text+='''The test blocks PUBLISHED in a DB trigger after an actual broker ack,
stops the peer relay, kills the publishing app, terminates its abandoned sleeping
DB session, removes the trigger, and restarts both apps. Republish is expected
and explicitly authorized; a single terminal order is the invariant. This
deterministic case includes those additional controls and is not described as
an uncontrolled kill alone. See '''+link(crash_root/'mid-batch-postgres-activity.json')+'.\n'
text+='\n### HTTP load before / after\n\n'+table(['run','202','CONFIRMED','stock','lost','drained','req/s','p50 ms','p95 ms','p99 ms','HTTP error fraction','k6 exit'],normal)
text+='''
The first baseline overlaps Maven tests, the next overlaps packaging, and the
third overlaps a failed test compilation; all remain reported as noisy attempts.
The formal before-comparison run overlaps none of those commands. Before uses
the existing Redis-arbiter image; after uses the outbox image, both with the
same compose replica count, JVM/container limits and unmodified bench/load.js.
JIT/cache age was not matched or proven identical. Captured source diffs and
image IDs distinguish deployed code from edits to the workspace.

HTTP semantics changed: before waits for broker confirmation; after acknowledges
durable intent. These are HTTP response costs for the whole architecture change,
not an isolated Postgres-insert cost or a claim about consumer completion latency.
'''
text+=table(['metric','before','after','after minus before'],[[k,bm[k],am[k],delta[k]] for k in bm])
if am['req/s'] < bm['req/s']:
 text+='\nMeasured HTTP throughput was lower after the outbox change in this comparison. All reported latency percentiles increased. The attributable bottleneck remains unisolated; the result compares the whole architecture, with the warm-state limitation above.\n'
text+='\nAfter-load raw reconciliation:\n```json\n'+(after/'reconciliation.json').read_text()+'```\n'
text+='After-load atomic oracle: '+link(after/'outbox-invariants.json')+'.\n'
text+='\n### Environment, schema and resource evidence\n\n'
text+='```text\n'+(R/'baseline/environment.log').read_text()+'\n```\n'
text+='Actual fixture schema: '+link(R/'testcontainers/schema.sql')+'; runtime versions: '+link(R/'testcontainers/runtime-versions.log')+'.\n'
text+='Before image: `'+(R/'baseline/app-image-before.txt').read_text().strip()+'`; after image: `'+(R/'app-image-after.txt').read_text().strip()+'`.\n'
text+='Source-hash command output for the before and after runs:\n```text\n'+(before/'git.txt').read_text()+(after/'git.txt').read_text()+'```\n'
text+='Resource counters before each case and after the oracle are preserved in '+link(R/'resource-audit.json')+'. Empty/unavailable observations are null. Docker CPU/memory snapshots and free -b output are retained in every stats.log. The failed collector OOM remains a separate negative result; do not infer zero OOM from the earlier pre-oracle inspect.\n'
text+='Cgroup counters can reset at container restart. Sampled CPU/memory observations are not guaranteed peaks and do not measure host thermal throttling.\n'
text+='''
Redis has no coordinated recovery design; it is now outside the inventory
correctness boundary. PostgreSQL/broker permanence, infrastructure replication,
live generation migration and an unconditional accepted-order success guarantee
remain limitations. A prolonged outage can reach the configured cancellation
deadline. Inventory row locking can limit a hot sale. Outbox drain extending
past HTTP load demonstrates that accepted HTTP throughput is not completed-order
throughput. The per-run error fractions above are measured; no historic outage
error range is substituted.
The captured Testcontainers PostgreSQL command includes fsync=off; these tests
exercise app/broker failures, not PostgreSQL or power-loss durability. The compose
comparison uses its separate original PostgreSQL container.

### DRAFT — resume numbers, copied from result files

'''
oversell=read(http/'oversell-reconciliation.json')
text+=f"DRAFT: HTTP oversell test: {oversell['requests']} requests, stock {oversell['initial_stock']}, accepted {oversell['statuses']['202']}, PostgreSQL `{oversell['postgres']}`, remaining stock {oversell['postgres_stock']}. Source: {link(http/'oversell-reconciliation.json')}.\n\n"
text+=f'DRAFT: completed broker-restart reruns {n}; invariant passes {sum(read(p/"outbox-invariants.json")["pass"] for p in rabbits)}. Per-run source files listed above; retained collector failure and interrupted attempt remain disclosed.\n'
Path=pathlib.Path
Path('bench/OUTBOX_REPORT.md').write_text('# Outbox session evidence\n'+text.replace('](bench/', ']('))
results=Path('RESULTS.md'); prior=results.read_text(); marker='\n<!-- outbox-session -->'
prefix=prior.split(marker)[0]
(R/'results-prefix.sha256').write_text(hashlib.sha256(prefix.encode()).hexdigest()+'\n')
results.write_text(prefix+text)
(R/'report-metrics.json').write_text(json.dumps({'tests':tests,'recovery_rows':rows,'before':bm,'after':am,'delta':delta,'verdict_pass':all_pass},indent=2)+'\n')
print(json.dumps({'verdict_pass':all_pass,'tests':tests,'rabbit_runs':n,'kill_runs':len(kills),'before':bm,'after':am,'delta':delta}),flush=True)
