<script setup>
import {
    Management,
    Promotion,
    UserFilled,
    User,
    Crop,
    EditPen,
    SwitchButton,
    CaretBottom,
    Monitor
} from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'
import { userInfoService, userLogoutService } from '@/api/user';
import useUserInfoStore from '@/stores/userInfo.js' 
const userInfoStore = useUserInfoStore()
//调用函数获取用户详细信息
const getuserInfo = async()=>{
  let result = await userInfoService()
  userInfoStore.setInfo(result.data)
}
getuserInfo()

//条目被点击后调用的函数
import { useRouter } from 'vue-router';
import { ElMessage,ElMessageBox } from 'element-plus';
import {useTokenStore} from '@/stores/token.js'
const tokenStore= useTokenStore()
const router = useRouter()
const handleCommand = (commend)=>{
   if(commend=== 'logout'){
      //退出登录
      //提示用户 确认框
    ElMessageBox.confirm(
        '你确认要退出吗？',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            //先通知后端删除Redis中的token并置为离线，失败也不阻塞本地退出
            try {
                await userLogoutService()
            } catch (e) {
                // 忽略退出接口异常，本地登录态仍然清理
            }
            //退出登录
            //清空pinia中的token和个人资料
            tokenStore.removeToken()
            userInfoStore.removeInfo()
            //跳转登录页
            router.push('/login')   
            ElMessage({
                type: 'success',
                message: '退出登录成功',
            })
            
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '用户取消了退出登录',
            })
        })
   }else{
     //路由
      router.push('/user/'+commend)
   }
}
</script>

<template>
    <el-container class="layout-container">
        <!-- 左侧菜单 -->
        <el-aside width="200px">
            <div class="el-aside__logo"></div>
            <el-menu active-text-color="#ffd04b" background-color="#232323" text-color="#fff" router>
                <el-menu-item index="/article/category">
                    <el-icon>
                        <Management />
                    </el-icon>
                    <span>文章分类</span>
                </el-menu-item>
                <el-menu-item index="/article/manage">
                    <el-icon>
                        <Promotion />
                    </el-icon>
                    <span>文章管理</span>
                </el-menu-item>
                
                <!-- 【新增】只有管理员才能看到这个菜单 -->
                <!-- 注意：这里的 info.role 需要根据你实际后端返回的字段名修改，比如可能是 info.userRole -->
                <el-menu-item v-if="userInfoStore.info.role === 'admin'" index="/user/online">
                    <el-icon>
                        <Monitor />
                    </el-icon>
                    <span>管理在线用户</span>
                </el-menu-item>

                <!-- 【修复】补上了 index="user"，解决之前的 Missing required prop 报错 -->
                <el-sub-menu index="user">
                    <template #title>
                        <el-icon>
                            <UserFilled />
                        </el-icon>
                        <span>个人中心</span>
                    </template>
                    <el-menu-item index="/user/info">
                        <el-icon>
                            <User />
                        </el-icon>
                        <span>基本资料</span>
                    </el-menu-item>
                    <el-menu-item index="/user/avatar">
                        <el-icon>
                            <Crop />
                        </el-icon>
                        <span>更换头像</span>
                    </el-menu-item>
                    <el-menu-item index="/user/resetPassword">
                        <el-icon>
                            <EditPen />
                        </el-icon>
                        <span>重置密码</span>
                    </el-menu-item>
                </el-sub-menu>
            </el-menu>
        </el-aside>
        
        <!-- 右侧主区域（保持不变） -->
        <el-container>
            <!-- ... 原代码中间的 Header 和 Main ... -->
            <el-header>
                <div>当前用户：<strong>{{ userInfoStore.info.nickname }}</strong></div>
                <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.info.userPic?userInfoStore.info.userPic:avatar" />
                        <el-icon>
                            <CaretBottom />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="info" :icon="User">基本资料</el-dropdown-item>
                            <el-dropdown-item command="avatar" :icon="Crop">更换头像</el-dropdown-item>
                            <el-dropdown-item command="resetPassword" :icon="EditPen">重置密码</el-dropdown-item>
                            <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>
            <el-main>
                  <router-view></router-view>
            </el-main>
            <el-footer>大事件 ©2026 Created by 归零</el-footer>
        </el-container>
    </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;

    .el-aside {
        background-color: #232323;

        &__logo {
            height: 120px;
            background: url('@/assets/logo.png') no-repeat center / 120px auto;
        }

        .el-menu {
            border-right: none;
        }
    }

    .el-header {
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .el-dropdown__box {
            display: flex;
            align-items: center;

            .el-icon {
                color: #999;
                margin-left: 10px;
            }

            &:active,
            &:focus {
                outline: none;
            }
        }
    }

    .el-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #666;
    }
}
</style>