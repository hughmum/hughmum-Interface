package com.mu.api.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

//注释
/*这段代码实现的是一个基于令牌桶算法的限流器，用于限制每个手机号在一分钟内只能发送一次短信。具体的令牌桶算法过程如下：
*1初始化一个容量为1的令牌桶，每个手机号对应一个令牌桶。

2每个令牌桶按照固定速率（permitsPerMinute）填充令牌，以保证每个手机号在一分钟内只能发送一次短信。

3当有短信请求到来时，从对应的令牌桶中获取一个令牌。

4如果令牌桶中没有令牌，则表示该手机号在一分钟内已经发送过短信，拒绝该请求。

5如果令牌桶中有令牌，则表示该手机号还没有发送过短信，允许请求通过，并从令牌桶中移除一个令牌。
* 在一分钟内，如果有多个请求到来，则前面的请求已经消耗了令牌，后面的请求就无法通过限流器，直到一分钟后令牌桶重新填充令牌。
该限流器是如何与发短信结合的呢？在实现上，该限流器使用Redis作为令牌桶容器，每个手机号对应一个令牌桶，每个令牌桶包含一个容量（maxPermits）和当前令牌数（tokens）。在请求到来时，先根据当前时间戳、上次填充时间戳和令牌桶容量计算出当前令牌数，如果当前令牌数大于等于1，则获取令牌并返回true，否则返回false。在获取令牌时，需要先将令牌数减1，然后更新Redis中的令牌桶容器，以保证其他请求不能再使用该令牌。同时，为了防止内存泄漏，需要设置Redis中令牌桶的过期时间（EXPIRE_TIME）。因此，该限流器就可以与实际的短信发送业务结合起来，实现限制每个手机号在一分钟内只能发送一次短信的功能。
*
* */


/**
 * @author 沐
 */
@Component
public class RedisTokenBucket {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private final long EXPIRE_TIME = 400; //400秒后过期

    /**
     * 令牌桶算法，一分钟以内，每个手机号只能发送一次
     * @param phoneNum
     * @return
     */
    public boolean tryAcquire(String phoneNum) {
        // 每个手机号码一分钟内只能发送一条短信
        int permitsPerMinute = 1;
        // 令牌桶容量
        int maxPermits = 1;
        // 获取当前时间戳
        long now = System.currentTimeMillis();
        // 计算令牌桶内令牌数
        int tokens = Integer.parseInt(redisTemplate.opsForValue().get(phoneNum + "_tokens") == null ? "0" : redisTemplate.opsForValue().get(phoneNum + "_tokens"));
        // 计算令牌桶上次填充的时间戳
        long lastRefillTime = Long.parseLong(redisTemplate.opsForValue().get(phoneNum + "_last_refill_time") == null ? "0" : redisTemplate.opsForValue().get(phoneNum + "_last_refill_time"));
        // 计算当前时间与上次填充时间的时间差
        long timeSinceLast = now - lastRefillTime;
        // 计算需要填充的令牌数
        int refill = (int) (timeSinceLast / 1000 * permitsPerMinute / 60);
        // 更新令牌桶内令牌数
        tokens = Math.min(refill + tokens, maxPermits);
        // 更新上次填充时间戳
        redisTemplate.opsForValue().set(phoneNum + "_last_refill_time", String.valueOf(now),EXPIRE_TIME, TimeUnit.SECONDS);
        // 如果令牌数大于等于1，则获取令牌
        if (tokens >= 1) {
            tokens--;
            redisTemplate.opsForValue().set(phoneNum + "_tokens", String.valueOf(tokens),EXPIRE_TIME, TimeUnit.SECONDS);
            // 如果获取到令牌，则返回true
            return true;
        }
        // 如果没有获取到令牌，则返回false
        return false;
    }
}
