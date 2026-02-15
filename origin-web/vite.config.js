import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // 记得引入 path 模块

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // 设置 @ 指向 src 目录
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    host: '0.0.0.0',
    proxy: {
      '/origin': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/origin/, '')
      }
    }
  }
})