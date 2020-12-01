package io.imwj.miaosha.service;

import io.imwj.miaosha.dao.GoodsMapper;
import io.imwj.miaosha.domain.MiaoshaGoods;
import io.imwj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品Service
 * @author langao_q
 * @since 2020-11-26 18:05
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取商品列表
     * @return
     */
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    /**
     * 获取商品详细信息
     * @param id
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(String id) {
        return goodsMapper.getGoodVoById(id);
    }

    /**
     * 减少秒杀商品表中的库存
     * @param goods
     */
    public void reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        goodsMapper.reduceStock(g);
    }
}
