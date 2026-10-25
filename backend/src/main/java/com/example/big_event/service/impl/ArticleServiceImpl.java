package com.example.big_event.service.impl;

import com.example.big_event.mapper.ArticleMapper;
import com.example.big_event.pojo.Article;
import com.example.big_event.pojo.PageBean;
import com.example.big_event.service.ArticleService;
import com.example.big_event.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void add(Article article) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);
        articleMapper.add(article);
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        //创建PageBean对象
        PageBean<Article> pb = new PageBean<>();
        //开启分页查询PageHelper
        PageHelper.startPage(pageNum,pageSize);
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        List<Article> as= articleMapper.list(categoryId,state,userId);
        //Page中提供了方法，可以获取PageHelper分页查询后的总条数和当前页数据
        Page<Article> p=(Page<Article>) as;
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    @Override
    public Article findById(Integer id) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        return articleMapper.findById(id,userId);
    }

    @Override
    public int update(Article article) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);
        return articleMapper.update(article);
    }

    @Override
    public int delete(Integer id) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        return articleMapper.delete(id,userId);
    }
}
