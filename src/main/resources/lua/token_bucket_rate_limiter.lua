--将请求url当作key
local url = KEYS[1]
--当前请求获取令牌的时间
local current_time = tonumber(KEYS[2])

--令牌桶容量
local capacity = 5
--令牌桶剩余的令牌数
local left_token
--令牌生成速率
local generate_rate = 1
--上次获取令牌的时间
local last_time
--上次获取令牌的时间与当前时间的差值
local interval_time
--返回结果
local result

--从redis获取上次时间和剩余的令牌数
local token_info = redis.call("hmget",url,"last_time","left_token")
last_time = token_info[1]
left_token = tonumber(token_info[2])

--判断redis是否存在key=url的数据
if last_time == nil or left_token == nil then
    last_time = current_time
    left_token = capacity
end

--计算时间间隔段内生产的令牌数量
interval_time = current_time - last_time
local generate_num = interval_time*generate_rate

--防止整数溢出
if generate_num < 0 then
    generate_num = capacity
end

--防止令牌溢出
left_token = generate_num+left_token
if left_token > capacity then
    left_token = capacity
end

--获取令牌
last_time = current_time
if left_token > 0 then
    left_token = left_token - 1
    result = 1
else
    result = 0
end

--将请求时间和剩余令牌数存入redis
redis.call("hmset",url,"last_time",last_time,"left_token",left_token)

return result





