package com.example.big_event.service.impl;

import com.example.big_event.mapper.UserMapper;
import com.example.big_event.pojo.PageBean;
import com.example.big_event.pojo.User;
import com.example.big_event.service.UserService;
import com.example.big_event.utils.Md5Util;
import com.example.big_event.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByUserName(String username) {
        User u=userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username, String password) {
        //加密
        String bcryptPassword = passwordEncoder.encode(password);
        //添加
        userMapper.add(username,bcryptPassword);
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        //更新
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id= (Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl,id);
    }

    @Override
    public void updatePwd(String newPwd,Integer userId) {


        userMapper.updatePwd(passwordEncoder.encode(newPwd),userId);
    }

    @Override
    public void updateOnlineStatus(Integer userId, Boolean isOnline) {
        userMapper.updateOnlineStatus(userId,isOnline);
    }

    @Override
    public PageBean<User> list(Integer pageNum, Integer pageSize) {
        PageBean<User> pageBean = new PageBean<>();
        PageHelper.startPage(pageNum,pageSize);
        List<User> users = userMapper.list();
        Page<User> p=(Page<User>) users;
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public User findById(Integer userId) {
        return userMapper.findById(userId);
    }
}
