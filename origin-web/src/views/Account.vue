<template>
  <div style="padding: 20px; max-width: 600px; margin: 0 auto;">
    <h1 style="color: #409EFF;">{{ t('账户','Account') }}</h1>
    <el-card style="margin-top: 16px;">
      <el-descriptions :column="1" border>
        <el-descriptions-item :label="t('邮箱','Email')">{{ user.email || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="t('昵称','Name')">{{ user.name || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="t('状态','Status')">{{ user.status === 1 ? t('正常','Active') : t('停用','Inactive') }}</el-descriptions-item>
        <el-descriptions-item :label="t('创建时间','Create Time')">{{ user.create_time || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="t('更新时间','Update Time')">{{ user.update_time || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <div style="margin-top: 20px; display: flex; gap: 12px;">
      <el-button type="primary" @click="goSchedule">{{ t('返回课表','Back to Schedule') }}</el-button>
      <el-button type="danger" @click="handleLogout">{{ t('退出登录','Logout') }}</el-button>
    </div>
  </div>
  </template>

<script setup>
import { ref, onMounted } from 'vue'
import { t } from '../utils/i18n'
import request from '../utils/request'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const user = ref({})

const fetchMe = async () => {
  try {
    const res = await request.get('/user/me')
    if (res.data?.success) {
      user.value = res.data.data || {}
    } else {
      router.replace('/')
    }
  } catch (e) {
    ElMessage.error(t('网络暂时不可用，请稍后重试','Network unavailable. Please retry.'))
  }
}

const handleLogout = async () => {
  try {
    await request.post('/user/logout')
    ElMessage.success(t('已退出登录','Logged out'))
    router.replace('/')
  } catch (e) {
    ElMessage.error(t('退出失败','Unable to log out'))
  }
}

const goSchedule = () => router.push('/schedule')

onMounted(fetchMe)
</script>

<style scoped>
</style>
