package io.imwj.miaosha.controller;

import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.result.Result;
import io.imwj.miaosha.service.MiaoShaUserService;
import io.imwj.miaosha.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 登陆控制
 * @author langao_q
 * @since 2020-11-24 16:50
 */
@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MiaoShaUserService userService;

    @GetMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Result<String> login(HttpServletResponse response, @Valid LoginVo loginVo){
        userService.login(response, loginVo);
        return Result.success(CodeMsg.SUCCESS);
    }
}
