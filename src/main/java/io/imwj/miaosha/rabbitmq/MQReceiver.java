package io.imwj.miaosha.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * rabbitMQ消息接收者
 * @author langao_q
 * @since 2021-01-11 17:12
 */
@Slf4j
@Service
public class MQReceiver {

    @RabbitListener(queues = MQConfig.QUEUE_DIRECT)
    public void receive(String msg){
        log.info("MQReceiver.receive" + msg);
    }

}
