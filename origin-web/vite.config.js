import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/origin': {
        target: 'http://43.138.166.243:2528',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/origin/, '')
      }
    }
  }
})
