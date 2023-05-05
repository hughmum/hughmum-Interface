package com.mu.thirdParty.listener;

import com.rabbitmq.client.Channel;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import com.mu.thirdParty.Tencent.SendSmsUtils;
import common.constant.RabbitMqConstant;
import common.to.SmsTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author 沐
 * 发送短信验证码
 */
@Slf4j
@Component
public class SendSmsListener  {

    @Autowired
    private SendSmsUtils sendSmsUtils;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 监听普通队列 - 实际发送短信
     * 出现异常，使用消息重传。
     * @param sms
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "api-sms-queue")
    public void listener(SmsTo sms, Message message, Channel channel) throws IOException {
        String messageId = message.getMessageProperties().getMessageId();
        //获取该消息已重试的次数 retryCount
        int retryCount = (int) redisTemplate.opsForHash().get(RabbitMqConstant.SMS_HASH_PREFIX+messageId, "retryCount");
        if (retryCount >= 3){
            //投递次数大于三次，放入死信队列
            log.error("重试次数大于三次");
            //如果消息已重试的次数大于等于3，就会将该消息投递到死信队列，并从 Redis 中删除该消息的缓存。
            // 在这里，使用了 channel 对象的 basicReject 方法来将消息投递到死信队列。
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            redisTemplate.delete(RabbitMqConstant.SMS_HASH_PREFIX + messageId);
            return;
        }
        try{
            String mobile = sms.getMobile();
            String code = sms.getCode();
            if (null == mobile || null == code){
                throw new RuntimeException("请求参数错误");
            }
            //发送验证码
            SendSmsResponse response = null;
            response = sendSmsUtils.sendSmsResponse(mobile.toString(), code);
            SendStatus[] sendStatusSet = response.getSendStatusSet();
            SendStatus sendStatus = sendStatusSet[0];
            String statusCode = sendStatus.getCode();
            String res = sendStatus.getMessage();
            if(!"OK".equals(statusCode) || "send success".equals(res)){
                throw new RuntimeException("发送验证码失败");
            }
            //手动确认消息
            /*
            当 RabbitMQ 服务器将消息推送给消费者时，会将消息标记为 unacknowledged 状态。这意味着 RabbitMQ 认为消息已经被消费者接收并正在处理，但还没有得到确认。在这种情况下，如果消费者在处理消息期间崩溃或断开连接，RabbitMQ 将重新将该消息发送给其他消费者。这会导致消息被重复处理或消息丢失。
            为了避免这种情况，消费者需要向 RabbitMQ 服务器发送确认消息，即告诉 RabbitMQ 服务器已经成功处理了该消息。这可以通过 basicAck 方法来完成。basicAck 方法接受两个参数，分别是 deliveryTag 和 multiple。
            deliveryTag 表示消息的投递标签，它是一个唯一的、递增的整数。消费者必须在此基础上进行确认。
            multiple 是一个布尔值，表示是否确认所有传递标记小于或等于给定标记的所有消息。通常情况下，我们将其设置为 false，即只确认当前消息。
            在这段代码中，channel.basicAck(message.getMessageProperties().getDeliveryTag(),false) 表示消费者已经成功处理了该消息，并将其从队列中删除。其中，message.getMessageProperties().getDeliveryTag() 获取了消息的投递标记，即 deliveryTag。false 参数表示只确认当前消息。
            总之，这行代码的作用是向 RabbitMQ 服务器发送确认消息，告诉服务器已经成功处理并删除了该消息。
            */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("发送短信成功--{}",sms);
            //发送成功后，从redis中删除该缓存
            Boolean delete = redisTemplate.delete(RabbitMqConstant.SMS_HASH_PREFIX + messageId);
        }catch (Exception e){
            //进行重试，重试次数加1
            redisTemplate.opsForHash().put(RabbitMqConstant.SMS_HASH_PREFIX+messageId,"retryCount",retryCount+1);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    /**
     * 监听死信队列 - 记录发送短信失败后的日志
     * （可以记录日志，入库，人工干预处理）
     * @param sms
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "sms.delay.queue")
    public void delayListener(SmsTo sms, Message message, Channel channel) throws IOException {
        try{
            log.error("监听到死信队列消息==>{}",sms);
            //确认消费者已经成功消费了一条消息，并告诉 RabbitMQ 服务器可以将该消息从队列中删除。
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
