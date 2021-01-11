package io.imwj.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitMQ配置类
 *
 * @author langao_q
 * @since 2021-01-11 17:12
 */
@Configuration
public class MQConfig {

    public static final String QUEUE_DIRECT = "QUEUE_DIRECT";

    /**
     * 注入交换机
     * @return
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE_DIRECT, true);
    }

}
