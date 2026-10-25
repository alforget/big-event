package com.example.big_event.mapper;

import com.example.big_event.pojo.Category;
import com.example.big_event.utils.ThreadLocalUtil;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper {
    //增加类目
    @Insert("insert into category(category_name,category_alias,create_user,create_time,update_time)" +
            " values(#{categoryName},#{categoryAlias},#{createUser},now(),now())")
    void add(Category category);
    //查询该用户创建的文章类别
    @Select("select * from category where create_user=#{userId}")
    List<Category> list(@Param("userId") Integer userId);
    //根据id和所属用户查询分类信息
    @Select("select * from category where id=#{id} and create_user=#{createUser}")
    Category findById(@Param("id") Integer id,@Param("createUser") Integer createUser);
    //更改文章分类
    @Update("update category set category_name=#{categoryName},category_alias=#{categoryAlias},update_time=now() where id=#{id} and create_user=#{createUser}")
    int update(Category category);
    //删除文章分类
    @Delete("delete from category where id=#{id} and create_user=#{userId}")
    int delete(@Param("id") Integer id,@Param("userId") Integer userId);
}
