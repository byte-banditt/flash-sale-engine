"""Build final reviewable report from complete local result files."""
import pathlib,json,re,subprocess,collections
base=pathlib.Path('bench/results/decision-fix');data=json.loads((base/'metrics.json').read_text());groups={g['group']:g for g in data['groups']}
for name,count in [('repro',10),('before-normal',3),('after-rabbit',20),('after-kill',3),('after-normal',3)]:
 assert groups[name]['runs']==count,(name,groups.get(name))
root=pathlib.Path('bench/REPORT.md');lines=[]
def add(s):lines.append(s)
def fence(s):return '\n```text\n'+'\n'.join(line.rstrip() for line in s.rstrip().splitlines())+'\n```\n'
def table(rows,cols):
 return '\n| '+' | '.join(cols)+' |\n| '+' | '.join('---' for _ in cols)+' |\n'+'\n'.join('| '+' | '.join(str(r.get(c)) for c in cols)+' |' for r in rows)+'\n'
add('# Refund/persistence race: executed evidence\n')
add('All measurements below come from local executed runs. k6, nginx, both app replicas, Redis, RabbitMQ and PostgreSQL shared one laptop. No parallel load campaigns, builds or tests ran during measured load. Host-side report preparation and saved-snapshot audits did run during parts of the fault campaigns; that local tooling noise is retained. The normal-ramp comparison was not overlapped with those full snapshot scans. Docker snapshots are sampled observations, not guaranteed peaks. No runs were removed for threshold failures or noise.\n')
outage=[r for r in data['runs'] if r['group']=='after-rabbit'];worst=max(outage,key=lambda r:r['http_error_rate'])
(base/'availability-finding.json').write_text(json.dumps({'cohort':'after-rabbit','min_http_error_fraction':min(r['http_error_rate'] for r in outage),'max_http_error_fraction':worst['http_error_rate'],'worst_run':worst['run']},indent=2)+'\n')
add('**Negative availability finding:** fixed broker-restart runs still failed HTTP requests. The largest measured HTTP error fraction was `'+str(worst['http_error_rate'])+'` in `'+worst['run']+'`; that run is retained, not dismissed as noise. Inventory arbitration does not fix broker-outage HTTP availability.\n')
add('## Code and environment\n')
add('Initial source HEAD: `'+(base/'setup/head.txt').read_text().strip()+'`. The pre-existing RabbitMQConfig comment cleanup is recorded in setup/preexisting.diff and remains excluded from commits. The normal baseline source matches that HEAD; its later runs record a bench-only commit. All baseline containers used the same app image, checked below.\n')
add('Raw machine/version output:'+fence((base/'setup/environment.txt').read_text()))
image=(base/'setup/baseline-image.txt').read_text().strip();add('Baseline app image: `'+image+'`.\n')
add('Raw jar checksums:'+fence((base/'setup/baseline-jar.sha256').read_text()+(base/'after-setup/jar.sha256').read_text()))
heads=[];limits=[];resource_events=[];host=[]
for row in data['runs']:
 d=base/row['group']/row['run'];text=(d/'git.txt').read_text();h=re.search(r'^([0-9a-f]{40})$',text,re.M)[1];heads.append({'group':row['group'],'run':row['run'],'HEAD':h})
 inspect=(d/'inspect.log').read_text();start=inspect.index('\n[')+1;objects=json.JSONDecoder().raw_decode(inspect[start:])[0]
 for item in objects:
  if row['group'] in ['before-normal','repro'] and '-app-' in item['Name']:assert item['Image']==image,(d,item['Image'])
  if '-app-' in item['Name']:
   assert item['HostConfig']['Memory']==671088640
   assert 'JAVA_TOOL_OPTIONS=-Xmx256m' in item['Config']['Env']
   if row['group']!='diagnostic':assert not any(v.startswith('LOGGING_LEVEL_') for v in item['Config']['Env']),d
  if row['run']=='rabbit-1' and row['group'] in ['repro','after-rabbit']:
   limits.append({'group':row['group'],'container':item['Name'],'memory_bytes':item['HostConfig']['Memory'],'memory_plus_swap_bytes':item['HostConfig']['MemorySwap'],'cpu_quota':item['HostConfig']['CpuQuota'],'oom_killed':item['State']['OOMKilled']})
 for p in sorted(d.glob('*-cgroup-*.txt')):
  t=p.read_text();fields={k:int(v) for k,v in re.findall(r'^(nr_throttled|throttled_usec|oom|oom_kill|max) (\d+)$',t,re.M)}
  resource_events.append({'group':row['group'],'run':row['run'],'snapshot':p.name,**fields})
 mem=[];swap=[]
 for line in (d/'stats.log').read_text().splitlines():
  if line.startswith('Mem:'):mem.append(int(line.split()[-1]))
  if line.startswith('Swap:'):swap.append(int(line.split()[2]))
 host.append({'group':row['group'],'run':row['run'],'min_observed_available_host_bytes':min(mem) if mem else None,'max_observed_swap_used_bytes':max(swap) if swap else None})
(base/'resource-audit.json').write_text(json.dumps({'limits':limits,'cgroup_snapshots':resource_events,'host_samples':host},indent=2)+'\n')
add('Exact recorded limits (CPU quota values come from inspect):'+table(limits,['group','container','memory_bytes','memory_plus_swap_bytes','cpu_quota','oom_killed']))
add('Each run records HEAD, source diff, jar/image provenance, Compose configuration, Docker inspect and unchanged harness checksums. Source diffs and bench-only hashes are retained rather than labeling every run as the initial HEAD. '+table(heads,['group','run','HEAD']))
add('## Tests\n')
for title,path in [('Initial original tests','setup/tests.log'),('Trace-only original tests','trace-setup/tests.log'),('Fix original tests','fix-setup/original-tests.log'),('Original plus added tests','test-setup/tests-attempt-1.log')]:
 text=(base/path).read_text();tail='\n'.join(text.splitlines()[-13:]);add(title+' — raw Maven output:'+fence(tail))
add('Original test methods were compared byte-for-byte to the initial HEAD. Added tests use a real broker delivery with the original confirm future left unresolved; force both refund-first and consumer-first outcomes; cover a nondelivered nack followed by same-key retry; claim-before-crash redelivery; and claimed-DLQ continuation. Raw method audit:'+fence((base/'test-setup/original-methods.json').read_text()))
add('## Reproduction and cause\n')
add(table([groups['repro'],groups['diagnostic']],['group','runs','violating_runs','undrained_runs','i1_excess_units','i1_violation_units','i2_violations','lost','duplicates','stuck','stuck_decisions']))
add('The initial reproduction logs had no per-key confirm/refund/commit timestamps. Their timeline.json files explicitly say unobserved; created_at is not substituted for commit time. Thus the initial violating keys cannot individually be assigned a proven event ordering. An observability-only commit then added failure type, Redis Lua start/end, and transaction afterCommit events. Its diagnostic run preserves all logs and reproduced the same accounting failure. DEBUG tracing adds overhead; diagnostic latency is excluded from normal cost comparisons.\n')
add('Exact diagnostic event ordering:'+fence((base/'root-cause.json').read_text()))
add('Both diagnostic violations had a negative confirm caused by connection shutdown. The log type is NACK, but this is a client confirm-future failure on connection.close, not evidence of a broker basic.nack rejecting an undelivered message. Applied refund completed before the later afterCommit observation; the raw row creation timestamp also falls after the refund. afterCommit is an observation after successful transaction commit, not the physical PostgreSQL commit timestamp. These records demonstrate refund and persistence for the same order, with timing consistent with the proposed refund-first race; it does not establish TIMEOUT or RETURN as the observed cause for these keys.\n')
timeline=json.loads((base/'diagnostic/trace-rabbit-1/timeline.json').read_text())
for item in timeline:add('Key `'+item['key']+'`:'+fence('\n'.join(e['raw'] for e in item['events'])))
add('## Decision design and limits\n')
add('The per-order Redis decision is permanent for this inventory generation. Claim and refund Lua scripts serialize on the same decision key. REFUND wins: release stock/reservation once, mark REFUND, and discard later delivery before DB writes. CONSUMER wins: retain stock/reservation and skip refund even if the publisher returns HTTP failure. CONSUMER is ownership by order, not by delivery, so redelivery can resume after a consumer dies before committing. PostgreSQL uniqueness still arbitrates simultaneous duplicate inserts. A claimed dead letter resumes persistence rather than acknowledging an orphan after a rejected refund.\n')
add('A consumer-first confirm failure can still yield HTTP failure with a persisted order. Retry with the same idempotency key. This change prevents inventory inflation; it does not remove ambiguous client outcomes. Redis state loss or stale restoration destroys the decision/reservation foundation; image-default RDB is not a coordinated durable recovery design. A process dying after reservation but before publish can leave an orphan. Permanent DB failure can leave claimed reservations pending; queue drain may time out. No background reconciliation, bounded permanent-failure compensation, infrastructure replication, or distributed transaction was added. These fault cases are not covered by a claim of general durability.\n')
add('## Every run\n')
add('I1 uses CONFIRMED quantity plus exact Redis stock. The table delta is signed units; nonzero is failure. Campaign i1_excess_units is the signed net delta; i1_violation_units sums absolute deltas so opposite signs cannot conceal violations. I2 verifies reservation value matches the order ID or a CONSUMER decision exists; REFUND/rolled-back markers never count as fulfillment. I3 separately counts missing accepted keys, duplicates, orphan reservations and orphan CONSUMER decisions. Latencies are milliseconds, rate is requests/second, error rate is the exact k6 fraction. The init HTTP request belongs to k6 HTTP totals; the accepted ledger covers only order HTTP responses.\n')
add((base/'run-table.md').read_text())
add('Comparison by campaign:'+table([groups['repro'],groups['after-rabbit'],groups['after-kill'],groups['before-normal'],groups['after-normal']],['group','runs','violating_runs','undrained_runs','i1_excess_units','i1_violation_units','i2_violations','lost','duplicates','stuck','stuck_decisions','rule_of_three_upper_bound']))
add('For each zero-violation campaign: rule of three, approximate 95% upper bound ≈ 3/n, for probability that a comparable run shows a violation. Values above are computed from executed n. This assumes independent comparable trials; shared hardware and sequential runs limit that assumption. This is not proof of consistency and is not a per-order bound.\n')
add('## Decision cost and outage failures\n')
add('Matched normal ramp pairs, exact after-minus-before deltas:'+table(data['normal_comparison'],['pair','req_per_s_delta_after_minus_before','p50_ms_delta_after_minus_before','p95_ms_delta_after_minus_before','p99_ms_delta_after_minus_before','http_error_rate_delta_after_minus_before']))
add('These are observed full-path differences for the complete change, including decision lookup, claimed-DLQ behavior and trace hooks with DEBUG disabled. The baseline normal cohort followed the reproduction cohort; the fixed normal cohort followed SIGKILL recovery. JVM/container limits and load settings match, but warm state and kernel caches were not reset to identical states. These deltas do not isolate Lua CPU time or eliminate laptop/warmup variability. No rounding or favorable-run selection is applied.\n')
cost=json.loads((base/'decision-cost/summary.json').read_text());add('Isolated cached decision-Lua RPC wall time, with k6 stopped; each sample/result and elapsed duration is retained. Python on localhost uses the exposed Redis port, rather than the app Lettuce connection, so this is component evidence, not HTTP overhead under load. Sample quantiles use linear interpolation of measured durations:'+table(cost['runs'],['batch','case','samples','calls_per_s','p50_ms','p95_ms','p99_ms','errors']))
rates={g:[r['http_error_rate'] for r in data['runs'] if r['group']==g] for g in ['repro','after-rabbit']};(base/'outage-error-rates.json').write_text(json.dumps(rates,indent=2)+'\n');add('Broker outage HTTP failures remain in scope as reported evidence, not a behavior fixed by this task. The historical range in the task is not substituted for current measurements. Exact per-run values:'+fence(json.dumps(rates,indent=2)))
add('## Resources and all violating keys\n')
add('Maximum observed Docker-displayed values (retained verbatim):'+table(data['resources'],['group','run','container','max_observed_cpu','max_observed_memory']))
add('Host resource observations:'+table(host,['group','run','min_observed_available_host_bytes','max_observed_swap_used_bytes']))
nonzero=[r for r in resource_events if any(r.get(k,0)>0 for k in ['nr_throttled','throttled_usec','oom','oom_kill','max'])];missing=[r for r in resource_events if any(k not in r for k in ['nr_throttled','throttled_usec','oom','oom_kill','max'])]
(base/'resource-findings.json').write_text(json.dumps({'nonzero_counters':nonzero,'missing_counters':missing},indent=2)+'\n');add('Nonzero or unavailable cgroup counters:'+fence(json.dumps({'nonzero_counters':nonzero,'missing_counters':missing},indent=2)))
add('Before/after counters can reset on restart; no unavailable delta is filled with zero. Raw docker-events.log and inspect.log retain OOM/death observations; broker restarts and injected SIGKILL death events are expected. Host swap usage is reported independently of container no-swap limits.\n')
events=[]
for row in data['runs']:
 for line in (base/row['group']/row['run']/'docker-events.log').read_text().splitlines():
  if line.startswith('{'):
   value=json.loads(line);events.append({'group':row['group'],'run':row['run'],'action':value['Action'],'container':value['Actor']['Attributes'].get('name')})
facts={'oom_events':sum(e['action']=='oom' for e in events),'nonzero_throttling_snapshots':sum(r.get('nr_throttled',0)>0 or r.get('throttled_usec',0)>0 for r in resource_events),'nonzero_oom_snapshots':sum(r.get('oom',0)>0 or r.get('oom_kill',0)>0 for r in resource_events),'unavailable_cgroup_snapshots':len(missing),'events':events}
jvm_oom=[]
for row in data['runs']:
 for line in (base/row['group']/row['run']/'app.log').read_text().splitlines():
  if re.search(r'OutOfMemoryError|Java heap space|GC overhead limit exceeded|unable to create native thread',line):
   jvm_oom.append({'group':row['group'],'run':row['run'],'raw':line})
facts['jvm_oom_matching_log_lines']=len(jvm_oom)
(base/'jvm-oom-audit.json').write_text(json.dumps(jvm_oom,indent=2)+'\n')
(base/'event-audit.json').write_text(json.dumps(facts,indent=2)+'\n');add('Observed resource findings:'+fence(json.dumps({k:v for k,v in facts.items() if k!='events'},indent=2)))

for row in data['runs']:
 d=base/row['group']/row['run'];a=json.loads((d/'invariants.json').read_text());viol={k:a[k] for k in ['i2_keys','lost_keys','duplicate_keys','stuck_keys','stuck_decision_keys'] if a[k]}
 if viol:add(row['group']+'/'+row['run']+' — complete violating key lists:'+fence(json.dumps(viol,indent=2)))
add('Every run also retains confirmed_without_http_202; these can be legitimate consumer-first outcomes, not loss or duplication. Counts:'+table([{'group':r['group'],'run':r['run'],'confirmed_without_http_202':len(json.loads((base/r['group']/r['run']/'invariants.json').read_text())['confirmed_without_http_202'])} for r in data['runs']],['group','run','confirmed_without_http_202']))
add('## Raw commands and reconciliation output\n')
add('Quoted excerpts omit trailing padding spaces; original raw files remain verbatim. For every run, raw command output and failed thresholds are in [results/decision-fix](results/decision-fix). Nothing is reconstructed from memory. Commands used:'+fence('JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test\nJAVA_TOOL_OPTIONS=-Xmx256m ./mvnw package -DskipTests\ndocker compose --profile distributed up -d --build --scale app=2\npython3 bench/experiment.py OUTPUT rabbit\npython3 bench/experiment.py OUTPUT kill\npython3 bench/experiment.py OUTPUT\npython3 bench/decision_reconcile.py OUTPUT\npython3 bench/decision_cost.py bench/results/decision-fix/decision-cost\npython3 bench/decision_report.py\npython3 bench/decision_document.py'))
for row in data['runs']:
 d=base/row['group']/row['run'];add(row['group']+'/'+row['run']+' — raw reconciliation JSON:'+fence((d/'reconciliation.json').read_text()))
add('## DRAFT resume figures\n')
a=groups['repro'];b=groups['after-rabbit'];k=groups['after-kill']
resume=[f"DRAFT: Reproduced inventory-conservation failures in {a['violating_runs']}/{a['runs']} broker-restart runs ({a['i2_violations']} affected orders); after Redis decision arbitration, {b['violating_runs']}/{b['runs']} broker-restart runs violated the audited invariants.",f"DRAFT: Post-fix broker-restart cohort: lost orders {b['lost']}, duplicates {b['duplicates']}, stuck reservations {b['stuck']}, orphan consumer decisions {b['stuck_decisions']}; SIGKILL cohort: {k['violating_runs']}/{k['runs']} runs violated audited invariants."]
last=(base/'test-setup/tests-attempt-1.log').read_text();match=re.findall(r'Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)',last)[-1];resume.append(f'DRAFT: Integration tests: {match[0]} passed, failures {match[1]}, errors {match[2]}, skipped {match[3]}; original test bodies unchanged.')
(base/'resume-draft.txt').write_text('\n'.join(resume)+'\n');add('Copied verbatim from [results/decision-fix/resume-draft.txt](results/decision-fix/resume-draft.txt); cohort numbers come from metrics.json, test numbers from test-setup/tests-attempt-1.log. Limited local-run evidence, not a durability guarantee:'+fence('\n'.join(resume)))
root.write_text('\n'.join(lines))
results=pathlib.Path('RESULTS.md');old=results.read_text();marker='<!-- decision-fix-report -->';end='<!-- /decision-fix-report -->'
if marker in old:old=old[:old.index(marker)]+old[old.index(end)+len(end):]
intro=marker+'\n# Refund/persistence defect — executed follow-up\n\nWorst fixed broker-restart HTTP error fraction: `'+str(worst['http_error_rate'])+'` (`'+worst['run']+'`). This negative availability result is retained; inventory arbitration does not fix outage HTTP failures. Quoted excerpts omit trailing padding spaces; full raw logs remain verbatim.\n\nSee [bench/REPORT.md](bench/REPORT.md) for exact runs, violating keys, root-cause timestamps, limits, resource output and DRAFT resume text. Historical measurements below remain retained; their old present-tense failure statements describe the former code.\n\n'+table([a,b,k],['group','runs','violating_runs','undrained_runs','i1_excess_units','i1_violation_units','i2_violations','lost','duplicates','stuck','stuck_decisions'])+'\nRaw initial test command output:'+fence('\n'.join((base/'setup/tests.log').read_text().splitlines()[-13:]))+'\nRaw final test command output:'+fence('\n'.join(last.splitlines()[-13:]))+'\n'+(base/'run-table.md').read_text()+'\nRaw diagnostic evidence:'+fence('\n'.join(e['raw'] for t in timeline for e in t['events']))+'\nFailed k6 thresholds and raw reconciliation for each run are preserved in bench/results/decision-fix; full raw reconciliation JSON is embedded in bench/REPORT.md. k6, nginx, apps and infrastructure shared one laptop.\n\n'+end+'\n\n'
results.write_text(intro+old)
print(json.dumps({'report':str(root),'campaigns':groups,'resume':resume}))
