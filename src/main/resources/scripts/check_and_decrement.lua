-- check_and_decrement.lua
-- KEYS[1]: product stock key (e.g., product:101:stock)
-- KEYS[2]: idempotency key
-- ARGV[1]: requested quantity (e.g., 1)
-- ARGV[2]: generated order ID

if redis.call('EXISTS', KEYS[2]) == 1 then
    return 2 -- This key already owns a reservation.
end

local stock = tonumber(redis.call('GET', KEYS[1]))
local requested = tonumber(ARGV[1])

if not stock then
    return -1 -- Key does not exist / uninitialized
end

if stock >= requested then
    redis.call('DECRBY', KEYS[1], requested)
    redis.call('SET', KEYS[2], ARGV[2])
    return 1 -- Success: stock reserved
else
    return 0 -- Out of stock
end
