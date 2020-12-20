--将请求url当作key
local url = KEYS[1]
--当前请求的时间
local current_time = tonumber(KEYS[2])

--滑动时间窗口的大小
local window_period = 60
--滑动时间窗口的时间段内允许的最大请求数量
local max_num = 10
--返回结果
local result

--每访问一次,在Zset中增加一次记录
redis.call("zAdd",url,current_time,current_time)
--移除掉时间窗口之外的访问记录
redis.call("zRemRangeByScore",url,0,current_time-window_period)
--获取时间窗口内的访问记录数量
result = redis.call("zCard",url)
if result == nil or result < max_num then
    result = 1
else
    result = 0
end

return result





