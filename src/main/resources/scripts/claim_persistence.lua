-- KEYS: decision, rollback marker, idempotency reservation; ARGV: orderId.
-- A durable winner, not a per-delivery lock: a crashed consumer can resume.
local decision = redis.call('GET', KEYS[1])
if decision == 'REFUND' or redis.call('EXISTS', KEYS[2]) == 1 then
    return 0
end
if decision == 'CONSUMER' then
    return 1
end
if decision then
    return redis.error_reply('Unknown order decision')
end
if redis.call('GET', KEYS[3]) ~= ARGV[1] then
    return redis.error_reply('Matching reservation missing; persistence not claimed')
end
redis.call('SET', KEYS[1], 'CONSUMER')
return 1
