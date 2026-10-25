<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 核心数据 ---
const loading = ref(false)
const userList = ref([]) // 存放用户列表数据
const total = ref(0) // 总条数
const pageNum = ref(1) // 当前页码
const pageSize = ref(10) // 每页条数
import { userListService, userKickoutService } from '@/api/user';
//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
    pageSize.value = size
    fetchUserList()
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
    pageNum.value = num
    fetchUserList()
}
const params = ref({
    pageNum: pageNum.value,
    pageSize: pageSize.value
})
// --- 获取用户列表数据 (你需要在这里调用后端接口) ---
const fetchUserList = async () => {
    loading.value = true
    try {
        let result = await userListService(params.value)
        userList.value = result.data.items
        total.value = result.data.total
        loading.value = false

    } catch (error) {
        console.error(error)
        ElMessage.error('获取用户列表失败')
        loading.value = false
    }
}

// --- 强制下线按钮事件 ---
const handleKickout = (row) => {
    // 二次确认，防止误点
    ElMessageBox.confirm(
        `确认要将用户 "${row.nickname || row.username}" 强制下线吗？`,
        '强制下线确认',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            // 【TODO】你需要在这里绑定对应的后端“踢人”接口
            // await request.delete(`/user/kickout/${row.id}`)

            let result = await userKickoutService(row.id)
            ElMessage.success(result.msg ? result.msg : '踢下线成功')
            row.isOnline = false
            // 成功后刷新列表，或者直接在前端把状态改掉
            // row.isOnline = false (如果只是局部更新的话)
            fetchUserList() // 重新拉取最新数据
        } catch (error) {
            ElMessage.error('操作失败')
        }
    }).catch(() => {
        // 取消操作
        ElMessage.info('已取消强制下线操作')
    })
}

// 页面挂载时加载数据
onMounted(() => {
    fetchUserList()
})
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>在线用户管理</span>
                <div class="extra">
                    <!-- 预留：点击刷新列表，更新用户的在线状态 -->
                    <el-button type="primary" @click="fetchUserList">刷新状态</el-button>
                </div>
            </div>
        </template>

        <!-- 用户列表表格 -->
        <!-- 用户列表表格 -->
        <!-- 添加了 border 让列之间有分割线，更容易看清铺满的效果 -->
        <el-table :data="userList" style="width: 100%" v-loading="loading" border>

            <!-- 用户名：去掉固定 width，添加 align="center" 居中 -->
            <el-table-column prop="username" label="用户名" align="center" />

            <!-- 昵称 -->
            <el-table-column prop="nickname" label="昵称" align="center" />

            <!-- 角色 -->
            <el-table-column prop="role" label="角色" align="center">
                <template #default="{ row }">
                    <el-tag v-if="row.role === 'admin'" type="warning">管理员</el-tag>
                    <el-tag v-else type="info">普通用户</el-tag>
                </template>
            </el-table-column>

            <!-- 在线状态 -->
            <el-table-column label="在线状态" align="center">
                <template #default="{ row }">
                    <el-tag v-if="row.isOnline === true" type="success" effect="dark">在线</el-tag>
                    <el-tag v-else-if="row.isOnline === false" type="danger" effect="plain">离线</el-tag>
                    <el-tag v-else type="info">未知</el-tag>
                </template>
            </el-table-column>

            <!-- 操作 -->
            <el-table-column label="操作" align="center">
                <template #default="{ row }">
                    <el-button type="danger" plain size="small" :disabled="row.isOnline === false"
                        @click="handleKickout(row)">
                        强制下线
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页组件 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" class="pagination" />
    </el-card>
</template>



<style lang="scss" scoped>
.page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    /* 分页靠右对齐 */
}
</style>