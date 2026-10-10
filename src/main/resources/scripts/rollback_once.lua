-- KEYS: stock, rollback marker, decision, optional idempotency reservation.
-- ARGV: quantity, orderId. Decision and refund share one atomic Redis script.
local decision = redis.call('GET', KEYS[3])
if decision == 'CONSUMER' then
    return 0
end
if decision == 'REFUND' or redis.call('EXISTS', KEYS[2]) == 1 then
    return 0
end
if decision then
    return redis.error_reply('Unknown order decision')
end
-- Validate before any writes: Lua runtime errors do not undo earlier writes.
if not tonumber(redis.call('GET', KEYS[1])) then
    return redis.error_reply('Stock key missing or nonnumeric; rollback not recorded')
end
if KEYS[4] and redis.call('GET', KEYS[4]) ~= ARGV[2] then
    return redis.error_reply('Matching reservation missing; rollback not recorded')
end
redis.call('INCRBY', KEYS[1], ARGV[1])
redis.call('SET', KEYS[3], 'REFUND')
redis.call('SET', KEYS[2], '1')
if KEYS[4] then
    redis.call('DEL', KEYS[4])
end
return 1
