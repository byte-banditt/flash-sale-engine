# Unchanged-code reproduction

Initial HEAD: `c6163ac8c68a24ca246c0a30a982d9e183b7f9a3`.

Original Maven test result: 8 tests, no failures/errors/skips; see setup/tests.log.
All original test bodies were checked byte-for-byte against the initial HEAD.

[
  {
    "group": "repro",
    "runs": 10,
    "violating_runs": 4,
    "i1_excess_units": 25,
    "i2_violations": 25,
    "lost": 0,
    "duplicates": 0,
    "stuck": 0,
    "stuck_decisions": 0,
    "rule_of_three_upper_bound": null
  }
]

| group | run | http_202 | confirmed | redis_stock | i1_delta_units | i2_violations | lost | duplicates | stuck | stuck_decisions | http_error_rate | req_per_s | p50_ms | p95_ms | p99_ms | k6_exit |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| repro | rabbit-1 | 48222 | 48225 | 51778 | 3 | 3 | 0 | 0 | 0 | 0 | 0.10537446895349054 | 448.7886895673295 | 6.630068 | 14.726210899999996 | 29.08196867999985 | 99 |
| repro | rabbit-10 | 51496 | 51496 | 48504 | 0 | 0 | 0 | 0 | 0 | 0 | 0.06787698879577171 | 459.98843281416276 | 6.711908 | 10.439460799999999 | 15.07479134000001 | 99 |
| repro | rabbit-2 | 49670 | 49683 | 50330 | 13 | 13 | 0 | 0 | 0 | 0 | 0.10213119791760814 | 454.39561683830607 | 6.307512 | 10.527123 | 14.840332800000002 | 99 |
| repro | rabbit-3 | 50461 | 50468 | 49539 | 7 | 7 | 0 | 0 | 0 | 0 | 0.06111969040132473 | 447.2556073115293 | 6.350171 | 10.197942399999997 | 14.269778240000043 | 99 |
| repro | rabbit-4 | 50198 | 50198 | 49802 | 0 | 0 | 0 | 0 | 0 | 0 | 0.08975684055921232 | 459.0144536415538 | 6.810147 | 10.9667094 | 19.315309959999897 | 99 |
| repro | rabbit-5 | 51033 | 51033 | 48967 | 0 | 0 | 0 | 0 | 0 | 0 | 0.0743642761272536 | 459.03306404729807 | 6.775098 | 11.0743944 | 24.231341869999966 | 99 |
| repro | rabbit-6 | 49713 | 49713 | 50287 | 0 | 0 | 0 | 0 | 0 | 0 | 0.09676598837209302 | 458.2322006828908 | 6.786129 | 11.476123199999998 | 28.83634894000001 | 99 |
| repro | rabbit-7 | 51121 | 51121 | 48879 | 0 | 0 | 0 | 0 | 0 | 0 | 0.07675359387416023 | 460.9555084589265 | 6.548674500000001 | 10.1276057 | 12.534952340000007 | 99 |
| repro | rabbit-8 | 50978 | 50980 | 49022 | 2 | 2 | 0 | 0 | 0 | 0 | 0.07511021607794045 | 458.97810671450725 | 6.887033 | 10.806290199999998 | 18.561960399999997 | 99 |
| repro | rabbit-9 | 51418 | 51418 | 48582 | 0 | 0 | 0 | 0 | 0 | 0 | 0.06965930268324015 | 460.17213255857195 | 6.725151 | 10.559860599999999 | 13.50877944 | 99 |

Each violating key is retained in each run's invariants.json. Existing app logs contain no per-key confirm/refund/commit events; timeline.json explicitly records unobserved rather than inferring event times from Hibernate created_at. Observability-only diagnostic runs are required before fixing the decision race.

k6, nginx, application replicas, Redis, RabbitMQ and PostgreSQL all ran on the same laptop. No source edits were made before reproduction. The pre-existing RabbitMQConfig comment cleanup is recorded separately in setup/preexisting.diff and is excluded from this commit.
