import http from 'k6/http';
import exec from 'k6/execution';
import { sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const productId = Number(__ENV.PRODUCT_ID || 101);
const stock = Number(__ENV.STOCK || 100000);

http.setResponseCallback(http.expectedStatuses(200, 202, 409));

const acceptedOrders = new Counter('accepted_orders');
const unexpectedResponses = new Rate('unexpected_responses');

export const options = {
  scenarios: {
    orders: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 20 },
        { duration: '1m', target: 100 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    unexpected_responses: ['rate<0.01'],
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(99)<1000'],
  },
};

export function setup() {
  const response = http.post(`${baseUrl}/api/v1/orders/init?productId=${productId}&stock=${stock}`);
  if (response.status !== 200) {
    throw new Error(`Stock initialization failed: HTTP ${response.status}: ${response.body}`);
  }
  return { runId: `${Date.now()}-${Math.floor(Math.random() * 1000000000)}` };
}

export default function (data) {
  const key = `k6-${data.runId}-${exec.vu.idInTest}-${exec.scenario.iterationInTest}`;
  const response = http.post(`${baseUrl}/api/v1/orders`, JSON.stringify({
    productId,
    quantity: 1,
    idempotencyKey: key,
  }), { headers: { 'Content-Type': 'application/json' } });

  if (response.status === 202) acceptedOrders.add(1);
  unexpectedResponses.add(![200, 202, 409].includes(response.status));
  sleep(0.1);
}
