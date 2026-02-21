<template>
  <div style="padding: 20px; max-width: 600px; margin: 0 auto;">
    <h1 style="color: #409EFF;">Account</h1>
    <el-card style="margin-top: 16px;">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="Email">{{ user.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Name">{{ user.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Status">{{ user.status === 1 ? 'Active' : 'Inactive' }}</el-descriptions-item>
        <el-descriptions-item label="Create Time">{{ user.create_time || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Update Time">{{ user.update_time || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <div style="margin-top: 20px; display: flex; gap: 12px;">
      <el-button type="primary" @click="goSchedule">Back to Schedule</el-button>
      <el-button type="danger" @click="handleLogout">Logout</el-button>
    </div>
  </div>
  </template>

<script setup>
import { ref, onMounted } from 'vue'
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
    router.replace('/')
  }
}

const handleLogout = async () => {
  try {
    await request.post('/user/logout')
    ElMessage.success('已退出登录')
    router.replace('/')
  } catch (e) {
    ElMessage.error('退出失败')
  }
}

const goSchedule = () => router.push('/schedule')

onMounted(fetchMe)
</script>

<style scoped>
</style>
