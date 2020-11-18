# miaosha
秒杀项目源码及其笔记， 视频链接 ->  [Java秒杀系统方案优化 高性能高并发实战](https://coding.imooc.com/class/chapter/168.html)

# 目录
## 第一章
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