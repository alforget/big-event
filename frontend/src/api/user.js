import request from '@/utils/request.js'

//提供调用注册接口的函数
export const userRegisterService = (registerData)=>{
    //借助urlSearchParams完成传递
    const params = new URLSearchParams()
    for(let key in registerData){
        params.append(key,registerData[key])
    }
    return request.post('/user/register',params)
}
//调用登录接口的函数
export const userLoginService = (LoginData)=>{
    const params = new URLSearchParams()
    for(let key in LoginData){
        params.append(key,LoginData[key])
    }
    return request.post('/user/login',params)
}

//获取用户详细信息
export const userInfoService= ()=>{
    return request.get('/user/userInfo')
}

//修改个人信息
export const userInfoUpdateService = (userInfoData)=>{
   return request.put('/user/update',userInfoData)
}

//头像修改
export const userAvatarUpdateService = (avartUrl)=>{
    const params=new URLSearchParams()
    params.append('avatarUrl',avartUrl)
  return request.patch('/user/updateAvatar',params)
}

//修改密码
export const userPasswordUpdateService = (pwdData)=>{
    return request.patch('/user/updatePwd',pwdData)
}

//获取所有用户信息
export const userListService = (params)=>{
    return request.get('/user/list',{params:params})
}

//强制下线用户
export const userKickoutService = (userId)=>{
    return request.delete('/user/deleteToken?userId='+userId)
}

//退出登录
export const userLogoutService = ()=>{
    return request.post('/user/logout')
}
