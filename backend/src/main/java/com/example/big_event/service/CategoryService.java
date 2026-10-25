package com.example.big_event.service;

import com.example.big_event.pojo.Category;

import java.util.List;

public interface CategoryService {
    //新增类目
    void add(Category category);
    //查询该用户创建的文章类别
    List<Category> list();
    //根据id查询分类信息
    Category findById(Integer id);
    //更改文章分类
    int update(Category category);
    //删除文章分类
    int delete(Integer id);
}
