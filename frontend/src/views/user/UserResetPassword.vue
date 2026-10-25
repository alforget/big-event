<script setup>
import { ref } from 'vue'
const userPassword = ref({
    old_pwd:'',
    new_pwd:'',
    re_pwd:''
})
const rules = {
    old_pwd: [
        { required: true, message: '请输入原密码', trigger: 'blur' },
    ],
    new_pwd: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
    ],
    re_pwd: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
    ]
}
import { userPasswordUpdateService} from '@/api/user'
import useUserInfoStore from '@/stores/userInfo'
import { ElMessage } from 'element-plus'
import { useRouter} from 'vue-router'
import { useTokenStore } from '@/stores/token'
const tokenStore = useTokenStore()
const router = useRouter()
const userInfoStore = useUserInfoStore()
//修改密码
const updatePassword =async ()=>{
  let  result =await userPasswordUpdateService(userPassword.value)
  ElMessage.success(result.msg?result.msg:'修改成功')

    //清空pinia中的token和个人资料
    tokenStore.removeToken()
    userInfoStore.removeInfo()
    //跳转登录页
    router.push('/login')
}
</script>
<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>重置密码</span>
            </div>
        </template>
        <el-row>
            <el-col :span="12">
                <el-form :model="userPassword" :rules="rules" label-width="100px" size="large">
                    <el-form-item label="原密码" prop="old_pwd">
                        <el-input v-model="userPassword.old_pwd" ></el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="new_pwd">
                        <el-input v-model="userPassword.new_pwd"></el-input>
                    </el-form-item>
                    <el-form-item label="重复新密码" prop="re_pwd">
                        <el-input v-model="userPassword.re_pwd"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="updatePassword">提交修改</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
    </el-card>
</template>