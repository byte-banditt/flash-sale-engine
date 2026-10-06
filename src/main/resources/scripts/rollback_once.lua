-- KEYS[1]: product stock key; KEYS[2]: permanent rollback marker for orderId
-- ARGV[1]: quantity
-- Marker and increment share one Redis script: redelivery cannot increment twice.
if redis.call('SETNX', KEYS[2], '1') == 0 then
    return 0
end
redis.call('INCRBY', KEYS[1], ARGV[1])
return 1
