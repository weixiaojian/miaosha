### 集成rabbitMQ
0. [rabbitMQ相关博文](http://imwj.club/article/117#directory0925539917769536311)  
1.引入依赖  
```
        <!--rabbitmq-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```

2.yml配置  
```
spring
  rabbitmq:
    host: 127.0.0.1
    username: guest
    password: guest
    port: 5672
    virtual-host: /
    listener:
      simple: #消费者
        auto-startup: true #消费者自动启动
        concurrency: 10 #消费者数量
        max-concurrency: 10 #最大消费者数量
        prefetch: 1 #每次取出的个数
        default-requeue-rejected: true #失败后重试
    template:
      retry: #生产者
        enabled: true
        initial-interval: 1000
        max-attempts: 3
        max-interval: 10000
        multiplier: 1.0
```

3.配置类  
```
@Configuration
public class MQConfig {

    public static final String DIRECT_QUEUE = "DIRECT_QUEUE";

    /**
     * 注入队列
     * @return
     */
    @Bean
    public Queue queue(){
        return new Queue(DIRECT_QUEUE, true);
    }
}
```

4.发送消息  
```
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
        amqpTemplate.convertAndSend(MQConfig.DIRECT_QUEUE, msgStr);
        log.info("MQSender.send:" + msgStr);
    }
}
```

5.接收消息  
```
@Slf4j
@Service
public class MQReceiver {

    @RabbitListener(queues = MQConfig.DIRECT_QUEUE)
    public void receive(String msg){
        log.info("MQReceiver.receive" + msg);
    }
}
```