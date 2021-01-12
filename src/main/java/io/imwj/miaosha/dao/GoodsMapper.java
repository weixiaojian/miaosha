package io.imwj.miaosha.dao;

import io.imwj.miaosha.domain.MiaoshaGoods;
import io.imwj.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author langao_q
 * @since 2020-11-24 17:06
 */
@Mapper
public interface GoodsMapper {

    /**
     * 查询商品列表
     * @return
     */
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    /**
     * 查询商品详情
     * @param goodsId
     * @return
     */
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodVoById(@Param("goodsId")String goodsId);

    /**
     * 减少秒杀商品库存
     * @param g
     */
    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    boolean reduceStock(MiaoshaGoods g);
}
