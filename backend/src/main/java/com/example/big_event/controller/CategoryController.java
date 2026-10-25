package com.example.big_event.controller;

import com.example.big_event.pojo.Category;
import com.example.big_event.pojo.Result;
import com.example.big_event.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    //增加文章类别
    @PostMapping
    public Result add(@RequestBody @Validated(Category.Add.class) Category category){
        categoryService.add(category);
        return Result.success();
    }
    //查询该用户创建的文章类别
    @GetMapping
    public Result<List<Category>> list(){
        List<Category> cs = categoryService.list();
        return Result.success(cs);
    }
    //获取文章分类详情
    @GetMapping("/detail")
    public Result<Category> detail(Integer id){
        Category c=categoryService.findById(id);
        return Result.success(c);
    }
    //更改文章分类
    @PutMapping
    public Result update(@RequestBody @Validated(Category.Update.class) Category category){
        int i=categoryService.update(category);
        if(i==0){
            return Result.error("更新失败");
        }
        return Result.success();
    }
    //删除文章分类
    @DeleteMapping
    public Result delete(Integer id){
        int i=categoryService.delete(id);
        if(i==0){
            return Result.error("删除失败");
        }
        return Result.success();
    }
}
