package com.example.big_event.mapper;

import com.example.big_event.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    //根据用户名查询用户
    @Select("select * from user where username=#{username}")
    User findByUserName(@Param("username") String username);
    //添加用户
    @Insert("insert into user(username,password,create_time,update_time)" +
            " values(#{username},#{password},now(),now())")
    void add(@Param("username") String username, @Param("password") String password);
    //更新用户信息
    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);
    //更新在线状态
    @Update("update user set isOnline=#{isOnline},update_time=now() where id=#{id}")
    void updateOnlineStatus(@Param("id") Integer id,@Param("isOnline") Boolean isOnline);
    //更新头像
    @Update("update user set user_pic=#{avatarUrl},update_time=now() where id=#{id}")
    void updateAvatar(@Param("avatarUrl") String avatarUrl,@Param("id") Integer id);
    //更新密码
    @Update("update user set password=#{password},update_time=now() where id=#{id}")
    void updatePwd(@Param("password") String password,@Param("id") Integer id);
    //获取所有用户信息
    @Select("select * from user")
    List<User> list();
    //根据用户ID查询用户信息
    @Select("select * from user where id=#{userId}")
    User findById(Integer userId);
}
