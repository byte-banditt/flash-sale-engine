# Refund/persistence race: executed evidence

All measurements below come from local executed runs. k6, nginx, both app replicas, Redis, RabbitMQ and PostgreSQL shared one laptop. No parallel load campaigns, builds or tests ran during measured load. Host-side report preparation and saved-snapshot audits did run during parts of the fault campaigns; that local tooling noise is retained. The normal-ramp comparison was not overlapped with those full snapshot scans. Docker snapshots are sampled observations, not guaranteed peaks. No runs were removed for threshold failures or noise.

**Negative availability finding:** fixed broker-restart runs still failed HTTP requests. The largest measured HTTP error fraction was `0.3659522769984244` in `rabbit-6`; that run is retained, not dismissed as noise. Inventory arbitration does not fix broker-outage HTTP availability.

## Code and environment

Initial source HEAD: `c6163ac8c68a24ca246c0a30a982d9e183b7f9a3`. The pre-existing RabbitMQConfig comment cleanup is recorded in setup/preexisting.diff and remains excluded from commits. The normal baseline source matches that HEAD; its later runs record a bench-only commit. All baseline containers used the same app image, checked below.

Raw machine/version output:
```text
2026-10-10T12:43:58+00:00
Linux DESKTOP-P6C785V 6.18.40.1-microsoft-standard-WSL2 #1 SMP PREEMPT_DYNAMIC Fri Jul 31 22:12:15 UTC 2026 x86_64 GNU/Linux
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
               total        used        free      shared  buff/cache   available
Mem:      5157515264  2504663040   461438976   116948992  2491170816  2652852224
Swap:     4294967296    55836672  4239130624
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
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-deb13u1-Debian)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-1-deb13u1-Debian, mixed mode, sharing)
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
k6 v2.2.0 (commit/00a9a1b7f5, go1.26.5, linux/amd64)
```

Baseline app image: `sha256:6080530e47496bc5e173c3a1e08dba34e0987267d6860fe51365196168a5bbc2`.

Raw jar checksums:
```text
62ff628a71deab6d516d3a5f47776adb8d784bd2ab0cc81cf81d947030bbb04e  target/flash-sale-engine-0.0.1-SNAPSHOT.jar
3d120ef54dfd3fc83a29f99183dff37414c554f52ecfc15563cd7780253ddf3f  target/flash-sale-engine-0.0.1-SNAPSHOT.jar
```

Exact recorded limits (CPU quota values come from inspect):
| group | container | memory_bytes | memory_plus_swap_bytes | cpu_quota | oom_killed |
| --- | --- | --- | --- | --- | --- |
| after-rabbit | /flash-sale-engine-app-1 | 671088640 | 671088640 | 0 | False |
| after-rabbit | /flash-sale-engine-app-2 | 671088640 | 671088640 | 0 | False |
| after-rabbit | /flash_postgres | 402653184 | 402653184 | 0 | False |
| after-rabbit | /flash_redis | 402653184 | 402653184 | 0 | False |
| after-rabbit | /flash_rabbitmq | 402653184 | 402653184 | 0 | False |
| repro | /flash-sale-engine-app-1 | 671088640 | 671088640 | 0 | False |
| repro | /flash-sale-engine-app-2 | 671088640 | 671088640 | 0 | False |
| repro | /flash_postgres | 402653184 | 402653184 | 0 | False |
| repro | /flash_redis | 402653184 | 402653184 | 0 | False |
| repro | /flash_rabbitmq | 402653184 | 402653184 | 0 | False |

Each run records HEAD, source diff, jar/image provenance, Compose configuration, Docker inspect and unchanged harness checksums. Source diffs and bench-only hashes are retained rather than labeling every run as the initial HEAD. 
| group | run | HEAD |
| --- | --- | --- |
| after-kill | kill-1 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-kill | kill-2 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-kill | kill-3 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-normal | normal-1 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-normal | normal-2 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-normal | normal-3 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-1 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-2 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-3 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-4 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-5 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-6 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-7 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-8 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-9 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-10 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-11 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-12 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-13 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-14 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-15 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-16 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-17 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-18 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-19 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| after-rabbit | rabbit-20 | 3510244b3bdf63901ceeb618f0bb0d820c8360a5 |
| before-normal | normal-1 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| before-normal | normal-2 | 34261c96995fe333e68b9b562598bb2b72d66732 |
| before-normal | normal-3 | 34261c96995fe333e68b9b562598bb2b72d66732 |
| diagnostic | trace-rabbit-1 | 67d1658d8d00729802fa0d890f93785373328e7c |
| repro | rabbit-1 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-2 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-3 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-4 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-5 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-6 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-7 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-8 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-9 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |
| repro | rabbit-10 | c6163ac8c68a24ca246c0a30a982d9e183b7f9a3 |

## Tests

Initial original tests — raw Maven output:
```text
2026-10-10T12:44:44.952Z  INFO 62436 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:60590]
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  45.333 s
[INFO] Finished at: 2026-10-10T12:44:45Z
[INFO] ------------------------------------------------------------------------
exit_code=0
```

Trace-only original tests — raw Maven output:
```text
2026-10-10T13:14:44.881Z  WARN 72250 --- [flash-sale-engine] [127.0.0.1:49777] c.r.c.impl.ForgivingExceptionHandler     : An unexpected connection driver error occurred (Exception message: Connection reset)
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 43.58 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  51.414 s
[INFO] Finished at: 2026-10-10T13:14:46Z
[INFO] ------------------------------------------------------------------------
```

Fix original tests — raw Maven output:
```text
2026-10-10T13:20:30.044Z  INFO 73916 --- [flash-sale-engine] [ntContainer#0-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:58624]
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 47.16 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  58.886 s
[INFO] Finished at: 2026-10-10T13:20:35Z
[INFO] ------------------------------------------------------------------------
```

Original plus added tests — raw Maven output:
```text
2026-10-10T13:22:55.930Z  WARN 74552 --- [flash-sale-engine] [127.0.0.1:54991] c.r.c.impl.ForgivingExceptionHandler     : An unexpected connection driver error occurred (Exception message: Connection reset)
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 47.37 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  55.925 s
[INFO] Finished at: 2026-10-10T13:22:57Z
[INFO] ------------------------------------------------------------------------
```

Original test methods were compared byte-for-byte to the initial HEAD. Added tests use a real broker delivery with the original confirm future left unresolved; force both refund-first and consumer-first outcomes; cover a nondelivered nack followed by same-key retry; claim-before-crash redelivery; and claimed-DLQ continuation. Raw method audit:
```text
{"original_methods_unchanged": 8, "current_methods": 13}
```

## Reproduction and cause


| group | runs | violating_runs | undrained_runs | i1_excess_units | i1_violation_units | i2_violations | lost | duplicates | stuck | stuck_decisions |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| repro | 10 | 4 | 0 | 25 | 25 | 25 | 0 | 0 | 0 | 0 |
| diagnostic | 1 | 1 | 0 | 2 | 2 | 2 | 0 | 0 | 0 | 0 |

The initial reproduction logs had no per-key confirm/refund/commit timestamps. Their timeline.json files explicitly say unobserved; created_at is not substituted for commit time. Thus the initial violating keys cannot individually be assigned a proven event ordering. An observability-only commit then added failure type, Redis Lua start/end, and transaction afterCommit events. Its diagnostic run preserves all logs and reproduced the same accounting failure. DEBUG tracing adds overhead; diagnostic latency is excluded from normal cost comparisons.

Exact diagnostic event ordering:
```text
[
  {
    "key": "bench-trace-rabbit-1-24-17515",
    "failure_types": [
      "NACK"
    ],
    "refund_end_epoch_ms": 1791638171701,
    "persist_commit_epoch_ms": 1791638186841,
    "persist_minus_refund_ms": 15140,
    "refund_before_persist": true
  },
  {
    "key": "bench-trace-rabbit-1-3-17513",
    "failure_types": [
      "NACK"
    ],
    "refund_end_epoch_ms": 1791638171702,
    "persist_commit_epoch_ms": 1791638186847,
    "persist_minus_refund_ms": 15145,
    "refund_before_persist": true
  }
]
```

Both diagnostic violations had a negative confirm caused by connection shutdown. The log type is NACK, but this is a client confirm-future failure on connection.close, not evidence of a broker basic.nack rejecting an undelivered message. Applied refund completed before the later afterCommit observation; the raw row creation timestamp also falls after the refund. afterCommit is an observation after successful transaction commit, not the physical PostgreSQL commit timestamp. These records demonstrate refund and persistence for the same order, with timing consistent with the proposed refund-first race; it does not establish TIMEOUT or RETURN as the observed cause for these keys.

Key `bench-trace-rabbit-1-24-17515`:
```text
app-2  | 2026-10-10T13:16:11.690Z  WARN 1 --- [flash-sale-engine] [nio-8080-exec-8] c.f.e.config.ConfirmingRabbitTemplate    : ORDER_TRACE event=CONFIRM_FAILURE type=NACK key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638171690 reason=connection error; protocol method: #method<connection.close>(reply-code=320, reply-text=CONNECTION_FORCED - broker forced connection closure with reason 'shutdown', class-id=0, method-id=0)
app-2  | 2026-10-10T13:16:11.691Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-8] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_START key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638171691
app-2  | 2026-10-10T13:16:11.701Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-8] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_END key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638171701 applied=true
app-2  | 2026-10-10T13:16:26.841Z DEBUG 1 --- [flash-sale-engine] [ntContainer#1-5] c.f.e.service.OrderProcessingService     : ORDER_TRACE event=PERSIST_COMMIT key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638186841
```

Key `bench-trace-rabbit-1-3-17513`:
```text
app-2  | 2026-10-10T13:16:11.685Z  WARN 1 --- [flash-sale-engine] [nio-8080-exec-5] c.f.e.config.ConfirmingRabbitTemplate    : ORDER_TRACE event=CONFIRM_FAILURE type=NACK key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638171685 reason=connection error; protocol method: #method<connection.close>(reply-code=320, reply-text=CONNECTION_FORCED - broker forced connection closure with reason 'shutdown', class-id=0, method-id=0)
app-2  | 2026-10-10T13:16:11.691Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-5] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_START key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638171691
app-2  | 2026-10-10T13:16:11.702Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-5] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_END key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638171702 applied=true
app-2  | 2026-10-10T13:16:26.847Z DEBUG 1 --- [flash-sale-engine] [ntContainer#1-5] c.f.e.service.OrderProcessingService     : ORDER_TRACE event=PERSIST_COMMIT key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638186847
```

## Decision design and limits

The per-order Redis decision is permanent for this inventory generation. Claim and refund Lua scripts serialize on the same decision key. REFUND wins: release stock/reservation once, mark REFUND, and discard later delivery before DB writes. CONSUMER wins: retain stock/reservation and skip refund even if the publisher returns HTTP failure. CONSUMER is ownership by order, not by delivery, so redelivery can resume after a consumer dies before committing. PostgreSQL uniqueness still arbitrates simultaneous duplicate inserts. A claimed dead letter resumes persistence rather than acknowledging an orphan after a rejected refund.

A consumer-first confirm failure can still yield HTTP failure with a persisted order. Retry with the same idempotency key. This change prevents inventory inflation; it does not remove ambiguous client outcomes. Redis state loss or stale restoration destroys the decision/reservation foundation; image-default RDB is not a coordinated durable recovery design. A process dying after reservation but before publish can leave an orphan. Permanent DB failure can leave claimed reservations pending; queue drain may time out. No background reconciliation, bounded permanent-failure compensation, infrastructure replication, or distributed transaction was added. These fault cases are not covered by a claim of general durability.

## Every run

I1 uses CONFIRMED quantity plus exact Redis stock. The table delta is signed units; nonzero is failure. Campaign i1_excess_units is the signed net delta; i1_violation_units sums absolute deltas so opposite signs cannot conceal violations. I2 verifies reservation value matches the order ID or a CONSUMER decision exists; REFUND/rolled-back markers never count as fulfillment. I3 separately counts missing accepted keys, duplicates, orphan reservations and orphan CONSUMER decisions. Latencies are milliseconds, rate is requests/second, error rate is the exact k6 fraction. The init HTTP request belongs to k6 HTTP totals; the accepted ledger covers only order HTTP responses.

| group | run | http_202 | confirmed | redis_stock | i1_delta_units | i2_violations | lost | duplicates | stuck | stuck_decisions | http_error_rate | req_per_s | p50_ms | p95_ms | p99_ms | k6_exit | drained |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| after-kill | kill-1 | 54830 | 54830 | 45170 | 0 | 0 | 0 | 0 | 0 | 0 | 0.0001458815806269261 | 456.1037763741756 | 7.365893 | 14.0296969 | 34.9936591800001 | 0 | True |
| after-kill | kill-2 | 54577 | 54579 | 45421 | 0 | 0 | 0 | 0 | 0 | 0 | 0.00010992232155943134 | 452.09951774048477 | 7.3165575 | 13.42489185 | 33.338959689999996 | 0 | True |
| after-kill | kill-3 | 54116 | 54117 | 45883 | 0 | 0 | 0 | 0 | 0 | 0 | 0.0003694331049005301 | 450.336695842349 | 7.206196 | 12.768917199999997 | 33.81466599999988 | 0 | True |
| after-normal | normal-1 | 49080 | 49080 | 50920 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 408.5589651801389 | 7.737202 | 13.886602 | 29.352827399999825 | 0 | True |
| after-normal | normal-2 | 49385 | 49385 | 50615 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 411.24779253768537 | 7.5244595 | 12.81938925 | 16.883174400000005 | 0 | True |
| after-normal | normal-3 | 49496 | 49496 | 50504 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 412.06249399627376 | 7.374106 | 12.3386082 | 15.277612600000007 | 0 | True |
| after-rabbit | rabbit-1 | 41856 | 41856 | 58144 | 0 | 0 | 0 | 0 | 0 | 0 | 0.15217743568969008 | 411.0239087289456 | 7.866487 | 28.899264149999908 | 81.13371135999951 | 99 | True |
| after-rabbit | rabbit-2 | 47509 | 47509 | 52491 | 0 | 0 | 0 | 0 | 0 | 0 | 0.12305960093766728 | 446.00397957596005 | 7.431069 | 15.517226599999988 | 53.49908859999944 | 99 | True |
| after-rabbit | rabbit-3 | 48087 | 48087 | 51913 | 0 | 0 | 0 | 0 | 0 | 0 | 0.11425466467738668 | 451.48400491461143 | 7.419607 | 15.118237 | 49.94771659999996 | 99 | True |
| after-rabbit | rabbit-4 | 46890 | 46890 | 53110 | 0 | 0 | 0 | 0 | 0 | 0 | 0.12139778902004872 | 443.2719046329444 | 7.752534499999999 | 23.323990099999964 | 58.457757329999836 | 99 | True |
| after-rabbit | rabbit-5 | 46747 | 46747 | 53253 | 0 | 0 | 0 | 0 | 0 | 0 | 0.12028603688370343 | 440.6424430085575 | 7.222478 | 24.509653649999994 | 91.25364572000012 | 99 | True |
| after-rabbit | rabbit-6 | 33400 | 33400 | 66600 | 0 | 0 | 0 | 0 | 0 | 0 | 0.3659522769984244 | 437.38212647277305 | 7.168293 | 34.17094309999998 | 127.84436324000004 | 99 | True |
| after-rabbit | rabbit-7 | 48073 | 48073 | 51927 | 0 | 0 | 0 | 0 | 0 | 0 | 0.11952380952380952 | 454.3913784372395 | 7.065906 | 14.145556649999993 | 38.50982646000002 | 99 | True |
| after-rabbit | rabbit-8 | 48837 | 48840 | 51160 | 0 | 0 | 0 | 0 | 0 | 0 | 0.10900698739350154 | 455.7710586260295 | 6.725959 | 12.659037799999997 | 32.26112143999995 | 99 | True |
| after-rabbit | rabbit-9 | 47959 | 47959 | 52041 | 0 | 0 | 0 | 0 | 0 | 0 | 0.11957998311120902 | 452.9439018230963 | 7.353736 | 13.582713099999996 | 33.05598171999993 | 99 | True |
| after-rabbit | rabbit-10 | 46738 | 46738 | 53262 | 0 | 0 | 0 | 0 | 0 | 0 | 0.12965997541990987 | 445.7085395590831 | 7.5175105 | 19.68682459999999 | 72.03108436999999 | 99 | True |
| after-rabbit | rabbit-11 | 43430 | 43430 | 56570 | 0 | 0 | 0 | 0 | 0 | 0 | 0.1520032802249297 | 425.3384950952672 | 8.5797775 | 45.0675895 | 113.07595164999991 | 99 | True |
| after-rabbit | rabbit-12 | 47435 | 47438 | 52562 | 0 | 0 | 0 | 0 | 0 | 0 | 0.13271779870189232 | 454.92035760717556 | 6.974362 | 13.315797499999993 | 38.99800729999999 | 99 | True |
| after-rabbit | rabbit-13 | 46368 | 46368 | 53632 | 0 | 0 | 0 | 0 | 0 | 0 | 0.1225470716245624 | 439.0042381200238 | 7.88348 | 31.473864599999988 | 81.3322231999989 | 99 | True |
| after-rabbit | rabbit-14 | 47706 | 47706 | 52294 | 0 | 0 | 0 | 0 | 0 | 0 | 0.12504355800091702 | 453.60348026878154 | 7.123548 | 13.682100599999995 | 44.83654336000008 | 99 | True |
| after-rabbit | rabbit-15 | 44791 | 44791 | 55209 | 0 | 0 | 0 | 0 | 0 | 0 | 0.14497871609368737 | 435.6822276424505 | 8.2846 | 35.11693109999998 | 112.26093431999954 | 99 | True |
| after-rabbit | rabbit-16 | 43429 | 43429 | 56571 | 0 | 0 | 0 | 0 | 0 | 0 | 0.16266604970405074 | 430.85027036299334 | 8.481552 | 38.774804299999985 | 109.48488789999944 | 99 | True |
| after-rabbit | rabbit-17 | 41776 | 41776 | 58224 | 0 | 0 | 0 | 0 | 0 | 0 | 0.1686500039799411 | 416.28867876715435 | 9.230383 | 51.0110106 | 201.94526293999996 | 99 | True |
| after-rabbit | rabbit-18 | 46935 | 46935 | 53065 | 0 | 0 | 0 | 0 | 0 | 0 | 0.11931700910029083 | 442.43156006939745 | 7.574785 | 25.265536899999905 | 68.64752953999995 | 99 | True |
| after-rabbit | rabbit-19 | 46027 | 46027 | 53973 | 0 | 0 | 0 | 0 | 0 | 0 | 0.1309240587591103 | 439.8927604341719 | 7.288543000000001 | 26.963433749999993 | 96.48897523999992 | 99 | True |
| after-rabbit | rabbit-20 | 48780 | 48787 | 51213 | 0 | 0 | 0 | 0 | 0 | 0 | 0.10708205963647013 | 454.4846733000136 | 7.055305 | 12.555642500000001 | 40.20053189999959 | 99 | True |
| before-normal | normal-1 | 48921 | 48921 | 51079 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 407.32480746004944 | 7.0934989999999996 | 15.553272649999997 | 35.835293880000016 | 0 | True |
| before-normal | normal-2 | 48894 | 48894 | 51106 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 406.90755813511294 | 8.175344 | 15.02713969999999 | 28.017806039999858 | 0 | True |
| before-normal | normal-3 | 49090 | 49090 | 50910 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 408.59552101562707 | 7.728246 | 14.7226795 | 28.13137869999999 | 0 | True |
| diagnostic | trace-rabbit-1 | 45195 | 45197 | 54805 | 2 | 2 | 0 | 0 | 0 | 0 | 0.11050756725905808 | 423.08606077135204 | 8.369834 | 36.402017 | 96.63515280000006 | 99 | True |
| repro | rabbit-1 | 48222 | 48225 | 51778 | 3 | 3 | 0 | 0 | 0 | 0 | 0.10537446895349054 | 448.7886895673295 | 6.630068 | 14.726210899999996 | 29.08196867999985 | 99 | True |
| repro | rabbit-2 | 49670 | 49683 | 50330 | 13 | 13 | 0 | 0 | 0 | 0 | 0.10213119791760814 | 454.39561683830607 | 6.307512 | 10.527123 | 14.840332800000002 | 99 | True |
| repro | rabbit-3 | 50461 | 50468 | 49539 | 7 | 7 | 0 | 0 | 0 | 0 | 0.06111969040132473 | 447.2556073115293 | 6.350171 | 10.197942399999997 | 14.269778240000043 | 99 | True |
| repro | rabbit-4 | 50198 | 50198 | 49802 | 0 | 0 | 0 | 0 | 0 | 0 | 0.08975684055921232 | 459.0144536415538 | 6.810147 | 10.9667094 | 19.315309959999897 | 99 | True |
| repro | rabbit-5 | 51033 | 51033 | 48967 | 0 | 0 | 0 | 0 | 0 | 0 | 0.0743642761272536 | 459.03306404729807 | 6.775098 | 11.0743944 | 24.231341869999966 | 99 | True |
| repro | rabbit-6 | 49713 | 49713 | 50287 | 0 | 0 | 0 | 0 | 0 | 0 | 0.09676598837209302 | 458.2322006828908 | 6.786129 | 11.476123199999998 | 28.83634894000001 | 99 | True |
| repro | rabbit-7 | 51121 | 51121 | 48879 | 0 | 0 | 0 | 0 | 0 | 0 | 0.07675359387416023 | 460.9555084589265 | 6.548674500000001 | 10.1276057 | 12.534952340000007 | 99 | True |
| repro | rabbit-8 | 50978 | 50980 | 49022 | 2 | 2 | 0 | 0 | 0 | 0 | 0.07511021607794045 | 458.97810671450725 | 6.887033 | 10.806290199999998 | 18.561960399999997 | 99 | True |
| repro | rabbit-9 | 51418 | 51418 | 48582 | 0 | 0 | 0 | 0 | 0 | 0 | 0.06965930268324015 | 460.17213255857195 | 6.725151 | 10.559860599999999 | 13.50877944 | 99 | True |
| repro | rabbit-10 | 51496 | 51496 | 48504 | 0 | 0 | 0 | 0 | 0 | 0 | 0.06787698879577171 | 459.98843281416276 | 6.711908 | 10.439460799999999 | 15.07479134000001 | 99 | True |

Comparison by campaign:
| group | runs | violating_runs | undrained_runs | i1_excess_units | i1_violation_units | i2_violations | lost | duplicates | stuck | stuck_decisions | rule_of_three_upper_bound |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| repro | 10 | 4 | 0 | 25 | 25 | 25 | 0 | 0 | 0 | 0 | None |
| after-rabbit | 20 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0.15 |
| after-kill | 3 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 1.0 |
| before-normal | 3 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 1.0 |
| after-normal | 3 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 1.0 |

For each zero-violation campaign: rule of three, approximate 95% upper bound ≈ 3/n, for probability that a comparable run shows a violation. Values above are computed from executed n. This assumes independent comparable trials; shared hardware and sequential runs limit that assumption. This is not proof of consistency and is not a per-order bound.

## Decision cost and outage failures

Matched normal ramp pairs, exact after-minus-before deltas:
| pair | req_per_s_delta_after_minus_before | p50_ms_delta_after_minus_before | p95_ms_delta_after_minus_before | p99_ms_delta_after_minus_before | http_error_rate_delta_after_minus_before |
| --- | --- | --- | --- | --- | --- |
| 1 | 1.2341577200894562 | 0.6437030000000004 | -1.6666706499999968 | -6.482466480000191 | 0 |
| 2 | 4.340234402572435 | -0.650884500000001 | -2.20775044999999 | -11.134631639999853 | 0 |
| 3 | 3.4669729806466876 | -0.3541400000000001 | -2.3840713000000004 | -12.853766099999982 | 0 |

These are observed full-path differences for the complete change, including decision lookup, claimed-DLQ behavior and trace hooks with DEBUG disabled. The baseline normal cohort followed the reproduction cohort; the fixed normal cohort followed SIGKILL recovery. JVM/container limits and load settings match, but warm state and kernel caches were not reset to identical states. These deltas do not isolate Lua CPU time or eliminate laptop/warmup variability. No rounding or favorable-run selection is applied.

Isolated cached decision-Lua RPC wall time, with k6 stopped; each sample/result and elapsed duration is retained. Python on localhost uses the exposed Redis port, rather than the app Lettuce connection, so this is component evidence, not HTTP overhead under load. Sample quantiles use linear interpolation of measured durations:
| batch | case | samples | calls_per_s | p50_ms | p95_ms | p99_ms | errors |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | new_claim | 1000 | 5231.9291703940235 | 0.16202 | 0.27437154999999996 | 0.9864851499999999 | 0 |
| 1 | resume_claim | 1000 | 6785.480428978072 | 0.1335615 | 0.20783605 | 0.37831864999999953 | 0 |
| 1 | refund_blocks | 1000 | 7425.083781860988 | 0.1258215 | 0.17943924999999997 | 0.24052002999999988 | 0 |
| 2 | new_claim | 1000 | 5870.381816853937 | 0.159828 | 0.23761159999999995 | 0.30048021 | 0 |
| 2 | resume_claim | 1000 | 6741.314500509516 | 0.141322 | 0.18860829999999995 | 0.22275385999999991 | 0 |
| 2 | refund_blocks | 1000 | 7004.762306735193 | 0.1301975 | 0.20745459999999996 | 0.30684479 | 0 |
| 3 | new_claim | 1000 | 6996.871467869957 | 0.134639 | 0.19169079999999988 | 0.25032684 | 0 |
| 3 | resume_claim | 1000 | 7858.536473161286 | 0.1208445 | 0.16239239999999994 | 0.22090332 | 0 |
| 3 | refund_blocks | 1000 | 7260.996670542587 | 0.130463 | 0.18171284999999995 | 0.27431027999999985 | 0 |

Broker outage HTTP failures remain in scope as reported evidence, not a behavior fixed by this task. The historical range in the task is not substituted for current measurements. Exact per-run values:
```text
{
  "repro": [
    0.10537446895349054,
    0.10213119791760814,
    0.06111969040132473,
    0.08975684055921232,
    0.0743642761272536,
    0.09676598837209302,
    0.07675359387416023,
    0.07511021607794045,
    0.06965930268324015,
    0.06787698879577171
  ],
  "after-rabbit": [
    0.15217743568969008,
    0.12305960093766728,
    0.11425466467738668,
    0.12139778902004872,
    0.12028603688370343,
    0.3659522769984244,
    0.11952380952380952,
    0.10900698739350154,
    0.11957998311120902,
    0.12965997541990987,
    0.1520032802249297,
    0.13271779870189232,
    0.1225470716245624,
    0.12504355800091702,
    0.14497871609368737,
    0.16266604970405074,
    0.1686500039799411,
    0.11931700910029083,
    0.1309240587591103,
    0.10708205963647013
  ]
}
```

## Resources and all violating keys

Maximum observed Docker-displayed values (retained verbatim):
| group | run | container | max_observed_cpu | max_observed_memory |
| --- | --- | --- | --- | --- |
| after-kill | kill-1 | flash-sale-engine-app-2 | 88.69% | 367.5MiB / 640MiB |
| after-kill | kill-1 | flash-sale-engine-app-1 | 271.55% | 374.6MiB / 640MiB |
| after-kill | kill-1 | flash-sale-engine-nginx-1 | 17.58% | 9.785MiB / 64MiB |
| after-kill | kill-1 | flash_rabbitmq | 245.01% | 247.7MiB / 384MiB |
| after-kill | kill-1 | flash_redis | 25.38% | 42.08MiB / 384MiB |
| after-kill | kill-1 | flash_postgres | 41.02% | 103MiB / 384MiB |
| after-kill | kill-2 | flash-sale-engine-app-2 | 75.39% | 368.6MiB / 640MiB |
| after-kill | kill-2 | flash-sale-engine-app-1 | 221.65% | 376.4MiB / 640MiB |
| after-kill | kill-2 | flash-sale-engine-nginx-1 | 16.73% | 10.18MiB / 64MiB |
| after-kill | kill-2 | flash_rabbitmq | 224.39% | 235.7MiB / 384MiB |
| after-kill | kill-2 | flash_redis | 11.90% | 41.25MiB / 384MiB |
| after-kill | kill-2 | flash_postgres | 48.65% | 103.2MiB / 384MiB |
| after-kill | kill-3 | flash-sale-engine-app-2 | 95.14% | 367.6MiB / 640MiB |
| after-kill | kill-3 | flash-sale-engine-app-1 | 262.38% | 352.5MiB / 640MiB |
| after-kill | kill-3 | flash-sale-engine-nginx-1 | 16.52% | 10.26MiB / 64MiB |
| after-kill | kill-3 | flash_rabbitmq | 243.59% | 207.2MiB / 384MiB |
| after-kill | kill-3 | flash_redis | 11.44% | 40.27MiB / 384MiB |
| after-kill | kill-3 | flash_postgres | 41.48% | 104.5MiB / 384MiB |
| after-normal | normal-1 | flash-sale-engine-app-2 | 75.78% | 366.8MiB / 640MiB |
| after-normal | normal-1 | flash-sale-engine-app-1 | 76.50% | 341.2MiB / 640MiB |
| after-normal | normal-1 | flash-sale-engine-nginx-1 | 25.82% | 10.29MiB / 64MiB |
| after-normal | normal-1 | flash_rabbitmq | 242.52% | 212.1MiB / 384MiB |
| after-normal | normal-1 | flash_redis | 12.37% | 41.32MiB / 384MiB |
| after-normal | normal-1 | flash_postgres | 49.92% | 103.6MiB / 384MiB |
| after-normal | normal-2 | flash-sale-engine-app-2 | 114.53% | 366.4MiB / 640MiB |
| after-normal | normal-2 | flash-sale-engine-app-1 | 79.12% | 345.2MiB / 640MiB |
| after-normal | normal-2 | flash-sale-engine-nginx-1 | 24.91% | 10.15MiB / 64MiB |
| after-normal | normal-2 | flash_rabbitmq | 232.13% | 264.1MiB / 384MiB |
| after-normal | normal-2 | flash_redis | 11.39% | 40.23MiB / 384MiB |
| after-normal | normal-2 | flash_postgres | 49.53% | 105.7MiB / 384MiB |
| after-normal | normal-3 | flash-sale-engine-app-2 | 71.35% | 365.8MiB / 640MiB |
| after-normal | normal-3 | flash-sale-engine-app-1 | 78.14% | 346.1MiB / 640MiB |
| after-normal | normal-3 | flash-sale-engine-nginx-1 | 25.72% | 9.715MiB / 64MiB |
| after-normal | normal-3 | flash_rabbitmq | 233.74% | 262.2MiB / 384MiB |
| after-normal | normal-3 | flash_redis | 11.69% | 40.76MiB / 384MiB |
| after-normal | normal-3 | flash_postgres | 49.03% | 104.4MiB / 384MiB |
| after-rabbit | rabbit-1 | flash-sale-engine-app-2 | 392.88% | 368.8MiB / 640MiB |
| after-rabbit | rabbit-1 | flash-sale-engine-app-1 | 302.10% | 341.4MiB / 640MiB |
| after-rabbit | rabbit-1 | flash-sale-engine-nginx-1 | 22.69% | 10.72MiB / 64MiB |
| after-rabbit | rabbit-1 | flash_rabbitmq | 293.99% | 211.7MiB / 384MiB |
| after-rabbit | rabbit-1 | flash_redis | 15.94% | 25.9MiB / 384MiB |
| after-rabbit | rabbit-1 | flash_postgres | 65.57% | 106.2MiB / 384MiB |
| after-rabbit | rabbit-2 | flash-sale-engine-app-2 | 208.35% | 365.7MiB / 640MiB |
| after-rabbit | rabbit-2 | flash-sale-engine-app-1 | 250.54% | 342.7MiB / 640MiB |
| after-rabbit | rabbit-2 | flash-sale-engine-nginx-1 | 19.18% | 10.09MiB / 64MiB |
| after-rabbit | rabbit-2 | flash_rabbitmq | 273.95% | 208.4MiB / 384MiB |
| after-rabbit | rabbit-2 | flash_redis | 25.37% | 32.31MiB / 384MiB |
| after-rabbit | rabbit-2 | flash_postgres | 56.84% | 102.7MiB / 384MiB |
| after-rabbit | rabbit-3 | flash-sale-engine-app-2 | 120.63% | 368.4MiB / 640MiB |
| after-rabbit | rabbit-3 | flash-sale-engine-app-1 | 66.93% | 342.8MiB / 640MiB |
| after-rabbit | rabbit-3 | flash-sale-engine-nginx-1 | 21.16% | 9.836MiB / 64MiB |
| after-rabbit | rabbit-3 | flash_rabbitmq | 248.09% | 221.5MiB / 384MiB |
| after-rabbit | rabbit-3 | flash_redis | 21.46% | 33.86MiB / 384MiB |
| after-rabbit | rabbit-3 | flash_postgres | 52.64% | 104.5MiB / 384MiB |
| after-rabbit | rabbit-4 | flash-sale-engine-app-2 | 88.13% | 369.5MiB / 640MiB |
| after-rabbit | rabbit-4 | flash-sale-engine-app-1 | 100.61% | 343.2MiB / 640MiB |
| after-rabbit | rabbit-4 | flash-sale-engine-nginx-1 | 27.21% | 10.21MiB / 64MiB |
| after-rabbit | rabbit-4 | flash_rabbitmq | 237.42% | 215.6MiB / 384MiB |
| after-rabbit | rabbit-4 | flash_redis | 26.10% | 33.74MiB / 384MiB |
| after-rabbit | rabbit-4 | flash_postgres | 51.80% | 104.4MiB / 384MiB |
| after-rabbit | rabbit-5 | flash-sale-engine-app-2 | 234.78% | 368.8MiB / 640MiB |
| after-rabbit | rabbit-5 | flash-sale-engine-app-1 | 235.35% | 346.9MiB / 640MiB |
| after-rabbit | rabbit-5 | flash-sale-engine-nginx-1 | 29.50% | 9.797MiB / 64MiB |
| after-rabbit | rabbit-5 | flash_rabbitmq | 223.01% | 188.6MiB / 384MiB |
| after-rabbit | rabbit-5 | flash_redis | 25.80% | 38.35MiB / 384MiB |
| after-rabbit | rabbit-5 | flash_postgres | 47.39% | 102.1MiB / 384MiB |
| after-rabbit | rabbit-6 | flash-sale-engine-app-2 | 279.22% | 369.5MiB / 640MiB |
| after-rabbit | rabbit-6 | flash-sale-engine-app-1 | 255.51% | 344.2MiB / 640MiB |
| after-rabbit | rabbit-6 | flash-sale-engine-nginx-1 | 62.49% | 9.863MiB / 64MiB |
| after-rabbit | rabbit-6 | flash_rabbitmq | 271.68% | 240.6MiB / 384MiB |
| after-rabbit | rabbit-6 | flash_redis | 29.08% | 37.88MiB / 384MiB |
| after-rabbit | rabbit-6 | flash_postgres | 40.35% | 105.3MiB / 384MiB |
| after-rabbit | rabbit-7 | flash-sale-engine-app-2 | 59.62% | 370.1MiB / 640MiB |
| after-rabbit | rabbit-7 | flash-sale-engine-app-1 | 59.04% | 343.4MiB / 640MiB |
| after-rabbit | rabbit-7 | flash-sale-engine-nginx-1 | 17.02% | 9.812MiB / 64MiB |
| after-rabbit | rabbit-7 | flash_rabbitmq | 263.73% | 210.7MiB / 384MiB |
| after-rabbit | rabbit-7 | flash_redis | 22.06% | 37.5MiB / 384MiB |
| after-rabbit | rabbit-7 | flash_postgres | 43.33% | 104.5MiB / 384MiB |
| after-rabbit | rabbit-8 | flash-sale-engine-app-2 | 110.67% | 370.2MiB / 640MiB |
| after-rabbit | rabbit-8 | flash-sale-engine-app-1 | 70.03% | 344.2MiB / 640MiB |
| after-rabbit | rabbit-8 | flash-sale-engine-nginx-1 | 23.81% | 9.777MiB / 64MiB |
| after-rabbit | rabbit-8 | flash_rabbitmq | 184.38% | 212.6MiB / 384MiB |
| after-rabbit | rabbit-8 | flash_redis | 13.75% | 39.23MiB / 384MiB |
| after-rabbit | rabbit-8 | flash_postgres | 43.51% | 105.8MiB / 384MiB |
| after-rabbit | rabbit-9 | flash-sale-engine-app-2 | 148.14% | 372.7MiB / 640MiB |
| after-rabbit | rabbit-9 | flash-sale-engine-app-1 | 84.18% | 343.7MiB / 640MiB |
| after-rabbit | rabbit-9 | flash-sale-engine-nginx-1 | 22.92% | 9.855MiB / 64MiB |
| after-rabbit | rabbit-9 | flash_rabbitmq | 251.39% | 192.9MiB / 384MiB |
| after-rabbit | rabbit-9 | flash_redis | 54.80% | 40.61MiB / 384MiB |
| after-rabbit | rabbit-9 | flash_postgres | 53.85% | 109.1MiB / 384MiB |
| after-rabbit | rabbit-10 | flash-sale-engine-app-2 | 126.88% | 369.8MiB / 640MiB |
| after-rabbit | rabbit-10 | flash-sale-engine-app-1 | 130.66% | 344.3MiB / 640MiB |
| after-rabbit | rabbit-10 | flash-sale-engine-nginx-1 | 36.46% | 9.66MiB / 64MiB |
| after-rabbit | rabbit-10 | flash_rabbitmq | 292.27% | 202.3MiB / 384MiB |
| after-rabbit | rabbit-10 | flash_redis | 17.96% | 39.58MiB / 384MiB |
| after-rabbit | rabbit-10 | flash_postgres | 50.51% | 106.8MiB / 384MiB |
| after-rabbit | rabbit-11 | flash-sale-engine-app-2 | 137.04% | 368.6MiB / 640MiB |
| after-rabbit | rabbit-11 | flash-sale-engine-app-1 | 148.40% | 345.9MiB / 640MiB |
| after-rabbit | rabbit-11 | flash-sale-engine-nginx-1 | 49.13% | 9.941MiB / 64MiB |
| after-rabbit | rabbit-11 | flash_rabbitmq | 236.28% | 242.1MiB / 384MiB |
| after-rabbit | rabbit-11 | flash_redis | 21.89% | 38.23MiB / 384MiB |
| after-rabbit | rabbit-11 | flash_postgres | 66.06% | 104.3MiB / 384MiB |
| after-rabbit | rabbit-12 | flash-sale-engine-app-2 | 71.89% | 368.9MiB / 640MiB |
| after-rabbit | rabbit-12 | flash-sale-engine-app-1 | 65.86% | 343.7MiB / 640MiB |
| after-rabbit | rabbit-12 | flash-sale-engine-nginx-1 | 18.54% | 10.45MiB / 64MiB |
| after-rabbit | rabbit-12 | flash_rabbitmq | 245.26% | 253.1MiB / 384MiB |
| after-rabbit | rabbit-12 | flash_redis | 22.50% | 37.04MiB / 384MiB |
| after-rabbit | rabbit-12 | flash_postgres | 61.51% | 103.6MiB / 384MiB |
| after-rabbit | rabbit-13 | flash-sale-engine-app-2 | 152.95% | 369.2MiB / 640MiB |
| after-rabbit | rabbit-13 | flash-sale-engine-app-1 | 156.85% | 343.4MiB / 640MiB |
| after-rabbit | rabbit-13 | flash-sale-engine-nginx-1 | 24.65% | 9.762MiB / 64MiB |
| after-rabbit | rabbit-13 | flash_rabbitmq | 264.37% | 227.1MiB / 384MiB |
| after-rabbit | rabbit-13 | flash_redis | 14.33% | 37.47MiB / 384MiB |
| after-rabbit | rabbit-13 | flash_postgres | 49.04% | 104.2MiB / 384MiB |
| after-rabbit | rabbit-14 | flash-sale-engine-app-2 | 68.31% | 369.2MiB / 640MiB |
| after-rabbit | rabbit-14 | flash-sale-engine-app-1 | 50.84% | 342.9MiB / 640MiB |
| after-rabbit | rabbit-14 | flash-sale-engine-nginx-1 | 19.10% | 9.867MiB / 64MiB |
| after-rabbit | rabbit-14 | flash_rabbitmq | 201.05% | 209.2MiB / 384MiB |
| after-rabbit | rabbit-14 | flash_redis | 13.61% | 39.11MiB / 384MiB |
| after-rabbit | rabbit-14 | flash_postgres | 36.88% | 104.4MiB / 384MiB |
| after-rabbit | rabbit-15 | flash-sale-engine-app-2 | 149.95% | 370.8MiB / 640MiB |
| after-rabbit | rabbit-15 | flash-sale-engine-app-1 | 144.66% | 343.1MiB / 640MiB |
| after-rabbit | rabbit-15 | flash-sale-engine-nginx-1 | 24.48% | 9.922MiB / 64MiB |
| after-rabbit | rabbit-15 | flash_rabbitmq | 278.47% | 242.7MiB / 384MiB |
| after-rabbit | rabbit-15 | flash_redis | 52.83% | 38.71MiB / 384MiB |
| after-rabbit | rabbit-15 | flash_postgres | 67.82% | 102.7MiB / 384MiB |
| after-rabbit | rabbit-16 | flash-sale-engine-app-2 | 126.70% | 370.4MiB / 640MiB |
| after-rabbit | rabbit-16 | flash-sale-engine-app-1 | 111.58% | 345.9MiB / 640MiB |
| after-rabbit | rabbit-16 | flash-sale-engine-nginx-1 | 27.14% | 10.12MiB / 64MiB |
| after-rabbit | rabbit-16 | flash_rabbitmq | 256.69% | 224.8MiB / 384MiB |
| after-rabbit | rabbit-16 | flash_redis | 34.33% | 37.12MiB / 384MiB |
| after-rabbit | rabbit-16 | flash_postgres | 112.24% | 101.5MiB / 384MiB |
| after-rabbit | rabbit-17 | flash-sale-engine-app-2 | 281.98% | 365.3MiB / 640MiB |
| after-rabbit | rabbit-17 | flash-sale-engine-app-1 | 108.88% | 349.2MiB / 640MiB |
| after-rabbit | rabbit-17 | flash-sale-engine-nginx-1 | 64.17% | 10.01MiB / 64MiB |
| after-rabbit | rabbit-17 | flash_rabbitmq | 223.86% | 228.1MiB / 384MiB |
| after-rabbit | rabbit-17 | flash_redis | 26.48% | 39.05MiB / 384MiB |
| after-rabbit | rabbit-17 | flash_postgres | 60.87% | 101.6MiB / 384MiB |
| after-rabbit | rabbit-17 | peaceful_mcclintock | 99.73% | 967.2MiB / 4GiB |
| after-rabbit | rabbit-18 | flash-sale-engine-app-2 | 144.71% | 363.3MiB / 640MiB |
| after-rabbit | rabbit-18 | flash-sale-engine-app-1 | 134.82% | 353.6MiB / 640MiB |
| after-rabbit | rabbit-18 | flash-sale-engine-nginx-1 | 27.13% | 9.887MiB / 64MiB |
| after-rabbit | rabbit-18 | flash_rabbitmq | 213.03% | 208.8MiB / 384MiB |
| after-rabbit | rabbit-18 | flash_redis | 23.35% | 45.22MiB / 384MiB |
| after-rabbit | rabbit-18 | flash_postgres | 76.27% | 101.4MiB / 384MiB |
| after-rabbit | rabbit-19 | flash-sale-engine-app-2 | 129.70% | 368.2MiB / 640MiB |
| after-rabbit | rabbit-19 | flash-sale-engine-app-1 | 66.48% | 353MiB / 640MiB |
| after-rabbit | rabbit-19 | flash-sale-engine-nginx-1 | 24.51% | 9.578MiB / 64MiB |
| after-rabbit | rabbit-19 | flash_rabbitmq | 218.58% | 221.3MiB / 384MiB |
| after-rabbit | rabbit-19 | flash_redis | 22.02% | 41.58MiB / 384MiB |
| after-rabbit | rabbit-19 | flash_postgres | 59.44% | 102.9MiB / 384MiB |
| after-rabbit | rabbit-19 | wireless-triage-smoke | 171.09% | 749.1MiB / 4GiB |
| after-rabbit | rabbit-20 | flash-sale-engine-app-2 | 117.91% | 367.9MiB / 640MiB |
| after-rabbit | rabbit-20 | flash-sale-engine-app-1 | 123.55% | 349.8MiB / 640MiB |
| after-rabbit | rabbit-20 | flash-sale-engine-nginx-1 | 23.74% | 9.715MiB / 64MiB |
| after-rabbit | rabbit-20 | flash_rabbitmq | 238.64% | 206.5MiB / 384MiB |
| after-rabbit | rabbit-20 | flash_redis | 9.90% | 41.18MiB / 384MiB |
| after-rabbit | rabbit-20 | flash_postgres | 42.40% | 112MiB / 384MiB |
| before-normal | normal-1 | flash-sale-engine-app-1 | 80.08% | 365.7MiB / 640MiB |
| before-normal | normal-1 | flash-sale-engine-app-2 | 82.70% | 348.2MiB / 640MiB |
| before-normal | normal-1 | flash-sale-engine-nginx-1 | 31.66% | 10.54MiB / 64MiB |
| before-normal | normal-1 | flash_rabbitmq | 248.63% | 207.7MiB / 384MiB |
| before-normal | normal-1 | flash_redis | 16.67% | 25.82MiB / 384MiB |
| before-normal | normal-1 | flash_postgres | 72.30% | 100.7MiB / 384MiB |
| before-normal | normal-2 | flash-sale-engine-app-1 | 73.31% | 364.1MiB / 640MiB |
| before-normal | normal-2 | flash-sale-engine-app-2 | 67.68% | 347.9MiB / 640MiB |
| before-normal | normal-2 | flash-sale-engine-nginx-1 | 29.57% | 9.914MiB / 64MiB |
| before-normal | normal-2 | flash_rabbitmq | 265.81% | 234.6MiB / 384MiB |
| before-normal | normal-2 | flash_redis | 27.88% | 25.92MiB / 384MiB |
| before-normal | normal-2 | flash_postgres | 108.63% | 101MiB / 384MiB |
| before-normal | normal-3 | flash-sale-engine-app-1 | 63.22% | 363MiB / 640MiB |
| before-normal | normal-3 | flash-sale-engine-app-2 | 65.07% | 347MiB / 640MiB |
| before-normal | normal-3 | flash-sale-engine-nginx-1 | 25.16% | 10.09MiB / 64MiB |
| before-normal | normal-3 | flash_rabbitmq | 147.59% | 239.1MiB / 384MiB |
| before-normal | normal-3 | flash_redis | 18.04% | 23.98MiB / 384MiB |
| before-normal | normal-3 | flash_postgres | 60.86% | 110.1MiB / 384MiB |
| diagnostic | trace-rabbit-1 | flash-sale-engine-app-1 | 277.74% | 341.3MiB / 640MiB |
| diagnostic | trace-rabbit-1 | flash-sale-engine-app-2 | 385.99% | 363.7MiB / 640MiB |
| diagnostic | trace-rabbit-1 | flash-sale-engine-nginx-1 | 27.53% | 11.1MiB / 64MiB |
| diagnostic | trace-rabbit-1 | flash_rabbitmq | 267.35% | 224.1MiB / 384MiB |
| diagnostic | trace-rabbit-1 | flash_redis | 13.27% | 24.66MiB / 384MiB |
| diagnostic | trace-rabbit-1 | flash_postgres | 53.53% | 102.6MiB / 384MiB |
| repro | rabbit-1 | flash-sale-engine-app-1 | 499.74% | 362.6MiB / 640MiB |
| repro | rabbit-1 | flash-sale-engine-app-2 | 249.33% | 337.7MiB / 640MiB |
| repro | rabbit-1 | flash-sale-engine-nginx-1 | 29.11% | 10.91MiB / 64MiB |
| repro | rabbit-1 | flash_rabbitmq | 257.01% | 243.6MiB / 384MiB |
| repro | rabbit-1 | flash_redis | 22.21% | 18.02MiB / 384MiB |
| repro | rabbit-1 | flash_postgres | 71.84% | 101.6MiB / 384MiB |
| repro | rabbit-2 | flash-sale-engine-app-1 | 82.27% | 365.4MiB / 640MiB |
| repro | rabbit-2 | flash-sale-engine-app-2 | 144.44% | 349.2MiB / 640MiB |
| repro | rabbit-2 | flash-sale-engine-nginx-1 | 16.57% | 10.04MiB / 64MiB |
| repro | rabbit-2 | flash_rabbitmq | 247.30% | 214.7MiB / 384MiB |
| repro | rabbit-2 | flash_redis | 12.48% | 24.74MiB / 384MiB |
| repro | rabbit-2 | flash_postgres | 39.17% | 104.1MiB / 384MiB |
| repro | rabbit-3 | flash-sale-engine-app-1 | 52.87% | 369.2MiB / 640MiB |
| repro | rabbit-3 | flash-sale-engine-app-2 | 55.85% | 348.9MiB / 640MiB |
| repro | rabbit-3 | flash-sale-engine-nginx-1 | 15.27% | 9.875MiB / 64MiB |
| repro | rabbit-3 | flash_rabbitmq | 196.10% | 217.5MiB / 384MiB |
| repro | rabbit-3 | flash_redis | 6.51% | 28.12MiB / 384MiB |
| repro | rabbit-3 | flash_postgres | 42.29% | 109.6MiB / 384MiB |
| repro | rabbit-4 | flash-sale-engine-app-1 | 52.99% | 369MiB / 640MiB |
| repro | rabbit-4 | flash-sale-engine-app-2 | 45.22% | 348.5MiB / 640MiB |
| repro | rabbit-4 | flash-sale-engine-nginx-1 | 15.02% | 9.637MiB / 64MiB |
| repro | rabbit-4 | flash_rabbitmq | 236.49% | 221.5MiB / 384MiB |
| repro | rabbit-4 | flash_redis | 9.69% | 27.64MiB / 384MiB |
| repro | rabbit-4 | flash_postgres | 45.91% | 106.6MiB / 384MiB |
| repro | rabbit-5 | flash-sale-engine-app-1 | 48.28% | 368MiB / 640MiB |
| repro | rabbit-5 | flash-sale-engine-app-2 | 41.25% | 347.8MiB / 640MiB |
| repro | rabbit-5 | flash-sale-engine-nginx-1 | 16.20% | 10.04MiB / 64MiB |
| repro | rabbit-5 | flash_rabbitmq | 230.21% | 233.2MiB / 384MiB |
| repro | rabbit-5 | flash_redis | 6.53% | 27.14MiB / 384MiB |
| repro | rabbit-5 | flash_postgres | 32.92% | 106MiB / 384MiB |
| repro | rabbit-6 | flash-sale-engine-app-1 | 45.65% | 368.1MiB / 640MiB |
| repro | rabbit-6 | flash-sale-engine-app-2 | 45.99% | 348MiB / 640MiB |
| repro | rabbit-6 | flash-sale-engine-nginx-1 | 14.34% | 9.711MiB / 64MiB |
| repro | rabbit-6 | flash_rabbitmq | 241.78% | 231.7MiB / 384MiB |
| repro | rabbit-6 | flash_redis | 6.83% | 27.04MiB / 384MiB |
| repro | rabbit-6 | flash_postgres | 46.55% | 108.1MiB / 384MiB |
| repro | rabbit-7 | flash-sale-engine-app-1 | 40.76% | 371.4MiB / 640MiB |
| repro | rabbit-7 | flash-sale-engine-app-2 | 40.07% | 347.8MiB / 640MiB |
| repro | rabbit-7 | flash-sale-engine-nginx-1 | 15.17% | 9.797MiB / 64MiB |
| repro | rabbit-7 | flash_rabbitmq | 217.88% | 196.6MiB / 384MiB |
| repro | rabbit-7 | flash_redis | 9.32% | 26.45MiB / 384MiB |
| repro | rabbit-7 | flash_postgres | 33.94% | 103.8MiB / 384MiB |
| repro | rabbit-8 | flash-sale-engine-app-1 | 40.78% | 369.9MiB / 640MiB |
| repro | rabbit-8 | flash-sale-engine-app-2 | 43.71% | 347.4MiB / 640MiB |
| repro | rabbit-8 | flash-sale-engine-nginx-1 | 14.44% | 9.738MiB / 64MiB |
| repro | rabbit-8 | flash_rabbitmq | 249.12% | 167.8MiB / 384MiB |
| repro | rabbit-8 | flash_redis | 8.37% | 25.54MiB / 384MiB |
| repro | rabbit-8 | flash_postgres | 49.57% | 103.3MiB / 384MiB |
| repro | rabbit-9 | flash-sale-engine-app-1 | 42.89% | 368.4MiB / 640MiB |
| repro | rabbit-9 | flash-sale-engine-app-2 | 42.02% | 347.1MiB / 640MiB |
| repro | rabbit-9 | flash-sale-engine-nginx-1 | 15.30% | 9.902MiB / 64MiB |
| repro | rabbit-9 | flash_rabbitmq | 234.68% | 188.5MiB / 384MiB |
| repro | rabbit-9 | flash_redis | 5.98% | 25.39MiB / 384MiB |
| repro | rabbit-9 | flash_postgres | 33.74% | 106.2MiB / 384MiB |
| repro | rabbit-10 | flash-sale-engine-app-1 | 52.94% | 366.3MiB / 640MiB |
| repro | rabbit-10 | flash-sale-engine-app-2 | 52.84% | 346MiB / 640MiB |
| repro | rabbit-10 | flash-sale-engine-nginx-1 | 16.12% | 10.1MiB / 64MiB |
| repro | rabbit-10 | flash_rabbitmq | 220.27% | 209.7MiB / 384MiB |
| repro | rabbit-10 | flash_redis | 5.93% | 25.85MiB / 384MiB |
| repro | rabbit-10 | flash_postgres | 48.81% | 105.5MiB / 384MiB |

Host resource observations:
| group | run | min_observed_available_host_bytes | max_observed_swap_used_bytes |
| --- | --- | --- | --- |
| after-kill | kill-1 | 2975379456 | 425631744 |
| after-kill | kill-2 | 2979741696 | 424538112 |
| after-kill | kill-3 | 2878091264 | 426127360 |
| after-normal | normal-1 | 2923769856 | 425275392 |
| after-normal | normal-2 | 2875170816 | 425160704 |
| after-normal | normal-3 | 2837549056 | 424964096 |
| after-rabbit | rabbit-1 | 2773618688 | 268210176 |
| after-rabbit | rabbit-2 | 2816069632 | 268201984 |
| after-rabbit | rabbit-3 | 2834644992 | 268509184 |
| after-rabbit | rabbit-4 | 2474643456 | 282742784 |
| after-rabbit | rabbit-5 | 2006155264 | 312057856 |
| after-rabbit | rabbit-6 | 2466676736 | 319643648 |
| after-rabbit | rabbit-7 | 2795925504 | 319377408 |
| after-rabbit | rabbit-8 | 2834255872 | 319029248 |
| after-rabbit | rabbit-9 | 2904481792 | 318812160 |
| after-rabbit | rabbit-10 | 2879377408 | 322183168 |
| after-rabbit | rabbit-11 | 2325958656 | 345198592 |
| after-rabbit | rabbit-12 | 2836586496 | 347090944 |
| after-rabbit | rabbit-13 | 2868850688 | 352464896 |
| after-rabbit | rabbit-14 | 2741211136 | 355074048 |
| after-rabbit | rabbit-15 | 2690490368 | 369344512 |
| after-rabbit | rabbit-16 | 2545520640 | 383496192 |
| after-rabbit | rabbit-17 | 2633306112 | 412205056 |
| after-rabbit | rabbit-18 | 2578874368 | 417411072 |
| after-rabbit | rabbit-19 | 2632577024 | 426983424 |
| after-rabbit | rabbit-20 | 2943131648 | 427503616 |
| before-normal | normal-1 | 2686578688 | 73232384 |
| before-normal | normal-2 | 2677518336 | 74346496 |
| before-normal | normal-3 | 2673033216 | 78872576 |
| diagnostic | trace-rabbit-1 | 2830856192 | 177217536 |
| repro | rabbit-1 | 2720079872 | 59920384 |
| repro | rabbit-2 | 2713182208 | 61685760 |
| repro | rabbit-3 | 2697879552 | 63082496 |
| repro | rabbit-4 | 2697175040 | 64966656 |
| repro | rabbit-5 | 2679750656 | 66846720 |
| repro | rabbit-6 | 2692415488 | 67977216 |
| repro | rabbit-7 | 2705956864 | 68960256 |
| repro | rabbit-8 | 2702880768 | 70037504 |
| repro | rabbit-9 | 2712764416 | 71245824 |
| repro | rabbit-10 | 2714787840 | 71954432 |

Nonzero or unavailable cgroup counters:
```text
{
  "nonzero_counters": [],
  "missing_counters": []
}
```

Before/after counters can reset on restart; no unavailable delta is filled with zero. Raw docker-events.log and inspect.log retain OOM/death observations; broker restarts and injected SIGKILL death events are expected. Host swap usage is reported independently of container no-swap limits.

Observed resource findings:
```text
{
  "oom_events": 0,
  "nonzero_throttling_snapshots": 0,
  "nonzero_oom_snapshots": 0,
  "unavailable_cgroup_snapshots": 0,
  "jvm_oom_matching_log_lines": 0
}
```

diagnostic/trace-rabbit-1 — complete violating key lists:
```text
{
  "i2_keys": [
    {
      "key": "bench-trace-rabbit-1-24-17515",
      "order_id": "a6402d51-cd58-4c4b-a2ae-8de1c9029707",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T13:16:26.839973"
    },
    {
      "key": "bench-trace-rabbit-1-3-17513",
      "order_id": "03c0f838-4fb0-4135-a94d-12b9876bf0b6",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T13:16:26.843224"
    }
  ]
}
```

repro/rabbit-1 — complete violating key lists:
```text
{
  "i2_keys": [
    {
      "key": "bench-rabbit-1-15-19633",
      "order_id": "e62c8e62-7594-4b96-acec-33abe3d550c7",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:46:04.817171"
    },
    {
      "key": "bench-rabbit-1-41-19634",
      "order_id": "6bf67590-7e95-463b-acbb-6932ac480a57",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:46:04.817172"
    },
    {
      "key": "bench-rabbit-1-42-19632",
      "order_id": "7e6fb613-5795-4865-b9e1-459d36c6fdc9",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:46:04.811966"
    }
  ]
}
```

repro/rabbit-2 — complete violating key lists:
```text
{
  "i2_keys": [
    {
      "key": "bench-rabbit-2-10-20022",
      "order_id": "3c4401e5-5b64-479a-9117-6f01d2737416",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:32.991012"
    },
    {
      "key": "bench-rabbit-2-11-20021",
      "order_id": "544f34d0-48cd-4bf9-aac3-6ff6b4feeea1",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:32.98852"
    },
    {
      "key": "bench-rabbit-2-15-20016",
      "order_id": "12c27843-d694-44a8-8d05-de28a2b5a12b",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:17.657906"
    },
    {
      "key": "bench-rabbit-2-17-20014",
      "order_id": "21feff06-fba4-4576-b6c7-d4202eb6d58c",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:17.658691"
    },
    {
      "key": "bench-rabbit-2-23-20027",
      "order_id": "2fad5c4a-538c-47e3-9ec4-7a557923eae5",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:33.000519"
    },
    {
      "key": "bench-rabbit-2-26-20023",
      "order_id": "4a41991c-2a30-4325-8414-622f52757e11",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:17.661064"
    },
    {
      "key": "bench-rabbit-2-28-20019",
      "order_id": "f502e564-86b6-44b2-aa31-9673c2cad405",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:32.993573"
    },
    {
      "key": "bench-rabbit-2-29-20018",
      "order_id": "531c7a4b-eddc-45ea-a722-9209e8c1d6cd",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:17.659659"
    },
    {
      "key": "bench-rabbit-2-30-20020",
      "order_id": "ee3f98ba-16d4-42ed-bc61-f1ddf2001263",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:32.982345"
    },
    {
      "key": "bench-rabbit-2-39-20017",
      "order_id": "88b5923f-c849-4489-873d-3bf08c657684",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:17.660389"
    },
    {
      "key": "bench-rabbit-2-40-20025",
      "order_id": "5701aba1-d30a-40d6-a99d-529281ca45cb",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:32.998144"
    },
    {
      "key": "bench-rabbit-2-43-20024",
      "order_id": "2047e5d6-98a6-41aa-bc9a-a845abb5b7a5",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:32.996115"
    },
    {
      "key": "bench-rabbit-2-9-20026",
      "order_id": "1b8dd338-b797-48d1-a870-0301cd6a4651",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:48:33.002844"
    }
  ]
}
```

repro/rabbit-3 — complete violating key lists:
```text
{
  "i2_keys": [
    {
      "key": "bench-rabbit-3-11-20874",
      "order_id": "ab723a25-3e21-4847-b4c3-661ea16d5e2b",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:50:46.463466"
    },
    {
      "key": "bench-rabbit-3-22-20876",
      "order_id": "7c0467a9-2b65-4226-8cdc-63287445c20a",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:50:46.460888"
    },
    {
      "key": "bench-rabbit-3-26-20878",
      "order_id": "509f6925-8ac6-4574-ad0c-39840f90de01",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:50:46.466375"
    },
    {
      "key": "bench-rabbit-3-28-20880",
      "order_id": "0b690fce-62b5-4428-afa1-9b09375d04d8",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:50:46.468233"
    },
    {
      "key": "bench-rabbit-3-30-20879",
      "order_id": "dd978cfc-884c-45df-ade5-00adf4d41084",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:50:31.669828"
    },
    {
      "key": "bench-rabbit-3-41-20877",
      "order_id": "d5391fba-488b-4bc8-8559-883d605e6cfb",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:50:31.667675"
    },
    {
      "key": "bench-rabbit-3-9-20875",
      "order_id": "f460ee58-0352-4d0e-a48d-db1e4bf05e25",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T12:50:46.465176"
    }
  ]
}
```

repro/rabbit-8 — complete violating key lists:
```text
{
  "i2_keys": [
    {
      "key": "bench-rabbit-8-10-20717",
      "order_id": "4bcbf6ed-5730-44ed-a0ef-5141715ceade",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T13:01:26.848817"
    },
    {
      "key": "bench-rabbit-8-26-20718",
      "order_id": "b6089fb9-294b-4cc6-9772-b96339323bf9",
      "quantity": 1,
      "reservation": null,
      "decision": null,
      "rolled_back": "1",
      "created_at": "2026-10-10T13:01:26.84722"
    }
  ]
}
```

Every run also retains confirmed_without_http_202; these can be legitimate consumer-first outcomes, not loss or duplication. Counts:
| group | run | confirmed_without_http_202 |
| --- | --- | --- |
| after-kill | kill-1 | 0 |
| after-kill | kill-2 | 2 |
| after-kill | kill-3 | 1 |
| after-normal | normal-1 | 0 |
| after-normal | normal-2 | 0 |
| after-normal | normal-3 | 0 |
| after-rabbit | rabbit-1 | 0 |
| after-rabbit | rabbit-2 | 0 |
| after-rabbit | rabbit-3 | 0 |
| after-rabbit | rabbit-4 | 0 |
| after-rabbit | rabbit-5 | 0 |
| after-rabbit | rabbit-6 | 0 |
| after-rabbit | rabbit-7 | 0 |
| after-rabbit | rabbit-8 | 3 |
| after-rabbit | rabbit-9 | 0 |
| after-rabbit | rabbit-10 | 0 |
| after-rabbit | rabbit-11 | 0 |
| after-rabbit | rabbit-12 | 3 |
| after-rabbit | rabbit-13 | 0 |
| after-rabbit | rabbit-14 | 0 |
| after-rabbit | rabbit-15 | 0 |
| after-rabbit | rabbit-16 | 0 |
| after-rabbit | rabbit-17 | 0 |
| after-rabbit | rabbit-18 | 0 |
| after-rabbit | rabbit-19 | 0 |
| after-rabbit | rabbit-20 | 7 |
| before-normal | normal-1 | 0 |
| before-normal | normal-2 | 0 |
| before-normal | normal-3 | 0 |
| diagnostic | trace-rabbit-1 | 2 |
| repro | rabbit-1 | 3 |
| repro | rabbit-2 | 13 |
| repro | rabbit-3 | 7 |
| repro | rabbit-4 | 0 |
| repro | rabbit-5 | 0 |
| repro | rabbit-6 | 0 |
| repro | rabbit-7 | 0 |
| repro | rabbit-8 | 2 |
| repro | rabbit-9 | 0 |
| repro | rabbit-10 | 0 |

## Raw commands and reconciliation output

Quoted excerpts omit trailing padding spaces; original raw files remain verbatim. For every run, raw command output and failed thresholds are in [results/decision-fix](results/decision-fix). Nothing is reconstructed from memory. Commands used:
```text
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw package -DskipTests
docker compose --profile distributed up -d --build --scale app=2
python3 bench/experiment.py OUTPUT rabbit
python3 bench/experiment.py OUTPUT kill
python3 bench/experiment.py OUTPUT
python3 bench/decision_reconcile.py OUTPUT
python3 bench/decision_cost.py bench/results/decision-fix/decision-cost
python3 bench/decision_report.py
python3 bench/decision_document.py
```

after-kill/kill-1 — raw reconciliation JSON:
```text
{
  "http_202": 54830,
  "confirmed": 54830,
  "redis_stock": 45170,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

after-kill/kill-2 — raw reconciliation JSON:
```text
{
  "http_202": 54577,
  "confirmed": 54579,
  "redis_stock": 45421,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 2,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

after-kill/kill-3 — raw reconciliation JSON:
```text
{
  "http_202": 54116,
  "confirmed": 54117,
  "redis_stock": 45883,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 1,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

after-normal/normal-1 — raw reconciliation JSON:
```text
{
  "http_202": 49080,
  "confirmed": 49080,
  "redis_stock": 50920,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

after-normal/normal-2 — raw reconciliation JSON:
```text
{
  "http_202": 49385,
  "confirmed": 49385,
  "redis_stock": 50615,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

after-normal/normal-3 — raw reconciliation JSON:
```text
{
  "http_202": 49496,
  "confirmed": 49496,
  "redis_stock": 50504,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

after-rabbit/rabbit-1 — raw reconciliation JSON:
```text
{
  "http_202": 41856,
  "confirmed": 41856,
  "redis_stock": 58144,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-2 — raw reconciliation JSON:
```text
{
  "http_202": 47509,
  "confirmed": 47509,
  "redis_stock": 52491,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-3 — raw reconciliation JSON:
```text
{
  "http_202": 48087,
  "confirmed": 48087,
  "redis_stock": 51913,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-4 — raw reconciliation JSON:
```text
{
  "http_202": 46890,
  "confirmed": 46890,
  "redis_stock": 53110,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-5 — raw reconciliation JSON:
```text
{
  "http_202": 46747,
  "confirmed": 46747,
  "redis_stock": 53253,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-6 — raw reconciliation JSON:
```text
{
  "http_202": 33400,
  "confirmed": 33400,
  "redis_stock": 66600,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-7 — raw reconciliation JSON:
```text
{
  "http_202": 48073,
  "confirmed": 48073,
  "redis_stock": 51927,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-8 — raw reconciliation JSON:
```text
{
  "http_202": 48837,
  "confirmed": 48840,
  "redis_stock": 51160,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 3,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-9 — raw reconciliation JSON:
```text
{
  "http_202": 47959,
  "confirmed": 47959,
  "redis_stock": 52041,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-10 — raw reconciliation JSON:
```text
{
  "http_202": 46738,
  "confirmed": 46738,
  "redis_stock": 53262,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-11 — raw reconciliation JSON:
```text
{
  "http_202": 43430,
  "confirmed": 43430,
  "redis_stock": 56570,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-12 — raw reconciliation JSON:
```text
{
  "http_202": 47435,
  "confirmed": 47438,
  "redis_stock": 52562,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 3,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-13 — raw reconciliation JSON:
```text
{
  "http_202": 46368,
  "confirmed": 46368,
  "redis_stock": 53632,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-14 — raw reconciliation JSON:
```text
{
  "http_202": 47706,
  "confirmed": 47706,
  "redis_stock": 52294,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-15 — raw reconciliation JSON:
```text
{
  "http_202": 44791,
  "confirmed": 44791,
  "redis_stock": 55209,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-16 — raw reconciliation JSON:
```text
{
  "http_202": 43429,
  "confirmed": 43429,
  "redis_stock": 56571,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-17 — raw reconciliation JSON:
```text
{
  "http_202": 41776,
  "confirmed": 41776,
  "redis_stock": 58224,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-18 — raw reconciliation JSON:
```text
{
  "http_202": 46935,
  "confirmed": 46935,
  "redis_stock": 53065,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-19 — raw reconciliation JSON:
```text
{
  "http_202": 46027,
  "confirmed": 46027,
  "redis_stock": 53973,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

after-rabbit/rabbit-20 — raw reconciliation JSON:
```text
{
  "http_202": 48780,
  "confirmed": 48787,
  "redis_stock": 51213,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 7,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

before-normal/normal-1 — raw reconciliation JSON:
```text
{
  "http_202": 48921,
  "confirmed": 48921,
  "redis_stock": 51079,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

before-normal/normal-2 — raw reconciliation JSON:
```text
{
  "http_202": 48894,
  "confirmed": 48894,
  "redis_stock": 51106,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

before-normal/normal-3 — raw reconciliation JSON:
```text
{
  "http_202": 49090,
  "confirmed": 49090,
  "redis_stock": 50910,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}
```

diagnostic/trace-rabbit-1 — raw reconciliation JSON:
```text
{
  "http_202": 45195,
  "confirmed": 45197,
  "redis_stock": 54805,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 2,
  "stock_accounting_excess": 2,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-1 — raw reconciliation JSON:
```text
{
  "http_202": 48222,
  "confirmed": 48225,
  "redis_stock": 51778,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 3,
  "stock_accounting_excess": 3,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-2 — raw reconciliation JSON:
```text
{
  "http_202": 49670,
  "confirmed": 49683,
  "redis_stock": 50330,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 13,
  "stock_accounting_excess": 13,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-3 — raw reconciliation JSON:
```text
{
  "http_202": 50461,
  "confirmed": 50468,
  "redis_stock": 49539,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 7,
  "stock_accounting_excess": 7,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-4 — raw reconciliation JSON:
```text
{
  "http_202": 50198,
  "confirmed": 50198,
  "redis_stock": 49802,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-5 — raw reconciliation JSON:
```text
{
  "http_202": 51033,
  "confirmed": 51033,
  "redis_stock": 48967,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-6 — raw reconciliation JSON:
```text
{
  "http_202": 49713,
  "confirmed": 49713,
  "redis_stock": 50287,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-7 — raw reconciliation JSON:
```text
{
  "http_202": 51121,
  "confirmed": 51121,
  "redis_stock": 48879,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-8 — raw reconciliation JSON:
```text
{
  "http_202": 50978,
  "confirmed": 50980,
  "redis_stock": 49022,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 2,
  "stock_accounting_excess": 2,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-9 — raw reconciliation JSON:
```text
{
  "http_202": 51418,
  "confirmed": 51418,
  "redis_stock": 48582,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

repro/rabbit-10 — raw reconciliation JSON:
```text
{
  "http_202": 51496,
  "confirmed": 51496,
  "redis_stock": 48504,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}
```

## DRAFT resume figures

Copied verbatim from [results/decision-fix/resume-draft.txt](results/decision-fix/resume-draft.txt); cohort numbers come from metrics.json, test numbers from test-setup/tests-attempt-1.log. Limited local-run evidence, not a durability guarantee:
```text
DRAFT: Reproduced inventory-conservation failures in 4/10 broker-restart runs (25 affected orders); after Redis decision arbitration, 0/20 broker-restart runs violated the audited invariants.
DRAFT: Post-fix broker-restart cohort: lost orders 0, duplicates 0, stuck reservations 0, orphan consumer decisions 0; SIGKILL cohort: 0/3 runs violated audited invariants.
DRAFT: Integration tests: 13 passed, failures 0, errors 0, skipped 0; original test bodies unchanged.
```
