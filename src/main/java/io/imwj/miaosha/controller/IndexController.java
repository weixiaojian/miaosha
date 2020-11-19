package io.imwj.miaosha.controller;

import io.imwj.miaosha.domain.User;
import io.imwj.miaosha.redis.RedisService;
import io.imwj.miaosha.redis.UserKey;
import io.imwj.miaosha.result.CodeMsg;
import io.imwj.miaosha.result.Result;
import io.imwj.miaosha.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author langao_q
 * @since 2020-11-17 16:43
 */
@Slf4j
@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

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

    @ResponseBody
    @GetMapping("/getUser")
    public Result<User> getUser(@RequestParam(value = "id") Integer id) {
        return Result.success(userService.getById(id));
    }

    @ResponseBody
    @GetMapping("/getUserAll")
    public Result<List> getUserAll() {
        return Result.success(userService.getAll());
    }

    @ResponseBody
    @GetMapping("/tx")
    public Result<Boolean> tx() {
        return Result.success(userService.tx());
    }

    @ResponseBody
    @GetMapping("/setRedis")
    public Result<Boolean> setRedis() {

        redisService.set(UserKey.getById,"a", 1);
        redisService.set(UserKey.getById,"b", 2L);
        redisService.set(UserKey.getById,"c", "C");

        User user = new User();
        user.setId(001);
        user.setName("一号员工");
        redisService.set(UserKey.getById,"d", user);

        User user1 = new User();
        user1.setId(002);
        user1.setName("二号员工");
        ArrayList<User> list = new ArrayList<>();
        list.add(user);
        list.add(user1);
        redisService.set(UserKey.getByIdEx(30),"e", list);
        return Result.success(true);
    }

    @ResponseBody
    @GetMapping("/getRedis")
    public Result<Boolean> getRedis() {

        Integer a = redisService.get(UserKey.getById, "a", Integer.class);
        log.info("a：" + a);
        Long b = redisService.get(UserKey.getById,"b", Long.class);
        log.info("b：" + b);
        String c = redisService.get(UserKey.getById,"c", String.class);
        log.info("c：" + c);

        User user = redisService.get(UserKey.getById,"d", User.class);
        log.info("user：" + user);

        List<User> list = redisService.get(UserKey.getByIdEx(30),"e", List.class);
        log.info("list：" + list);

        Long incr = redisService.incr(UserKey.getById, "f");
        log.info("incr：" + incr);

        Long dncr = redisService.dncr(UserKey.getById, "f");
        log.info("dncr：" + dncr);

        return Result.success(true);
    }


}
