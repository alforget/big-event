package com.example.big_event.service.impl;

import com.example.big_event.mapper.CategoryMapper;
import com.example.big_event.pojo.Category;
import com.example.big_event.service.CategoryService;
import com.example.big_event.utils.JwtUtil;
import com.example.big_event.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void add(Category category) {

        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer)map.get("id");
        category.setCreateUser(id);
        categoryMapper.add(category);
    }

    @Override
    public List<Category> list() {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer)map.get("id");
        return categoryMapper.list(id);
    }

    @Override
    public Category findById(Integer id) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
       return categoryMapper.findById(id,userId);
    }

    @Override
    public int update(Category category) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        category.setCreateUser(userId);
        return categoryMapper.update(category);
    }

    @Override
    public int delete(Integer id) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        return categoryMapper.delete(id,userId);
    }
}
