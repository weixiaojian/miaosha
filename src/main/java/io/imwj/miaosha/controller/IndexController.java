package io.imwj.miaosha.controller;

import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author langao_q
 * @since 2020-11-17 16:43
 */
@Controller
public class IndexController {


    @ResponseBody
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @ResponseBody
    @GetMapping("/hello1")
    public Result<String> hello1(@RequestParam(value = "name", defaultValue = "World") String name) {
        return Result.success("hello miaosha");
    }

    @ResponseBody
    @GetMapping("/hello2")
    public Result<String> hello2(@RequestParam(value = "name", defaultValue = "World") String name) {
        return Result.error(CodeMsg.ERROR);
    }

    @GetMapping("/hello3")
    public String hello3(Model model) {
        model.addAttribute("hello", "hello world");
        return "hello";
    }

}
