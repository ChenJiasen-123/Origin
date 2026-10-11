import axios from 'axios'
import { Capacitor } from '@capacitor/core'
import { syncWidget } from './scheduleWidget'
import { clearAlarms } from './scheduleAlarms'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || (Capacitor.isNativePlatform() ? 'http://43.138.166.243:2528' : '/origin'),
  timeout: 5000,
  withCredentials: true
})

request.interceptors.response.use(async response => {
  if (response.config.url === '/user/me') {
    if (response.data?.success) {
      sessionStorage.setItem('origin.authenticated', '1')
      const user = response.data.data
      if (user?.id != null || user?.email) sessionStorage.setItem('origin.chatOwner', String(user.id ?? user.email))
    }
    else { sessionStorage.removeItem('origin.authenticated'); sessionStorage.removeItem('origin.chatOwner'); await Promise.allSettled([syncWidget([]),clearAlarms()]) }
  }
  if (response.data?.success && response.config.url === '/user/logout') {
    sessionStorage.removeItem('origin.authenticated'); sessionStorage.removeItem('origin.chatOwner')
    await Promise.allSettled([syncWidget([]),clearAlarms()])
  }
  if (response.data?.success && response.config.url === '/user/login') await Promise.allSettled([syncWidget([]),clearAlarms()])
  return response
})

export default request
