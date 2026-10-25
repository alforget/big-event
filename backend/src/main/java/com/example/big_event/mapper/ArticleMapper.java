package com.example.big_event.mapper;

import com.example.big_event.pojo.Article;
import com.example.big_event.utils.ThreadLocalUtil;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {
    //新增文章
    @Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time)" +
            " values(#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},now(),now())")
    void add(Article article);
    //文章列表查询
    //动态sql语句用映射文件
    List<Article> list(@Param("categoryId") Integer categoryId,@Param("state") String state,@Param("userId") Integer userId);
    //获取文章信息
    @Select("select * from article where id=#{id} and create_user=#{userId}")
    Article findById(@Param("id") Integer id,@Param("userId") Integer userId);
    //更新文章信息
    @Update("update article set title=#{title},content=#{content},cover_img=#{coverImg},state=#{state},category_id=#{categoryId},update_time=now()" +
            " where id=#{id} and create_user=#{createUser}")
    int update(Article article);
    //删除文章
    @Delete("delete from article where id=#{id} and create_user=#{userId}")
    int delete(@Param("id") Integer id,@Param("userId") Integer userId);
}
