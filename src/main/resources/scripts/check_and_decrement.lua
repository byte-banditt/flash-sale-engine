-- check_and_decrement.lua
-- KEYS[1]: product stock key (e.g., product:101:stock)
-- ARGV[1]: requested quantity (e.g., 1)

local stock = tonumber(redis.call('GET', KEYS[1]))
local requested = tonumber(ARGV[1])

if not stock then
    return -1 -- Key does not exist / uninitialized
end

if stock >= requested then
    redis.call('DECRBY', KEYS[1], requested)
    return 1 -- Success: stock reserved
else
    return 0 -- Out of stock
end