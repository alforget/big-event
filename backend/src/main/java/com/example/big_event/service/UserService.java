package com.example.big_event.service;

import com.example.big_event.pojo.PageBean;
import com.example.big_event.pojo.User;

import java.util.List;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);
    //注册
    void register(String username, String password);
    //更新用户信息
    void update(User user);
    //更新头像
    void updateAvatar(String avatarUrl);
    //更新密码
    void updatePwd(String newPwd,Integer userId);
    //更新在线状态
    void updateOnlineStatus(Integer userId,Boolean isOnline);
    //获取所有用户信息
    PageBean<User> list(Integer pageNum, Integer pageSize);

    //根据用户ID查询用户信息
    User findById(Integer userId);
}
