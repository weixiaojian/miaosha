package io.imwj.miaosha.rabbitmq;

import io.imwj.miaosha.domain.MiaoShaUser;
import lombok.Data;

/**
 * rabbitMQ消息实体
 * @author langao_q
 * @since 2021-01-12 17:55
 */
@Data
public class MiaoshaMessage {
    private MiaoShaUser user;
    private long goodsId;
}
