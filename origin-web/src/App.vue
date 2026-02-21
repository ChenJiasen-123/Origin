<template>
  <div id="app-container">
    <header class="navbar">
      <div class="header-content">
        <h2 class="logo">Origin</h2>

        <nav class="desktop-nav" v-if="isAuthed">
          <router-link to="/friend" class="nav-item">Friend</router-link>
          <router-link to="/schedule" class="nav-item">Schedule</router-link>
          <router-link to="/account" class="nav-item">Account</router-link>
        </nav>

        <el-button
          class="mobile-menu-btn"
          type="primary"
          @click="drawer = true"
          circle
          v-if="isAuthed"
        >
          <el-icon><Menu /></el-icon>
        </el-button>
      </div>
    </header>

    <el-drawer
      v-model="drawer"
      direction="ltr"
      size="60%"
    >
      <div class="mobile-nav-list" v-if="isAuthed">
        <router-link to="/friend" class="mobile-nav-item" @click="drawer = false">Friend</router-link>
        <router-link to="/schedule" class="mobile-nav-item" @click="drawer = false">Schedule</router-link>
        <router-link to="/account" class="mobile-nav-item" @click="drawer = false">Account</router-link>
        <div style="margin-top: 12px;">
          <el-button type="danger" plain style="width:100%" @click="handleLogout">Logout</el-button>
        </div>
      </div>
    </el-drawer>

    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Menu } from '@element-plus/icons-vue'
import request from './utils/request'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const drawer = ref(false)
const isAuthed = ref(false)
const router = useRouter()
const route = useRoute()

const checkAuth = async () => {
  try {
    const res = await request.get('/user/me')
    isAuthed.value = !!(res.data && res.data.success)
  } catch (e) {
    isAuthed.value = false
  }
}

const handleLogout = async () => {
  try {
    await request.post('/user/logout')
    isAuthed.value = false
    drawer.value = false
    ElMessage.success('已退出登录')
    router.replace('/')
  } catch {
    ElMessage.error('退出失败')
  }
}

onMounted(checkAuth)
watch(() => route.fullPath, () => {
  checkAuth()
})
</script>

<style>
/* 1. 基础样式 */
body { margin: 0; font-family: Arial, sans-serif; }

.navbar {
  background-color: #409EFF;
  color: white;
  padding: 0 15px; /* 移动端间距稍小 */
  height: 60px;
  display: flex;
  align-items: center;
  position: sticky; /* 吸顶 */
  top: 0;
  z-index: 1000;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo { margin: 0; font-style: italic; font-size: 20px; }

/* 2. 【默认状态】就是手机 App 状态 */
.desktop-nav {
  display: none !important; /* 默认不显示横向导航 */
}

.mobile-menu-btn {
  display: flex !important; /* 默认显示汉堡按钮 */
  align-items: center;
  justify-content: center;
}

/* 抽屉内菜单样式 */
.mobile-nav-list { display: flex; flex-direction: column; padding: 0 10px; }
.mobile-nav-item {
  padding: 18px 0;
  text-decoration: none;
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #f5f5f5;
  font-weight: 500;
}

/* 3. 【判断电脑端】只有大屏幕才切换布局 */
@media screen and (min-width: 769px) {
  .mobile-menu-btn {
    display: none !important; /* 电脑端隐藏汉堡按钮 */
  }

  .desktop-nav {
    display: flex !important; /* 电脑端显示横向导航 */
  }

  .nav-item {
    color: white;
    margin-left: 25px;
    text-decoration: none;
    font-weight: bold;
    font-size: 14px;
    transition: opacity 0.2s;
  }
  .nav-item:hover { opacity: 0.8; }
}

.main-content { padding: 0; } /* 既然是 App，内容区通常不建议给死 padding */
</style>
