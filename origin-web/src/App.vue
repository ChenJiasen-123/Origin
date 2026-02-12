<template>
  <div id="app-container">
    <header class="navbar">
      <div class="header-content">
        <h2 class="logo">Origin</h2>

        <nav class="desktop-nav">
          <router-link to="/" class="nav-item">Home</router-link>
          <router-link to="/friend" class="nav-item">Friend</router-link>
          <router-link to="/notification" class="nav-item">Notification</router-link>
          <router-link to="/schedule" class="nav-item">Schedule</router-link>
        </nav>

        <el-button
          class="mobile-menu-btn"
          type="primary"
          @click="drawer = true"
          circle
        >
          <el-icon><Menu /></el-icon>
        </el-button>
      </div>
    </header>

    <el-drawer
      v-model="drawer"
      title="Menu"
      direction="ltr"
      size="60%"
    >
      <div class="mobile-nav-list">
        <router-link to="/" class="mobile-nav-item" @click="drawer = false">Home</router-link>
        <router-link to="/friend" class="mobile-nav-item" @click="drawer = false">Friend</router-link>
        <router-link to="/notification" class="mobile-nav-item" @click="drawer = false">Notification</router-link>
        <router-link to="/schedule" class="mobile-nav-item" @click="drawer = false">Schedule</router-link>
      </div>
    </el-drawer>

    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Menu } from '@element-plus/icons-vue' // 需要先安装图标库

const drawer = ref(false) // 控制抽屉打开/关闭
</script>

<style>
/* 基础样式 */
body { margin: 0; font-family: Arial, sans-serif; }

.navbar {
  background-color: #409EFF;
  color: white;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo { margin: 0; font-style: italic; }

/* 电脑端导航样式 */
.desktop-nav { display: flex; }
.nav-item {
  color: white;
  margin-left: 20px;
  text-decoration: none;
  font-weight: bold;
}

/* 手机端按钮默认隐藏 */
.mobile-menu-btn { display: none; }

/* 抽屉内菜单样式 */
.mobile-nav-list { display: flex; flex-direction: column; }
.mobile-nav-item {
  padding: 15px 0;
  text-decoration: none;
  color: #333;
  font-size: 18px;
  border-bottom: 1px solid #eee;
}

/* 响应式核心：媒体查询 */
@media (max-width: 768px) {
  .desktop-nav { display: none; }      /* 隐藏电脑导航 */
  .mobile-menu-btn { display: block; } /* 显示手机按钮 */
}

.main-content { padding: 20px; }
</style>