<!-- decision-fix-report -->
# Refund/persistence defect — executed follow-up

Worst fixed broker-restart HTTP error fraction: `0.3659522769984244` (`rabbit-6`). This negative availability result is retained; inventory arbitration does not fix outage HTTP failures. Quoted excerpts omit trailing padding spaces; full raw logs remain verbatim.

See [bench/REPORT.md](bench/REPORT.md) for exact runs, violating keys, root-cause timestamps, limits, resource output and DRAFT resume text. Historical measurements below remain retained; their old present-tense failure statements describe the former code.


| group | runs | violating_runs | undrained_runs | i1_excess_units | i1_violation_units | i2_violations | lost | duplicates | stuck | stuck_decisions |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| repro | 10 | 4 | 0 | 25 | 25 | 25 | 0 | 0 | 0 | 0 |
| after-rabbit | 20 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| after-kill | 3 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |

Raw initial test command output:
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

Raw final test command output:
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

Raw diagnostic evidence:
```text
app-2  | 2026-10-10T13:16:11.690Z  WARN 1 --- [flash-sale-engine] [nio-8080-exec-8] c.f.e.config.ConfirmingRabbitTemplate    : ORDER_TRACE event=CONFIRM_FAILURE type=NACK key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638171690 reason=connection error; protocol method: #method<connection.close>(reply-code=320, reply-text=CONNECTION_FORCED - broker forced connection closure with reason 'shutdown', class-id=0, method-id=0)
app-2  | 2026-10-10T13:16:11.691Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-8] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_START key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638171691
app-2  | 2026-10-10T13:16:11.701Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-8] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_END key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638171701 applied=true
app-2  | 2026-10-10T13:16:26.841Z DEBUG 1 --- [flash-sale-engine] [ntContainer#1-5] c.f.e.service.OrderProcessingService     : ORDER_TRACE event=PERSIST_COMMIT key=bench-trace-rabbit-1-24-17515 orderId=a6402d51-cd58-4c4b-a2ae-8de1c9029707 epochMs=1791638186841
app-2  | 2026-10-10T13:16:11.685Z  WARN 1 --- [flash-sale-engine] [nio-8080-exec-5] c.f.e.config.ConfirmingRabbitTemplate    : ORDER_TRACE event=CONFIRM_FAILURE type=NACK key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638171685 reason=connection error; protocol method: #method<connection.close>(reply-code=320, reply-text=CONNECTION_FORCED - broker forced connection closure with reason 'shutdown', class-id=0, method-id=0)
app-2  | 2026-10-10T13:16:11.691Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-5] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_START key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638171691
app-2  | 2026-10-10T13:16:11.702Z DEBUG 1 --- [flash-sale-engine] [nio-8080-exec-5] c.f.engine.service.InventoryService      : ORDER_TRACE event=REFUND_END key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638171702 applied=true
app-2  | 2026-10-10T13:16:26.847Z DEBUG 1 --- [flash-sale-engine] [ntContainer#1-5] c.f.e.service.OrderProcessingService     : ORDER_TRACE event=PERSIST_COMMIT key=bench-trace-rabbit-1-3-17513 orderId=03c0f838-4fb0-4135-a94d-12b9876bf0b6 epochMs=1791638186847
```

Failed k6 thresholds and raw reconciliation for each run are preserved in bench/results/decision-fix; full raw reconciliation JSON is embedded in bench/REPORT.md. k6, nginx, apps and infrastructure shared one laptop.

<!-- /decision-fix-report -->



# Distributed and fault-tolerance measurements

**Result: app replication and publisher confirms are implemented, but full fault tolerance is not proven. A broker restart after confirms violated stock conservation.**

Baseline was measured and committed before source changes. Original tests remain intact; final tests passed. Replicated throughput did not scale in the requested paced ramp. Confirms added latency and reduced request rate. Accepted-order loss and stuck reservations were absent before and after, so no outbox was added. HTTP failures, client outcome ambiguities, and the stock-accounting failure remain visible below. The load generator and services share one laptop.

Current executed evidence starts at [Measured baseline](#measured-baseline-for-distributedfault-tolerance-work), followed by replica, before-fault, confirm and conclusion sections. Earlier verification below is historical and is not used as evidence for this task.

# Verification results — 2026-10-06 UTC

## Environment

Commands run:

```bash
lscpu | rg 'Architecture:|CPU\(s\):|Model name:|Core\(s\) per socket:|Thread\(s\) per core:|Hypervisor vendor:'
rg '^MemTotal:|^SwapTotal:' /proc/meminfo
cat /etc/os-release | rg 'PRETTY_NAME|VERSION_ID'
uname -r
java -version
./mvnw -version
docker version --format 'Client {{.Client.Version}} / Server {{.Server.Version}}'
docker compose version
k6 version
```

Raw output (relevant lines):

```text
Architecture:                            x86_64
CPU(s):                                  16
Model name:                              AMD Ryzen 7 7840HS w/ Radeon 780M Graphics
Thread(s) per core:                      2
Core(s) per socket:                      8
Hypervisor vendor:                       Microsoft
MemTotal:        3636900 kB
SwapTotal:       1048576 kB
PRETTY_NAME="Debian GNU/Linux 13 (trixie)"
VERSION_ID="13"
6.18.33.2-microsoft-standard-wsl2
openjdk version "21.0.12.1" 2026-08-18
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Client 29.8.0 / Server 29.8.0
Docker Compose version v5.5.1
k6 v2.2.0 (commit/00a9a1b7f5, go1.26.5, linux/amd64)
```

Docker Desktop reported version 4.91.0; Testcontainers reported version 2.0.5 in test log below.

## Integration tests

Oversell: M=100 stock, N=2000 concurrent requests with distinct keys, 64 client threads. Same-key test: K=40 concurrent requests. Other tests cover HTTP validation, exhausted consumer retries and duplicate DLQ delivery, and publish failure. Assertions require exactly M accepted and CONFIRMED, Redis stock zero, one same-key order/decrement, and exactly one rollback.

Command run:

```bash
./mvnw test -q > /tmp/flash-sale-test-final.log 2>&1; code=$?; tail -40 /tmp/flash-sale-test-final.log; exit "$code"
```

Exit code: 0. Surefire XML reported `tests="5" errors="0" skipped="0" failures="0" flakes="0"`. Raw command log follows. RabbitMQ connection reset lines occurred during container shutdown after tests passed.

```text
17:49:17.005 [main] INFO org.testcontainers.images.PullPolicy -- Image pull policy will be performed by: DefaultPullPolicy()
17:49:17.012 [main] INFO org.testcontainers.utility.ImageNameSubstitutor -- Image name substitution will be performed by: DefaultImageNameSubstitutor (composite of 'ConfigurationFileImageNameSubstitutor' and 'PrefixingImageNameSubstitutor')
17:49:17.034 [main] INFO org.testcontainers.DockerClientFactory -- Testcontainers version: 2.0.5
17:49:17.487 [main] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Loaded org.testcontainers.dockerclient.UnixSocketClientProviderStrategy from ~/.testcontainers.properties, will try it first
17:49:18.377 [main] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Found Docker environment with local Unix socket (unix:///var/run/docker.sock)
17:49:18.382 [main] INFO org.testcontainers.DockerClientFactory -- Docker host IP address is localhost
17:49:18.482 [main] INFO org.testcontainers.DockerClientFactory -- Connected to docker:
  Server Version: 29.8.0
  API Version: 1.56
  Operating System: Docker Desktop
  Total Memory: 3551 MB
  Labels:
    com.docker.desktop.address=unix:///var/run/docker-cli.sock
17:49:18.894 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Creating container for image: testcontainers/ryuk:0.14.0
17:49:19.179 [main] INFO org.testcontainers.utility.RegistryAuthLocator -- Credential helper/store (docker-credential-desktop.exe) does not have credentials for https://index.docker.io/v1/
17:49:19.726 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Container testcontainers/ryuk:0.14.0 is starting: 86e4b1715c39e3290a9ab99ed9c57faf5e20de88c4a36eb627829d3aa1dd53a6
17:49:21.006 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Container testcontainers/ryuk:0.14.0 started in PT2.110892404S
17:49:21.024 [main] INFO org.testcontainers.utility.RyukResourceReaper -- Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
17:49:21.025 [main] INFO org.testcontainers.DockerClientFactory -- Checking the system...
17:49:21.026 [main] INFO org.testcontainers.DockerClientFactory -- ✔︎ Docker server version should be at least 1.6.0
17:49:21.028 [main] INFO tc.rabbitmq:3.13-management-alpine -- Creating container for image: rabbitmq:3.13-management-alpine
17:49:21.197 [main] INFO tc.rabbitmq:3.13-management-alpine -- Container rabbitmq:3.13-management-alpine is starting: cfe67487d23d628e34ae13ccdbbd4b0339fa75fec308fa1ac93dd53a5b613933
17:49:29.556 [main] INFO tc.rabbitmq:3.13-management-alpine -- Container rabbitmq:3.13-management-alpine started in PT8.527699348S
17:49:29.564 [main] INFO tc.redis:7-alpine -- Creating container for image: redis:7-alpine
17:49:29.645 [main] INFO tc.redis:7-alpine -- Container redis:7-alpine is starting: 4b7d212a76142c667027ed70d00b736da6d1a14d21af6eb02e244d44b91b3e6b
17:49:30.020 [main] INFO tc.redis:7-alpine -- Container redis:7-alpine started in PT0.455569393S
17:49:30.021 [main] INFO tc.postgres:16-alpine -- Creating container for image: postgres:16-alpine
17:49:30.115 [main] INFO tc.postgres:16-alpine -- Container postgres:16-alpine is starting: bd86750788af58d508cab04c537e4281fe110f1f10b297d3eed883a08fa7c95b
17:49:32.238 [main] INFO tc.postgres:16-alpine -- Container postgres:16-alpine started in PT2.217571976S
17:49:32.240 [main] INFO tc.postgres:16-alpine -- Container is started (JDBC URL: jdbc:postgresql://localhost:55738/test?loggerLevel=OFF)
17:49:32.546 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.flashsale.engine.OrderFlowIntegrationTests]: OrderFlowIntegrationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
17:49:32.721 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.flashsale.engine.FlashSaleEngineApplication for test class com.flashsale.engine.OrderFlowIntegrationTests
17:49:32.819 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.flashsale.engine.OrderFlowIntegrationTests]: OrderFlowIntegrationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
17:49:32.822 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.flashsale.engine.FlashSaleEngineApplication for test class com.flashsale.engine.OrderFlowIntegrationTests

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.1)

2026-10-06T17:49:33.589Z  INFO 28275 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : Starting OrderFlowIntegrationTests using Java 21.0.12.1 with PID 28275 (started by ayussh in /home/ayussh/projects/flash-sale-engine)
2026-10-06T17:49:33.591Z  INFO 28275 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : No active profile set, falling back to 1 default profile: "default"
2026-10-06T17:49:34.250Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-10-06T17:49:34.254Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-10-06T17:49:34.433Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 168 ms. Found 2 JPA repository interfaces.
2026-10-06T17:49:34.510Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-10-06T17:49:34.512Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2026-10-06T17:49:34.534Z  INFO 28275 --- [flash-sale-engine] [           main] .RepositoryConfigurationExtensionSupport : Spring Data Redis - Could not safely identify store assignment for repository candidate interface com.flashsale.engine.repository.OrderRepository; If you want this repository to be a Redis repository, consider annotating your entities with one of these annotations: org.springframework.data.redis.core.RedisHash (preferred), or consider extending one of the following types with your repository: org.springframework.data.keyvalue.repository.KeyValueRepository
2026-10-06T17:49:34.534Z  INFO 28275 --- [flash-sale-engine] [           main] .RepositoryConfigurationExtensionSupport : Spring Data Redis - Could not safely identify store assignment for repository candidate interface com.flashsale.engine.repository.ProductRepository; If you want this repository to be a Redis repository, consider annotating your entities with one of these annotations: org.springframework.data.redis.core.RedisHash (preferred), or consider extending one of the following types with your repository: org.springframework.data.keyvalue.repository.KeyValueRepository
2026-10-06T17:49:34.535Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 6 ms. Found 0 Redis repository interfaces.
2026-10-06T17:49:35.222Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 0 (http)
2026-10-06T17:49:35.241Z  INFO 28275 --- [flash-sale-engine] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-10-06T17:49:35.241Z  INFO 28275 --- [flash-sale-engine] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.24]
2026-10-06T17:49:35.291Z  INFO 28275 --- [flash-sale-engine] [           main] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 1679 ms
2026-10-06T17:49:35.495Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]
2026-10-06T17:49:35.586Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.4.5.Final
2026-10-06T17:49:36.053Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-10-06T17:49:36.098Z  INFO 28275 --- [flash-sale-engine] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-10-06T17:49:36.341Z  INFO 28275 --- [flash-sale-engine] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6d2693f
2026-10-06T17:49:36.342Z  INFO 28275 --- [flash-sale-engine] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-10-06T17:49:36.373Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-10-06T17:49:36.393Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
	Database JDBC URL [jdbc:postgresql://localhost:55738/test?loggerLevel=OFF]
	Database driver: PostgreSQL JDBC Driver
	Database dialect: PostgreSQLDialect
	Database version: 16.15
	Default catalog/schema: test/public
	Autocommit mode: undefined/unknown
	Isolation level: READ_COMMITTED [default READ_COMMITTED]
	JDBC fetch size: none
	Pool: DataSourceConnectionProvider
	Minimum pool size: undefined/unknown
	Maximum pool size: undefined/unknown
2026-10-06T17:49:37.202Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-10-06T17:49:37.275Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : HHH000247: ErrorCode: 0, SQLState: 00000
2026-10-06T17:49:37.275Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : constraint "ukd1kkvl4hi9hp3peub1umk2xeo" of relation "orders" does not exist, skipping
2026-10-06T17:49:37.278Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : HHH000247: ErrorCode: 0, SQLState: 00000
2026-10-06T17:49:37.278Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : constraint "ukhmsk25beh6atojvle1xuymjj0" of relation "orders" does not exist, skipping
2026-10-06T17:49:37.280Z  INFO 28275 --- [flash-sale-engine] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
Mockito is currently self-attaching to enable the inline-mock-maker. This will no longer work in future releases of the JDK. Please add Mockito as an agent to your build as described in Mockito's documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
WARNING: A Java agent has been loaded dynamically (/home/ayussh/.m2/repository/net/bytebuddy/byte-buddy-agent/1.18.11/byte-buddy-agent-1.18.11.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
2026-10-06T17:49:38.850Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.d.j.r.query.QueryEnhancerFactories   : Hibernate is in classpath; If applicable, HQL parser will be used.
2026-10-06T17:49:39.173Z  WARN 28275 --- [flash-sale-engine] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2026-10-06T17:49:39.939Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 40785 (http) with context path '/'
2026-10-06T17:49:39.945Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:56817]
2026-10-06T17:49:40.020Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#46ae1e6b:0/SimpleConnection@f4e4cb2 [delegate=amqp://guest@127.0.0.1:56817/, localPort=33750]
2026-10-06T17:49:40.150Z  INFO 28275 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : Started OrderFlowIntegrationTests in 7.233 seconds (process running for 24.959)
2026-10-06T17:49:40.932Z  INFO 28275 --- [flash-sale-engine] [o-auto-1-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-10-06T17:49:40.932Z  INFO 28275 --- [flash-sale-engine] [o-auto-1-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-10-06T17:49:40.933Z  INFO 28275 --- [flash-sale-engine] [o-auto-1-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2026-10-06T17:49:49.781Z  WARN 28275 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.retry.RepublishMessageRecoverer  : Republishing failed message to exchange 'order.dlx' with routing key order.failed
2026-10-06T17:49:49.862Z ERROR 28275 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=true. orderId=1ebe375c-0dbc-484b-819a-e68f65dbaccc, productId=101, quantity=1, idempotencyKey=fail-55850836-d2f1-4b0b-80d9-cc72f82a0ae0
2026-10-06T17:49:50.054Z ERROR 28275 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=false. orderId=1ebe375c-0dbc-484b-819a-e68f65dbaccc, productId=101, quantity=1, idempotencyKey=fail-55850836-d2f1-4b0b-80d9-cc72f82a0ae0
2026-10-06T17:49:50.061Z ERROR 28275 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=false. orderId=1ebe375c-0dbc-484b-819a-e68f65dbaccc, productId=101, quantity=1, idempotencyKey=fail-55850836-d2f1-4b0b-80d9-cc72f82a0ae0
2026-10-06T17:49:51.862Z  INFO 28275 --- [flash-sale-engine] [xecutorLoop-1-1] i.l.core.protocol.ConnectionWatchdog     : Reconnecting, last destination was localhost/127.0.0.1:55737
2026-10-06T17:49:51.902Z  WARN 28275 --- [flash-sale-engine] [ioEventLoop-4-2] i.l.core.protocol.ConnectionWatchdog     : Cannot reconnect to [localhost/<unresolved>:55737]: Connection closed prematurely

io.lettuce.core.RedisConnectionException: Connection closed prematurely
	at io.lettuce.core.protocol.RedisHandshakeHandler.channelInactive(RedisHandshakeHandler.java:76) ~[lettuce-core-7.5.2.RELEASE.jar:7.5.2.RELEASE/5728917]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:251) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelInactive(ChannelInboundHandlerAdapter.java:81) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.lettuce.core.ChannelGroupListener.channelInactive(ChannelGroupListener.java:54) ~[lettuce-core-7.5.2.RELEASE.jar:7.5.2.RELEASE/5728917]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:251) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelInactive(DefaultChannelPipeline.java:1424) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelInactive(DefaultChannelPipeline.java:876) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe$6.run(AbstractChannel.java:676) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:148) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:141) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:535) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.SingleThreadIoEventLoop.run(SingleThreadIoEventLoop.java:201) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1204) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]

2026-10-06T17:49:52.792Z  WARN 28275 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.l.SimpleMessageListenerContainer : Consumer raised exception, processing can restart if the connection factory supports it

com.rabbitmq.client.ShutdownSignalException: connection error
	at com.rabbitmq.client.impl.AMQConnection.startShutdown(AMQConnection.java:1014) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.shutdown(AMQConnection.java:1004) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.handleFailure(AMQConnection.java:804) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.access$500(AMQConnection.java:49) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:703) ~[amqp-client-5.30.0.jar:5.30.0]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.io.EOFException
	at java.base/java.io.DataInputStream.readUnsignedByte(DataInputStream.java:297) ~[na:na]
	at com.rabbitmq.client.impl.Frame.readFrom(Frame.java:92) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.SocketFrameHandler.readFrame(SocketFrameHandler.java:199) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:694) ~[amqp-client-5.30.0.jar:5.30.0]
	... 1 common frames omitted

2026-10-06T17:49:52.811Z  INFO 28275 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.l.SimpleMessageListenerContainer : Restarting Consumer@37994ff: tags=[[amq.ctag-OlLr2iui4IqXoJc5cZj-NQ]], channel=Cached Rabbit Channel: AMQChannel(amqp://guest@127.0.0.1:56817/,2), conn: Proxy@6bb37a5 Shared Rabbit Connection: SimpleConnection@f4e4cb2 [delegate=amqp://guest@127.0.0.1:56817/, localPort=33750], acknowledgeMode=AUTO local queue size=0
2026-10-06T17:49:52.836Z  INFO 28275 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:56817]
2026-10-06T17:49:52.849Z ERROR 28275 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.l.SimpleMessageListenerContainer : Failed to check/redeclare auto-delete queue(s).

org.springframework.amqp.AmqpConnectException: java.net.ConnectException: Connection refused
	at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException(RabbitExceptionTranslator.java:60) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:629) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection(CachingConnectionFactory.java:746) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils.createConnection(ConnectionFactoryUtils.java:256) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.doExecute(RabbitTemplate.java:2239) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2211) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2191) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitAdmin.getQueueInfo(RabbitAdmin.java:472) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitAdmin.getQueueProperties(RabbitAdmin.java:456) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.attemptDeclarations(AbstractMessageListenerContainer.java:1978) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.redeclareElementsIfNecessary(AbstractMessageListenerContainer.java:1941) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.initialize(SimpleMessageListenerContainer.java:1602) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.run(SimpleMessageListenerContainer.java:1400) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.net.ConnectException: Connection refused
	at java.base/sun.nio.ch.Net.pollConnect(Native Method) ~[na:na]
	at java.base/sun.nio.ch.Net.pollConnectNow(Net.java:694) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.timedFinishConnect(NioSocketImpl.java:542) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.connect(NioSocketImpl.java:592) ~[na:na]
	at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:327) ~[na:na]
	at java.base/java.net.Socket.connect(Socket.java:751) ~[na:na]
	at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create(SocketFrameHandlerFactory.java:61) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1355) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1297) ~[amqp-client-5.30.0.jar:5.30.0]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:676) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:644) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:590) ~[spring-rabbit-4.1.1.jar:4.1.1]
	... 12 common frames omitted

2026-10-06T17:49:52.857Z  INFO 28275 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:56817]
```

## k6 load test

Commands run:

```bash
docker compose up -d
./mvnw spring-boot:run > /tmp/flash-sale-app.log 2>&1
k6 inspect scripts/load_test.js
k6 run scripts/load_test.js > /tmp/flash-sale-k6-final.log 2>&1; code=$?; tail -45 /tmp/flash-sale-k6-final.log; exit "$code"
```

Final k6 exit code: 0. Scenario ramps 0 → 20 → 100 → 0 VUs over 2 minutes, with 100000 initial stock and unique keys per run. `accepted_orders` rate counts HTTP 202 only. HTTP 409 is an expected sold-out response; it does not count as an HTTP error. Raw final k6 summary:

```text
  █ THRESHOLDS

    http_req_duration
    ✓ 'p(99)<1000' p(99)=21.15ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS

    CUSTOM
    accepted_orders................: 51385 427.793126/s
    unexpected_responses...........: 0.00% 0 out of 51385

    HTTP
    http_req_duration..............: avg=3.68ms   min=1.16ms   med=2.69ms   max=333.35ms p(90)=4.2ms    p(95)=5.63ms
      { expected_response:true }...: avg=3.68ms   min=1.16ms   med=2.69ms   max=333.35ms p(90)=4.2ms    p(95)=5.63ms
    http_req_failed................: 0.00% 0 out of 51386
    http_reqs......................: 51386 427.801451/s

    EXECUTION
    iteration_duration.............: avg=104.58ms min=101.44ms med=103.45ms max=434.45ms p(90)=105.05ms p(95)=106.66ms
    iterations.....................: 51385 427.793126/s
    vus............................: 1     min=0          max=99
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 101 kB/s
    data_sent......................: 11 MB 94 kB/s




running (2m00.1s), 000/100 VUs, 51385 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s
```

## Earlier failed runs and correction

- Initial `./mvnw test -q` failed: `Connection to localhost:5432 refused` because local PostgreSQL was stopped.
- After adding Testcontainers, `./mvnw test -q` failed: `Could not find a valid Docker environment`. Docker Desktop was started; the same test command then passed.
- First `k6 run scripts/load_test.js` failed during setup: `connect: connection refused` at `localhost:8080`. Compose services and Spring Boot were started.
- First live k6 run exhausted stock early: 10000 accepted; built-in `http_req_failed` showed 98.26% because it classified expected HTTP 409 responses as errors. Script now classifies HTTP 409 as expected and uses a fresh key prefix per run. Final run above passed both error-rate thresholds.

## Post-load queue drain and persistence

Commands run after k6 finished:

```bash
docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
docker exec flash_postgres psql -U postgres -d flashsale_db -tAc "select count(*) from orders where idempotency_key ~ '^k6-[0-9]{13}-[0-9]+-[0-9]+-[0-9]+$' and status='CONFIRMED'"
docker exec flash_redis redis-cli GET product:101:stock
```

Raw output:

```text
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
51385
48615
```

All 51385 orders accepted in final run were CONFIRMED after queue drain; 100000 - 51385 = 48615 Redis stock.

## Measured baseline for distributed/fault-tolerance work

All measurements below were executed in this session. Load generator and system share one laptop. Baseline revision: `fcc43e010a672b8a3c52a9bc7a6db381f11768b5`. Existing comment-only RabbitMQConfig diff was present before work, recorded in `bench/results/baseline/preexisting.diff`, and is excluded from commits. No source was edited before this baseline.

Commands: `JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test`; `docker compose up -d`; `docker update --memory 384m --memory-swap 384m flash_postgres flash_redis flash_rabbitmq`; `JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw package -DskipTests`; `JAVA_TOOL_OPTIONS=-Xmx256m java -jar target/flash-sale-engine-0.0.1-SNAPSHOT.jar`; `python3 bench/baseline.py`. The existing `scripts/load_test.js` was unchanged. Every run uses clean orders and Redis state, then waits for ready/unacknowledged queues to reach zero.

Raw environment:

```text
2026-10-09T07:37:37+00:00
fcc43e010a672b8a3c52a9bc7a6db381f11768b5
 M src/main/java/com/flashsale/engine/config/RabbitMQConfig.java
?? bench/
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
Mem:      5157515264  1369636864  2064973824    53964800  1946628096  3787878400
Swap:     4294967296           0  4294967296
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
Docker Compose version v5.5.1
k6 v2.2.0 (commit/00a9a1b7f5, go1.26.5, linux/amd64)
```

Host app has capped heap but no container memory limit. Raw JVM flags/RSS: [jvm.txt](bench/results/baseline/jvm.txt). Dependencies have memory limits; [limits.txt](bench/results/baseline/limits.txt). Live stats started during the first run; sampling coverage is partial there: [live-stats.log](bench/results/baseline/live-stats.log). Initial Docker-unavailable attempt and recovery: [raw failure](bench/results/baseline/docker-initial-failure.txt). No benchmark was run with Docker unavailable.

Executed test summary:

```text
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 30.27 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

| Run | HTTP 202 | req/s | p50 ms | p95 ms | p99 ms | error rate | CONFIRMED | Redis stock |
|---|---|---|---|---|---|---|---|---|
| run-1 | 52079 | 433.86762161230314 | 2.273946 | 5.323319649999997 | 7.7905503099999995 | 0 | 52079 | 47921 |
| run-2 | 52168 | 434.51179425973214 | 2.133507 | 4.724691 | 7.137312359999998 | 0 | 52168 | 47832 |
| run-3 | 52180 | 434.630519918471 | 2.167679 | 4.307293 | 6.181404799999997 | 0 | 52180 | 47820 |

### run-1 raw output

```text
  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=7.79ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 52079 433.859291/s
    unexpected_responses...........: 0.00% 0 out of 52079

    HTTP
    http_req_duration..............: avg=2.64ms   min=751.43µs med=2.27ms   max=506.89ms p(90)=4.38ms   p(95)=5.32ms   p(99)=7.79ms  
      { expected_response:true }...: avg=2.64ms   min=751.43µs med=2.27ms   max=506.89ms p(90)=4.38ms   p(95)=5.32ms   p(99)=7.79ms  
    http_req_failed................: 0.00% 0 out of 52080
    http_reqs......................: 52080 433.867622/s

    EXECUTION
    iteration_duration.............: avg=103.19ms min=100.89ms med=102.85ms max=610.03ms p(90)=105.05ms p(95)=106.02ms p(99)=108.56ms
    iterations.....................: 52079 433.859291/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 103 kB/s
    data_sent......................: 12 MB 96 kB/s




running (2m00.0s), 000/100 VUs, 52079 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s

exit_code=0
```

```text
$ docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0

exit_code=0
$ docker exec flash_postgres psql -U postgres -d flashsale_db -tAc select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'
52079|52079|52079

exit_code=0
$ docker exec flash_redis redis-cli GET product:101:stock
47921

exit_code=0
```

### run-2 raw output

```text
  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=7.13ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 52168 434.503465/s
    unexpected_responses...........: 0.00% 0 out of 52168

    HTTP
    http_req_duration..............: avg=2.44ms   min=764.55µs med=2.13ms   max=57.36ms  p(90)=3.77ms   p(95)=4.72ms   p(99)=7.13ms  
      { expected_response:true }...: avg=2.44ms   min=764.55µs med=2.13ms   max=57.36ms  p(90)=3.77ms   p(95)=4.72ms   p(99)=7.13ms  
    http_req_failed................: 0.00% 0 out of 52169
    http_reqs......................: 52169 434.511794/s

    EXECUTION
    iteration_duration.............: avg=103.01ms min=100.94ms med=102.75ms max=157.78ms p(90)=104.39ms p(95)=105.29ms p(99)=107.69ms
    iterations.....................: 52168 434.503465/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 103 kB/s
    data_sent......................: 12 MB 96 kB/s




running (2m00.1s), 000/100 VUs, 52168 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s

exit_code=0
```

```text
$ docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0

exit_code=0
$ docker exec flash_postgres psql -U postgres -d flashsale_db -tAc select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'
52168|52168|52168

exit_code=0
$ docker exec flash_redis redis-cli GET product:101:stock
47832

exit_code=0
```

### run-3 raw output

```text
  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=6.18ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 52180 434.622191/s
    unexpected_responses...........: 0.00% 0 out of 52180

    HTTP
    http_req_duration..............: avg=2.39ms   min=727.79µs med=2.16ms  max=13.36ms  p(90)=3.57ms  p(95)=4.3ms   p(99)=6.18ms  
      { expected_response:true }...: avg=2.39ms   min=727.79µs med=2.16ms  max=13.36ms  p(90)=3.57ms  p(95)=4.3ms   p(99)=6.18ms  
    http_req_failed................: 0.00% 0 out of 52181
    http_reqs......................: 52181 434.63052/s

    EXECUTION
    iteration_duration.............: avg=102.98ms min=100.97ms med=102.8ms max=128.81ms p(90)=104.2ms p(95)=104.9ms p(99)=106.84ms
    iterations.....................: 52180 434.622191/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 103 kB/s
    data_sent......................: 12 MB 96 kB/s




running (2m00.1s), 000/100 VUs, 52180 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s

exit_code=0
```

```text
$ docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0

exit_code=0
$ docker exec flash_postgres psql -U postgres -d flashsale_db -tAc select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'
52180|52180|52180

exit_code=0
$ docker exec flash_redis redis-cli GET product:101:stock
47820

exit_code=0
```

All baseline accepted counts equal CONFIRMED counts; stock conservation holds. Duplicate-key counts are in each raw Postgres output. OOMKilled is false in captured Docker inspect data; captured dependency cgroup snapshots show no OOM kills or CPU throttling. Host app JVM RSS and WSL memory are recorded; host/JVM throttling was not instrumented. Full raw output, including test shutdown warnings: [baseline directory](bench/results/baseline/). Existing RESULTS.md sections above are historical and are not evidence for this task.

## Multi-replica deployment and HTTP concurrency

Executed `JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test` after adding deployment, then again after capping Testcontainers. Original assertions unchanged. Both executions passed; full logs in [replicas](bench/results/replicas/).

```text
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 36.84 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Executed `docker compose --profile distributed up -d --build --scale app=2` and `python3 bench/http_concurrency.py`. This script releases concurrent tasks against nginx, records each HTTP status/upstream, waits for persistence and queue drain, asserts accepted/CONFIRMED/distinct-key counts and remaining stock.

Initial failed burst, with proxy connect timeout before tuning:

```text
{'requests': 2000, 'initial_stock': 100, 'statuses': {409: 1859, 202: 100, 502: 41}, 'postgres': '100|100|100', 'redis_stock': 0}
Traceback (most recent call last):
  File "/home/ayussh/projects/flash-sale-engine/bench/http_concurrency.py", line 37, in <module>
    assert set(counts)<=({200,202} if same else {202,409}),summary
           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
AssertionError: {'requests': 2000, 'initial_stock': 100, 'statuses': {409: 1859, 202: 100, 502: 41}, 'postgres': '100|100|100', 'redis_stock': 0}
exit_code=1
```

nginx logged `upstream timed out (110: Operation timed out) while connecting to upstream`; full [nginx output](bench/results/replicas/nginx-http.log). Increased connect timeout, retaining all HTTP assertions. Successful attempt without explicit queue/upstream assertions remains in [http-attempt-2](bench/results/replicas/http-attempt-2/). Final run with those assertions:

```text
{'requests': 2000, 'initial_stock': 100, 'statuses': {202: 100, 409: 1900}, 'upstreams': {'172.18.0.5:8080': 1000, '172.18.0.6:8080': 1000}, 'postgres': '100|100|100', 'redis_stock': 0}
{'requests': 40, 'initial_stock': 10, 'statuses': {200: 39, 202: 1}, 'upstreams': {'172.18.0.6:8080': 20, '172.18.0.5:8080': 20}, 'postgres': '1|1|1', 'redis_stock': 9}
HTTP assertions passed
exit_code=0
```

Per-response evidence and queue snapshots: [HTTP raw results](bench/results/replicas/http/). Docker image IDs and in-container Java version: [images](bench/results/replicas/images.json), [Java](bench/results/replicas/java.txt). Host baseline Java and image Java are different distributions; use the replicated runs for matched scaling/confirm comparisons rather than treating host baseline as a controlled container comparison. Container resource limits and app JVM heap are constant across replicated comparisons. Testcontainers memory caps were added after baseline; baseline tests used their existing uncapped infrastructure containers.

## Phase A: replicated k6 ramps

Each row is an executed run. Error rate is k6 HTTP failure fraction; expected sold-out/idempotent statuses do not count as failures. Latencies are milliseconds, from full-precision k6 exports. Threshold failures remain in the table and raw files.

| Run | req/s | p50 | p95 | p99 | errors | HTTP 202 | CONFIRMED | stock | oversold | lost | duplicates | stuck | k6 exit |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| r1-run-1 | 429.93907653071693 | 3.3375595000000002 | 6.189688299999999 | 8.68396245 | 0 | 51593 | 51593 | 48407 | 0 | 0 | 0 | 0 | 0 |
| r1-run-2 | 430.3156403469725 | 3.217983 | 6.3168050000000004 | 8.7737525 | 0 | 51650 | 51650 | 48350 | 0 | 0 | 0 | 0 | 0 |
| r1-run-3 | 429.94684061574856 | 3.322792 | 6.1541383 | 8.281102059999998 | 0 | 51618 | 51618 | 48382 | 0 | 0 | 0 | 0 | 0 |
| r2-run-1 | 426.9330053557005 | 3.4913435 | 6.5029985 | 8.9240443 | 0 | 51495 | 51495 | 48505 | 0 | 0 | 0 | 0 | 0 |
| r2-run-2 | 429.1972420560015 | 3.506063 | 6.3572945 | 8.33397190000001 | 0 | 51510 | 51510 | 48490 | 0 | 0 | 0 | 0 | 0 |
| r2-run-3 | 428.9298256551053 | 3.540799 | 6.5604379999999995 | 8.474353880000013 | 0 | 51488 | 51488 | 48512 | 0 | 0 | 0 | 0 | 0 |
| r3-run-1 | 425.05837109113247 | 3.6753715 | 7.005737949999995 | 10.016573759999968 | 0 | 51337 | 51337 | 48663 | 0 | 0 | 0 | 0 | 0 |
| r3-run-2 | 428.9040413870723 | 3.3403215 | 6.514284549999994 | 10.650055580000007 | 0 | 51481 | 51481 | 48519 | 0 | 0 | 0 | 0 | 0 |
| r3-run-3 | 428.40369484545056 | 3.4626695 | 6.687611899999997 | 9.88536535 | 0 | 51431 | 51431 | 48569 | 0 | 0 | 0 | 0 | 0 |

Maximum observed per-container snapshots (Docker display precision; not guaranteed peaks), generated by executed `python3 bench/summarize.py bench/results/replicas`:

```text
{"run": "r1-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "23.30%", "max_observed_memory": "13.24MiB / 64MiB"}
{"run": "r1-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "101.82%", "max_observed_memory": "447.2MiB / 640MiB"}
{"run": "r1-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "139.56%", "max_observed_memory": "267.2MiB / 384MiB"}
{"run": "r1-run-1", "container": "flash_redis", "max_observed_cpu": "7.29%", "max_observed_memory": "23.53MiB / 384MiB"}
{"run": "r1-run-1", "container": "flash_postgres", "max_observed_cpu": "39.45%", "max_observed_memory": "65.7MiB / 384MiB"}
{"run": "r1-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.99%", "max_observed_memory": "13.34MiB / 64MiB"}
{"run": "r1-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "82.60%", "max_observed_memory": "447.2MiB / 640MiB"}
{"run": "r1-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "222.58%", "max_observed_memory": "217.3MiB / 384MiB"}
{"run": "r1-run-2", "container": "flash_redis", "max_observed_cpu": "9.40%", "max_observed_memory": "22.85MiB / 384MiB"}
{"run": "r1-run-2", "container": "flash_postgres", "max_observed_cpu": "37.82%", "max_observed_memory": "68.53MiB / 384MiB"}
{"run": "r1-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.76%", "max_observed_memory": "13.43MiB / 64MiB"}
{"run": "r1-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "81.69%", "max_observed_memory": "447.6MiB / 640MiB"}
{"run": "r1-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "192.05%", "max_observed_memory": "270.8MiB / 384MiB"}
{"run": "r1-run-3", "container": "flash_redis", "max_observed_cpu": "6.96%", "max_observed_memory": "23.8MiB / 384MiB"}
{"run": "r1-run-3", "container": "flash_postgres", "max_observed_cpu": "42.46%", "max_observed_memory": "69.36MiB / 384MiB"}
{"run": "r2-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "146.29%", "max_observed_memory": "311.9MiB / 640MiB"}
{"run": "r2-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "22.09%", "max_observed_memory": "14.45MiB / 64MiB"}
{"run": "r2-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "55.67%", "max_observed_memory": "447.5MiB / 640MiB"}
{"run": "r2-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "203.34%", "max_observed_memory": "269.8MiB / 384MiB"}
{"run": "r2-run-1", "container": "flash_redis", "max_observed_cpu": "6.37%", "max_observed_memory": "22.87MiB / 384MiB"}
{"run": "r2-run-1", "container": "flash_postgres", "max_observed_cpu": "50.11%", "max_observed_memory": "88.84MiB / 384MiB"}
{"run": "r2-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "59.42%", "max_observed_memory": "318MiB / 640MiB"}
{"run": "r2-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "23.77%", "max_observed_memory": "13.79MiB / 64MiB"}
{"run": "r2-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "57.00%", "max_observed_memory": "447.8MiB / 640MiB"}
{"run": "r2-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "186.77%", "max_observed_memory": "261.9MiB / 384MiB"}
{"run": "r2-run-2", "container": "flash_redis", "max_observed_cpu": "7.05%", "max_observed_memory": "22.42MiB / 384MiB"}
{"run": "r2-run-2", "container": "flash_postgres", "max_observed_cpu": "52.93%", "max_observed_memory": "89.41MiB / 384MiB"}
{"run": "r2-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "65.08%", "max_observed_memory": "317.6MiB / 640MiB"}
{"run": "r2-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "26.10%", "max_observed_memory": "13.71MiB / 64MiB"}
{"run": "r2-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "67.40%", "max_observed_memory": "447.6MiB / 640MiB"}
{"run": "r2-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "178.67%", "max_observed_memory": "253.6MiB / 384MiB"}
{"run": "r2-run-3", "container": "flash_redis", "max_observed_cpu": "8.73%", "max_observed_memory": "22.34MiB / 384MiB"}
{"run": "r2-run-3", "container": "flash_postgres", "max_observed_cpu": "73.22%", "max_observed_memory": "89.07MiB / 384MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-app-3", "max_observed_cpu": "126.97%", "max_observed_memory": "314.1MiB / 640MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "48.93%", "max_observed_memory": "318.3MiB / 640MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "26.98%", "max_observed_memory": "13.55MiB / 64MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "51.29%", "max_observed_memory": "446MiB / 640MiB"}
{"run": "r3-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "215.84%", "max_observed_memory": "256.6MiB / 384MiB"}
{"run": "r3-run-1", "container": "flash_redis", "max_observed_cpu": "13.07%", "max_observed_memory": "23.74MiB / 384MiB"}
{"run": "r3-run-1", "container": "flash_postgres", "max_observed_cpu": "98.20%", "max_observed_memory": "106.2MiB / 384MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-app-3", "max_observed_cpu": "58.90%", "max_observed_memory": "318.2MiB / 640MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "52.90%", "max_observed_memory": "319.2MiB / 640MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "29.73%", "max_observed_memory": "12.04MiB / 64MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "53.94%", "max_observed_memory": "446.2MiB / 640MiB"}
{"run": "r3-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "210.31%", "max_observed_memory": "267.7MiB / 384MiB"}
{"run": "r3-run-2", "container": "flash_redis", "max_observed_cpu": "10.58%", "max_observed_memory": "23.16MiB / 384MiB"}
{"run": "r3-run-2", "container": "flash_postgres", "max_observed_cpu": "57.05%", "max_observed_memory": "109.1MiB / 384MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-app-3", "max_observed_cpu": "47.39%", "max_observed_memory": "318.7MiB / 640MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "46.07%", "max_observed_memory": "319.5MiB / 640MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "27.65%", "max_observed_memory": "12.57MiB / 64MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "47.27%", "max_observed_memory": "445.9MiB / 640MiB"}
{"run": "r3-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "202.54%", "max_observed_memory": "262.6MiB / 384MiB"}
{"run": "r3-run-3", "container": "flash_redis", "max_observed_cpu": "8.53%", "max_observed_memory": "22.98MiB / 384MiB"}
{"run": "r3-run-3", "container": "flash_postgres", "max_observed_cpu": "55.82%", "max_observed_memory": "113.6MiB / 384MiB"}
```

### r1-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r1-run-1](bench/results/replicas/r1-run-1/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.68ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51593 429.930743/s
    unexpected_responses...........: 0.00% 0 out of 51593

    HTTP
    http_req_duration..............: avg=3.59ms   min=937.83µs med=3.33ms   max=15.94ms  p(90)=5.33ms   p(95)=6.18ms   p(99)=8.68ms  
      { expected_response:true }...: avg=3.59ms   min=937.83µs med=3.33ms   max=15.94ms  p(90)=5.33ms   p(95)=6.18ms   p(99)=8.68ms  
    http_req_failed................: 0.00% 0 out of 51594
    http_reqs......................: 51594 429.939077/s

    EXECUTION
    iteration_duration.............: avg=104.16ms min=101.19ms med=103.92ms max=138.68ms p(90)=105.96ms p(95)=106.78ms p(99)=109.19ms
    iterations.....................: 51593 429.930743/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51593,
  "confirmed": 51593,
  "redis_stock": 48407,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r1-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r1-run-2](bench/results/replicas/r1-run-2/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.77ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51650 430.307309/s
    unexpected_responses...........: 0.00% 0 out of 51650

    HTTP
    http_req_duration..............: avg=3.5ms    min=999.35µs med=3.21ms   max=14.63ms  p(90)=5.3ms    p(95)=6.31ms   p(99)=8.77ms  
      { expected_response:true }...: avg=3.5ms    min=999.35µs med=3.21ms   max=14.63ms  p(90)=5.3ms    p(95)=6.31ms   p(99)=8.77ms  
    http_req_failed................: 0.00% 0 out of 51651
    http_reqs......................: 51651 430.31564/s

    EXECUTION
    iteration_duration.............: avg=104.04ms min=101.21ms med=103.78ms max=119.71ms p(90)=105.88ms p(95)=106.87ms p(99)=109.32ms
    iterations.....................: 51650 430.307309/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51650,
  "confirmed": 51650,
  "redis_stock": 48350,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r1-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r1-run-3](bench/results/replicas/r1-run-3/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.28ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51618 429.938511/s
    unexpected_responses...........: 0.00% 0 out of 51618

    HTTP
    http_req_duration..............: avg=3.55ms   min=931.62µs med=3.32ms  max=13ms     p(90)=5.3ms    p(95)=6.15ms   p(99)=8.28ms  
      { expected_response:true }...: avg=3.55ms   min=931.62µs med=3.32ms  max=13ms     p(90)=5.3ms    p(95)=6.15ms   p(99)=8.28ms  
    http_req_failed................: 0.00% 0 out of 51619
    http_reqs......................: 51619 429.946841/s

    EXECUTION
    iteration_duration.............: avg=104.11ms min=101.11ms med=103.9ms max=124.75ms p(90)=105.84ms p(95)=106.71ms p(99)=108.91ms
    iterations.....................: 51618 429.938511/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51618,
  "confirmed": 51618,
  "redis_stock": 48382,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r2-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r2-run-1](bench/results/replicas/r2-run-1/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.92ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51495 426.924715/s
    unexpected_responses...........: 0.00% 0 out of 51495

    HTTP
    http_req_duration..............: avg=3.79ms   min=1.14ms   med=3.49ms   max=521.21ms p(90)=5.59ms   p(95)=6.5ms    p(99)=8.92ms  
      { expected_response:true }...: avg=3.79ms   min=1.14ms   med=3.49ms   max=521.21ms p(90)=5.59ms   p(95)=6.5ms    p(99)=8.92ms  
    http_req_failed................: 0.00% 0 out of 51496
    http_reqs......................: 51496 426.933005/s

    EXECUTION
    iteration_duration.............: avg=104.36ms min=101.45ms med=104.07ms max=606.18ms p(90)=106.21ms p(95)=107.15ms p(99)=109.51ms
    iterations.....................: 51495 426.924715/s
    vus............................: 3     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 132 kB/s
    data_sent......................: 11 MB 89 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51495,
  "confirmed": 51495,
  "redis_stock": 48505,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r2-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r2-run-2](bench/results/replicas/r2-run-2/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.33ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51510 429.18891/s
    unexpected_responses...........: 0.00% 0 out of 51510

    HTTP
    http_req_duration..............: avg=3.75ms   min=1.14ms   med=3.5ms    max=89.91ms  p(90)=5.48ms   p(95)=6.35ms   p(99)=8.33ms 
      { expected_response:true }...: avg=3.75ms   min=1.14ms   med=3.5ms    max=89.91ms  p(90)=5.48ms   p(95)=6.35ms   p(99)=8.33ms 
    http_req_failed................: 0.00% 0 out of 51511
    http_reqs......................: 51511 429.197242/s

    EXECUTION
    iteration_duration.............: avg=104.32ms min=101.44ms med=104.09ms max=190.91ms p(90)=106.08ms p(95)=106.93ms p(99)=108.9ms
    iterations.....................: 51510 429.18891/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51510,
  "confirmed": 51510,
  "redis_stock": 48490,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r2-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r2-run-3](bench/results/replicas/r2-run-3/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.47ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51488 428.921495/s
    unexpected_responses...........: 0.00% 0 out of 51488

    HTTP
    http_req_duration..............: avg=3.8ms    min=1.15ms  med=3.54ms   max=13.68ms p(90)=5.65ms   p(95)=6.56ms   p(99)=8.47ms  
      { expected_response:true }...: avg=3.8ms    min=1.15ms  med=3.54ms   max=13.68ms p(90)=5.65ms   p(95)=6.56ms   p(99)=8.47ms  
    http_req_failed................: 0.00% 0 out of 51489
    http_reqs......................: 51489 428.929826/s

    EXECUTION
    iteration_duration.............: avg=104.37ms min=101.3ms med=104.11ms max=132.4ms p(90)=106.24ms p(95)=107.13ms p(99)=109.08ms
    iterations.....................: 51488 428.921495/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51488,
  "confirmed": 51488,
  "redis_stock": 48512,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r3-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r3-run-1](bench/results/replicas/r3-run-1/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.01ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51337 425.050091/s
    unexpected_responses...........: 0.00% 0 out of 51337

    HTTP
    http_req_duration..............: avg=4.06ms   min=59.36µs  med=3.67ms   max=798.27ms p(90)=5.88ms   p(95)=7ms      p(99)=10.01ms 
      { expected_response:true }...: avg=4.06ms   min=59.36µs  med=3.67ms   max=798.27ms p(90)=5.88ms   p(95)=7ms      p(99)=10.01ms 
    http_req_failed................: 0.00% 0 out of 51338
    http_reqs......................: 51338 425.058371/s

    EXECUTION
    iteration_duration.............: avg=104.67ms min=101.57ms med=104.29ms max=899.25ms p(90)=106.57ms p(95)=107.65ms p(99)=110.66ms
    iterations.....................: 51337 425.050091/s
    vus............................: 3     min=0          max=100
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 132 kB/s
    data_sent......................: 11 MB 89 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51337,
  "confirmed": 51337,
  "redis_stock": 48663,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r3-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r3-run-2](bench/results/replicas/r3-run-2/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.65ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51481 428.89571/s
    unexpected_responses...........: 0.00% 0 out of 51481

    HTTP
    http_req_duration..............: avg=3.78ms   min=1.3ms    med=3.34ms   max=591.28ms p(90)=5.45ms   p(95)=6.51ms   p(99)=10.65ms 
      { expected_response:true }...: avg=3.78ms   min=1.3ms    med=3.34ms   max=591.28ms p(90)=5.45ms   p(95)=6.51ms   p(99)=10.65ms 
    http_req_failed................: 0.00% 0 out of 51482
    http_reqs......................: 51482 428.904041/s

    EXECUTION
    iteration_duration.............: avg=104.39ms min=101.62ms med=103.96ms max=691.65ms p(90)=106.09ms p(95)=107.13ms p(99)=111.47ms
    iterations.....................: 51481 428.89571/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51481,
  "confirmed": 51481,
  "redis_stock": 48519,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r3-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r3-run-3](bench/results/replicas/r3-run-3/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=9.88ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51431 428.395365/s
    unexpected_responses...........: 0.00% 0 out of 51431

    HTTP
    http_req_duration..............: avg=3.88ms   min=1.15ms   med=3.46ms   max=63.94ms  p(90)=5.66ms   p(95)=6.68ms   p(99)=9.88ms 
      { expected_response:true }...: avg=3.88ms   min=1.15ms   med=3.46ms   max=63.94ms  p(90)=5.66ms   p(95)=6.68ms   p(99)=9.88ms 
    http_req_failed................: 0.00% 0 out of 51432
    http_reqs......................: 51432 428.403695/s

    EXECUTION
    iteration_duration.............: avg=104.49ms min=101.51ms med=104.09ms max=164.94ms p(90)=106.26ms p(95)=107.29ms p(99)=110.4ms
    iterations.....................: 51431 428.395365/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 89 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51431,
  "confirmed": 51431,
  "redis_stock": 48569,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```


OOMKilled flags from executed Docker inspect:

```text
('r1-run-1', '/flash-sale-engine-app-1', False)
('r1-run-1', '/flash_postgres', False)
('r1-run-1', '/flash_redis', False)
('r1-run-1', '/flash_rabbitmq', False)
('r1-run-2', '/flash-sale-engine-app-1', False)
('r1-run-2', '/flash_postgres', False)
('r1-run-2', '/flash_redis', False)
('r1-run-2', '/flash_rabbitmq', False)
('r1-run-3', '/flash-sale-engine-app-1', False)
('r1-run-3', '/flash_postgres', False)
('r1-run-3', '/flash_redis', False)
('r1-run-3', '/flash_rabbitmq', False)
('r2-run-1', '/flash-sale-engine-app-1', False)
('r2-run-1', '/flash-sale-engine-app-2', False)
('r2-run-1', '/flash_postgres', False)
('r2-run-1', '/flash_redis', False)
('r2-run-1', '/flash_rabbitmq', False)
('r2-run-2', '/flash-sale-engine-app-1', False)
('r2-run-2', '/flash-sale-engine-app-2', False)
('r2-run-2', '/flash_postgres', False)
('r2-run-2', '/flash_redis', False)
('r2-run-2', '/flash_rabbitmq', False)
('r2-run-3', '/flash-sale-engine-app-1', False)
('r2-run-3', '/flash-sale-engine-app-2', False)
('r2-run-3', '/flash_postgres', False)
('r2-run-3', '/flash_redis', False)
('r2-run-3', '/flash_rabbitmq', False)
('r3-run-1', '/flash-sale-engine-app-1', False)
('r3-run-1', '/flash-sale-engine-app-2', False)
('r3-run-1', '/flash-sale-engine-app-3', False)
('r3-run-1', '/flash_postgres', False)
('r3-run-1', '/flash_redis', False)
('r3-run-1', '/flash_rabbitmq', False)
('r3-run-2', '/flash-sale-engine-app-1', False)
('r3-run-2', '/flash-sale-engine-app-2', False)
('r3-run-2', '/flash-sale-engine-app-3', False)
('r3-run-2', '/flash_postgres', False)
('r3-run-2', '/flash_redis', False)
('r3-run-2', '/flash_rabbitmq', False)
('r3-run-3', '/flash-sale-engine-app-1', False)
('r3-run-3', '/flash-sale-engine-app-2', False)
('r3-run-3', '/flash-sale-engine-app-3', False)
('r3-run-3', '/flash_postgres', False)
('r3-run-3', '/flash_redis', False)
('r3-run-3', '/flash_rabbitmq', False)
```

CPU throttling and memory OOM counters (raw before/after cgroup snapshots):

```text
('r1-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-0.txt', 'oom 0')
('r1-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-0.txt', 'oom 0')
('r1-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('r1-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-2.txt', 'oom 0')
('r1-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('r1-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-1.txt', 'oom 0')
('r1-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('r1-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-3.txt', 'oom 0')
('r1-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-2.txt', 'oom 0')
('r1-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-1.txt', 'oom 0')
('r1-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-3.txt', 'oom 0')
('r1-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-0.txt', 'oom 0')
('r1-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-0.txt', 'oom 0')
('r1-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-2.txt', 'oom 0')
('r1-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-1.txt', 'oom 0')
('r1-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-3.txt', 'oom 0')
('r1-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-2.txt', 'oom 0')
('r1-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-1.txt', 'oom 0')
('r1-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-3.txt', 'oom 0')
('r1-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-0.txt', 'oom 0')
('r1-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-0.txt', 'oom 0')
('r1-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-2.txt', 'oom 0')
('r1-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-1.txt', 'oom 0')
('r1-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-3.txt', 'oom 0')
('r1-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-2.txt', 'oom 0')
('r1-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-1.txt', 'oom 0')
('r1-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-3.txt', 'oom 0')
('r1-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-0.txt', 'oom 0')
('r2-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-0.txt', 'oom 0')
('r2-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-2.txt', 'oom 0')
('r2-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-1.txt', 'oom 0')
('r2-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-4.txt', 'oom 0')
('r2-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-3.txt', 'oom 0')
('r2-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-2.txt', 'oom 0')
('r2-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-4.txt', 'oom 0')
('r2-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-1.txt', 'oom 0')
('r2-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-3.txt', 'oom 0')
('r2-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-0.txt', 'oom 0')
('r2-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-0.txt', 'oom 0')
('r2-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-2.txt', 'oom 0')
('r2-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-1.txt', 'oom 0')
('r2-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-4.txt', 'oom 0')
('r2-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-3.txt', 'oom 0')
('r2-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-2.txt', 'oom 0')
('r2-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-4.txt', 'oom 0')
('r2-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-1.txt', 'oom 0')
('r2-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-3.txt', 'oom 0')
('r2-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-0.txt', 'oom 0')
('r2-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-0.txt', 'oom 0')
('r2-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-2.txt', 'oom 0')
('r2-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-1.txt', 'oom 0')
('r2-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-4.txt', 'oom 0')
('r2-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-3.txt', 'oom 0')
('r2-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-2.txt', 'oom 0')
('r2-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-4.txt', 'oom 0')
('r2-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-1.txt', 'oom 0')
('r2-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-3.txt', 'oom 0')
('r2-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-0.txt', 'oom 0')
('r3-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-0.txt', 'oom 0')
('r3-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-2.txt', 'oom 0')
('r3-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-1.txt', 'oom 0')
('r3-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-4.txt', 'oom 0')
('r3-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-3.txt', 'oom 0')
('r3-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-2.txt', 'oom 0')
('r3-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-4.txt', 'oom 0')
('r3-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-1.txt', 'oom 0')
('r3-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-5.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-5.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-5.txt', 'oom 0')
('r3-run-1', 'after-cgroup-5.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-5.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-5.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-5.txt', 'oom 0')
('r3-run-1', 'before-cgroup-5.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-3.txt', 'oom 0')
('r3-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-0.txt', 'oom 0')
('r3-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-0.txt', 'oom 0')
('r3-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-2.txt', 'oom 0')
('r3-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-1.txt', 'oom 0')
('r3-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-4.txt', 'oom 0')
('r3-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-3.txt', 'oom 0')
('r3-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-2.txt', 'oom 0')
('r3-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-4.txt', 'oom 0')
('r3-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-1.txt', 'oom 0')
('r3-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-5.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-5.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-5.txt', 'oom 0')
('r3-run-2', 'after-cgroup-5.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-5.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-5.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-5.txt', 'oom 0')
('r3-run-2', 'before-cgroup-5.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-3.txt', 'oom 0')
('r3-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-0.txt', 'oom 0')
('r3-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-0.txt', 'oom 0')
('r3-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-2.txt', 'oom 0')
('r3-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-1.txt', 'oom 0')
('r3-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-4.txt', 'oom 0')
('r3-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-3.txt', 'oom 0')
('r3-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-2.txt', 'oom 0')
('r3-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-4.txt', 'oom 0')
('r3-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-1.txt', 'oom 0')
('r3-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-5.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-5.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-5.txt', 'oom 0')
('r3-run-3', 'after-cgroup-5.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-5.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-5.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-5.txt', 'oom 0')
('r3-run-3', 'before-cgroup-5.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-3.txt', 'oom 0')
('r3-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
```

Throughput did not scale in the requested ramp: every replicated multi-app run was below every single-app run. Executed range calculation:

```text
{1: [429.93907653071693, 430.3156403469725, 429.94684061574856], 2: [426.9330053557005, 429.1972420560015, 428.9298256551053], 3: [425.05837109113247, 428.9040413870723, 428.40369484545056]}
{1: (429.93907653071693, 430.3156403469725), 2: (426.9330053557005, 429.1972420560015), 3: (425.05837109113247, 428.9040413870723)}
```

The measured limiting factor for this closed-loop workload is offered-load pacing: bounded VUs sleep between requests; this is not an unpaced saturation test. nginx and extra replicas added latency rather than raising offered request rate. CPU/memory snapshots, empty queues and captured zero throttling/OOM counters do not establish saturation of app CPU, Redis, RabbitMQ, Postgres, or the laptop. We cannot honestly name one of those as a demonstrated capacity bottleneck from these runs. Server maximum capacity and the capacity bottleneck remain unmeasured. The laptop co-hosts k6, nginx, app replicas and infrastructure. No throughput scaling claim is made.

Redis runtime configuration differs from the assumption that it has no persistence at all:

```text
appendonly
no
save
3600 1 300 100 60 10000
dbfilename
dump.rdb
dir
/data
[{"Type":"volume","Name":"2c50c3d6439f56cc652d6f34838dc12c9794bd8cb4c23880572e5f98dd0261ae","Source":"/var/lib/docker/volumes/2c50c3d6439f56cc652d6f34838dc12c9794bd8cb4c23880572e5f98dd0261ae/_data","Destination":"/data","Driver":"local","Mode":"z","RW":true,"Propagation":""}]
```

Compose does not explicitly configure persistence, but this image enables RDB snapshots and mounts an anonymous data volume. A normal restart may preserve data; it is not a durable inventory design.

## Phase B: fault injection before confirms

Each row is an executed run. Error rate is k6 HTTP failure fraction; expected sold-out/idempotent statuses do not count as failures. Latencies are milliseconds, from full-precision k6 exports. Threshold failures remain in the table and raw files.

| Run | req/s | p50 | p95 | p99 | errors | HTTP 202 | CONFIRMED | stock | oversold | lost | duplicates | stuck | k6 exit |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| kill-run-1 | 476.2091285856997 | 3.524826 | 7.256963 | 12.6046405 | 0.0004364829946225295 | 57250 | 57250 | 42750 | 0 | 0 | 0 | 0 | 0 |
| kill-run-2 | 476.54047772893625 | 3.632466 | 6.939588249999999 | 9.814697580000008 | 0.00010480349344978167 | 57243 | 57243 | 42757 | 0 | 0 | 0 | 0 | 0 |
| kill-run-3 | 477.6585536292755 | 3.672739 | 6.724177799999999 | 9.805730940000005 | 0.00043594259507907996 | 57321 | 57321 | 42679 | 0 | 0 | 0 | 0 | 0 |
| rabbit-run-1 | 472.83416849317416 | 3.7358729999999998 | 6.839857099999998 | 10.29100013000001 | 0.08939265835270908 | 51696 | 51696 | 48304 | 0 | 0 | 0 | 0 | 99 |
| rabbit-run-2 | 473.086130663348 | 3.6203890000000003 | 7.1388950499999995 | 11.035664539999997 | 0.09335960868494211 | 51527 | 51527 | 48473 | 0 | 0 | 0 | 0 | 99 |
| rabbit-run-3 | 472.68307513661875 | 3.577152 | 6.933515 | 10.830669600000014 | 0.11742444244952562 | 50139 | 50139 | 49861 | 0 | 0 | 0 | 0 | 99 |
| redis-observation | 452.1143395571845 | 3.718795 | 7.716246799999997 | 14.586090159999985 | 0.0007369196757553427 | 54239 | 54239 | 45761 | 0 | 0 | 0 | 0 | 0 |

Maximum observed per-container snapshots (Docker display precision; not guaranteed peaks), generated by executed `python3 bench/summarize.py bench/results/faults-before`:

```text
{"run": "kill-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "59.75%", "max_observed_memory": "325.9MiB / 640MiB"}
{"run": "kill-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.57%", "max_observed_memory": "11.3MiB / 64MiB"}
{"run": "kill-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "269.93%", "max_observed_memory": "419MiB / 640MiB"}
{"run": "kill-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "212.61%", "max_observed_memory": "259.1MiB / 384MiB"}
{"run": "kill-run-1", "container": "flash_redis", "max_observed_cpu": "11.75%", "max_observed_memory": "22.49MiB / 384MiB"}
{"run": "kill-run-1", "container": "flash_postgres", "max_observed_cpu": "49.84%", "max_observed_memory": "91.47MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "59.91%", "max_observed_memory": "327.2MiB / 640MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.36%", "max_observed_memory": "11.67MiB / 64MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "309.39%", "max_observed_memory": "328.4MiB / 640MiB"}
{"run": "kill-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "250.22%", "max_observed_memory": "263.9MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash_redis", "max_observed_cpu": "12.16%", "max_observed_memory": "21.98MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash_postgres", "max_observed_cpu": "43.00%", "max_observed_memory": "93.3MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "53.75%", "max_observed_memory": "328.2MiB / 640MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.15%", "max_observed_memory": "11.7MiB / 64MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "213.12%", "max_observed_memory": "319.3MiB / 640MiB"}
{"run": "kill-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "222.31%", "max_observed_memory": "207.8MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash_redis", "max_observed_cpu": "5.88%", "max_observed_memory": "22.26MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash_postgres", "max_observed_cpu": "37.26%", "max_observed_memory": "94.05MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "47.23%", "max_observed_memory": "332.6MiB / 640MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "16.85%", "max_observed_memory": "11.63MiB / 64MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "66.79%", "max_observed_memory": "330.5MiB / 640MiB"}
{"run": "rabbit-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "217.68%", "max_observed_memory": "222.6MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash_redis", "max_observed_cpu": "6.18%", "max_observed_memory": "22MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash_postgres", "max_observed_cpu": "43.39%", "max_observed_memory": "95.15MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "81.26%", "max_observed_memory": "333.3MiB / 640MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "25.22%", "max_observed_memory": "11.39MiB / 64MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "119.87%", "max_observed_memory": "326.5MiB / 640MiB"}
{"run": "rabbit-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "237.14%", "max_observed_memory": "264.2MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash_redis", "max_observed_cpu": "6.51%", "max_observed_memory": "22.14MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash_postgres", "max_observed_cpu": "57.91%", "max_observed_memory": "95.27MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "68.14%", "max_observed_memory": "333.4MiB / 640MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "21.24%", "max_observed_memory": "11.13MiB / 64MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "60.50%", "max_observed_memory": "330.7MiB / 640MiB"}
{"run": "rabbit-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "259.17%", "max_observed_memory": "237MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash_redis", "max_observed_cpu": "10.96%", "max_observed_memory": "22.38MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash_postgres", "max_observed_cpu": "54.21%", "max_observed_memory": "95.03MiB / 384MiB"}
{"run": "redis-observation", "container": "flash-sale-engine-app-2", "max_observed_cpu": "230.40%", "max_observed_memory": "368.7MiB / 640MiB"}
{"run": "redis-observation", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "28.47%", "max_observed_memory": "11.5MiB / 64MiB"}
{"run": "redis-observation", "container": "flash-sale-engine-app-1", "max_observed_cpu": "205.14%", "max_observed_memory": "335.9MiB / 640MiB"}
{"run": "redis-observation", "container": "flash_rabbitmq", "max_observed_cpu": "276.22%", "max_observed_memory": "241MiB / 384MiB"}
{"run": "redis-observation", "container": "flash_redis", "max_observed_cpu": "23.22%", "max_observed_memory": "17.18MiB / 384MiB"}
{"run": "redis-observation", "container": "flash_postgres", "max_observed_cpu": "58.97%", "max_observed_memory": "94.77MiB / 384MiB"}
```

### kill-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-1](bench/results/faults-before/kill-run-1/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:12:38.318536+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:12:38.320914+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=12.6ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.04%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.04%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 57250 475.992957/s
    unexpected_responses...........: 0.04% 25 out of 57275

    HTTP
    http_req_duration..............: avg=4.15ms   min=45.74µs  med=3.52ms   max=790.42ms p(90)=5.86ms   p(95)=7.25ms   p(99)=12.6ms  
      { expected_response:true }...: avg=4.14ms   min=45.74µs  med=3.52ms   max=790.42ms p(90)=5.86ms   p(95)=7.23ms   p(99)=12.39ms 
    http_req_failed................: 0.04% 25 out of 57276
    http_reqs......................: 57276 476.209129/s

    EXECUTION
    iteration_duration.............: avg=104.78ms min=101.33ms med=104.16ms max=890.68ms p(90)=106.54ms p(95)=107.95ms p(99)=113.36ms
    iterations.....................: 57275 476.200814/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 18 MB 148 kB/s
    data_sent......................: 12 MB 100 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 57250,
  "confirmed": 57250,
  "redis_stock": 42750,
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

```text
$ docker start 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:13:56.363056+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:13:56.732754+00:00

```

```text
$ docker kill -s KILL 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:13:35.568957+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:13:36.360495+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### kill-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-2](bench/results/faults-before/kill-run-2/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:14:59.565769+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:14:59.582491+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=9.81ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.01%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.01%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 57243 476.482211/s
    unexpected_responses...........: 0.01% 6 out of 57249

    HTTP
    http_req_duration..............: avg=4.26ms   min=822.49µs med=3.63ms   max=686.18ms p(90)=5.93ms   p(95)=6.93ms  p(99)=9.81ms  
      { expected_response:true }...: avg=4.26ms   min=822.49µs med=3.63ms   max=686.18ms p(90)=5.93ms   p(95)=6.93ms  p(99)=9.81ms  
    http_req_failed................: 0.01% 6 out of 57250
    http_reqs......................: 57250 476.540478/s

    EXECUTION
    iteration_duration.............: avg=104.85ms min=101.33ms med=104.23ms max=786.87ms p(90)=106.58ms p(95)=107.6ms p(99)=110.47ms
    iterations.....................: 57249 476.532154/s
    vus............................: 50    min=50         max=50
    vus_max........................: 50    min=50         max=50

    NETWORK
    data_received..................: 18 MB 148 kB/s
    data_sent......................: 12 MB 100 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 57243,
  "confirmed": 57243,
  "redis_stock": 42757,
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

```text
$ docker start 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:16:06.091294+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:16:06.405701+00:00

```

```text
$ docker kill -s KILL 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:15:45.432426+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:15:46.092864+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### kill-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-3](bench/results/faults-before/kill-run-3/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:17:08.590877+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:17:08.600617+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=9.8ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.04%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.04%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 57321 477.441993/s
    unexpected_responses...........: 0.04% 25 out of 57346

    HTTP
    http_req_duration..............: avg=4.02ms   min=136.28µs med=3.67ms   max=648.01ms p(90)=5.78ms   p(95)=6.72ms   p(99)=9.8ms   
      { expected_response:true }...: avg=4.01ms   min=136.28µs med=3.67ms   max=648.01ms p(90)=5.78ms   p(95)=6.71ms   p(99)=9.68ms  
    http_req_failed................: 0.04% 25 out of 57347
    http_reqs......................: 57347 477.658554/s

    EXECUTION
    iteration_duration.............: avg=104.62ms min=101.26ms med=104.28ms max=748.85ms p(90)=106.44ms p(95)=107.38ms p(99)=110.41ms
    iterations.....................: 57346 477.650224/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 18 MB 148 kB/s
    data_sent......................: 12 MB 101 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 57321,
  "confirmed": 57321,
  "redis_stock": 42679,
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

```text
$ docker start 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:18:15.081348+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:18:15.396637+00:00

```

```text
$ docker kill -s KILL 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:17:54.320276+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:17:55.080601+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-1](bench/results/faults-before/rabbit-run-1/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:19:17.613928+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:19:17.620798+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.29ms

    http_req_failed
    ✗ 'rate<0.01' rate=8.93%

    unexpected_responses
    ✗ 'rate<0.01' rate=8.93%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51696 430.557937/s
    unexpected_responses...........: 8.93% 5075 out of 56771

    HTTP
    http_req_duration..............: avg=5.09ms   min=429.25µs med=3.73ms   max=1.02s    p(90)=5.87ms  p(95)=6.83ms   p(99)=10.29ms
      { expected_response:true }...: avg=4.03ms   min=429.25µs med=3.66ms   max=131.12ms p(90)=5.68ms  p(95)=6.54ms   p(99)=9.32ms 
    http_req_failed................: 8.93% 5075 out of 56772
    http_reqs......................: 56772 472.834168/s

    EXECUTION
    iteration_duration.............: avg=105.69ms min=101.44ms med=104.35ms max=1.12s    p(90)=106.5ms p(95)=107.44ms p(99)=110.9ms
    iterations.....................: 56771 472.82584/s
    vus............................: 50    min=50            max=50
    vus_max........................: 50    min=50            max=50

    NETWORK
    data_received..................: 18 MB 146 kB/s
    data_sent......................: 12 MB 101 kB/s



time="2026-10-09T08:21:18Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 51696,
  "confirmed": 51696,
  "redis_stock": 48304,
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

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:20:03.347793+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:20:05.980090+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-2](bench/results/faults-before/rabbit-run-2/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:21:27.740771+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:21:27.756408+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=11.03ms

    http_req_failed
    ✗ 'rate<0.01' rate=9.33%

    unexpected_responses
    ✗ 'rate<0.01' rate=9.33%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51527 428.910671/s
    unexpected_responses...........: 9.33% 5306 out of 56833

    HTTP
    http_req_duration..............: avg=4.94ms  min=1.22ms  med=3.62ms  max=1.01s   p(90)=5.94ms   p(95)=7.13ms   p(99)=11.03ms 
      { expected_response:true }...: avg=3.97ms  min=1.22ms  med=3.55ms  max=62.65ms p(90)=5.86ms   p(95)=7ms      p(99)=10.35ms 
    http_req_failed................: 9.33% 5306 out of 56834
    http_reqs......................: 56834 473.086131/s

    EXECUTION
    iteration_duration.............: avg=105.6ms min=101.5ms med=104.3ms max=1.11s   p(90)=106.65ms p(95)=107.83ms p(99)=111.75ms
    iterations.....................: 56833 473.077807/s
    vus............................: 50    min=50            max=50
    vus_max........................: 50    min=50            max=50

    NETWORK
    data_received..................: 18 MB 146 kB/s
    data_sent......................: 12 MB 101 kB/s



time="2026-10-09T08:23:29Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 51527,
  "confirmed": 51527,
  "redis_stock": 48473,
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

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:22:13.616559+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:22:16.119119+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-3](bench/results/faults-before/rabbit-run-3/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:23:37.794428+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:23:37.810342+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.83ms

    http_req_failed
    ✗ 'rate<0.01' rate=11.74%

    unexpected_responses
    ✗ 'rate<0.01' rate=11.74%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 50139  417.170208/s
    unexpected_responses...........: 11.74% 6671 out of 56810

    HTTP
    http_req_duration..............: avg=4.96ms   min=1.13ms   med=3.57ms   max=1.02s    p(90)=5.81ms   p(95)=6.93ms   p(99)=10.83ms 
      { expected_response:true }...: avg=3.89ms   min=1.13ms   med=3.5ms    max=118.53ms p(90)=5.52ms   p(95)=6.5ms    p(99)=9.37ms  
    http_req_failed................: 11.74% 6671 out of 56811
    http_reqs......................: 56811  472.683075/s

    EXECUTION
    iteration_duration.............: avg=105.65ms min=101.53ms med=104.27ms max=1.12s    p(90)=106.55ms p(95)=107.72ms p(99)=111.62ms
    iterations.....................: 56810  472.674755/s
    vus............................: 50     min=50            max=50
    vus_max........................: 50     min=50            max=50

    NETWORK
    data_received..................: 18 MB  146 kB/s
    data_sent......................: 12 MB  101 kB/s



time="2026-10-09T08:25:40Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 50139,
  "confirmed": 50139,
  "redis_stock": 49861,
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

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:24:24.148649+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:24:26.908162+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### redis-observation raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [redis-observation](bench/results/faults-before/redis-observation/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:25:48.262997+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:25:48.276588+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=14.58ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.07%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.07%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 54239 451.772838/s
    unexpected_responses...........: 0.07% 40 out of 54279

    HTTP
    http_req_duration..............: avg=9.85ms   min=1.16ms   med=3.71ms   max=6.14s p(90)=6.19ms   p(95)=7.71ms   p(99)=14.58ms 
      { expected_response:true }...: avg=6.98ms   min=1.16ms   med=3.71ms   max=3.21s p(90)=6.17ms   p(95)=7.67ms   p(99)=14.05ms 
    http_req_failed................: 0.07% 40 out of 54280
    http_reqs......................: 54280 452.11434/s

    EXECUTION
    iteration_duration.............: avg=110.53ms min=101.41ms med=104.41ms max=6.24s p(90)=106.92ms p(95)=108.53ms p(99)=115.67ms
    iterations.....................: 54279 452.10601/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 17 MB 140 kB/s
    data_sent......................: 12 MB 98 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 54239,
  "confirmed": 54239,
  "redis_stock": 45761,
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

```text
$ docker restart flash_redis
started=2026-10-09T08:26:34.111273+00:00
flash_redis

exit_code=0
ended=2026-10-09T08:26:36.911301+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```


OOMKilled flags from executed Docker inspect:

```text
('kill-run-1', '/flash-sale-engine-app-1', False)
('kill-run-1', '/flash-sale-engine-app-2', False)
('kill-run-1', '/flash_postgres', False)
('kill-run-1', '/flash_redis', False)
('kill-run-1', '/flash_rabbitmq', False)
('kill-run-2', '/flash-sale-engine-app-1', False)
('kill-run-2', '/flash-sale-engine-app-2', False)
('kill-run-2', '/flash_postgres', False)
('kill-run-2', '/flash_redis', False)
('kill-run-2', '/flash_rabbitmq', False)
('kill-run-3', '/flash-sale-engine-app-1', False)
('kill-run-3', '/flash-sale-engine-app-2', False)
('kill-run-3', '/flash_postgres', False)
('kill-run-3', '/flash_redis', False)
('kill-run-3', '/flash_rabbitmq', False)
('rabbit-run-1', '/flash-sale-engine-app-1', False)
('rabbit-run-1', '/flash-sale-engine-app-2', False)
('rabbit-run-1', '/flash_postgres', False)
('rabbit-run-1', '/flash_redis', False)
('rabbit-run-1', '/flash_rabbitmq', False)
('rabbit-run-2', '/flash-sale-engine-app-1', False)
('rabbit-run-2', '/flash-sale-engine-app-2', False)
('rabbit-run-2', '/flash_postgres', False)
('rabbit-run-2', '/flash_redis', False)
('rabbit-run-2', '/flash_rabbitmq', False)
('rabbit-run-3', '/flash-sale-engine-app-1', False)
('rabbit-run-3', '/flash-sale-engine-app-2', False)
('rabbit-run-3', '/flash_postgres', False)
('rabbit-run-3', '/flash_redis', False)
('rabbit-run-3', '/flash_rabbitmq', False)
('redis-observation', '/flash-sale-engine-app-1', False)
('redis-observation', '/flash-sale-engine-app-2', False)
('redis-observation', '/flash_postgres', False)
('redis-observation', '/flash_redis', False)
('redis-observation', '/flash_rabbitmq', False)
```

CPU throttling and memory OOM counters (raw before/after cgroup snapshots):

```text
('kill-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-0.txt', 'oom 0')
('kill-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-0.txt', 'oom 0')
('kill-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-2.txt', 'oom 0')
('kill-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-1.txt', 'oom 0')
('kill-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-4.txt', 'oom 0')
('kill-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-3.txt', 'oom 0')
('kill-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-2.txt', 'oom 0')
('kill-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-4.txt', 'oom 0')
('kill-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-1.txt', 'oom 0')
('kill-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-3.txt', 'oom 0')
('kill-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-0.txt', 'oom 0')
('kill-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-0.txt', 'oom 0')
('kill-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-2.txt', 'oom 0')
('kill-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-1.txt', 'oom 0')
('kill-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-4.txt', 'oom 0')
('kill-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-3.txt', 'oom 0')
('kill-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-2.txt', 'oom 0')
('kill-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-4.txt', 'oom 0')
('kill-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-1.txt', 'oom 0')
('kill-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-3.txt', 'oom 0')
('kill-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-0.txt', 'oom 0')
('kill-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-0.txt', 'oom 0')
('kill-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-2.txt', 'oom 0')
('kill-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-1.txt', 'oom 0')
('kill-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-4.txt', 'oom 0')
('kill-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-3.txt', 'oom 0')
('kill-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-2.txt', 'oom 0')
('kill-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-4.txt', 'oom 0')
('kill-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-1.txt', 'oom 0')
('kill-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-3.txt', 'oom 0')
('kill-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-0.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-0.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-0.txt', 'oom 0')
('redis-observation', 'before-cgroup-0.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-0.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-0.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-0.txt', 'oom 0')
('redis-observation', 'after-cgroup-0.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-2.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-2.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-2.txt', 'oom 0')
('redis-observation', 'before-cgroup-2.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-1.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-1.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-1.txt', 'oom 0')
('redis-observation', 'before-cgroup-1.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-4.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-4.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-4.txt', 'oom 0')
('redis-observation', 'after-cgroup-4.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-3.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-3.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-3.txt', 'oom 0')
('redis-observation', 'before-cgroup-3.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-2.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-2.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-2.txt', 'oom 0')
('redis-observation', 'after-cgroup-2.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-4.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-4.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-4.txt', 'oom 0')
('redis-observation', 'before-cgroup-4.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-1.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-1.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-1.txt', 'oom 0')
('redis-observation', 'after-cgroup-1.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-3.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-3.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-3.txt', 'oom 0')
('redis-observation', 'after-cgroup-3.txt', 'oom_kill 0')
```

Before confirms, every executed app-kill and broker-restart run had zero lost orders, stuck reservations, duplicates and oversold orders after drain. This is a null result: these runs did not reproduce accepted-order loss. Broker restart breached HTTP error thresholds in every run; failures remain in the results. SIGKILL produced some HTTP failures despite successful reconciliation.

The Redis row is observation only, not a Redis-loss/HA assertion. Normal restart retained inventory and reservation data. Executed Redis logs show graceful shutdown saved a final RDB, then reloaded it:

```text
1:M 09 Oct 2026 07:47:25.137 * Loading RDB produced by version 7.4.11
1:M 09 Oct 2026 07:47:25.266 * Done loading RDB, keys loaded: 52181, keys expired: 0.
1:M 09 Oct 2026 07:47:25.266 * DB loaded from disk: 0.130 seconds
1:signal-handler (1791534394) Received SIGTERM scheduling shutdown...
1:M 09 Oct 2026 08:26:34.441 * User requested shutdown...
1:M 09 Oct 2026 08:26:34.442 * Saving the final RDB snapshot before exiting.
1:M 09 Oct 2026 08:26:37.068 * Loading RDB produced by version 7.4.11
1:M 09 Oct 2026 08:26:37.125 * Done loading RDB, keys loaded: 21496, keys expired: 0.
1:M 09 Oct 2026 08:26:37.125 * DB loaded from disk: 0.060 seconds
```

Known limitation: Redis inventory and idempotency state are not coordinated with Postgres or broker transactions. No explicit named persistence volume/AOF/recovery policy is configured; normal restart with the same anonymous volume and graceful RDB save is not evidence of safety under hard loss, missing volume, or stale snapshots. Redis loss is not a pass/fail test in this campaign. Full logs: [redis-full.log](bench/results/faults-before/redis-full.log).

## Publisher-confirm implementation validation

`JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test` was executed after the change. Original assertions are retained; added cases simulate a broker nack, remove an actual broker binding to force a mandatory return, and suppress a confirmation to force timeout. Each checks HTTP 503, restored stock, released key and no persisted order. DLQ recovery uses Spring AMQP confirmed republish.

```text
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 32.90 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Full executed output: [tests.log](bench/results/confirms/tests.log). No outbox decision is inferred from unit tests; it depends on the following fault reruns.

## Phase C: confirms cost and identical fault reruns

Each row is an executed run. Error rate is k6 HTTP failure fraction; expected sold-out/idempotent statuses do not count as failures. Latencies are milliseconds, from full-precision k6 exports. Threshold failures remain in the table and raw files.

| Run | req/s | p50 | p95 | p99 | errors | HTTP 202 | CONFIRMED | stock | oversold | lost | duplicates | stuck | k6 exit |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| kill-run-1 | 464.6017426761397 | 6.493244 | 10.170674100000001 | 13.35117307999999 | 0.00023299578815306031 | 55781 | 55785 | 44215 | 0 | 0 | 0 | 0 | 0 |
| kill-run-2 | 462.9682763731145 | 6.6825575 | 10.231186299999989 | 13.185564189999994 | 0.00025167181994678936 | 55613 | 55614 | 44386 | 0 | 0 | 0 | 0 | 0 |
| kill-run-3 | 462.7543640578583 | 6.795168 | 10.3951338 | 12.964104040000016 | 0.0001439030093716835 | 55584 | 55584 | 44416 | 0 | 0 | 0 | 0 | 0 |
| normal-run-1 | 409.63689727629014 | 6.844158999999999 | 11.058139749999999 | 14.704708789999975 | 0 | 49201 | 49201 | 50799 | 0 | 0 | 0 | 0 | 0 |
| normal-run-2 | 415.5532886364096 | 6.5155695 | 9.9147412 | 11.988108439999985 | 0 | 49973 | 49973 | 50027 | 0 | 0 | 0 | 0 | 0 |
| normal-run-3 | 416.50574681805085 | 6.364679 | 10.10993545 | 13.000242089999999 | 0 | 49987 | 49987 | 50013 | 0 | 0 | 0 | 0 | 0 |
| rabbit-run-1 | 459.88600496925886 | 6.547588 | 10.197875199999993 | 12.9441175 | 0.0905388527883867 | 50244 | 50244 | 49756 | 0 | 0 | 0 | 0 | 99 |
| rabbit-run-2 | 460.0674501260111 | 6.7292645 | 10.294487 | 12.856773749999991 | 0.08082380194006081 | 50789 | 50792 | 49211 | 0 | 0 | 0 | 0 | 99 |
| rabbit-run-3 | 458.3583459530039 | 6.9397635 | 11.127887999999999 | 16.392501719999974 | 0.07592821332558308 | 50871 | 50871 | 49129 | 0 | 0 | 0 | 0 | 99 |

Maximum observed per-container snapshots (Docker display precision; not guaranteed peaks), generated by executed `python3 bench/summarize.py bench/results/confirms`:

```text
{"run": "kill-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "72.66%", "max_observed_memory": "350.2MiB / 640MiB"}
{"run": "kill-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "286.85%", "max_observed_memory": "345.4MiB / 640MiB"}
{"run": "kill-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.09%", "max_observed_memory": "11.82MiB / 64MiB"}
{"run": "kill-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "236.61%", "max_observed_memory": "207.9MiB / 384MiB"}
{"run": "kill-run-1", "container": "flash_redis", "max_observed_cpu": "16.86%", "max_observed_memory": "20.3MiB / 384MiB"}
{"run": "kill-run-1", "container": "flash_postgres", "max_observed_cpu": "48.40%", "max_observed_memory": "100.4MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "83.22%", "max_observed_memory": "351.1MiB / 640MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "282.88%", "max_observed_memory": "338.5MiB / 640MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "18.36%", "max_observed_memory": "11.67MiB / 64MiB"}
{"run": "kill-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "248.33%", "max_observed_memory": "159.5MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash_redis", "max_observed_cpu": "8.59%", "max_observed_memory": "22.44MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash_postgres", "max_observed_cpu": "44.89%", "max_observed_memory": "100MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "85.30%", "max_observed_memory": "352MiB / 640MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "286.37%", "max_observed_memory": "343.8MiB / 640MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "22.70%", "max_observed_memory": "11.96MiB / 64MiB"}
{"run": "kill-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "258.23%", "max_observed_memory": "211MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash_redis", "max_observed_cpu": "6.75%", "max_observed_memory": "23.59MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash_postgres", "max_observed_cpu": "50.01%", "max_observed_memory": "99.63MiB / 384MiB"}
{"run": "normal-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "122.44%", "max_observed_memory": "349.6MiB / 640MiB"}
{"run": "normal-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "136.30%", "max_observed_memory": "338.4MiB / 640MiB"}
{"run": "normal-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "28.79%", "max_observed_memory": "12.39MiB / 64MiB"}
{"run": "normal-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "278.25%", "max_observed_memory": "201.5MiB / 384MiB"}
{"run": "normal-run-1", "container": "flash_redis", "max_observed_cpu": "11.33%", "max_observed_memory": "18.62MiB / 384MiB"}
{"run": "normal-run-1", "container": "flash_postgres", "max_observed_cpu": "65.30%", "max_observed_memory": "97.96MiB / 384MiB"}
{"run": "normal-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "73.63%", "max_observed_memory": "348MiB / 640MiB"}
{"run": "normal-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "74.90%", "max_observed_memory": "340.7MiB / 640MiB"}
{"run": "normal-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "24.78%", "max_observed_memory": "11.48MiB / 64MiB"}
{"run": "normal-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "193.16%", "max_observed_memory": "186.6MiB / 384MiB"}
{"run": "normal-run-2", "container": "flash_redis", "max_observed_cpu": "6.59%", "max_observed_memory": "15.65MiB / 384MiB"}
{"run": "normal-run-2", "container": "flash_postgres", "max_observed_cpu": "142.43%", "max_observed_memory": "99.42MiB / 384MiB"}
{"run": "normal-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "74.91%", "max_observed_memory": "349.1MiB / 640MiB"}
{"run": "normal-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "74.22%", "max_observed_memory": "341.4MiB / 640MiB"}
{"run": "normal-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "26.85%", "max_observed_memory": "11.24MiB / 64MiB"}
{"run": "normal-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "220.78%", "max_observed_memory": "202.2MiB / 384MiB"}
{"run": "normal-run-3", "container": "flash_redis", "max_observed_cpu": "13.95%", "max_observed_memory": "19.84MiB / 384MiB"}
{"run": "normal-run-3", "container": "flash_postgres", "max_observed_cpu": "58.03%", "max_observed_memory": "98.52MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "46.65%", "max_observed_memory": "357.6MiB / 640MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "59.55%", "max_observed_memory": "343.6MiB / 640MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "15.60%", "max_observed_memory": "11.56MiB / 64MiB"}
{"run": "rabbit-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "231.26%", "max_observed_memory": "242.8MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash_redis", "max_observed_cpu": "10.03%", "max_observed_memory": "24.67MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash_postgres", "max_observed_cpu": "54.99%", "max_observed_memory": "101.8MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "40.72%", "max_observed_memory": "356MiB / 640MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "50.25%", "max_observed_memory": "344.2MiB / 640MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "17.00%", "max_observed_memory": "10.91MiB / 64MiB"}
{"run": "rabbit-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "233.10%", "max_observed_memory": "202.7MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash_redis", "max_observed_cpu": "7.33%", "max_observed_memory": "24.23MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash_postgres", "max_observed_cpu": "36.02%", "max_observed_memory": "105.4MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "41.93%", "max_observed_memory": "355.4MiB / 640MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "42.40%", "max_observed_memory": "346.5MiB / 640MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "14.49%", "max_observed_memory": "10.95MiB / 64MiB"}
{"run": "rabbit-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "95.25%", "max_observed_memory": "164.2MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash_redis", "max_observed_cpu": "10.47%", "max_observed_memory": "23.21MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash_postgres", "max_observed_cpu": "33.74%", "max_observed_memory": "102MiB / 384MiB"}
```

### kill-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-1](bench/results/confirms/kill-run-1/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:44:19.298348+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:44:19.300226+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=13.35ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.02%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.02%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 55781 464.485165/s
    unexpected_responses...........: 0.02% 13 out of 55794

    HTTP
    http_req_duration..............: avg=6.88ms   min=3.08ms   med=6.49ms   max=1.01s p(90)=9.24ms   p(95)=10.17ms  p(99)=13.35ms 
      { expected_response:true }...: avg=6.87ms   min=3.08ms   med=6.49ms   max=1.01s p(90)=9.23ms   p(95)=10.16ms  p(99)=13.3ms  
    http_req_failed................: 0.02% 13 out of 55795
    http_reqs......................: 55795 464.601743/s

    EXECUTION
    iteration_duration.............: avg=107.57ms min=103.26ms med=107.18ms max=1.11s p(90)=109.94ms p(95)=110.91ms p(99)=114.09ms
    iterations.....................: 55794 464.593416/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 17 MB 144 kB/s
    data_sent......................: 12 MB 98 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 55781,
  "confirmed": 55785,
  "redis_stock": 44215,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 4,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

```text
$ docker start 749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1
started=2026-10-09T08:45:25.691101+00:00
749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1

exit_code=0
ended=2026-10-09T08:45:26.022946+00:00

```

```text
$ docker kill -s KILL 749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1
started=2026-10-09T08:45:04.923489+00:00
749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1

exit_code=0
ended=2026-10-09T08:45:05.692618+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### kill-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-2](bench/results/confirms/kill-run-2/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:46:28.323420+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:46:28.336782+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=13.18ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.02%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.02%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 55613 462.843438/s
    unexpected_responses...........: 0.02% 14 out of 55627

    HTTP
    http_req_duration..............: avg=7.2ms    min=3.39ms   med=6.68ms   max=927.61ms p(90)=9.34ms   p(95)=10.23ms  p(99)=13.18ms 
      { expected_response:true }...: avg=7.19ms   min=3.39ms   med=6.68ms   max=927.61ms p(90)=9.34ms   p(95)=10.22ms  p(99)=13.12ms 
    http_req_failed................: 0.02% 14 out of 55628
    http_reqs......................: 55628 462.968276/s

    EXECUTION
    iteration_duration.............: avg=107.88ms min=103.88ms med=107.38ms max=1.02s    p(90)=110.02ms p(95)=110.93ms p(99)=113.88ms
    iterations.....................: 55627 462.959954/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 17 MB 144 kB/s
    data_sent......................: 12 MB 98 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 55613,
  "confirmed": 55614,
  "redis_stock": 44386,
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

```text
$ docker start 749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1
started=2026-10-09T08:47:34.722894+00:00
749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1

exit_code=0
ended=2026-10-09T08:47:34.990941+00:00

```

```text
$ docker kill -s KILL 749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1
started=2026-10-09T08:47:14.120428+00:00
749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1

exit_code=0
ended=2026-10-09T08:47:14.724048+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### kill-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-3](bench/results/confirms/kill-run-3/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:48:37.565331+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:48:37.576006+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=12.96ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.01%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.01%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 55584 462.679448/s
    unexpected_responses...........: 0.01% 8 out of 55592

    HTTP
    http_req_duration..............: avg=7.26ms   min=3.26ms   med=6.79ms   max=814.67ms p(90)=9.41ms   p(95)=10.39ms p(99)=12.96ms 
      { expected_response:true }...: avg=7.26ms   min=3.26ms   med=6.79ms   max=814.67ms p(90)=9.41ms   p(95)=10.38ms p(99)=12.93ms 
    http_req_failed................: 0.01% 8 out of 55593
    http_reqs......................: 55593 462.754364/s

    EXECUTION
    iteration_duration.............: avg=107.95ms min=103.73ms med=107.49ms max=916.69ms p(90)=110.09ms p(95)=111.1ms p(99)=113.78ms
    iterations.....................: 55592 462.74604/s
    vus............................: 50    min=50         max=50
    vus_max........................: 50    min=50         max=50

    NETWORK
    data_received..................: 17 MB 144 kB/s
    data_sent......................: 12 MB 98 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 55584,
  "confirmed": 55584,
  "redis_stock": 44416,
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

```text
$ docker start 749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1
started=2026-10-09T08:49:43.884410+00:00
749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1

exit_code=0
ended=2026-10-09T08:49:44.138863+00:00

```

```text
$ docker kill -s KILL 749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1
started=2026-10-09T08:49:23.314056+00:00
749592cc721d3eb5112aadbf13daf34b7ec87014826aa56a131fb7c6bbc396e1

exit_code=0
ended=2026-10-09T08:49:23.884043+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### normal-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [normal-run-1](bench/results/confirms/normal-run-1/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:37:38.510338+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:37:38.518586+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=14.7ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 49201 409.628572/s
    unexpected_responses...........: 0.00% 0 out of 49201

    HTTP
    http_req_duration..............: avg=8.52ms   min=3.36ms   med=6.84ms   max=1.32s p(90)=9.83ms  p(95)=11.05ms  p(99)=14.7ms  
      { expected_response:true }...: avg=8.52ms   min=3.36ms   med=6.84ms   max=1.32s p(90)=9.83ms  p(95)=11.05ms  p(99)=14.7ms  
    http_req_failed................: 0.00% 0 out of 49202
    http_reqs......................: 49202 409.636897/s

    EXECUTION
    iteration_duration.............: avg=109.23ms min=103.76ms med=107.57ms max=1.42s p(90)=110.6ms p(95)=111.81ms p(99)=115.59ms
    iterations.....................: 49201 409.628572/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 15 MB 127 kB/s
    data_sent......................: 11 MB 87 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 49201,
  "confirmed": 49201,
  "redis_stock": 50799,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### normal-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [normal-run-2](bench/results/confirms/normal-run-2/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:40:00.231606+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:40:00.247012+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=11.98ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 49973 415.544973/s
    unexpected_responses...........: 0.00% 0 out of 49973

    HTTP
    http_req_duration..............: avg=6.87ms   min=3.54ms   med=6.51ms   max=242.57ms p(90)=9.09ms   p(95)=9.91ms   p(99)=11.98ms
      { expected_response:true }...: avg=6.87ms   min=3.54ms   med=6.51ms   max=242.57ms p(90)=9.09ms   p(95)=9.91ms   p(99)=11.98ms
    http_req_failed................: 0.00% 0 out of 49974
    http_reqs......................: 49974 415.553289/s

    EXECUTION
    iteration_duration.............: avg=107.54ms min=103.83ms med=107.21ms max=145.38ms p(90)=109.77ms p(95)=110.57ms p(99)=112.6ms
    iterations.....................: 49973 415.544973/s
    vus............................: 2     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 129 kB/s
    data_sent......................: 11 MB 88 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 49973,
  "confirmed": 49973,
  "redis_stock": 50027,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### normal-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [normal-run-3](bench/results/confirms/normal-run-3/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:42:10.950697+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:42:10.953703+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=13ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 49987 416.497415/s
    unexpected_responses...........: 0.00% 0 out of 49987

    HTTP
    http_req_duration..............: avg=6.84ms   min=3.33ms   med=6.36ms   max=80.46ms  p(90)=9.15ms   p(95)=10.1ms   p(99)=13ms   
      { expected_response:true }...: avg=6.84ms   min=3.33ms   med=6.36ms   max=80.46ms  p(90)=9.15ms   p(95)=10.1ms   p(99)=13ms   
    http_req_failed................: 0.00% 0 out of 49988
    http_reqs......................: 49988 416.505747/s

    EXECUTION
    iteration_duration.............: avg=107.51ms min=103.67ms med=107.07ms max=181.32ms p(90)=109.81ms p(95)=110.76ms p(99)=113.7ms
    iterations.....................: 49987 416.497415/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 129 kB/s
    data_sent......................: 11 MB 89 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 49987,
  "confirmed": 49987,
  "redis_stock": 50013,
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

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-1](bench/results/confirms/rabbit-run-1/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:50:46.881992+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:50:46.887637+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=12.94ms

    http_req_failed
    ✗ 'rate<0.01' rate=9.05%

    unexpected_responses
    ✗ 'rate<0.01' rate=9.05%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 50244 418.240129/s
    unexpected_responses...........: 9.05% 5002 out of 55246

    HTTP
    http_req_duration..............: avg=7.96ms   min=1.76ms   med=6.54ms   max=1.02s   p(90)=9.31ms   p(95)=10.19ms  p(99)=12.94ms 
      { expected_response:true }...: avg=7.08ms   min=3.31ms   med=6.69ms   max=145.9ms p(90)=9.35ms   p(95)=10.2ms   p(99)=12.51ms 
    http_req_failed................: 9.05% 5002 out of 55247
    http_reqs......................: 55247 459.886005/s

    EXECUTION
    iteration_duration.............: avg=108.62ms min=101.91ms med=107.23ms max=1.12s   p(90)=109.97ms p(95)=110.83ms p(99)=113.61ms
    iterations.....................: 55246 459.877681/s
    vus............................: 50    min=50            max=50
    vus_max........................: 50    min=50            max=50

    NETWORK
    data_received..................: 17 MB 142 kB/s
    data_sent......................: 12 MB 98 kB/s



time="2026-10-09T08:52:48Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 50244,
  "confirmed": 50244,
  "redis_stock": 49756,
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

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:51:32.559758+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:51:35.205604+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-2](bench/results/confirms/rabbit-run-2/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:52:55.776524+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:52:55.783822+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=12.85ms

    http_req_failed
    ✗ 'rate<0.01' rate=8.08%

    unexpected_responses
    ✗ 'rate<0.01' rate=8.08%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 50789 422.874724/s
    unexpected_responses...........: 8.08% 4466 out of 55255

    HTTP
    http_req_duration..............: avg=7.96ms   min=1.6ms    med=6.72ms   max=1.03s    p(90)=9.37ms   p(95)=10.29ms  p(99)=12.85ms 
      { expected_response:true }...: avg=7.29ms   min=3.4ms    med=6.86ms   max=134.95ms p(90)=9.45ms   p(95)=10.35ms  p(99)=12.76ms 
    http_req_failed................: 8.08% 4466 out of 55256
    http_reqs......................: 55256 460.06745/s

    EXECUTION
    iteration_duration.............: avg=108.62ms min=101.84ms med=107.43ms max=1.13s    p(90)=110.05ms p(95)=110.94ms p(99)=113.52ms
    iterations.....................: 55255 460.059124/s
    vus............................: 50    min=50            max=50
    vus_max........................: 50    min=50            max=50

    NETWORK
    data_received..................: 17 MB 142 kB/s
    data_sent......................: 12 MB 98 kB/s



time="2026-10-09T08:54:57Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 50789,
  "confirmed": 50792,
  "redis_stock": 49211,
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

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:53:41.466013+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:53:43.726279+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-3](bench/results/confirms/rabbit-run-3/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:55:06.776326+00:00
92c91bc5232e7439d398a56e58baa06ea515d547

exit_code=0
ended=2026-10-09T08:55:06.781482+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=16.39ms

    http_req_failed
    ✗ 'rate<0.01' rate=7.59%

    unexpected_responses
    ✗ 'rate<0.01' rate=7.59%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 50871 423.54769/s
    unexpected_responses...........: 7.59% 4180 out of 55051

    HTTP
    http_req_duration..............: avg=8.35ms   min=1.38ms   med=6.93ms   max=1.04s   p(90)=9.86ms   p(95)=11.12ms  p(99)=16.39ms 
      { expected_response:true }...: avg=7.67ms   min=3.5ms    med=7.08ms   max=95.22ms p(90)=9.95ms   p(95)=11.19ms  p(99)=15.78ms 
    http_req_failed................: 7.59% 4180 out of 55052
    http_reqs......................: 55052 458.358346/s

    EXECUTION
    iteration_duration.............: avg=109.02ms min=101.68ms med=107.64ms max=1.14s   p(90)=110.58ms p(95)=111.84ms p(99)=116.98ms
    iterations.....................: 55051 458.35002/s
    vus............................: 50    min=50            max=50
    vus_max........................: 50    min=50            max=50

    NETWORK
    data_received..................: 17 MB 142 kB/s
    data_sent......................: 12 MB 98 kB/s



time="2026-10-09T08:57:08Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 50871,
  "confirmed": 50871,
  "redis_stock": 49129,
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

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:55:52.499743+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:55:54.579949+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```


OOMKilled flags from executed Docker inspect:

```text
('kill-run-1', '/flash-sale-engine-app-1', False)
('kill-run-1', '/flash-sale-engine-app-2', False)
('kill-run-1', '/flash_postgres', False)
('kill-run-1', '/flash_redis', False)
('kill-run-1', '/flash_rabbitmq', False)
('kill-run-2', '/flash-sale-engine-app-1', False)
('kill-run-2', '/flash-sale-engine-app-2', False)
('kill-run-2', '/flash_postgres', False)
('kill-run-2', '/flash_redis', False)
('kill-run-2', '/flash_rabbitmq', False)
('kill-run-3', '/flash-sale-engine-app-1', False)
('kill-run-3', '/flash-sale-engine-app-2', False)
('kill-run-3', '/flash_postgres', False)
('kill-run-3', '/flash_redis', False)
('kill-run-3', '/flash_rabbitmq', False)
('normal-run-1', '/flash-sale-engine-app-1', False)
('normal-run-1', '/flash-sale-engine-app-2', False)
('normal-run-1', '/flash_postgres', False)
('normal-run-1', '/flash_redis', False)
('normal-run-1', '/flash_rabbitmq', False)
('normal-run-2', '/flash-sale-engine-app-1', False)
('normal-run-2', '/flash-sale-engine-app-2', False)
('normal-run-2', '/flash_postgres', False)
('normal-run-2', '/flash_redis', False)
('normal-run-2', '/flash_rabbitmq', False)
('normal-run-3', '/flash-sale-engine-app-1', False)
('normal-run-3', '/flash-sale-engine-app-2', False)
('normal-run-3', '/flash_postgres', False)
('normal-run-3', '/flash_redis', False)
('normal-run-3', '/flash_rabbitmq', False)
('rabbit-run-1', '/flash-sale-engine-app-1', False)
('rabbit-run-1', '/flash-sale-engine-app-2', False)
('rabbit-run-1', '/flash_postgres', False)
('rabbit-run-1', '/flash_redis', False)
('rabbit-run-1', '/flash_rabbitmq', False)
('rabbit-run-2', '/flash-sale-engine-app-1', False)
('rabbit-run-2', '/flash-sale-engine-app-2', False)
('rabbit-run-2', '/flash_postgres', False)
('rabbit-run-2', '/flash_redis', False)
('rabbit-run-2', '/flash_rabbitmq', False)
('rabbit-run-3', '/flash-sale-engine-app-1', False)
('rabbit-run-3', '/flash-sale-engine-app-2', False)
('rabbit-run-3', '/flash_postgres', False)
('rabbit-run-3', '/flash_redis', False)
('rabbit-run-3', '/flash_rabbitmq', False)
```

CPU throttling and memory OOM counters (raw before/after cgroup snapshots):

```text
('kill-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-0.txt', 'oom 0')
('kill-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-0.txt', 'oom 0')
('kill-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-2.txt', 'oom 0')
('kill-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-1.txt', 'oom 0')
('kill-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-4.txt', 'oom 0')
('kill-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-3.txt', 'oom 0')
('kill-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-2.txt', 'oom 0')
('kill-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-4.txt', 'oom 0')
('kill-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-1.txt', 'oom 0')
('kill-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-3.txt', 'oom 0')
('kill-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-0.txt', 'oom 0')
('kill-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-0.txt', 'oom 0')
('kill-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-2.txt', 'oom 0')
('kill-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-1.txt', 'oom 0')
('kill-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-4.txt', 'oom 0')
('kill-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-3.txt', 'oom 0')
('kill-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-2.txt', 'oom 0')
('kill-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-4.txt', 'oom 0')
('kill-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-1.txt', 'oom 0')
('kill-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-3.txt', 'oom 0')
('kill-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-0.txt', 'oom 0')
('kill-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-0.txt', 'oom 0')
('kill-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-2.txt', 'oom 0')
('kill-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-1.txt', 'oom 0')
('kill-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-4.txt', 'oom 0')
('kill-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-3.txt', 'oom 0')
('kill-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-2.txt', 'oom 0')
('kill-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-4.txt', 'oom 0')
('kill-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-1.txt', 'oom 0')
('kill-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-3.txt', 'oom 0')
('kill-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('normal-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('normal-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('normal-run-1', 'before-cgroup-0.txt', 'oom 0')
('normal-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('normal-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('normal-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('normal-run-1', 'after-cgroup-0.txt', 'oom 0')
('normal-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('normal-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('normal-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('normal-run-1', 'before-cgroup-2.txt', 'oom 0')
('normal-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('normal-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('normal-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('normal-run-1', 'before-cgroup-1.txt', 'oom 0')
('normal-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('normal-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('normal-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('normal-run-1', 'after-cgroup-4.txt', 'oom 0')
('normal-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('normal-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('normal-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('normal-run-1', 'before-cgroup-3.txt', 'oom 0')
('normal-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('normal-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('normal-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('normal-run-1', 'after-cgroup-2.txt', 'oom 0')
('normal-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('normal-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('normal-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('normal-run-1', 'before-cgroup-4.txt', 'oom 0')
('normal-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('normal-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('normal-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('normal-run-1', 'after-cgroup-1.txt', 'oom 0')
('normal-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('normal-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('normal-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('normal-run-1', 'after-cgroup-3.txt', 'oom 0')
('normal-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('normal-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('normal-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('normal-run-2', 'before-cgroup-0.txt', 'oom 0')
('normal-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('normal-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('normal-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('normal-run-2', 'after-cgroup-0.txt', 'oom 0')
('normal-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('normal-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('normal-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('normal-run-2', 'before-cgroup-2.txt', 'oom 0')
('normal-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('normal-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('normal-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('normal-run-2', 'before-cgroup-1.txt', 'oom 0')
('normal-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('normal-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('normal-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('normal-run-2', 'after-cgroup-4.txt', 'oom 0')
('normal-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('normal-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('normal-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('normal-run-2', 'before-cgroup-3.txt', 'oom 0')
('normal-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('normal-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('normal-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('normal-run-2', 'after-cgroup-2.txt', 'oom 0')
('normal-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('normal-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('normal-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('normal-run-2', 'before-cgroup-4.txt', 'oom 0')
('normal-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('normal-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('normal-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('normal-run-2', 'after-cgroup-1.txt', 'oom 0')
('normal-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('normal-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('normal-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('normal-run-2', 'after-cgroup-3.txt', 'oom 0')
('normal-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('normal-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('normal-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('normal-run-3', 'before-cgroup-0.txt', 'oom 0')
('normal-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('normal-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('normal-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('normal-run-3', 'after-cgroup-0.txt', 'oom 0')
('normal-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('normal-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('normal-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('normal-run-3', 'before-cgroup-2.txt', 'oom 0')
('normal-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('normal-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('normal-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('normal-run-3', 'before-cgroup-1.txt', 'oom 0')
('normal-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('normal-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('normal-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('normal-run-3', 'after-cgroup-4.txt', 'oom 0')
('normal-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('normal-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('normal-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('normal-run-3', 'before-cgroup-3.txt', 'oom 0')
('normal-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('normal-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('normal-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('normal-run-3', 'after-cgroup-2.txt', 'oom 0')
('normal-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('normal-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('normal-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('normal-run-3', 'before-cgroup-4.txt', 'oom 0')
('normal-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('normal-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('normal-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('normal-run-3', 'after-cgroup-1.txt', 'oom 0')
('normal-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('normal-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('normal-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('normal-run-3', 'after-cgroup-3.txt', 'oom 0')
('normal-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
```

## Measured conclusions, comparisons and remaining failure

### Confirm latency/throughput cost

These compare chronological normal-ramp pairs on the same laptop, same replicated profile, app count, heap/container limits, JRE base image and instrumented request scenario. They are observed differences, not estimates of maximum system capacity. Every individual run is shown above; no failed/noisy run was removed.

| Pair | Measure | Before | After | After minus before |
|---|---|---|---|---|
| 1 | req_per_s | 426.9330053557005 | 409.63689727629014 | -17.296108079410374 |
| 1 | p50_ms | 3.4913435 | 6.844158999999999 | 3.3528154999999993 |
| 1 | p95_ms | 6.5029985 | 11.058139749999999 | 4.555141249999998 |
| 1 | p99_ms | 8.9240443 | 14.704708789999975 | 5.780664489999975 |
| 2 | req_per_s | 429.1972420560015 | 415.5532886364096 | -13.643953419591924 |
| 2 | p50_ms | 3.506063 | 6.5155695 | 3.0095064999999996 |
| 2 | p95_ms | 6.3572945 | 9.9147412 | 3.5574467 |
| 2 | p99_ms | 8.33397190000001 | 11.988108439999985 | 3.654136539999975 |
| 3 | req_per_s | 428.9298256551053 | 416.50574681805085 | -12.424078837054424 |
| 3 | p50_ms | 3.540799 | 6.364679 | 2.82388 |
| 3 | p95_ms | 6.5604379999999995 | 10.10993545 | 3.5494974500000005 |
| 3 | p99_ms | 8.474353880000013 | 13.000242089999999 | 4.5258882099999855 |

### Lost/stuck before versus after

```json
[
  {
    "fault": "kill",
    "pair": 1,
    "before_lost": 0,
    "after_lost": 0,
    "before_stuck": 0,
    "after_stuck": 0,
    "before_oversold": 0,
    "after_oversold": 0,
    "before_stock_accounting_excess": 0,
    "after_stock_accounting_excess": 0
  },
  {
    "fault": "kill",
    "pair": 2,
    "before_lost": 0,
    "after_lost": 0,
    "before_stuck": 0,
    "after_stuck": 0,
    "before_oversold": 0,
    "after_oversold": 0,
    "before_stock_accounting_excess": 0,
    "after_stock_accounting_excess": 0
  },
  {
    "fault": "kill",
    "pair": 3,
    "before_lost": 0,
    "after_lost": 0,
    "before_stuck": 0,
    "after_stuck": 0,
    "before_oversold": 0,
    "after_oversold": 0,
    "before_stock_accounting_excess": 0,
    "after_stock_accounting_excess": 0
  },
  {
    "fault": "rabbit",
    "pair": 1,
    "before_lost": 0,
    "after_lost": 0,
    "before_stuck": 0,
    "after_stuck": 0,
    "before_oversold": 0,
    "after_oversold": 0,
    "before_stock_accounting_excess": 0,
    "after_stock_accounting_excess": 0
  },
  {
    "fault": "rabbit",
    "pair": 2,
    "before_lost": 0,
    "after_lost": 0,
    "before_stuck": 0,
    "after_stuck": 0,
    "before_oversold": 0,
    "after_oversold": 0,
    "before_stock_accounting_excess": 0,
    "after_stock_accounting_excess": 3
  },
  {
    "fault": "rabbit",
    "pair": 3,
    "before_lost": 0,
    "after_lost": 0,
    "before_stuck": 0,
    "after_stuck": 0,
    "before_oversold": 0,
    "after_oversold": 0,
    "before_stock_accounting_excess": 0,
    "after_stock_accounting_excess": 0
  }
]
```

No lost HTTP-202 orders or stuck reservations were observed in either the before or after app-kill/broker-restart runs. Therefore there is no measured reduction to claim. No transactional outbox was added: the specified accepted-order-loss condition did not occur. This does not mean confirms alone establish end-to-end fault tolerance.

### Confirmed orders without HTTP 202 and broken stock accounting

Confirmed rows exceeded the client HTTP-202 ledger in the confirm app-kill runs. The first run has extra confirmed keys while retaining stock conservation; so does the second. Key lists are saved as `confirmed-without-202.json`, audited against the raw ledger and SQL export. This is a client outcome ambiguity, not a lost accepted order.

The confirm broker-restart second run is a stronger negative finding:

```json
{
  "http_202": 50789,
  "confirmed": 50792,
  "redis_stock": 49211,
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

Executed key-level evidence:

```json
{
  "confirmed_without_202": [
    "bench-rabbit-run-2-16-20798",
    "bench-rabbit-run-2-21-20796",
    "bench-rabbit-run-2-38-20797"
  ],
  "extra_confirmed_still_reserved": [],
  "confirmed_missing_redis_reservation": [
    "bench-rabbit-run-2-16-20798",
    "bench-rabbit-run-2-21-20796",
    "bench-rabbit-run-2-38-20797"
  ]
}
```

Those confirmed orders have no Redis reservation, and confirmed quantities plus remaining inventory exceed initialized inventory. The rollback-on-confirm-failure policy can refund a publish whose outcome is ambiguous while its consumer has already persisted it. The saved API logs do not distinguish which negative-confirm/timeout exception triggered each refund; the persisted-without-reservation state is directly observed. Actual CONFIRMED count remains below initial inventory, so `oversold_count` is zero under its documented definition, while `stock_accounting_excess` exposes the invariant violation. Full fault tolerance and inventory safety are not established by this implementation. It would be false to describe every fault run as passing. The outbox addition criterion was lost HTTP-202 orders, not this stock-accounting failure, so no outbox was added for a different purpose.

Broker-restart HTTP thresholds failed before and after confirms. App replication kept serving during app failure, but some requests failed; HTTP availability was not perfect. Redis restart was observational only, with RDB retention as documented above. Shared infrastructure HA, hard Redis loss, every instruction-boundary crash, and thermal/power throttling were not measured.

### Resource and campaign audit

Executed `python3 bench/audit.py`, `python3 bench/compare.py`, quantity/stock accounting checks and host sample aggregation:

```json
{
  "completed_instrumented_runs": 25,
  "completed_original_baseline_runs": 3,
  "maximum_observed_host_swap_used_bytes": 39215104,
  "observed_host_memory_total_bytes": [
    5157515264
  ]
}
```

Container OOMKilled and cgroup OOM/throttling counters are shown for every run above; captured counters were zero and no Docker OOM events were captured. SIGKILL exit events are intentional faults, not OOM evidence. Host swap was used; the exact maximum observed during recorded samples is above. This is not a guarantee that unsampled peaks or hardware throttling never occurred. Raw audit: [ledger-audit.json](bench/results/ledger-audit.json).

The load generator, proxy, apps and infrastructure share the laptop. The requested paced ramp did not demonstrate a saturated app/Redis/RabbitMQ/Postgres/laptop capacity limit. Its offered-load pacing is the measured workload constraint; selecting a physical capacity bottleneck from those options would be speculation.

### Final validation

After all load/fault runs, executed `python3 bench/http_concurrency.py bench/results/confirms/http` and `JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test` sequentially:

```text
{'requests': 2000, 'initial_stock': 100, 'statuses': {202: 100, 409: 1900}, 'upstreams': {'172.18.0.5:8080': 1000, '172.18.0.6:8080': 1000}, 'postgres': '100|100|100', 'redis_stock': 0}
{'requests': 40, 'initial_stock': 10, 'statuses': {200: 39, 202: 1}, 'upstreams': {'172.18.0.5:8080': 20, '172.18.0.6:8080': 20}, 'postgres': '1|1|1', 'redis_stock': 9}
HTTP assertions passed
exit_code=0

Picked up JAVA_TOOL_OPTIONS: -Xmx256m
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< com.flashsale:flash-sale-engine >-------------------
[INFO] Building  0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.5.0:resources (default-resources) @ flash-sale-engine ---
[INFO] Copying 1 resource from src/main/resources to target/classes
[INFO] Copying 2 resources from src/main/resources to target/classes
[INFO] 
[INFO] --- compiler:3.15.0:compile (default-compile) @ flash-sale-engine ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- resources:3.5.0:testResources (default-testResources) @ flash-sale-engine ---
[INFO] skip non existing resourceDirectory /home/ayussh/projects/flash-sale-engine/src/test/resources
[INFO] 
[INFO] --- compiler:3.15.0:testCompile (default-testCompile) @ flash-sale-engine ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- surefire:3.5.6:test (default-test) @ flash-sale-engine ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
Picked up JAVA_TOOL_OPTIONS: -Xmx256m
[INFO] Running com.flashsale.engine.OrderFlowIntegrationTests
08:58:40.331 [main] INFO org.testcontainers.images.PullPolicy -- Image pull policy will be performed by: DefaultPullPolicy()
08:58:40.337 [main] INFO org.testcontainers.utility.ImageNameSubstitutor -- Image name substitution will be performed by: DefaultImageNameSubstitutor (composite of 'ConfigurationFileImageNameSubstitutor' and 'PrefixingImageNameSubstitutor')
08:58:40.358 [main] INFO org.testcontainers.DockerClientFactory -- Testcontainers version: 2.0.5
08:58:40.797 [main] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Loaded org.testcontainers.dockerclient.UnixSocketClientProviderStrategy from ~/.testcontainers.properties, will try it first
08:58:41.297 [main] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Found Docker environment with local Unix socket (unix:///var/run/docker.sock)
08:58:41.298 [main] INFO org.testcontainers.DockerClientFactory -- Docker host IP address is localhost
08:58:41.327 [main] INFO org.testcontainers.DockerClientFactory -- Connected to docker: 
  Server Version: 29.8.0
  API Version: 1.56
  Operating System: Docker Desktop (containerized)
  Total Memory: 4918 MB
  Labels: 
    com.docker.desktop.address=unix:///var/run/docker-cli.sock
08:58:41.462 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Creating container for image: testcontainers/ryuk:0.14.0
08:58:41.729 [main] INFO org.testcontainers.utility.RegistryAuthLocator -- Credential helper/store (docker-credential-desktop.exe) does not have credentials for https://index.docker.io/v1/
08:58:41.954 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Container testcontainers/ryuk:0.14.0 is starting: 2e2c1db53f92fba45731053d53503bfeaa3fdbae4028fcc373fde564f0198929
08:58:42.692 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Container testcontainers/ryuk:0.14.0 started in PT1.229576144S
08:58:42.697 [main] INFO org.testcontainers.utility.RyukResourceReaper -- Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
08:58:42.697 [main] INFO org.testcontainers.DockerClientFactory -- Checking the system...
08:58:42.698 [main] INFO org.testcontainers.DockerClientFactory -- ✔︎ Docker server version should be at least 1.6.0
08:58:42.698 [main] INFO tc.rabbitmq:3.13-management-alpine -- Creating container for image: rabbitmq:3.13-management-alpine
08:58:42.790 [main] INFO tc.rabbitmq:3.13-management-alpine -- Container rabbitmq:3.13-management-alpine is starting: baac8c3008232ac4ba6a2bfcb79f60d2e5afb942dd4649d38339f0977ac8f223
08:58:52.980 [main] INFO tc.rabbitmq:3.13-management-alpine -- Container rabbitmq:3.13-management-alpine started in PT10.281587043S
08:58:52.982 [main] INFO tc.redis:7-alpine -- Creating container for image: redis:7-alpine
08:58:53.067 [main] INFO tc.redis:7-alpine -- Container redis:7-alpine is starting: 7335aeee662dd8efd181940015483ca2c84ec62c2885bf1bd95cedb761be948f
08:58:53.668 [main] INFO tc.redis:7-alpine -- Container redis:7-alpine started in PT0.686446877S
08:58:53.669 [main] INFO tc.postgres:16-alpine -- Creating container for image: postgres:16-alpine
08:58:53.754 [main] INFO tc.postgres:16-alpine -- Container postgres:16-alpine is starting: c4818701febdcf68d2e6e7f717366b15e51c89dc42df35784371a2c98e9caa03
08:58:56.842 [main] INFO tc.postgres:16-alpine -- Container postgres:16-alpine started in PT3.173426165S
08:58:56.844 [main] INFO tc.postgres:16-alpine -- Container is started (JDBC URL: jdbc:postgresql://localhost:55688/test?loggerLevel=OFF)
08:58:56.941 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.flashsale.engine.OrderFlowIntegrationTests]: OrderFlowIntegrationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
08:58:57.043 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.flashsale.engine.FlashSaleEngineApplication for test class com.flashsale.engine.OrderFlowIntegrationTests
08:58:57.111 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.flashsale.engine.OrderFlowIntegrationTests]: OrderFlowIntegrationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
08:58:57.113 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.flashsale.engine.FlashSaleEngineApplication for test class com.flashsale.engine.OrderFlowIntegrationTests

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.1)

2026-10-09T08:58:57.498Z  INFO 26304 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : Starting OrderFlowIntegrationTests using Java 21.0.12.1 with PID 26304 (started by ayussh in /home/ayussh/projects/flash-sale-engine)
2026-10-09T08:58:57.501Z  INFO 26304 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : No active profile set, falling back to 1 default profile: "default"
2026-10-09T08:58:58.189Z  INFO 26304 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-10-09T08:58:58.191Z  INFO 26304 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-10-09T08:58:58.342Z  INFO 26304 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 140 ms. Found 2 JPA repository interfaces.
2026-10-09T08:58:58.475Z  INFO 26304 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-10-09T08:58:58.477Z  INFO 26304 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2026-10-09T08:58:58.504Z  INFO 26304 --- [flash-sale-engine] [           main] .RepositoryConfigurationExtensionSupport : Spring Data Redis - Could not safely identify store assignment for repository candidate interface com.flashsale.engine.repository.OrderRepository; If you want this repository to be a Redis repository, consider annotating your entities with one of these annotations: org.springframework.data.redis.core.RedisHash (preferred), or consider extending one of the following types with your repository: org.springframework.data.keyvalue.repository.KeyValueRepository
2026-10-09T08:58:58.505Z  INFO 26304 --- [flash-sale-engine] [           main] .RepositoryConfigurationExtensionSupport : Spring Data Redis - Could not safely identify store assignment for repository candidate interface com.flashsale.engine.repository.ProductRepository; If you want this repository to be a Redis repository, consider annotating your entities with one of these annotations: org.springframework.data.redis.core.RedisHash (preferred), or consider extending one of the following types with your repository: org.springframework.data.keyvalue.repository.KeyValueRepository
2026-10-09T08:58:58.505Z  INFO 26304 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 6 ms. Found 0 Redis repository interfaces.
2026-10-09T08:58:59.165Z  INFO 26304 --- [flash-sale-engine] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 0 (http)
2026-10-09T08:58:59.186Z  INFO 26304 --- [flash-sale-engine] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-10-09T08:58:59.187Z  INFO 26304 --- [flash-sale-engine] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.24]
2026-10-09T08:58:59.238Z  INFO 26304 --- [flash-sale-engine] [           main] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 1721 ms
2026-10-09T08:58:59.525Z  INFO 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]
2026-10-09T08:58:59.613Z  INFO 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.4.5.Final
2026-10-09T08:59:00.134Z  INFO 26304 --- [flash-sale-engine] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-10-09T08:59:00.176Z  INFO 26304 --- [flash-sale-engine] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-10-09T08:59:00.485Z  INFO 26304 --- [flash-sale-engine] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@14f03f2
2026-10-09T08:59:00.486Z  INFO 26304 --- [flash-sale-engine] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-10-09T08:59:00.560Z  WARN 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-10-09T08:59:00.587Z  INFO 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
	Database JDBC URL [jdbc:postgresql://localhost:55688/test?loggerLevel=OFF]
	Database driver: PostgreSQL JDBC Driver
	Database dialect: PostgreSQLDialect
	Database version: 16.15
	Default catalog/schema: test/public
	Autocommit mode: undefined/unknown
	Isolation level: READ_COMMITTED [default READ_COMMITTED]
	JDBC fetch size: none
	Pool: DataSourceConnectionProvider
	Minimum pool size: undefined/unknown
	Maximum pool size: undefined/unknown
2026-10-09T08:59:01.761Z  INFO 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-10-09T08:59:01.948Z  WARN 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : HHH000247: ErrorCode: 0, SQLState: 00000
2026-10-09T08:59:01.948Z  WARN 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : constraint "ukd1kkvl4hi9hp3peub1umk2xeo" of relation "orders" does not exist, skipping
2026-10-09T08:59:01.951Z  WARN 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : HHH000247: ErrorCode: 0, SQLState: 00000
2026-10-09T08:59:01.951Z  WARN 26304 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : constraint "ukhmsk25beh6atojvle1xuymjj0" of relation "orders" does not exist, skipping
2026-10-09T08:59:01.953Z  INFO 26304 --- [flash-sale-engine] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
Mockito is currently self-attaching to enable the inline-mock-maker. This will no longer work in future releases of the JDK. Please add Mockito as an agent to your build as described in Mockito's documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
WARNING: A Java agent has been loaded dynamically (/home/ayussh/.m2/repository/net/bytebuddy/byte-buddy-agent/1.18.11/byte-buddy-agent-1.18.11.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
2026-10-09T08:59:03.442Z  INFO 26304 --- [flash-sale-engine] [           main] o.s.d.j.r.query.QueryEnhancerFactories   : Hibernate is in classpath; If applicable, HQL parser will be used.
2026-10-09T08:59:03.755Z  WARN 26304 --- [flash-sale-engine] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2026-10-09T08:59:04.287Z  INFO 26304 --- [flash-sale-engine] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 38549 (http) with context path '/'
2026-10-09T08:59:04.290Z  INFO 26304 --- [flash-sale-engine] [           main] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:63123]
2026-10-09T08:59:04.346Z  INFO 26304 --- [flash-sale-engine] [           main] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#2b4b3918:0/SimpleConnection@75defbd2 [delegate=amqp://guest@127.0.0.1:63123/, localPort=47738]
2026-10-09T08:59:04.439Z  INFO 26304 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : Started OrderFlowIntegrationTests in 7.283 seconds (process running for 25.629)
2026-10-09T08:59:05.291Z  INFO 26304 --- [flash-sale-engine] [o-auto-1-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-10-09T08:59:05.291Z  INFO 26304 --- [flash-sale-engine] [o-auto-1-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-10-09T08:59:05.302Z  INFO 26304 --- [flash-sale-engine] [o-auto-1-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 11 ms
2026-10-09T08:59:14.217Z  WARN 26304 --- [flash-sale-engine] [ntContainer#1-1] .r.RepublishMessageRecovererWithConfirms : Republishing failed message to exchange 'order.dlx' with routing key order.failed
2026-10-09T08:59:14.221Z ERROR 26304 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=true. orderId=65a704b1-722e-41f9-9919-5d42b861c4b8, productId=101, quantity=1, idempotencyKey=fail-63bc2c18-e408-41c2-9a97-0c5d3e69f2b6
2026-10-09T08:59:14.330Z ERROR 26304 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=false. orderId=65a704b1-722e-41f9-9919-5d42b861c4b8, productId=101, quantity=1, idempotencyKey=fail-63bc2c18-e408-41c2-9a97-0c5d3e69f2b6
2026-10-09T08:59:14.335Z ERROR 26304 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=false. orderId=65a704b1-722e-41f9-9919-5d42b861c4b8, productId=101, quantity=1, idempotencyKey=fail-63bc2c18-e408-41c2-9a97-0c5d3e69f2b6
2026-10-09T08:59:14.426Z  WARN 26304 --- [flash-sale-engine] [nectionFactory9] c.f.e.config.ConfirmingRabbitTemplate    : Returned message but no callback available
2026-10-09T08:59:17.559Z  INFO 26304 --- [flash-sale-engine] [xecutorLoop-1-1] i.l.core.protocol.ConnectionWatchdog     : Reconnecting, last destination was localhost/127.0.0.1:55687
2026-10-09T08:59:17.574Z  WARN 26304 --- [flash-sale-engine] [ioEventLoop-4-2] i.l.core.protocol.ConnectionWatchdog     : Cannot reconnect to [localhost/<unresolved>:55687]: Connection reset

java.net.SocketException: Connection reset
	at java.base/sun.nio.ch.SocketChannelImpl.throwConnectionReset(SocketChannelImpl.java:401) ~[na:na]
	at java.base/sun.nio.ch.SocketChannelImpl.read(SocketChannelImpl.java:434) ~[na:na]
	at io.netty.buffer.AdaptivePoolingAllocator$AdaptiveByteBuf.setBytes(AdaptivePoolingAllocator.java:2549) ~[netty-buffer-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.buffer.AbstractByteBuf.writeBytes(AbstractByteBuf.java:1139) ~[netty-buffer-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.socket.nio.NioSocketChannel.doReadBytes(NioSocketChannel.java:350) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:161) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.handle(AbstractNioChannel.java:445) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.nio.NioIoHandler$DefaultNioRegistration.handle(NioIoHandler.java:388) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.nio.NioIoHandler.processSelectedKey(NioIoHandler.java:596) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.nio.NioIoHandler.processSelectedKeysOptimized(NioIoHandler.java:571) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.nio.NioIoHandler.processSelectedKeys(NioIoHandler.java:512) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.nio.NioIoHandler.run(NioIoHandler.java:484) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.SingleThreadIoEventLoop.runIo(SingleThreadIoEventLoop.java:225) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.SingleThreadIoEventLoop.run(SingleThreadIoEventLoop.java:196) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1204) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]

2026-10-09T08:59:18.206Z  WARN 26304 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.l.SimpleMessageListenerContainer : Consumer raised exception, processing can restart if the connection factory supports it

com.rabbitmq.client.ShutdownSignalException: connection error
	at com.rabbitmq.client.impl.AMQConnection.startShutdown(AMQConnection.java:1014) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.shutdown(AMQConnection.java:1004) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.handleFailure(AMQConnection.java:804) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.access$500(AMQConnection.java:49) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:703) ~[amqp-client-5.30.0.jar:5.30.0]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.io.EOFException
	at java.base/java.io.DataInputStream.readUnsignedByte(DataInputStream.java:297) ~[na:na]
	at com.rabbitmq.client.impl.Frame.readFrom(Frame.java:92) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.SocketFrameHandler.readFrame(SocketFrameHandler.java:199) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:694) ~[amqp-client-5.30.0.jar:5.30.0]
	... 1 common frames omitted

2026-10-09T08:59:18.212Z  INFO 26304 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.l.SimpleMessageListenerContainer : Restarting Consumer@1ca9fd34: tags=[[amq.ctag-4mSwF9C4zlPABSlGsU3AEg]], channel=Cached Rabbit Channel: PublisherCallbackChannelImpl: AMQChannel(amqp://guest@127.0.0.1:63123/,2), conn: Proxy@11e930e Shared Rabbit Connection: SimpleConnection@75defbd2 [delegate=amqp://guest@127.0.0.1:63123/, localPort=47738], acknowledgeMode=AUTO local queue size=0
2026-10-09T08:59:18.216Z  INFO 26304 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:63123]
2026-10-09T08:59:18.219Z  WARN 26304 --- [flash-sale-engine] [127.0.0.1:63123] c.r.c.impl.ForgivingExceptionHandler     : An unexpected connection driver error occurred (Exception message: Connection reset)
2026-10-09T08:59:18.222Z ERROR 26304 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.l.SimpleMessageListenerContainer : Failed to check/redeclare auto-delete queue(s).

org.springframework.amqp.AmqpIOException: java.io.IOException
	at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException(RabbitExceptionTranslator.java:69) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:629) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection(CachingConnectionFactory.java:746) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils.createConnection(ConnectionFactoryUtils.java:256) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.doExecute(RabbitTemplate.java:2239) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2211) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2191) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitAdmin.getQueueInfo(RabbitAdmin.java:472) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitAdmin.getQueueProperties(RabbitAdmin.java:456) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.attemptDeclarations(AbstractMessageListenerContainer.java:1978) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.redeclareElementsIfNecessary(AbstractMessageListenerContainer.java:1941) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.initialize(SimpleMessageListenerContainer.java:1602) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.run(SimpleMessageListenerContainer.java:1400) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.io.IOException
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:140) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQChannel.wrap(AMQChannel.java:136) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.start(AMQConnection.java:414) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1357) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1297) ~[amqp-client-5.30.0.jar:5.30.0]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:676) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:644) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:590) ~[spring-rabbit-4.1.1.jar:4.1.1]
	... 12 common frames omitted
Caused by: com.rabbitmq.client.ShutdownSignalException: connection error
	at com.rabbitmq.utility.ValueOrException.getValue(ValueOrException.java:66) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.utility.BlockingValueOrException.uninterruptibleGetValue(BlockingValueOrException.java:36) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQChannel$BlockingRpcContinuation.getReply(AMQChannel.java:552) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.start(AMQConnection.java:341) ~[amqp-client-5.30.0.jar:5.30.0]
	... 17 common frames omitted
Caused by: java.net.SocketException: Connection reset
	at java.base/sun.nio.ch.NioSocketImpl.implRead(NioSocketImpl.java:318) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.read(NioSocketImpl.java:346) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl$1.read(NioSocketImpl.java:796) ~[na:na]
	at java.base/java.net.Socket$SocketInputStream.read(Socket.java:1099) ~[na:na]
	at java.base/java.io.BufferedInputStream.fill(BufferedInputStream.java:291) ~[na:na]
	at java.base/java.io.BufferedInputStream.implRead(BufferedInputStream.java:325) ~[na:na]
	at java.base/java.io.BufferedInputStream.read(BufferedInputStream.java:312) ~[na:na]
	at java.base/java.io.DataInputStream.readUnsignedByte(DataInputStream.java:295) ~[na:na]
	at com.rabbitmq.client.impl.Frame.readFrom(Frame.java:92) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.SocketFrameHandler.readFrame(SocketFrameHandler.java:199) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:694) ~[amqp-client-5.30.0.jar:5.30.0]
	... 1 common frames omitted

2026-10-09T08:59:18.223Z  INFO 26304 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:63123]
2026-10-09T08:59:18.225Z  WARN 26304 --- [flash-sale-engine] [127.0.0.1:63123] c.r.c.impl.ForgivingExceptionHandler     : An unexpected connection driver error occurred (Exception message: Connection reset)
2026-10-09T08:59:18.324Z  WARN 26304 --- [flash-sale-engine] [ntContainer#0-1] o.s.a.r.l.SimpleMessageListenerContainer : Consumer raised exception, processing can restart if the connection factory supports it

com.rabbitmq.client.ShutdownSignalException: connection error
	at com.rabbitmq.client.impl.AMQConnection.startShutdown(AMQConnection.java:1014) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.shutdown(AMQConnection.java:1004) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.handleFailure(AMQConnection.java:804) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.access$500(AMQConnection.java:49) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:703) ~[amqp-client-5.30.0.jar:5.30.0]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.io.EOFException
	at java.base/java.io.DataInputStream.readUnsignedByte(DataInputStream.java:297) ~[na:na]
	at com.rabbitmq.client.impl.Frame.readFrom(Frame.java:92) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.SocketFrameHandler.readFrame(SocketFrameHandler.java:199) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:694) ~[amqp-client-5.30.0.jar:5.30.0]
	... 1 common frames omitted

2026-10-09T08:59:18.325Z  INFO 26304 --- [flash-sale-engine] [ntContainer#0-1] o.s.a.r.l.SimpleMessageListenerContainer : Restarting Consumer@232fc6a6: tags=[[amq.ctag-QXODc6Ax5ldmc3aRA9dJ8A]], channel=Cached Rabbit Channel: PublisherCallbackChannelImpl: AMQChannel(amqp://guest@127.0.0.1:63123/,1), conn: Proxy@11e930e Shared Rabbit Connection: SimpleConnection@75defbd2 [delegate=amqp://guest@127.0.0.1:63123/, localPort=47738], acknowledgeMode=AUTO local queue size=0
2026-10-09T08:59:18.326Z  INFO 26304 --- [flash-sale-engine] [ntContainer#0-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:63123]
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 38.67 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  46.241 s
[INFO] Finished at: 2026-10-09T08:59:23Z
[INFO] ------------------------------------------------------------------------
exit_code=0
```

Raw output: [final tests](bench/results/confirms/tests-final.log), [HTTP evidence](bench/results/confirms/http/). Source-body comparison confirms every original test body is unchanged: [audit](bench/results/confirms/original-tests-audit.json). Active consumers after replay:

```text
Listing consumers in vhost / ...
queue_name	channel_pid	consumer_tag	ack_required	prefetch_count	active	arguments
order.dlq	<rabbit@af42eb2bb203.1791536156.817.0>	amq.ctag-Mx6q1cxMn6OuZHmIvzUYMw	true	250	true	[]
order.dlq	<rabbit@af42eb2bb203.1791536156.784.0>	amq.ctag-U_0SpbMTDQxJXcHfdShV5A	true	250	true	[]
order.queue	<rabbit@af42eb2bb203.1791536156.794.0>	amq.ctag-ansoQux60gZSmqQ6SNo1Xg	true	250	true	[]
order.queue	<rabbit@af42eb2bb203.1791536156.767.0>	amq.ctag-bRhUyRKotofho22YYseNkQ	true	250	true	[]
```

Matched-setting audit (executed image-layer and harness-hash comparisons):

```json
{
  "app_base_layers_identical": true,
  "before_and_after_fault_harness_sha256_identical": true,
  "harness_sha256": [
    "0815791a72c08f007d23b040ee9c6ab506cb6a62c56cea16aa82f9dc753f6fbd  bench/load.js",
    "bdfc81fa7fd919b2ac91d00f5e719d10ab59e5d69c4dea9d3bec047613ab53de  bench/experiment.py"
  ]
}
```

App source revision for confirm runs and original base image metadata are in each raw run directory and [image.json](bench/results/confirms/image.json). Source-only pre-existing comment diff remains uncommitted. No push was performed.

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
| tests.log | [13, 0, 0, 0] | BUILD SUCCESS | [tests.log](bench/results/outbox-session/baseline/tests.log) |
| compile-attempt-1.log | null | INTERRUPTED / no completed build verdict | [compile-attempt-1.log](bench/results/outbox-session/compile-attempt-1.log) |
| tests-attempt-1.log | null | BUILD FAILURE | [tests-attempt-1.log](bench/results/outbox-session/tests-attempt-1.log) |
| tests-attempt-2.log | [20, 0, 15, 0] | BUILD FAILURE | [tests-attempt-2.log](bench/results/outbox-session/tests-attempt-2.log) |
| tests-attempt-3.log | [20, 0, 0, 0] | BUILD SUCCESS | [tests-attempt-3.log](bench/results/outbox-session/tests-attempt-3.log) |
| tests-attempt-4.log | [21, 0, 0, 0] | BUILD SUCCESS | [tests-attempt-4.log](bench/results/outbox-session/tests-attempt-4.log) |
| full-fault-tests-attempt-1.log | null | INTERRUPTED / no completed build verdict | [full-fault-tests-attempt-1.log](bench/results/outbox-session/full-fault-tests-attempt-1.log) |
| full-fault-tests-attempt-2.log | null | INTERRUPTED / no completed build verdict | [full-fault-tests-attempt-2.log](bench/results/outbox-session/full-fault-tests-attempt-2.log) |
| full-fault-tests-attempt-3.log | [0, 0, 0, 0] | BUILD FAILURE | [full-fault-tests-attempt-3.log](bench/results/outbox-session/full-fault-tests-attempt-3.log) |
| poller-and-core-tests.log | [22, 0, 0, 0] | BUILD SUCCESS | [poller-and-core-tests.log](bench/results/outbox-session/poller-and-core-tests.log) |

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

Retained heap failure: [poller-log-oom.dump](bench/results/outbox-session/poller-log-oom.dump), [poller-log-oom.dumpstream](bench/results/outbox-session/poller-log-oom.dumpstream).

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
Interrupted next run: [interrupted.txt](bench/results/outbox-session/testcontainers/rabbit-2/interrupted.txt), [interruption-counts.log](bench/results/outbox-session/testcontainers/rabbit-2/interruption-counts.log). No final invariant counts are invented for it.

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
Diagnostic restart was recorded under rabbit-5 but occurred during rabbit-6, as the actual timestamps show. Neither is substituted for a standard run. Raw: [additional-diagnostic-restart.log](bench/results/outbox-session/testcontainers-rerun/rabbit-5/additional-diagnostic-restart.log), [drain.log](bench/results/outbox-session/testcontainers-rerun/rabbit-5/drain.log), [internal-management-probe.log](bench/results/outbox-session/testcontainers-rerun/internal-management-probe.log), [interrupted-campaign.txt](bench/results/outbox-session/testcontainers-rerun/interrupted-campaign.txt).

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
- rabbit-1: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-1/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-1/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-1/postgres-snapshot.jsonl)
- rabbit-2: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-2/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-2/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-2/postgres-snapshot.jsonl)
- rabbit-3: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-3/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-3/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-3/postgres-snapshot.jsonl)
- rabbit-4: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-4/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-4/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-4/postgres-snapshot.jsonl)
- rabbit-5: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-5/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-5/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-5/postgres-snapshot.jsonl)
- rabbit-6: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-6/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-6/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-6/postgres-snapshot.jsonl)
- rabbit-7: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-7/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-7/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-7/postgres-snapshot.jsonl)
- rabbit-8: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-8/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-8/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-8/postgres-snapshot.jsonl)
- rabbit-9: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-9/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-9/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-9/postgres-snapshot.jsonl)
- rabbit-10: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/rabbit-10/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/rabbit-10/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/rabbit-10/postgres-snapshot.jsonl)
- kill-1: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/kill-1/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/kill-1/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/kill-1/postgres-snapshot.jsonl)
- kill-2: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/kill-2/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/kill-2/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/kill-2/postgres-snapshot.jsonl)
- kill-3: [outbox-invariants.json](bench/results/outbox-session/testcontainers-final/kill-3/outbox-invariants.json), [summary.json](bench/results/outbox-session/testcontainers-final/kill-3/summary.json), [postgres-snapshot.jsonl](bench/results/outbox-session/testcontainers-final/kill-3/postgres-snapshot.jsonl)

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
an uncontrolled kill alone. See [mid-batch-postgres-activity.json](bench/results/outbox-session/testcontainers-poller-retry/poller-crash/mid-batch-postgres-activity.json).

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
After-load atomic oracle: [outbox-invariants.json](bench/results/outbox-session/after-comparison/outbox-invariants.json).

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
Actual fixture schema: [schema.sql](bench/results/outbox-session/testcontainers/schema.sql); runtime versions: [runtime-versions.log](bench/results/outbox-session/testcontainers/runtime-versions.log).
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
Resource counters before each case and after the oracle are preserved in [resource-audit.json](bench/results/outbox-session/resource-audit.json). Empty/unavailable observations are null. Docker CPU/memory snapshots and free -b output are retained in every stats.log. The failed collector OOM remains a separate negative result; do not infer zero OOM from the earlier pre-oracle inspect.
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

DRAFT: HTTP oversell test: 2000 requests, stock 100, accepted 100, PostgreSQL `100|100|100`, remaining stock 0. Source: [oversell-reconciliation.json](bench/results/outbox-session/testcontainers-final/http/oversell-reconciliation.json).

DRAFT: completed broker-restart reruns 10; invariant passes 10. Per-run source files listed above; retained collector failure and interrupted attempt remain disclosed.
> RabbitMQ-era benchmark sections and raw artifacts in this file are
> pre-migration evidence. They are not Kafka measurements.
