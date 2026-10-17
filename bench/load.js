import http from 'k6/http';
import exec from 'k6/execution';
import { sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';
const accepted = new Counter('accepted_orders');
const unexpected = new Rate('unexpected_responses');
http.setResponseCallback(http.expectedStatuses(200,202,409));
export const options = {
  scenarios: { orders: __ENV.CI_BENCH === 'true'
    ? {executor:'constant-vus', vus:20, duration:'25s'}
    : __ENV.FAULT_LOAD === 'true'
    ? {executor:'constant-vus', vus:50, duration:'2m'}
    : { executor: 'ramping-vus', startVUs: 0,
      stages: [{duration:'30s',target:20},{duration:'1m',target:100},{duration:'30s',target:0}] } },
  thresholds: { unexpected_responses:['rate<0.01'], http_req_failed:['rate<0.01'], http_req_duration:['p(99)<1000'] },
  summaryTrendStats:['avg','min','med','max','p(90)','p(95)','p(99)'],
};
export function setup() {
  const res=http.post(`${__ENV.BASE_URL}/api/v1/orders/init?productId=101&stock=100000`);
  if(res.status!==200) throw new Error(`Stock init: ${res.status} ${res.body}`);
  return {runId:__ENV.RUN_ID};
}
export default function(data) {
  const key=`bench-${data.runId}-${exec.vu.idInTest}-${exec.scenario.iterationInTest}`;
  const res=http.post(`${__ENV.BASE_URL}/api/v1/orders`,JSON.stringify({productId:101,quantity:1,idempotencyKey:key}),{headers:{'Content-Type':'application/json'}});
  if(res.status===202) { accepted.add(1); console.log(`ACCEPTED ${key}`); }
  unexpected.add(![200,202,409].includes(res.status));
  sleep(0.1);
}
