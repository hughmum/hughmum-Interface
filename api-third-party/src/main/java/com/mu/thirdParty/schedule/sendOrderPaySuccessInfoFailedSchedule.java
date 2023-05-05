package com.mu.thirdParty.schedule;

import com.mu.thirdParty.common.RabbitOrderPaySuccessUtils;
import common.constant.LockConstant;
import common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 沐
 */
@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class sendOrderPaySuccessInfoFailedSchedule {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private RabbitOrderPaySuccessUtils rabbitOrderUtils;

    /**
     * 消息可靠性保证（发送端可靠性保证）
     * 每分钟从生产者redis中重新发送发送订单支付成功的消息
     * 通过使用 Redis 分布式锁，保证了在同一时刻只有一个进程可以执行其中的代码，避免了多个进程同时执行相同任务的问题。这样可以保证代码的正确性和稳定性。
     */
    @Scheduled(cron = "*/60 * * * * ?")//  Spring 框架中用于定时任务的注解 表示每隔 60 秒执行一次任务
    public void sendFailedOrderPaySuccess(){
        RLock lock = redissonClient.getLock(LockConstant.order_pay_success);
        try {
            // 为加锁等待20秒时间，并在加锁成功10秒钟后自动解开
            boolean tryLock = lock.tryLock(20, 10, TimeUnit.SECONDS);
            if (tryLock){
                //重新向mq中发送订单消息
                // Redis 中获取所有键名以 RedisConstant.ORDER_PAY_SUCCESS_INFO 开头的键，这些键存储的是支付成功的订单号。
                Set keys = redisTemplate.keys(RedisConstant.ORDER_PAY_SUCCESS_INFO + "*");
                //遍历所有的键，获取对应的订单号，并删除 Redis 中该键的记录。
                for (Object key : keys) {
                    String orderSn = (String) redisTemplate.opsForValue().get(key);
                    //删除reids中的该条记录
                    redisTemplate.delete(key.toString());
                    rabbitOrderUtils.sendOrderPaySuccess(orderSn); //重新向 MQ 中发送订单支付成功的消息。
                }
            }
        } catch (InterruptedException e) {
            log.error("===定时任务:获取失败生产者发送消息redis出现bug===");
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }
}
