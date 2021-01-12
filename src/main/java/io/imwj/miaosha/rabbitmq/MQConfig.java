package io.imwj.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
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

    public static final String DIRECT_QUEUE = "DIRECT_QUEUE";

    public static final String TOPIC_QUEUE1 = "TOPIC_QUEUE1";
    public static final String TOPIC_QUEUE2 = "TOPIC_QUEUE2";
    public static final String FANOUT_QUEUE1 = "FANOUT_QUEUE1";
    public static final String FANOUT_QUEUE2 = "FANOUT_QUEUE2";

    public static final String TOPIC_EXCHAGE = "TOPIC_EXCHAGE";
    public static final String FANOUT_EXCHAGE = "FANOUT_EXCHAGE";

    //秒杀使用
    public static final String MIAOSHA_QUEUE = "MIAOSHA_QUEUE";

    /**
     * 注入秒杀队列
     * @return
     */
    @Bean
    public Queue miaoShaQueue(){
        return new Queue(MIAOSHA_QUEUE, true);
    }

    /**
     * DIRECT模式
     * 1.注入队列
     * 2.交换机等默认即可
     * @return
     */
    @Bean
    public Queue queue(){
        return new Queue(DIRECT_QUEUE, true);
    }

    /**
     * TOPIC模式
     * 1.注入队列
     * 2.注入交换机
     * 3.绑定队列和交换机
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1, true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2, true);
    }
    @Bean
    public TopicExchange topicExchage(){
        return new TopicExchange(TOPIC_EXCHAGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchage()).with("topic.key1");
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchage()).with("topic.#");
    }

    /**
     * FANOUT模式
     * 1.注入队列
     * 2.注入交换机
     * 3.绑定队列和交换机
     */
    @Bean
    public Queue fanoutQueue1(){
        return new Queue(FANOUT_QUEUE1, true);
    }
    @Bean
    public Queue fanoutQueue2(){
        return new Queue(FANOUT_QUEUE2, true);
    }
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHAGE);
    }
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }


}
