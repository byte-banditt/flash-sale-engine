# Reproducing measurements

Run experiments sequentially. Load generator and services share one laptop;
concurrent experiments or builds invalidate controlled comparisons.

`baseline.py` executes the unchanged existing k6 scenario against a host JVM,
clearing orders and Redis before each run and recording persistence/stock after
queue drain. Baseline was recorded and committed before source edits.

`http_concurrency.py` releases concurrent HTTP tasks against nginx and saves every
response and upstream. It checks accepted, confirmed and distinct-key counts,
remaining stock, multiple upstreams and drained queues. Stop load before running it.

`experiment.py OUTPUT_DIR [none|kill|rabbit|redis]` runs instrumented k6 load.
Normal runs use the original ramp. Fault runs use constant VUs for sustained load.
Normal stages, VU targets, request contents, thresholds and sleep match the original
scenario; fault load stays constant throughout injection and recovery. Instrumentation logs each accepted key. Every replicated ramp and fault
run uses this logging, so scaling and before/after confirms comparisons use matched
instrumentation. It differs from the original host baseline.

The runner starts faults at the same elapsed point in sustained load. For kill, it waits after
SIGKILL before starting the same container. Broker and Redis experiments use
`docker restart`. It does not restart or reset services before reconciliation.
Threshold failures are preserved, not discarded from the campaign.

Reconciliation waits for both queues' ready and unacknowledged counts to remain
empty across successive polls; timeout is reported. It records Postgres rows and
Redis idempotency keys, compares accepted keys with CONFIRMED keys, and saves
lost/stuck key lists.

Definitions:

- Lost orders: distinct keys with HTTP 202 but no CONFIRMED row after drain.
- Duplicate orders: CONFIRMED row count minus distinct CONFIRMED keys.
- Stuck reservations: Redis idempotency keys without CONFIRMED rows after drain.
  This includes reservations whose HTTP request never received 202.
- Oversold count: CONFIRMED quantities above initialized inventory (unit quantity
  requests). `stock_accounting_excess` additionally checks whether confirmed
  inventory plus remaining stock exceeds initialized stock.
- `unacknowledged_confirmed`: CONFIRMED keys absent from HTTP-202 ledger;
  an ambiguous client outcome, not a missing database order.

`stats.log` contains Docker's displayed CPU/memory values sampled during load.
Cgroup files record CPU throttling and OOM events; inspect records OOMKilled and
limits. Cgroup counters can reset when containers restart. Maximum observed
samples are not guaranteed peaks. Docker CPU percentages can exceed one core.

`summarize.py RESULTS_DIR` checks HTTP-202 ledger against k6 counter and prints
exact stored metrics and maximum observed resource samples without adding rounding.
Full precision exports are authoritative; k6 summaries use display precision.

Redis restart is observational, not a pass/fail HA test. Inspect runtime persistence
settings and logs rather than assuming absent Compose persistence configuration
disables image-default RDB behavior.
