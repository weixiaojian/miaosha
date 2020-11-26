package io.imwj.miaosha.controller;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.service.GoodsService;
import io.imwj.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 登陆控制
 * @author langao_q
 * @since 2020-11-24 16:50
 */
@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/toList")
    public String toLogin(Model model,MiaoShaUser user){

        //获取商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

}
