package io.imwj.miaosha.rabbitmq;

import io.imwj.miaosha.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * rabbitMQ消息生产者
 * @author langao_q
 * @since 2021-01-11 17:11
 */
@Slf4j
@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * DIRECT模式
     * 发送消息到mq中
     * @param msg
     */
    public void send(Object msg) {
        String msgStr = JsonUtil.beanToString(msg);
        amqpTemplate.convertAndSend(MQConfig.DIRECT_QUEUE, msgStr);
        log.info("MQSender.send:" + msgStr);
    }

    /**
     * TOPIC模式
     * 发送消息到mq中
     * @param msg
     */
    public void sendTopic(Object msg) {
        String msgStr = JsonUtil.beanToString(msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHAGE, "topic.key1", msgStr + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHAGE, "topic.key2", msgStr + "2");
        log.info("MQSender.sendTopic:" + msgStr);
    }

    /**
     * FANOUT模式
     * 发送消息到mq中
     * @param msg
     */
    public void sendFanout(Object msg) {
        String msgStr = JsonUtil.beanToString(msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHAGE, "", msgStr + "1");
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHAGE, "", msgStr + "2");
        log.info("MQSender.sendFanout:" + msgStr);
    }

    /**
     * DIRECT模式
     * 发送秒杀消息到mq中
     * @param msg
     */
    public void miaoShaSend(MiaoshaMessage msg) {
        String msgStr = JsonUtil.beanToString(msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msgStr);
        log.info("----秒杀数据入队----MQSender.miaoShaSend：" + msgStr);
    }
}
