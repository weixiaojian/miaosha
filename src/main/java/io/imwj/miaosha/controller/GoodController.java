package io.imwj.miaosha.controller;

import io.imwj.miaosha.domain.MiaoShaUser;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.service.MiaoShaUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    private RedisService redisService;

    @Autowired
    private MiaoShaUserService userService;

    @GetMapping("/toList")
    public String toLogin(Model model,MiaoShaUser user){
        model.addAttribute("user", user);
        return "goods_list";
    }

}
