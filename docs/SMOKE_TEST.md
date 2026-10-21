# Local smoke test

Run the checks through nginx:

```bash
scripts/smoke-test.sh http://localhost
```

Each run initializes a new product ID based on the current nanosecond timestamp,
so it is safe to repeat without resetting the sale. To manually reinitialize a
sale, use a product ID with no existing orders:

```bash
curl -X POST 'http://localhost/api/v1/orders/init?productId=<new-id>&stock=1'
```

The script verifies the frontend, SPA fallback, stock, order terminal status,
duplicate idempotency behavior, out-of-stock behavior, and that Actuator health
and metrics are not public through nginx.
