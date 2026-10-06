-- KEYS[1]: product stock key; KEYS[2]: permanent rollback marker for orderId
-- ARGV[1]: quantity
-- ARGV[2]: orderId (only when releasing idempotency key after publish failure)
-- Marker and increment share one Redis script: redelivery cannot increment twice.
if not tonumber(redis.call('GET', KEYS[1])) then
    return redis.error_reply('Stock key missing or nonnumeric; rollback not recorded')
end
if redis.call('SETNX', KEYS[2], '1') == 0 then
    return 0
end
redis.call('INCRBY', KEYS[1], ARGV[1])
if KEYS[3] and redis.call('GET', KEYS[3]) == ARGV[2] then
    redis.call('DEL', KEYS[3])
end
return 1
