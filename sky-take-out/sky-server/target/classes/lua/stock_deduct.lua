-- 库存扣减 + 防超卖 + 一人一单
-- KEYS[1]: seckill:stock:{goodsId}
-- KEYS[2]: seckill:user:limit:{userId}:{goodsId}
-- KEYS[3]: seckill:sold:{goodsId}
-- ARGV[1]: quantity
-- ARGV[2]: limitCount
-- ARGV[3]: userId
-- ARGV[4]: expireTime (seconds)

local stock = redis.call('GET', KEYS[1])
local userPurchased = redis.call('GET', KEYS[2]) or '0'
local quantity = tonumber(ARGV[1])
local limitCount = tonumber(ARGV[2])
local expireTime = tonumber(ARGV[4])

if (not stock) or (tonumber(stock) < quantity) then
    return cjson.encode({ code = 50006, msg = '库存不足' })
end

if (tonumber(userPurchased) + quantity) > limitCount then
    return cjson.encode({ code = 50008, msg = '用户已达购买上限' })
end

redis.call('DECRBY', KEYS[1], quantity)
redis.call('INCRBY', KEYS[2], quantity)
if expireTime and expireTime > 0 then
    redis.call('EXPIRE', KEYS[2], expireTime)
end
redis.call('INCRBY', KEYS[3], quantity)

local remaining = tonumber(stock) - quantity
return cjson.encode({ code = 1, msg = 'success', data = { remainingStock = remaining } })



