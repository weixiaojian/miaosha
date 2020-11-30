package io.imwj.miaosha.dao;

import io.imwj.miaosha.domain.MiaoshaOrder;
import io.imwj.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

/**
 * 订单order
 * @author langao_q
 * @since 2020-11-30 11:33
 */
@Mapper
public interface OrderMapper {

    /**
     * 根据用户id和商品id查询订单
     * @param userId
     * @param goodsId
     * @return
     */
    @Select("select id from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long userId, @Param("goodsId")String goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    Long insert(OrderInfo o);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    long insertMiaoshaOrder(MiaoshaOrder order);
}
