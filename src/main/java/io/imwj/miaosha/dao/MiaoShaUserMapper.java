package io.imwj.miaosha.dao;

import io.imwj.miaosha.domain.MiaoShaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author langao_q
 * @since 2020-11-24 17:06
 */
@Mapper
public interface MiaoShaUserMapper {

    @Select("SELECT * FROM miaosha_user WHERE id = #{id}")
    public MiaoShaUser getUserById(@Param("id") String id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    boolean update(MiaoShaUser toBeUpdate);
}
