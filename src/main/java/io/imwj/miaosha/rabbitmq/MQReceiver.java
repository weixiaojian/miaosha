package io.imwj.miaosha.rabbitmq;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.domain.MiaoshaOrder;
import io.imwj.miaosha.service.GoodsService;
import io.imwj.miaosha.service.MiaoShaService;
import io.imwj.miaosha.service.OrderService;
import io.imwj.miaosha.util.JsonUtil;
import io.imwj.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * rabbitMQ消息接收者
 * @author langao_q
 * @since 2021-01-11 17:12
 */
@Slf4j
@Service
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MiaoShaService miaoShaService;

    @Autowired
    private OrderService orderService;

    /**
     * DIRECT模式
     * 接收消息
     * @param msg
     */
    @RabbitListener(queues = MQConfig.DIRECT_QUEUE)
    public void receive(String msg){
        log.info("MQReceiver.receive：" + msg);
    }

    /**
     * TOPIC模式
     * 接收消息
     * @param msg
     */
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String msg){
        log.info("MQReceiver.receiveTopic1：" + msg);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String msg){
        log.info("MQReceiver.receiveTopic2：" + msg);
    }


    /**
     * FANOUT模式
     * 接收消息
     * @param msg
     */
    @RabbitListener(queues = MQConfig.FANOUT_QUEUE1)
    public void receiveFanout1(String msg){
        log.info("MQReceiver.receiveFanout1：" + msg);
    }
    @RabbitListener(queues = MQConfig.FANOUT_QUEUE1)
    public void receiveFanout2(String msg){
        log.info("MQReceiver.receiveFanout2：" + msg);
    }


    /**
     * DIRECT模式
     * 秒杀数据出队
     * 1.校验库存是否充足
     * 2.判断是否已经秒杀过了
     * 3.减库存 下订单 写入秒杀订单
     * @param msg
     */
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void miaoShaReceive(String msg){
        log.info("----秒杀数据出队----MQReceiver.receive：" + msg);
        MiaoshaMessage msMsg = JsonUtil.stringToBean(msg, MiaoshaMessage.class);
        MiaoShaUser user = msMsg.getUser();
        //1.校验库存是否充足
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(msMsg.getGoodsId() + "");
        if(goods.getStockCount() <= 0){
            return ;
        }
        //2.判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), msMsg.getGoodsId() + "");
        if(order != null){
            return ;
        }
        //3.减库存 下订单 写入秒杀订单
        miaoShaService.miaosha(user, goods);
    }

}
