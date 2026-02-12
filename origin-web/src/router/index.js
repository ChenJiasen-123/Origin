import { createRouter, createWebHistory } from 'vue-router'
// 1. 引入 Home 组件
import Home from '../views/Home.vue'
import FriendList from '../views/FriendList.vue'
import FriendDetail from '../views/FriendDetail.vue'
import FriendEdit from '../views/FriendEdit.vue'
import Schedule from '../views/Schedule.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/friend',
    name: 'FriendList',
    component: FriendList
  },
  {
    path: '/friend/add',
    name: 'FriendAdd',
    component: FriendEdit
  },
  {
    path: '/friend/:id',
    name: 'FriendDetail',
    component: FriendDetail
  },
  {
    path: '/friend/edit/:id',
    name: 'FriendEdit',
    component: FriendEdit
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: Schedule}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router