package com.example.big_event.controller;

import com.example.big_event.pojo.Article;
import com.example.big_event.pojo.PageBean;
import com.example.big_event.pojo.Result;
import com.example.big_event.service.ArticleService;
import com.example.big_event.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
   //新增文章
    @PostMapping
    public Result add(@RequestBody @Validated Article article){
        articleService.add(article);
        return Result.success();
    }

    //文章列表查询
    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String  state
    ){
         PageBean<Article> pb=articleService.list(pageNum,pageSize,categoryId,state);
         return Result.success(pb);
    }

    //获取文章信息
    @GetMapping("/detail")
    public Result<Article> detail(Integer id){
        Article a=articleService.findById(id);
        return Result.success(a);
    }
    //更新文章信息
    @PutMapping
    public Result update(@RequestBody @Validated(Article.Update.class) Article article){
        int i = articleService.update(article);
        if (i==0){
            return Result.error("更新失败");
        }
        return Result.success(i);
    }
    //删除文章
    @DeleteMapping
    public Result delete(Integer id){
        int i = articleService.delete(id);
        if (i==0){
            return Result.error("删除失败");
        }
        return Result.success(i);
    }
}
