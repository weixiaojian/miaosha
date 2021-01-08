package io.imwj.miaosha.controller;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.redis.GoodrKey;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.result.Result;
import io.imwj.miaosha.service.GoodsService;
import io.imwj.miaosha.vo.GoodsDetailVo;
import io.imwj.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 登陆控制
 *
 * @author langao_q
 * @since 2020-11-24 16:50
 */
@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    /**
     *  QPS：386
     *  5000线程/10次
     * 商品列表页
     * @param model
     * @param user
     * @return
     */
    @GetMapping("/toList")
    public String toList(Model model, MiaoShaUser user) {

        //获取商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    /**
     * 商品详情页面
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, MiaoShaUser user,
                         @PathVariable("goodsId") String goodsId) {
        model.addAttribute("user", user);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        //秒杀还没开始，倒计时
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        }
        //秒杀已经结束
        else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        //秒杀进行中
        else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
    }


    /**
     *  QPS：386
     *  5000线程/10次
     * 商品列表页2：使用redis缓存页面
     * @param model
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/toList2", produces = "text/html")
    public String toList2(HttpServletRequest request, HttpServletResponse response, Model model, MiaoShaUser user) {
        model.addAttribute("user", user);
        //取缓存
        String html = redisService.get(GoodrKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //获取商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        IWebContext ctx =new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodrKey.getGoodsList, "", html);
        }
        return html;
    }

    /**
     * 商品详情页面2
     * redis页面缓存
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    public String detail2(HttpServletRequest request, HttpServletResponse response,Model model, MiaoShaUser user,
                         @PathVariable("goodsId") String goodsId) {
        model.addAttribute("user", user);
        //取缓存
        String html = redisService.get(GoodrKey.getGoodsById, goodsId, String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //获取商品详细信息
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        //秒杀还没开始，倒计时
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        }
        //秒杀已经结束
        else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        //秒杀进行中
        else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        IWebContext ctx =new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodrKey.getGoodsById, goodsId, html);
        }
        return html;
    }

    /**
     * 商品详情页面3
     * 页面静态化
     * @param user
     * @param goodsId
     * @return
     */
    @ResponseBody
    @RequestMapping("/detail3/{goodsId}")
    public Result<GoodsDetailVo> detail3(MiaoShaUser user,
                                         @PathVariable("goodsId") String goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        //秒杀还没开始，倒计时
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        }
        //秒杀已经结束
        else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        //秒杀进行中
        else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo detailVo = new GoodsDetailVo();
        detailVo.setMiaoshaStatus(miaoshaStatus);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setUser(user);
        detailVo.setGoods(goods);
        return Result.success(detailVo);
    }



}
