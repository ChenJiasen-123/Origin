<template>
  <div style="text-align: center; margin-top: 120px; padding: 0 20px;">
    <div style="display: inline-block; text-align: right;">
      <h1 class="main-title">Welcome to Origin</h1>
      <p class="sub-title">———— 一款专门用于社交信息管理的软件</p>

      <div style="text-align: left; margin-top: 40px; max-width: 420px;">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="color:#409EFF; font-weight:bold">登录</span>
              <el-link type="primary" @click="isRegister = !isRegister">
                {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
              </el-link>
            </div>
          </template>

          <el-form :model="form" label-position="top">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="example@mail.com" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" placeholder="仅允许字母和数字" />
            </el-form-item>
            <el-form-item v-if="isRegister" label="昵称（可选）">
              <el-input v-model="form.name" placeholder="你的昵称" />
            </el-form-item>
            <el-form-item v-if="isRegister" label="邮箱验证码">
              <div style="display:flex; gap:8px; width:100%;">
                <el-input v-model="form.code" placeholder="6位验证码" />
                <el-button :disabled="countdown>0" @click="sendCode">{{ countdown>0 ? countdown+'s' : '获取验证码' }}</el-button>
              </div>
            </el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%;"
              :loading="loading"
              @click="handleSubmit"
            >
              {{ isRegister ? '注册并登录' : '登录' }}
            </el-button>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const isRegister = ref(false)
const loading = ref(false)
const form = ref({
  email: '',
  password: '',
  name: ''
})
const countdown = ref(0)
let timer = null

const sendCode = async () => {
  if (!form.value.email) {
    ElMessage.warning('请输入邮箱')
    return
  }
  try {
    const res = await request.post('/user/sendCode', { email: form.value.email })
    if (res.data?.success) {
      ElMessage.success('验证码已发送，请检查邮箱（注意垃圾箱）')
      countdown.value = 60
      timer = setInterval(() => {
        countdown.value -= 1
        if (countdown.value <= 0) {
          clearInterval(timer); timer = null
        }
      }, 1000)
    } else {
      throw new Error(res.data?.errorMsg || '发送失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '发送失败')
  }
}

const handleSubmit = async () => {
  if (!form.value.email || !form.value.password) {
    ElMessage.warning('请输入邮箱和密码')
    return
  }
  if (!/^[A-Za-z0-9]+$/.test(form.value.password)) {
    ElMessage.warning('密码仅允许大小写字母和数字')
    return
  }
  loading.value = true
  try {
    if (isRegister.value) {
      const res = await request.post('/user/register', form.value)
      if (!res.data?.success) {
        throw new Error(res.data?.errorMsg || '注册失败')
      }
    }
    const loginRes = await request.post('/user/login', {
      email: form.value.email,
      password: form.value.password
    })
    if (loginRes.data?.success) {
      ElMessage.success('登录成功')
      router.push('/friend')
    } else {
      throw new Error(loginRes.data?.errorMsg || '登录失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

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
