<template>
  <div style="text-align: center; margin-top: 200px; padding: 0 20px;">
    <div style="display: inline-block; text-align: right;">

      <h1 class="main-title">
        Welcome to Origin
      </h1>

      <p class="sub-title">
        ———— 一款专门用于社交信息管理的软件
      </p>

      <div style="text-align: center; margin-top: 50px;">
        <el-button type="primary" size="large" @click="$router.push('/friend')">
          立即进入
        </el-button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.main-title {
  color: #409EFF;
  font-weight: bold;
  font-style: italic;
  font-size: clamp(40px, 8vw, 80px); /* 响应式字体：最小40，随屏幕变大，最大80 */
  margin: 0;
}

.sub-title {
  font-style: italic;
  color: #909399;
  font-size: clamp(14px, 2vw, 18px); /* 同理响应式 */
  margin: 10px 0 0 0;
}
</style>