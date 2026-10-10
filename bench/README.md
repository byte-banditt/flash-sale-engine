# Reproducing measurements

Run experiments sequentially. Load generator and services share one laptop;
concurrent experiments or builds invalidate controlled comparisons.

`baseline.py` executes the unchanged existing k6 scenario against a host JVM,
clearing orders and Redis before each run and recording persistence/stock after
queue drain. Baseline was recorded and committed before source edits.

`http_concurrency.py` releases concurrent HTTP tasks against nginx and saves every
response and upstream. It checks accepted, confirmed and distinct-key counts,
remaining stock, multiple upstreams and drained queues. Stop load before running it.
An optional output-directory argument preserves separate pre/post-confirm results.

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

`audit.py` verifies every completed accepted-key ledger and saves confirmed keys
without a recorded HTTP 202. `compare.py` prints exact chronological before/after
normal-run deltas and lost/stuck fault comparisons. Neither substitutes missing runs.

Passing lost/stuck counts is not sufficient to pass all invariants. Inspect
`stock_accounting_excess` and confirmed-without-202 keys; the confirm broker-restart
campaign exposed persisted orders whose reservations had been released.

Redis restart is observational, not a pass/fail HA test. Inspect runtime persistence
settings and logs rather than assuming absent Compose persistence configuration
disables image-default RDB behavior.

## Refund/persistence decision follow-up

Keep `experiment.py` and `load.js` unchanged for comparisons. Use the distributed
profile with the same replicas and JVM/container limits, running each campaign
sequentially. `decision_reconcile.py OUTPUT_DIR` adds raw PostgreSQL order IDs,
quantities, creation timestamps and an atomic Redis key/value snapshot, plus
I1/I2/I3 audits and every violating key. REFUND markers cannot satisfy I2.
A CONSUMER marker without a persisted order is counted separately as stuck.

`decision_timeline.py OUTPUT_DIR` extracts actual per-key trace events. Original
unlogged sequences remain explicitly unobserved. `trace-compose.yml` enables
DEBUG only for diagnostic runs; remove that override for performance comparisons.
DB creation timestamps never substitute for transaction afterCommit observations.

`verify_original_tests.py` checks each original test body against the recorded
source HEAD. `decision_report.py` writes exact metrics, campaign counts, sampled
resources, and normal-ramp deltas. `decision_document.py` requires completed
campaigns and embeds their evidence into REPORT.md and RESULTS.md.

`decision_cost.py NEW_OUTPUT_DIR` measures cached decision-Lua RPCs with k6
stopped, using its own temporary Redis keys. This Python/localhost component
measurement is distinct from HTTP overhead through the app's Lettuce connection.
Raw samples, errors, and cleanup scope are recorded; no Redis database is flushed.


## Transactional outbox follow-up

The order path now uses PostgreSQL stock, pending orders and outbox intent in
one transaction. Use the explicit mode when running the existing harness:

```bash
OUTBOX_MODE=true python3 bench/experiment.py bench/results/outbox-session/after-comparison none
python3 bench/outbox_reconcile.py bench/results/outbox-session/after-comparison
```

`bench/load.js` is unchanged. Testcontainers supply their mapped nginx endpoint
and actual container IDs through the harness environment. Queue observations
retain failed CLI output and can use management counters through a surviving
app when Docker exec against the broker fails. Management statistics have their
refresh delay; PostgreSQL settlement and repeated quiet polls are both required.
Missing resource observations remain null rather than invented zeroes.

The outbox oracle streams rows within one repeatable-read snapshot. Its failed
aggregate-JSON predecessor and interrupted campaigns remain under
`results/outbox-session/`. Historical Redis reconciliation tools describe the
older source; use the outbox oracle for the new implementation. Results are in
[OUTBOX_REPORT.md](OUTBOX_REPORT.md) and the appended root RESULTS.md section.
