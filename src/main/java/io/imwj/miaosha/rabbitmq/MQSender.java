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
     * 发送消息到mq中
     * @param msg
     */
    public void send(Object msg) {
        String msgStr = JsonUtil.beanToString(msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_DIRECT, msgStr);
        log.info("MQSender.send:" + msgStr);
    }
}
