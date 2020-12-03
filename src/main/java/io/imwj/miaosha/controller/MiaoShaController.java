package io.imwj.miaosha.controller;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.domain.MiaoshaOrder;
import io.imwj.miaosha.domain.OrderInfo;
import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.result.Result;
import io.imwj.miaosha.service.GoodsService;
import io.imwj.miaosha.service.MiaoShaService;
import io.imwj.miaosha.service.OrderService;
import io.imwj.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 秒杀控制类
 * @author langao_q
 * @since 2020-11-30 11:22
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoShaController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MiaoShaService miaoShaService;

    @Autowired
    private OrderService orderService;


    /**
     *  QPS：56.7
     *  5000线程/1次
     *
     *  QPS：522
     *  5000线程/10次
     * 商品秒杀
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("do_miaosha")
    public String doMiaoSha(Model model,
                            MiaoShaUser user,
                            String goodsId){
        model.addAttribute("user", user);
        //1.判断用户是否登陆（拦截器AuthInterceptor已经处理）

        //2.校验库存是否充足
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount() <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        //3.判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

        //4.减库存 下订单 写入秒杀订单
        OrderInfo info = miaoShaService.miaosha(user, goods);
        model.addAttribute("orderInfo", info);
        model.addAttribute("goods", goods);
        return "order_detail";
    }

    /**
     *  QPS：56.7
     *  5000线程/1次
     *
     *  QPS：551
     *  5000线程/10次
     * 秒杀页面静态化
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @ResponseBody
    @RequestMapping("doMiaoSha2")
    public Result<OrderInfo> doMiaoSha2(Model model,
                             MiaoShaUser user,
                             String goodsId){
        model.addAttribute("user", user);
        //1.判断用户是否登陆（拦截器AuthInterceptor已经处理）

        //2.校验库存是否充足
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount() <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //3.判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //4.减库存 下订单 写入秒杀订单
        OrderInfo info = miaoShaService.miaosha(user, goods);
        return Result.success(info);
    }

}
