# miaosha
秒杀项目源码及其笔记， 视频链接 ->  [Java秒杀系统方案优化 高性能高并发实战](https://coding.imooc.com/class/chapter/168.html)

# 目录
## 第一章-项目搭建
* 搭建Springboot项目（springboot版本使用2.4.0）
* 集成前端页面模板Thymeleaf，使用统一的Result返回结果
* Docker中安装Mysql、集成Mybatis持久层，注解方式来写sql 操作数据库
```
1.拉取镜像
docker pull mysql:5.7

2.启动镜像
docker run --name mysql5.7  -e MYSQL_ROOT_PASSWORD=123456 -d  -p 3306:3306 mysql:5.7

3.进入容器并连接mysql
docker exec -it 88fd3d0a5e6a mysql -u root -p
```
* Docker中安装Redis、缓存选用Redis 二次封装Jedis + FastJson进行对象的序列化及反序列化
```
1.拉取镜像
1.拉取镜像
docker pull redis

2.启动镜像
docker run -p 6379:6379 -v $PWD/data:/data  -d redis:3.2 redis-server --appendonly yes

3.进入容器并连接redis-cli
docker exec -it b20f8fae515b redis-cli
```
## 第二章-用户登陆/分布式SESSION
### 两次MD5
* 用户端：pass = MD5（明文 + 固定SALT）
* 服务端：pass = MD5（用户输入 + 随机SALT）
```
@Slf4j
public class MD5Util {

    private static final String salt = "1a2b3c";

    /**
     * md5加密
     * @param str
     * @return
     */
    public static String MD5(String str){
        return DigestUtils.md5Hex(str);
    }

    /**
     * 输入的密码 + 固定salt加密
     * 截取salt指定位置的字符 + 输入的密码
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass){
        String str = salt.charAt(0) +  salt.charAt(2) + inputPass + salt.charAt(5) +  salt.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 获取到的表单的密码 + 随机salt加密
     * 截取salt指定位置的字符 + 输入的密码
     * @param formPass
     * @param saltForm
     * @return
     */
    public static String formPassToDbPass(String formPass, String saltForm){
        String str = saltForm.charAt(0) +  saltForm.charAt(2) + formPass + saltForm.charAt(5) +  saltForm.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 输入的密码 + 随机的salt加密  同时salt保存到数据库中
     * @param inputPass
     * @param saltDb
     * @return
     */
    public static String inputPassToDbPass(String inputPass, String saltDb){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDbPass(formPass, saltDb);
        return dbPass;
    }

    public static void main(String[] args) {
        String dbPass = inputPassToDbPass("15200991579", salt);
        log.info(dbPass);
    }
}
```

## JSR303校验（自定义注解实现手机号校验）  
1.注解类
```
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class })
public @interface IsMobile {

    boolean required() default true;

    String message() default "手机号码格式错误";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
```

2.注解实现类
```
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(required) {
            return ValidatorUtil.isMobile(value);
        }else {
            if(StringUtils.isEmpty(value)) {
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
    
    //校验工具类
    class ValidatorUtil {
    
        private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
    
        public static boolean isMobile(String src) {
            if(StringUtils.isEmpty(src)) {
                return false;
            }
            Matcher m = mobile_pattern.matcher(src);
            return m.matches();
        }
    }
}
```

## 全局异常/自定义异常处理  
1.全局异常处理器：出现异常时直接返回json结果
```
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

     @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        //自定义异常处理
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        }
        //Valid参数校验异常处理
        else if(e instanceof BindException){
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }
        //其他异常处理
        else{
            log.error("【出现异常】：", e);
            return Result.error(CodeMsg.ERROR);
        }
     }
}
```

2.自定义异常：必要时可以直接抛出自定义异常 终止执行返回结果到前台
```
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
```

3.使用自定义异常
```
        //验证用户是否存在
        if(dbUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
```

## 分布式Session(参数解析器实现)  
1.收到请求时先获取cookie或req中的token然后转换成user对象传入controller

```
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoShaUserService userService;

    /**
     * 先判断参数中的类型是不是MiaoShaUser 是的话再执行resolveArgument
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz== MiaoShaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String tokenParam = request.getParameter("token");
        String tokenCookie = getTokenCookie(request);
        String token = StringUtils.isNotEmpty(tokenParam)?tokenParam:tokenCookie;

        if(StringUtils.isEmpty(tokenParam) && StringUtils.isEmpty(tokenCookie)) {
            return null;
        }
        MiaoShaUser user = userService.getByToken(response, token);
        return user;
    }

    private String getTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(MiaoShaUserService.COOKI_NAME_TOKEN)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
```
2.注册该参数解析器
```
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    UserArgumentResolver userArgumentResolver;

    /**
     * 校验Cookie或Request中token，为空或者token没有对应用户信息的就去登陆页面
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/bootstrap/**", "/img/**", "/jquery-validation/**", "/js/**", "/layer/**");
    }
    
    /**
     * 根据Cookie或Request中token直接获取user对象注入的Controller中
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    /**
     * 对静态资源放行
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");
        super.addResourceHandlers(registry);
    }
}
```
3.controller中直接注入对象
```
    @GetMapping("/toList")
    public String toLogin(Model model, MiaoShaUser user) {

```

4.配合拦截器实现校验是否登陆  
<u>注意：登陆成功时要把用户的token放到redis和cookie中并设置时效 </u>
```
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private MiaoShaUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
        try {
            String tokenParam = req.getParameter(MiaoShaUserService.COOKI_NAME_TOKEN);
            String tokenCookie = getTokenCookie(req);

            String token = StringUtils.isNotEmpty(tokenParam)?tokenParam:tokenCookie;

            //为空时跳转到登陆页面
            if(StringUtils.isEmpty(tokenParam) && StringUtils.isEmpty(tokenCookie)) {
                res.sendRedirect("/login/toLogin");
                return false;
            }

            //redis中的用户数据为空时跳转到登陆页面
            MiaoShaUser user = userService.getByToken(res, token);
            if(user == null) {
                res.sendRedirect("/login/toLogin");
                return false;
            }

            //放行
            return true;
        } catch (Exception e) {
            log.error("-----授权错误-----" + e.getMessage());
            return false;
        }
    }

    /**
     * 获取cookie中的token
     * @param request
     * @return
     */
    private String getTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(MiaoShaUserService.COOKI_NAME_TOKEN)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
```

## 第三章-商品展示与秒杀功能
* 商品列表/详情页面实现
* 秒杀功能
* 订单详情  


## 第四章-Jmeter压测
### 步骤流程
* 创建线程组：配置线程数、循环次数

* 添加http请求默认值：配置协议、服务器ip、端口号

* 添加http请求：添加请求参数token

* 添加聚合报告：运行压测后查看结果

* 批量测试：需要添加CSV数据文件设置：选择data文件、配置变量名称（userId,token）、http请求参数token就可以通过${token}获取

### linux下进行压测
* 先在本地生成一个jmx文件然后导入到linux中，在linux下使用jmeter命令压测

* 压测命令
```
./apache-jmeter-5.3/bin/jmeter.sh -n -t xxx.jmx -l result.jtl
```

* 注意：如果要导入CSV数据文件直接打开.jmx文件修改其中的路径即可

### springboot打成war包
* 指定为war包
```
    <packaging>war</packaging>
```

* web排除tomcat依赖，同时导入单独的tomcat依赖
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
```

* 修改启动类：继承SpringBootServletInitializer，覆盖configure方法
```
/**
 * 项目启动类
 * @author Administrator
 */
@SpringBootApplication
public class MiaoshaApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MiaoshaApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MiaoshaApplication.class);
    }
}
```

## 第五章-页面级优化
* 页面缓存 + URL缓存 + 对象缓存  
页面缓存：不使用springboot的页面渲染，改成手动渲染 同时将页面数据缓存到redis  
URL缓存：根据传入的id不同，将数据缓存到redis中  
对象缓存：直接将用户数据放入redis中 不需要每次到数据库中取  

* 页面静态化，前后端分离  
页面静态化：常见的如JSP、thymeleaf等都属于动态页面，将其修改成普通的html，只有页面与页面间的跳转 通过ajax获取数据然后渲染到页面  
前后端分离：通过ajax获取数据然后渲染到页面  
同时springboot配置spring.resources.*，将页面缓存到浏览器
```
spring.resources.add-mappings=true 
spring.resources.cache-period=3600  #缓存时间 s
spring.resources.chain.cache=true 
spring.resources.chain.enabled=true
spring.resources.chain.gzipped=true
spring.resources.chain.html-application-cache=true
spring.resources.static-locations=classpath:/static/    #缓存页面的根目录（要放到static下）
```

* 静态资源优化  
js/css压缩、多个js/css组合连接(减少连接数)、[http://tengine.taobao.org/](http://tengine.taobao.org/)


* CDN优化  
通过中心平台的负载均衡、内容分发、调度等功能模块，使用户就近获取所需内容，降低网络拥塞，提高用户访问响应速度和命中率。

* 库存超卖
更新库存的时候在sql中判断库存大于0（使用mysql自带的锁）
```
update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0
```

* 一个用户秒杀到两个商品  
在秒杀订单表建立一个唯一索引：userId、orderId

## 第六章-服务级高并发秒杀优化（RabbitMQ+接口优化）
### 思路：减少数据库访问
* 系统初始化，把商品库存加载到redis中
* 收到请求，redis预减库存 若库存不足直接返回
* 请求入队rabbitMQ，立即返回排队中
* 请求出队，生成订单 减少库存
* 客户端轮询，是否秒杀成功

### 秒杀接口优化
0.系统初始化把库存加载到redis中（实现InitializingBean）
```
public class MiaoShaController implements InitializingBean {
        /**
         * 系统初始化：
         * 把商品库存都加载到redis中去
         * @throws Exception
         */
        @Override
        public void afterPropertiesSet() throws Exception {
            List<GoodsVo> goodsVos = goodsService.listGoodsVo();
            if(goodsVos == null){
                return ;
            }
            for (GoodsVo good : goodsVos){
                redisService.set(GoodsKey.getMiaoshaGoodsStock, good.getId() + "", good.getStockCount());
                localOverMap.put(good.getId() + "", false);
            }
        }
}
```

1.controller秒杀接口
```
    @Autowired
    private MQSender mqSender;

    /**
     *  QPS：934.6
     *  5000个商品    5000线程/1次
     *
     *  QPS：700 - 1500（商品库存秒杀完之后 qps开始上升）
     *  5000个商品    5000线程/10次
     * 【秒杀优化】：
     * 1.系统初始化，把商品库存加载到redis中
     * 2.收到请求，redis预减库存 若库存不足直接返回
     * 3.请求入队rabbitMQ，立即返回排队中
     * 4.请求出队，生成订单 减少库存
     * 5.客户端轮询，是否秒杀成功
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @ResponseBody
    @RequestMapping("do_miaosha2")
    public Result<Integer> doMiaoSha2(Model model,
                             MiaoShaUser user,
                             String goodsId){
        model.addAttribute("user", user);
        //1.判断用户是否登陆（拦截器AuthInterceptor已经处理）

        //2.校验库存是否充足（先使用内存标记 减少redis访问 同时避免redis库存出现负数）
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        Long stockCount = redisService.dncr(GoodsKey.getMiaoshaGoodsStock, goodsId);
        if(stockCount < 0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //3.判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //4.请求入队rabbitMQ（0：排队中）
        MiaoshaMessage msg = new MiaoshaMessage();
        msg.setUser(user);
        msg.setGoodsId(Long.parseLong(goodsId));
        mqSender.miaoShaSend(msg);
        return Result.success(0);
    }
```

2.rabbitMQ出队
```
    /**
     * DIRECT模式
     * 秒杀数据出队
     * 1.校验库存是否充足
     * 2.判断是否已经秒杀过了
     * 3.减库存 下订单 写入秒杀订单
     * @param msg
     */
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void miaoShaReceive(String msg){
        log.info("----秒杀数据出队----MQReceiver.receive：" + msg);
        MiaoshaMessage msMsg = JsonUtil.stringToBean(msg, MiaoshaMessage.class);
        MiaoShaUser user = msMsg.getUser();
        //1.校验库存是否充足
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(msMsg.getGoodsId() + "");
        if(goods.getStockCount() <= 0){
            return ;
        }
        //2.判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), msMsg.getGoodsId() + "");
        if(order != null){
            return ;
        }
        //3.减库存 下订单 写入秒杀订单
        miaoShaService.miaosha(user, goods);
    }
```

3.秒杀结果轮询接口
```
    /**
     * 秒杀结果轮询
     * 1 - 秒杀成功
     * 0 - 秒杀排队中
     * -1 - 秒杀失败
     * @param model
     * @return
     */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoShaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  = miaoShaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
```

## 第七章- 图形验证码及恶意防刷
### 隐藏秒杀地址
* 接口改造，带上PathVariable参数  
* 添加生成地址的接口（最后的秒杀地址为/PathVariable/do_miaosha）  
* 秒杀收到请求，先验证PatheVariable参数   


### 图形验证码  
* 添加生成验证码的接口  
* 在获取秒杀路径的时候，验证验证码  
* ScriptEngine使用  

### 接口限流防刷
* 使用注解器实现接口限流
* ThreadLocal：根据线程绑定数据（线程安全）
```
 @AccessLimit(seconds=5, maxCount=5, needLogin=true)
```
