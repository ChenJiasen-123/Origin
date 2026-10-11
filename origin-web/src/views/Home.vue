<template>
  <div class="auth-page">
    <section class="auth-story" aria-label="Origin 日程">
      <a class="brand" href="/" aria-label="Origin 首页"><img src="/brand.png" alt="" /><span>Origin</span></a>
      <div class="story-content"><h1>{{ t('我的课表','My schedule') }}</h1>
        <div class="day-preview" aria-hidden="true"><div class="preview-head"><span>{{ t('课表示例','Example timetable') }}</span><span class="live-dot"></span></div><div class="preview-date">{{ t('今天','Today') }}</div><div class="preview-line blue"><span>09:00</span><div><strong>{{ t('高等数学','Mathematics') }}</strong><small>{{ t('教学楼 A301','Room A301') }}</small></div><i></i></div><div class="preview-line orange"><span>11:30</span><div><strong>{{ t('英语','English') }}</strong><small>{{ t('教学楼 B201','Room B201') }}</small></div><i></i></div><div class="preview-line soft"><span>15:00</span><div><strong>{{ t('自习','Study') }}</strong><small>{{ t('图书馆','Library') }}</small></div><i></i></div></div>
      </div>
    </section>
    <section class="auth-panel">
      <div class="mobile-brand"><img src="/brand.png" alt=""/><span>Origin</span></div>
      <div class="auth-card">
        <h2>{{ isRegister ? t('创建账户','Create account') : t('登录','Log in') }}</h2>
        <div class="auth-tabs" role="tablist" aria-label="账户操作"><button type="button" role="tab" :aria-selected="!isRegister" :class="{selected: !isRegister}" @click="isRegister = false">{{ t('登录','Log in') }}</button><button type="button" role="tab" :aria-selected="isRegister" :class="{selected: isRegister}" @click="isRegister = true">{{ t('注册','Sign up') }}</button></div>
        <form class="auth-form" @submit.prevent="handleSubmit">
          <label class="field">{{ t('邮箱','Email') }}<input v-model.trim="form.email" type="email" autocomplete="email" placeholder="you@example.com" required /></label>
          <label class="field">{{ t('密码','Password') }}<input v-model="form.password" type="password" :autocomplete="isRegister ? 'new-password' : 'current-password'" :placeholder="isRegister ? t('使用英文字母和数字','Letters and numbers') : t('输入密码','Password')" required /></label>
          <template v-if="isRegister"><label class="field">{{ t('昵称','Name') }}<input v-model.trim="form.name" autocomplete="nickname" :placeholder="t('怎么称呼你？','Your name')" required /></label><label class="field">{{ t('邮箱验证码','Email code') }}<div class="code-field"><input v-model.trim="form.code" inputmode="numeric" autocomplete="one-time-code" maxlength="6" :placeholder="t('6 位验证码','6 digit code')" required/><button type="button" :disabled="countdown > 0" @click="sendCode">{{ countdown > 0 ? countdown + 's' : t('发送验证码','Send code') }}</button></div></label></template>
          <button class="submit-button" type="submit" :disabled="loading"><span>{{ loading ? t('请稍候…','Please wait…') : (isRegister ? t('创建账户','Create account') : t('登录','Log in')) }}</span></button>
        </form>
        
      </div>
    </section>
  </div>
</template>
<script setup>
import { ref, onUnmounted } from 'vue'
import { t } from '../utils/i18n'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const isRegister = ref(false)
const loading = ref(false)
const form = ref({
  email: '',
  password: '',
  name: '',
  code: ''
})
const countdown = ref(0)
let timer = null
onUnmounted(() => { if (timer) clearInterval(timer) })

const sendCode = async () => {
  if (!form.value.email) {
    ElMessage.warning(t('请输入邮箱','Enter your email'))
    return
  }
  try {
    const res = await request.post('/user/sendCode', { email: form.value.email })
    if (res.data?.success) {
      ElMessage.success(t('验证码已发送，请检查邮箱（注意垃圾箱）','Code sent. Check your email.'))
      countdown.value = 60
      timer = setInterval(() => {
        countdown.value -= 1
        if (countdown.value <= 0) {
          clearInterval(timer); timer = null
        }
      }, 1000)
    } else {
      throw new Error(res.data?.errorMsg || t('发送失败','Unable to send code'))
    }
  } catch (e) {
    ElMessage.error(e.message || t('发送失败','Unable to send code'))
  }
}

const handleSubmit = async () => {
  if (loading.value) return
  if (!form.value.email || !form.value.password) {
    ElMessage.warning(t('请输入邮箱和密码','Enter your email and password'))
    return
  }
  if (!/^[A-Za-z0-9]+$/.test(form.value.password)) {
    ElMessage.warning(t('密码仅允许大小写字母和数字','Password must contain only letters and numbers'))
    return
  }
  loading.value = true
  try {
    if (isRegister.value) {
      const res = await request.post('/user/register', form.value)
      if (!res.data?.success) {
        throw new Error(res.data?.errorMsg || t('注册失败','Unable to create account'))
      }
    }
    const loginRes = await request.post('/user/login', {
      email: form.value.email,
      password: form.value.password
    })
    if (loginRes.data?.success) {
      ElMessage.success(t('登录成功','Logged in'))
      try {
        await request.get('/user/me')
      } catch (e) {
        // ignore; 尝试继续跳转
      }
      router.replace('/schedule')
    } else {
      throw new Error(loginRes.data?.errorMsg || t('登录失败','Unable to log in'))
    }
  } catch (e) {
    ElMessage.error(e.message || t('操作失败','Unable to complete request'))
  } finally {
    loading.value = false
  }
}
</script>
<style scoped>
.auth-page{min-height:100dvh;display:grid;grid-template-columns:1fr 1fr;background:#fafbfd;color:#172c43;font-family:Inter,-apple-system,BlinkMacSystemFont,'Segoe UI','Microsoft YaHei',sans-serif}
.auth-story{position:relative;display:flex;flex-direction:column;justify-content:space-between;background:#eaf4ff;padding:40px clamp(32px,6vw,96px);overflow:hidden;isolation:isolate}
.auth-story:before{content:'';position:absolute;width:540px;height:540px;border:1px solid #409eff24;border-radius:50%;right:-280px;top:100px;z-index:-1}.auth-story:after{content:'';position:absolute;width:380px;height:380px;border:1px solid #409eff24;border-radius:50%;right:-200px;top:180px;z-index:-1}
.brand,.mobile-brand{display:flex;align-items:center;gap:12px;color:#172c43;text-decoration:none;font-weight:700;font-size:25px;letter-spacing:-1px}.brand img,.mobile-brand img{width:44px;height:44px;border-radius:13px}.story-content{padding:50px 0;max-width:410px}.eyebrow,.card-kicker,.story-footer,.panel-footer{font-size:10px;font-weight:700;letter-spacing:2px}.eyebrow{color:#4883ba}h1{font-size:clamp(48px,5.6vw,80px);line-height:1.06;letter-spacing:-4px;font-weight:650;margin:20px 0}h1 span{color:#409eff}.story-content>p{font-size:14px;line-height:1.7;color:#65819b;margin:0 0 34px}.day-preview{background:#ffffffd9;border:1px solid white;border-radius:20px;padding:23px;box-shadow:0 20px 50px #327abd0b;transform:rotate(-2deg)}.preview-head{display:flex;align-items:center;justify-content:space-between;font-size:9px;letter-spacing:1.8px;font-weight:700;color:#7d94a7}.live-dot{height:7px;width:7px;border-radius:50%;background:#409eff}.preview-date{font-size:23px;font-weight:650;letter-spacing:-.8px;margin:14px 0 20px}.preview-line{display:flex;align-items:center;gap:13px;margin-top:10px;padding:13px 12px;border-radius:10px;background:#edf6ff}.preview-line>span{font-size:10px;font-weight:600;color:#7090ac}.preview-line>div{flex:1;border-left:3px solid #409eff;padding-left:11px}.preview-line strong{display:block;font-size:12px;font-weight:600}.preview-line small{display:block;font-size:9px;color:#8296a8;margin-top:5px}.preview-line i{width:5px;height:5px;border-radius:50%;background:#409eff}.preview-line.orange{background:#fff6eb}.orange>div{border-color:#ff9f43}.orange i{background:#ff9f43}.preview-line.soft{background:#f4f7fa}.soft>div{border-color:#b8c8d8}.soft i{background:#b8c8d8}.story-footer{color:#87a1b8;font-size:9px}
.auth-panel{display:flex;flex-direction:column;justify-content:center;align-items:center;padding:48px 32px;position:relative;min-width:0}.auth-card{width:100%;max-width:360px}.card-kicker{color:#409eff;font-size:9px;letter-spacing:1.7px}.auth-card h2{font-size:35px;line-height:1.15;letter-spacing:-1.4px;font-weight:650;margin:14px 0 10px}.card-description{font-size:13px;color:#8492a3;line-height:1.6;margin:0 0 28px}.auth-tabs{display:flex;padding:4px;border-radius:11px;background:#edf1f6;margin-bottom:26px}.auth-tabs button{flex:1;background:none;border:0;padding:10px;border-radius:8px;color:#8190a2;font-size:13px;cursor:pointer}.auth-tabs button.selected{background:#fff;color:#2d85e7;font-weight:650;box-shadow:0 2px 7px #23486b0b}.auth-form{display:grid;gap:18px}.field{display:flex;flex-direction:column;gap:9px;font-size:12px;font-weight:600;color:#526579}.field input{width:100%;min-width:0;box-sizing:border-box;border:1px solid #e1e7ef;background:#fff;border-radius:10px;padding:14px 15px;font:inherit;font-size:14px;font-weight:400;color:#18334f;outline:none;transition:border-color .15s,box-shadow .15s}.field input::placeholder{color:#b0bdcb}.field input:focus{border-color:#409eff;box-shadow:0 0 0 3px #409eff16}.code-field{display:flex;gap:8px;min-width:0}.code-field button{flex-shrink:0;border:1px solid #d8e9fc;background:#eff7ff;border-radius:10px;color:#3289e8;padding:0 12px;font-size:12px;cursor:pointer}.code-field button:disabled{opacity:.55;cursor:default}.submit-button{display:flex;justify-content:center;align-items:center;width:100%;border:0;border-radius:11px;background:#409eff;color:#fff;padding:15px 18px;font-size:14px;font-weight:600;margin-top:8px;cursor:pointer;box-shadow:0 8px 20px #409eff23;transition:background .2s,transform .2s}.submit-button:hover{background:#258beb;transform:translateY(-1px)}.submit-button:disabled{opacity:.65;cursor:wait;transform:none}.submit-button span:last-child{font-size:20px;line-height:1}.auth-note{display:flex;justify-content:center;align-items:center;gap:7px;color:#9aa8b7;font-size:10px;margin-top:24px}.note-dot{width:4px;height:4px;border-radius:50%;background:#99b9d7}.panel-footer{color:#b3bfca;font-size:9px;letter-spacing:1px;margin-top:60px}.panel-footer span{margin:0 8px;color:#409eff}.mobile-brand{display:none}button:focus-visible,a:focus-visible{outline:3px solid #83bcff;outline-offset:3px}
@media(max-width:760px){.auth-page{display:block;background:radial-gradient(ellipse at 20% 0%,#e2f0ff 0%,transparent 55%),#fafbfd}.auth-story{display:none}.auth-panel{min-height:100dvh;box-sizing:border-box;padding:36px 28px 24px;justify-content:flex-start}.mobile-brand{display:flex;align-self:flex-start;font-size:23px;margin-bottom:56px}.mobile-brand img{width:38px;height:38px;border-radius:11px}.auth-card{max-width:380px}.auth-card h2{font-size:34px}.panel-footer{margin-top:auto;padding-top:45px}.auth-note{font-size:10px}.card-description{margin-bottom:26px}}
@media(max-width:350px){.auth-panel{padding:28px 20px 22px}.mobile-brand{margin-bottom:38px}.auth-card h2{font-size:30px}.auth-note{font-size:9px}}
@media(prefers-reduced-motion:reduce){*{transition:none!important}}
</style>
