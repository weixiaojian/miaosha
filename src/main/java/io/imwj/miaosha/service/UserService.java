package io.imwj.miaosha.service;

import io.imwj.miaosha.dao.UserMapper;
import io.imwj.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户Service
 * @author langao_q
 * @since 2020-11-18 11:38
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    /**
     * 获取所有用户
     * @return
     */
    public List<User> getAll(){
        return userMapper.findAll();
    }

    /**
     * 获取指定id用户
     * @param id
     * @return
     */
    public User getById(Integer id){
        return userMapper.findById(id);
    }

    /**
     * 保存用户
     * @param user
     * @return
     */
    public boolean saveUser(User user){
        return userMapper.insert(user)>0?true:false;
    }

    /**
     * 事务控制
     * @return
     */
    public boolean tx(){
        try {
            User u1= new User();
            u1.setId(4);
            u1.setName("2222");
            userMapper.insert(u1);

            User u2= new User();
            u2.setId(1);
            u2.setName("11111");
            userMapper.insert(u2);
        }catch (Exception ex){
            throw ex;
        }
        return true;
    }

}
