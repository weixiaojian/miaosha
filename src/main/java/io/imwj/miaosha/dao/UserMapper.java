package io.imwj.miaosha.dao;

import io.imwj.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户mapper操作类
 * @author langao_q
 * @since 2020-11-18 11:35
 */
@Mapper
public interface UserMapper {


    /**
     * 查询所有用户
     * @return
     */
    @Select("select * from user")
    List<User> findAll();

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @Select("select * from user where id=#{id}")
    User findById(@Param("id") Integer id);

    /**
     * 插入用户数据
     * @param user
     * @return
     */
    @Insert("insert into user(id, name) values(#{id}, #{name})")
    int insert(User user);
}
