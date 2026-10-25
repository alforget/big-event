import request from '@/utils/request.js'

//文章列表分类查询
export const articleCategoryListService = ()=>{
    //在pinia中定义的响应式数据，都不需要.value
    return request.get('/category')
}

//新增分类
export const articleCategoryAddService=(categoryData)=>{
    return request.post('/category',categoryData)
}

//编辑分类
export const articleCategoryUpdateService=(categoryData)=>{
      return  request.put('/category',categoryData)
}

//删除分类
export const articleCategoryDeleteService=(id)=>{
      return  request.delete('/category?id='+id)
}

//文章列表查询
export const articleListService = (params)=>{
    return request.get('/article',{params:params})
}

//文章添加
export const articleAddService  = (articleData)=>{
    return request.post('/article',articleData);
}

//删除文章
export const articleDeleteService = (id)=>{
    return  request.delete('/article?id='+id)
}

//编辑文章
export const articleUpdateService = (articleData)=>{
    return  request.put('/article',articleData)
}