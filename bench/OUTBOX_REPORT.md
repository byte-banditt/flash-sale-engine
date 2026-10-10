# Outbox session evidence

<!-- outbox-session -->
## 2026-10-10 — transactional outbox session

Verdict: the ambiguous-confirm refund defect is fixed in the executed recovery cases. The table below is this session's evidence, not a consistency proof.
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

| log | [run,fail,error,skip] | build verdict | raw |
| --- | --- | --- | --- |
| tests.log | [13, 0, 0, 0] | BUILD SUCCESS | [tests.log](results/outbox-session/baseline/tests.log) |
| compile-attempt-1.log | null | INTERRUPTED / no completed build verdict | [compile-attempt-1.log](results/outbox-session/compile-attempt-1.log) |
| tests-attempt-1.log | null | BUILD FAILURE | [tests-attempt-1.log](results/outbox-session/tests-attempt-1.log) |
| tests-attempt-2.log | [20, 0, 15, 0] | BUILD FAILURE | [tests-attempt-2.log](results/outbox-session/tests-attempt-2.log) |
| tests-attempt-3.log | [20, 0, 0, 0] | BUILD SUCCESS | [tests-attempt-3.log](results/outbox-session/tests-attempt-3.log) |
| tests-attempt-4.log | [21, 0, 0, 0] | BUILD SUCCESS | [tests-attempt-4.log](results/outbox-session/tests-attempt-4.log) |
| full-fault-tests-attempt-1.log | null | INTERRUPTED / no completed build verdict | [full-fault-tests-attempt-1.log](results/outbox-session/full-fault-tests-attempt-1.log) |
| full-fault-tests-attempt-2.log | null | INTERRUPTED / no completed build verdict | [full-fault-tests-attempt-2.log](results/outbox-session/full-fault-tests-attempt-2.log) |
| full-fault-tests-attempt-3.log | [0, 0, 0, 0] | BUILD FAILURE | [full-fault-tests-attempt-3.log](results/outbox-session/full-fault-tests-attempt-3.log) |
| poller-and-core-tests.log | [22, 0, 0, 0] | BUILD SUCCESS | [poller-and-core-tests.log](results/outbox-session/poller-and-core-tests.log) |

Raw final targeted Maven summary:
```text
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 110.7 s -- in com.flashsale.engine.OutboxFaultInjectionTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.097 s -- in com.flashsale.engine.OutboxUnitTests
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 61.28 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

The final long campaign completed the HTTP harness, every standard broker restart
and every replica kill, with per-case PASS logs and complete oracles. Its poller
test then exhausted the test JVM heap while reading the fixture's entire log
history; Maven reported BUILD FAILURE and its zero-test aggregate is incomplete,
not a claim that no cases executed. The dump identifies ContainerState.getLogs.
The replacement streams only the already-captured current-case log files. A
targeted rerun executes the poller crash and all core/unit tests; the unchanged
completed fault cases are not substituted with invented results or a fictitious
single green full-suite summary.

Retained heap failure: [poller-log-oom.dump](results/outbox-session/poller-log-oom.dump), [poller-log-oom.dumpstream](results/outbox-session/poller-log-oom.dumpstream).

The first container campaign's full-table JSON aggregation killed a capped
PostgreSQL backend. Its first run's basic reconciliation passed, but its full
oracle failed; the next run was interrupted to repair the collector. Neither
attempt is discarded. The replacement streams individual rows inside one
repeatable-read snapshot. Production caps were not increased.

First failed attempt raw reconciliation:
```json
{
  "http_202": 55784,
  "confirmed": 55784,
  "redis_stock": null,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0,
  "postgres_stock": 44216,
  "stock_source": "PostgreSQL",
  "pending_orders": 0,
  "cancelled_orders": 0
}
```
Collector failure:
```text
Traceback (most recent call last):
  File "/home/ayussh/projects/flash-sale-engine/bench/outbox_reconcile.py", line 12, in <module>
    if p.returncode: raise RuntimeError(p.stdout)
                     ^^^^^^^^^^^^^^^^^^^^^^^^^^^^
RuntimeError: server closed the connection unexpectedly
	This probably means the server terminated abnormally
	before or while processing the request.
connection to server was lost

```
Observed counters after the failed collector:
```text
$ docker exec f6a425892f6cba8589e7338513dddb09f05fc0b9ccee56d411504308d288c8a2 cat /sys/fs/cgroup/memory.events
low 0
high 0
max 122
oom 3
oom_kill 1
oom_group_kill 0
exit=0
```
Interrupted next run: [interrupted.txt](results/outbox-session/testcontainers/rabbit-2/interrupted.txt), [interruption-counts.log](results/outbox-session/testcontainers/rabbit-2/interruption-counts.log). No final invariant counts are invented for it.

The next campaign was also interrupted after Docker exec failed against a broker
that Docker still reported Running with OOMKilled=false. Its management host port
refused connections, while a management query through the app network succeeded.
The collector now retains failed CLI output, falls back to actual management queue
counters through the surviving app, and records unavailable cgroup data as null.
Missing observations cannot establish zero OOM or zero throttling. Management
counters have the broker's statistics refresh delay; the same repeated quiet-poll
condition and settled PostgreSQL intent are required before the oracle.

| retained run | stock/intent oracle | drained | 202 | CONFIRMED | stock | qualification |
| --- | --- | --- | --- | --- | --- | --- |
| rabbit-1 | True | True | 55845 | 55845 | 44155 | retained interrupted campaign |
| rabbit-2 | True | True | 56863 | 56863 | 43137 | retained interrupted campaign |
| rabbit-3 | True | True | 56947 | 56947 | 43053 | retained interrupted campaign |
| rabbit-4 | True | True | 56416 | 56416 | 43584 | retained interrupted campaign |
| rabbit-5 | True | False | 49207 | 49207 | 50793 | queue/exec observations failed |
| rabbit-6 | True | False | 55641 | 55641 | 44359 | extra diagnostic restart during load |
| rabbit-7 | null | null | null | null | null | retained interrupted campaign |
Diagnostic restart was recorded under rabbit-5 but occurred during rabbit-6, as the actual timestamps show. Neither is substituted for a standard run. Raw: [additional-diagnostic-restart.log](results/outbox-session/testcontainers-rerun/rabbit-5/additional-diagnostic-restart.log), [drain.log](results/outbox-session/testcontainers-rerun/rabbit-5/drain.log), [internal-management-probe.log](results/outbox-session/testcontainers-rerun/internal-management-probe.log), [interrupted-campaign.txt](results/outbox-session/testcontainers-rerun/interrupted-campaign.txt).

### Per-run recovery results

| run | result | 202 | CONFIRMED | qty | PG stock | stock delta | bad intent | lost | duplicate | PENDING orders | PENDING events | cancelled | queue drained | HTTP error fraction | req/s | p50 ms | p95 ms | p99 ms | k6 exit |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| rabbit-1 | PASS | 52510 | 52510 | 52510 | 47490 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 436.9752333484072 | 3.614751 | 23.698271 | 189.32641080000028 | 0 |
| rabbit-2 | PASS | 55797 | 55797 | 55797 | 44203 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 461.45496877957146 | 3.1894425 | 9.512109549999984 | 41.89542368999987 | 0 |
| rabbit-3 | PASS | 54391 | 54391 | 54391 | 45609 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 451.37311666874683 | 4.233280499999999 | 34.06455989999989 | 98.31400472999992 | 0 |
| rabbit-4 | PASS | 56252 | 56252 | 56252 | 43748 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 467.321495187519 | 3.252051 | 10.706265999999982 | 54.9068954399996 | 0 |
| rabbit-5 | PASS | 55774 | 55774 | 55774 | 44226 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 462.7155818561642 | 2.86565 | 7.228469699999998 | 63.83687560000197 | 0 |
| rabbit-6 | PASS | 56657 | 56657 | 56657 | 43343 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 470.3114186141253 | 3.22744 | 10.413675749999996 | 43.28664204 | 0 |
| rabbit-7 | PASS | 57708 | 57708 | 57708 | 42292 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 480.14802665550513 | 2.703299 | 5.353542599999999 | 13.555079559999998 | 0 |
| rabbit-8 | PASS | 51490 | 51490 | 51490 | 48510 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 427.3930219854306 | 4.188905 | 62.983612 | 209.64292409999965 | 0 |
| rabbit-9 | PASS | 56202 | 56202 | 56202 | 43798 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 466.4923530363663 | 3.622057 | 11.84328419999998 | 52.330151019999796 | 0 |
| rabbit-10 | PASS | 56519 | 56519 | 56519 | 43481 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0 | 469.4556066185468 | 3.454364 | 11.409687949999993 | 37.257170249999724 | 0 |
| kill-1 | PASS | 54061 | 54061 | 54061 | 45939 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0.000591562835064887 | 448.952935704223 | 4.52582 | 33.58722254999997 | 110.61712937 | 0 |
| kill-2 | PASS | 55274 | 55274 | 55274 | 44726 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0.00037977430555555556 | 458.9279093537282 | 4.158265999999999 | 20.723658 | 72.29899690000094 | 0 |
| kill-3 | PASS | 53332 | 53332 | 53332 | 46668 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | True | 0.00046853330334720194 | 442.35895388137976 | 4.4435835 | 40.87932439999989 | 144.85579015000005 | 0 |

Each row links to a raw HTTP ledger, queue polls, orders, a streamed PostgreSQL snapshot, every violating-key list, and post-oracle resource counters in its run directory.
- rabbit-1: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-1/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-1/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-1/postgres-snapshot.jsonl)
- rabbit-2: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-2/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-2/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-2/postgres-snapshot.jsonl)
- rabbit-3: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-3/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-3/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-3/postgres-snapshot.jsonl)
- rabbit-4: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-4/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-4/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-4/postgres-snapshot.jsonl)
- rabbit-5: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-5/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-5/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-5/postgres-snapshot.jsonl)
- rabbit-6: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-6/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-6/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-6/postgres-snapshot.jsonl)
- rabbit-7: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-7/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-7/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-7/postgres-snapshot.jsonl)
- rabbit-8: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-8/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-8/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-8/postgres-snapshot.jsonl)
- rabbit-9: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-9/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-9/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-9/postgres-snapshot.jsonl)
- rabbit-10: [outbox-invariants.json](results/outbox-session/testcontainers-final/rabbit-10/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/rabbit-10/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/rabbit-10/postgres-snapshot.jsonl)
- kill-1: [outbox-invariants.json](results/outbox-session/testcontainers-final/kill-1/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/kill-1/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/kill-1/postgres-snapshot.jsonl)
- kill-2: [outbox-invariants.json](results/outbox-session/testcontainers-final/kill-2/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/kill-2/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/kill-2/postgres-snapshot.jsonl)
- kill-3: [outbox-invariants.json](results/outbox-session/testcontainers-final/kill-3/outbox-invariants.json), [summary.json](results/outbox-session/testcontainers-final/kill-3/summary.json), [postgres-snapshot.jsonl](results/outbox-session/testcontainers-final/kill-3/postgres-snapshot.jsonl)

Failed-key lists remain in each oracle JSON even when empty. Zero observed violations does not establish universal consistency.
For these 10 completed broker-restart reruns: rule of three, approximate 95% per-run upper bound = 3/10 = 0.3, assuming independent comparable trials. Shared warm fixtures do not establish that independence.

### Existing HTTP scenarios and deterministic poller crash

```json
{
  "requests": 2000,
  "initial_stock": 100,
  "statuses": {
    "202": 100,
    "409": 1900
  },
  "upstreams": {
    "172.19.0.6:8080": 1000,
    "172.19.0.5:8080": 1000
  },
  "postgres": "100|100|100",
  "redis_stock": null,
  "postgres_stock": 0,
  "stock_source": "PostgreSQL"
}
```
```json
{
  "requests": 40,
  "initial_stock": 10,
  "statuses": {
    "200": 39,
    "202": 1
  },
  "upstreams": {
    "172.19.0.5:8080": 20,
    "172.19.0.6:8080": 20
  },
  "postgres": "1|1|1",
  "redis_stock": null,
  "postgres_stock": 9,
  "stock_source": "PostgreSQL"
}
```
Poller crash result:
```json
{
  "pass": true,
  "broker_ack_observations": 2,
  "confirmed_orders": 1,
  "available_stock": 0,
  "pending_events": 0
}
```
The test blocks PUBLISHED in a DB trigger after an actual broker ack,
stops the peer relay, kills the publishing app, terminates its abandoned sleeping
DB session, removes the trigger, and restarts both apps. Republish is expected
and explicitly authorized; a single terminal order is the invariant. This
deterministic case includes those additional controls and is not described as
an uncontrolled kill alone. See [mid-batch-postgres-activity.json](results/outbox-session/testcontainers-poller-retry/poller-crash/mid-batch-postgres-activity.json).

### HTTP load before / after

| run | 202 | CONFIRMED | stock | lost | drained | req/s | p50 ms | p95 ms | p99 ms | HTTP error fraction | k6 exit |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| before-normal-1 | 47049 | 47049 | 52951 | 0 | True | 389.8608738880163 | 9.2878295 | 26.47609824999998 | 65.38647879000004 | 0 | 0 |
| before-normal-clean-1 | 49664 | 49664 | 50336 | 0 | True | 413.05315002678356 | 6.633392 | 12.924245399999998 | 20.371945520000008 | 0 | 0 |
| before-final-1 | 48243 | 48243 | 51757 | 0 | True | 401.19652331196994 | 7.965952 | 25.82107 | 54.78765407999999 | 0 | 0 |
| before-comparison | 47871 | 47871 | 52129 | 0 | True | 397.86594312342464 | 8.302375999999999 | 26.85083364999996 | 67.84412472000002 | 0 | 0 |
| after-comparison | 29201 | 29201 | 70799 | 0 | True | 243.18342249063352 | 67.051761 | 223.79859144999992 | 340.2954917699981 | 0 | 0 |

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
| metric | before | after | after minus before |
| --- | --- | --- | --- |
| req/s | 397.86594312342464 | 243.18342249063352 | -154.68252063279112 |
| p50 ms | 8.302375999999999 | 67.051761 | 58.749385000000001 |
| p95 ms | 26.85083364999996 | 223.79859144999992 | 196.94775779999996 |
| p99 ms | 67.84412472000002 | 340.2954917699981 | 272.45136704999808 |
| HTTP error fraction | 0 | 0 | 0 |

Measured HTTP throughput was lower after the outbox change in this comparison. All reported latency percentiles increased. The attributable bottleneck remains unisolated; the result compares the whole architecture, with the warm-state limitation above.

After-load raw reconciliation:
```json
{
  "http_202": 29201,
  "confirmed": 29201,
  "redis_stock": null,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0,
  "postgres_stock": 70799,
  "stock_source": "PostgreSQL",
  "pending_orders": 0,
  "cancelled_orders": 0
}
```
After-load atomic oracle: [outbox-invariants.json](results/outbox-session/after-comparison/outbox-invariants.json).

### Environment, schema and resource evidence

```text
2026-10-10T19:23:12.554083+00:00
$ git rev-parse HEAD
aa7c512f434e7a371b2658aebc21ee05fe24d887
exit=0
$ git status --short
 M src/main/java/com/flashsale/engine/config/RabbitMQConfig.java
?? bench/results/outbox-session/
exit=0
$ uname -a
Linux DESKTOP-P6C785V 6.18.40.1-microsoft-standard-WSL2 #1 SMP PREEMPT_DYNAMIC Fri Jul 31 22:12:15 UTC 2026 x86_64 GNU/Linux
exit=0
$ cat /etc/os-release
PRETTY_NAME="Debian GNU/Linux 13 (trixie)"
NAME="Debian GNU/Linux"
VERSION_ID="13"
VERSION="13 (trixie)"
VERSION_CODENAME=trixie
DEBIAN_VERSION_FULL=13.4
ID=debian
HOME_URL="https://www.debian.org/"
SUPPORT_URL="https://www.debian.org/support"
BUG_REPORT_URL="https://bugs.debian.org/"
exit=0
$ lscpu
Architecture:                            x86_64
CPU op-mode(s):                          32-bit, 64-bit
Address sizes:                           48 bits physical, 48 bits virtual
Byte Order:                              Little Endian
CPU(s):                                  16
On-line CPU(s) list:                     0-15
Vendor ID:                               AuthenticAMD
Model name:                              AMD Ryzen 7 7840HS w/ Radeon 780M Graphics
CPU family:                              25
Model:                                   116
Thread(s) per core:                      2
Core(s) per socket:                      8
Socket(s):                               1
Stepping:                                1
BogoMIPS:                                7585.87
Flags:                                   fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ht syscall nx mmxext fxsr_opt pdpe1gb rdtscp lm constant_tsc rep_good nopl xtopology tsc_reliable nonstop_tsc cpuid extd_apicid tsc_known_freq pni pclmulqdq ssse3 fma cx16 sse4_1 sse4_2 movbe popcnt aes xsave avx f16c rdrand hypervisor lahf_lm cmp_legacy svm cr8_legacy abm sse4a misalignsse 3dnowprefetch osvw topoext perfctr_core ssbd ibrs ibpb stibp vmmcall fsgsbase bmi1 avx2 smep bmi2 erms invpcid avx512f avx512dq rdseed adx smap avx512ifma clflushopt clwb avx512cd sha_ni avx512bw avx512vl xsaveopt xsavec xgetbv1 xsaves avx512_bf16 clzero xsaveerptr arat npt nrip_save tsc_scale vmcb_clean flushbyasid decodeassists pausefilter pfthreshold avx512vbmi umip avx512_vbmi2 gfni vaes vpclmulqdq avx512_vnni avx512_bitalg avx512_vpopcntdq rdpid fsrm
Virtualization:                          AMD-V
Hypervisor vendor:                       Microsoft
Virtualization type:                     full
L1d cache:                               256 KiB (8 instances)
L1i cache:                               256 KiB (8 instances)
L2 cache:                                8 MiB (8 instances)
L3 cache:                                16 MiB (1 instance)
NUMA node(s):                            1
NUMA node0 CPU(s):                       0-15
Vulnerability Gather data sampling:      Not affected
Vulnerability Ghostwrite:                Not affected
Vulnerability Indirect target selection: Not affected
Vulnerability Itlb multihit:             Not affected
Vulnerability L1tf:                      Not affected
Vulnerability Mds:                       Not affected
Vulnerability Meltdown:                  Not affected
Vulnerability Mmio stale data:           Not affected
Vulnerability Old microcode:             Not affected
Vulnerability Reg file data sampling:    Not affected
Vulnerability Retbleed:                  Not affected
Vulnerability Spec rstack overflow:      Vulnerable: Safe RET, no microcode
Vulnerability Spec store bypass:         Mitigation; Speculative Store Bypass disabled via prctl
Vulnerability Spectre v1:                Mitigation; usercopy/swapgs barriers and __user pointer sanitization
Vulnerability Spectre v2:                Mitigation; Retpolines; IBPB conditional; IBRS_FW; STIBP always-on; RSB filling; PBRSB-eIBRS Not affected; BHI Not affected
Vulnerability Srbds:                     Not affected
Vulnerability Tsa:                       Vulnerable: No microcode
Vulnerability Tsx async abort:           Not affected
Vulnerability Vmscape:                   Not affected
exit=0
$ free -b
               total        used        free      shared  buff/cache   available
Mem:      5157515264  3001774080   126464000    79650816  2298007552  2155741184
Swap:     4294967296   412557312  3882409984
exit=0
$ java -version
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-deb13u1-Debian)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-1-deb13u1-Debian, mixed mode, sharing)
exit=0
$ docker version
Client:
 Version:           29.8.0
 API version:       1.56
 Go version:        go1.26.8
 Git commit:        88096ef
 Built:             Thu Sep  3 21:49:51 2026
 OS/Arch:           linux/amd64
 Context:           default

Server: Docker Desktop 4.91.0 (239619)
 Engine:
  Version:          29.8.0
  API version:      1.56 (minimum version 1.40)
  Go version:       go1.26.8
  Git commit:       3ce5872
  Built:            Thu Sep  3 21:51:20 2026
  OS/Arch:          linux/amd64
  Experimental:     false
 containerd:
  Version:          v2.3.4
  GitCommit:        db8809540e1a7a9da5d518876894933ff55692ab
 runc:
  Version:          1.4.3
  GitCommit:        v1.4.3-0-gbb14dabe
 docker-init:
  Version:          0.19.0
  GitCommit:        de40ad0
exit=0
$ k6 version
k6 v2.2.0 (commit/00a9a1b7f5, go1.26.5, linux/amd64)
exit=0
$ docker compose --profile distributed config
time="2026-10-10T19:23:14Z" level=warning msg="/home/ayussh/projects/flash-sale-engine/docker-compose.yml: the attribute `version` is obsolete, it will be ignored, please remove it to avoid potential confusion"
name: flash-sale-engine
services:
  app:
    profiles:
      - distributed
    build:
      context: /home/ayussh/projects/flash-sale-engine
      dockerfile: Dockerfile
    depends_on:
      postgres:
        condition: service_healthy
        required: true
      rabbitmq:
        condition: service_healthy
        required: true
      redis:
        condition: service_healthy
        required: true
    environment:
      JAVA_TOOL_OPTIONS: -Xmx256m
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/flashsale_db
      SPRING_RABBITMQ_HOST: rabbitmq
    image: flash-sale-engine:local
    mem_limit: "671088640"
    memswap_limit: "671088640"
    networks:
      default: null
  nginx:
    profiles:
      - distributed
    depends_on:
      app:
        condition: service_started
        required: true
    image: nginx:1.28-alpine
    mem_limit: "67108864"
    memswap_limit: "67108864"
    networks:
      default: null
    ports:
      - mode: ingress
        target: 8080
        published: "8080"
        protocol: tcp
    volumes:
      - type: bind
        source: /home/ayussh/projects/flash-sale-engine/deploy/nginx.conf
        target: /etc/nginx/nginx.conf
        read_only: true
        bind: {}
  postgres:
    container_name: flash_postgres
    environment:
      POSTGRES_DB: flashsale_db
      POSTGRES_PASSWORD: password
      POSTGRES_USER: postgres
    healthcheck:
      test:
        - CMD-SHELL
        - pg_isready -U postgres -d flashsale_db
      timeout: 3s
      interval: 5s
      retries: 30
    image: postgres:16-alpine
    mem_limit: "402653184"
    memswap_limit: "402653184"
    networks:
      default: null
    ports:
      - mode: ingress
        target: 5432
        published: "5432"
        protocol: tcp
  rabbitmq:
    container_name: flash_rabbitmq
    environment:
      RABBITMQ_DEFAULT_PASS: guest
      RABBITMQ_DEFAULT_USER: guest
    healthcheck:
      test:
        - CMD
        - rabbitmq-diagnostics
        - -q
        - ping
      timeout: 5s
      interval: 5s
      retries: 30
    image: rabbitmq:3.13-management-alpine
    mem_limit: "402653184"
    memswap_limit: "402653184"
    networks:
      default: null
    ports:
      - mode: ingress
        target: 5672
        published: "5672"
        protocol: tcp
      - mode: ingress
        target: 15672
        published: "15672"
        protocol: tcp
  redis:
    container_name: flash_redis
    healthcheck:
      test:
        - CMD
        - redis-cli
        - ping
      timeout: 3s
      interval: 5s
      retries: 30
    image: redis:7-alpine
    mem_limit: "402653184"
    memswap_limit: "402653184"
    networks:
      default: null
    ports:
      - mode: ingress
        target: 6379
        published: "6379"
        protocol: tcp
networks:
  default:
    name: flash-sale-engine_default
exit=0

```
Actual fixture schema: [schema.sql](results/outbox-session/testcontainers/schema.sql); runtime versions: [runtime-versions.log](results/outbox-session/testcontainers/runtime-versions.log).
Before image: `sha256:9f239275ea2f9f1b2f83360e4fa03ab81989a5bac7b9b12162a33c053dd4b4b1`; after image: `sha256:740c2cefee68159ac6d913d81015cb49970c02592ea465d7b44ca8b2499be8f8`.
Source-hash command output for the before and after runs:
```text
$ git rev-parse HEAD
started=2026-10-10T19:34:31.664685+00:00
aa7c512f434e7a371b2658aebc21ee05fe24d887

exit_code=0
ended=2026-10-10T19:34:31.669011+00:00
$ git rev-parse HEAD
started=2026-10-10T21:22:58.029757+00:00
edc0388faf5d9f3d4ac9f1f92d10ba5de0d0dd97

exit_code=0
ended=2026-10-10T21:22:58.080574+00:00
```
Resource counters before each case and after the oracle are preserved in [resource-audit.json](results/outbox-session/resource-audit.json). Empty/unavailable observations are null. Docker CPU/memory snapshots and free -b output are retained in every stats.log. The failed collector OOM remains a separate negative result; do not infer zero OOM from the earlier pre-oracle inspect.
Cgroup counters can reset at container restart. Sampled CPU/memory observations are not guaranteed peaks and do not measure host thermal throttling.

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

DRAFT: HTTP oversell test: 2000 requests, stock 100, accepted 100, PostgreSQL `100|100|100`, remaining stock 0. Source: [oversell-reconciliation.json](results/outbox-session/testcontainers-final/http/oversell-reconciliation.json).

DRAFT: completed broker-restart reruns 10; invariant passes 10. Per-run source files listed above; retained collector failure and interrupted attempt remain disclosed.
