import { createRouter, createWebHistory } from 'vue-router'
// 1. 引入 Home 组件
import Home from '../views/Home.vue'
import Settings from '../views/Settings.vue'
import Assistant from '../views/Assistant.vue'
import Schedule from '../views/Schedule.vue'
import request from '../utils/request'
import Account from '../views/Account.vue'

const routes = [
  { path: '/settings', name: 'Settings', component: Settings },
  { path: '/assistant', name: 'Assistant', component: Assistant },
  { path: '/', name: 'Home', component: Home },
  { path: '/schedule', name: 'Schedule', component: Schedule },
  { path: '/account', name: 'Account', component: Account },
  { path: '/friend/:pathMatch(.*)*', redirect: '/schedule' },
  { path: '/:pathMatch(.*)*', redirect: '/schedule' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：除 Home 外，其余页面需登录
router.beforeEach(async (to, from, next) => {
  if (to.path === '/') {
    try {
      const res = await request.get('/user/me')
      if (res.data?.success) return next('/schedule')
    } catch {
      if (sessionStorage.getItem('origin.authenticated')) return next('/schedule')
    }
    return next()
  }
  try {
    const res = await request.get('/user/me')
    if (res.data && res.data.success) {
      next()
    } else {
      next('/')
    }
  } catch (e) {
    // A network outage is not a logout; the server still authenticates every API call.
    if (sessionStorage.getItem('origin.authenticated')) next()
    else next('/')
  }
})

export default router
