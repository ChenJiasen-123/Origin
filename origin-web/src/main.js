import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 1. 引入 Element Plus 库（这一步相当于 Java 的 import）
import ElementPlus from 'element-plus'

// 2. 引入 Element Plus 的样式（这行不写，表格就会是乱码或看不见）
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(router) // 告诉 Vue：我们要用路由功能

// 3. 告诉 Vue 使用这个 UI 库（这是最关键的一步，解决 el-table 报错）
app.use(ElementPlus)

app.mount('#app')
