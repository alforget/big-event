package com.example.big_event.service;

import com.example.big_event.pojo.Article;
import com.example.big_event.pojo.PageBean;

public interface ArticleService {
    //新增文章
    void add(Article article);
    //文章列表查询
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);
    //获取文章信息
    Article findById(Integer id);
    //更新文章信息
    int update(Article article);
    //删除文章
    int delete(Integer id);

}
