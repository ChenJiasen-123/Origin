import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    proxy: {
      '/origin': {
        target: 'http://localhost:8080',//服务器IP和端口
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/origin/, '')
      }
    }
  }
})