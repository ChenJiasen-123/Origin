<template>
  <div class="settings-page"><h1>{{ t('设置','Settings') }}</h1>
    <form @submit.prevent="save" class="settings-layout">
      <section class="setting-card general-card"><h2>{{ t('通用','General') }}</h2><label>{{ t('语言','Language') }}<select :value="preferences.language" @change="changeLanguage($event.target.value)"><option value="zh">简体中文</option><option value="en">English</option></select></label></section>
      <section class="setting-card"><h2>{{ t('AI 服务','AI service') }}</h2>
        <label>{{ t('API 地址','API URL') }}<input v-model="form.endpoint" type="url" required autocomplete="off" placeholder="https://api.deepseek.com/v1" @input="endpointChanged"/></label>
        <label>{{ t('模型','Model') }}<input v-model="form.model" placeholder="deepseek-flash" autocomplete="off"/></label>
        <label>API Key<div class="key-field"><input v-model="form.apiKey" :type="showKey ? 'text' : 'password'" autocomplete="off" spellcheck="false" :placeholder="t('输入 API Key','Enter API Key')"/><button type="button" @click="showKey = !showKey">{{ showKey ? t('隐藏','Hide') : t('显示','Show') }}</button></div></label>
        <p class="field-help">{{ isAndroid ? t('密钥加密保存在此设备，重启后自动读取。','Key is encrypted on this device and restored after restart.') : t('密钥保存在此浏览器，请勿在共享设备保存。','Key is saved in this browser. Avoid shared devices.') }}</p>
        <button type="button" class="text-action" @click="clearKey">{{ t('清除密钥','Remove saved key') }}</button>
      </section>
      <section class="setting-card"><h2>{{ t('课前闹钟','Course alarms') }}</h2>
        <div class="toggle-row"><label>{{ t('启用闹钟','Enable alarms') }}</label><input v-model="form.alarmEnabled" type="checkbox" role="switch" :aria-label="t('启用闹钟','Enable alarms')" :disabled="!isAndroid"/></div>
        <div v-if="isAndroid" class="permission-box"><p>{{ status.exact && status.notifications ? t('权限已就绪','Permissions ready') : t('需要通知和精确闹钟权限','Notification and exact alarm access required') }} · {{ status.count || 0 }} {{ t('个闹钟','alarms') }}</p><button type="button" class="text-action" @click="authorize" :disabled="busy">{{ t('授权','Grant access') }}</button><button type="button" class="text-action" @click="refresh" :disabled="busy">{{ t('刷新','Refresh') }}</button></div>
        <p v-else class="field-help">{{ t('闹钟仅支持安卓安装版。','Alarms are available in the Android app.') }}</p>
      </section>
      <section v-if="isAndroid" class="setting-card widget-card"><h2>{{ t('桌面课表','Home screen widget') }}</h2><p class="field-help">{{ widgetState.count ? t('已添加','Added') : t('未添加','Not added') }} · {{ widgetState.launcher || t('系统桌面','System launcher') }}</p>
        <button type="button" class="primary-action" @click="addWidget" :disabled="widgetBusy">{{ t('添加桌面课表','Add schedule widget') }}</button>
        <p v-if="widgetMessage" class="field-help" role="status">{{ widgetMessage }}</p>
        <div v-if="widgetAttempted && !widgetState.count" class="widget-fallback"><p>{{ t('没有弹窗时：进入桌面，长按空白处 → 小组件／插件 → Origin。','No dialog? On your home screen, hold an empty area → Widgets → Origin.') }}</p><button type="button" class="text-action" @click="openHome">{{ t('前往桌面','Open home screen') }}</button><button type="button" class="text-action" @click="refreshWidget">{{ t('检查添加状态','Check widget status') }}</button></div>
      </section>
      <footer class="settings-footer"><p role="status" :class="{error:failed}">{{ message }}</p><button class="primary-action" :disabled="busy">{{ busy ? t('保存中…','Saving…') : t('保存','Save') }}</button></footer>
    </form>
  </div>
</template>
<script setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { preferences, preferencesReady, savePreferences, clearAIKey, setLanguage } from '../utils/preferences'
import { t } from '../utils/i18n'
import { isAndroid, alarmStatus, requestAlarmAccess, syncAlarms } from '../utils/scheduleAlarms'
import { pinWidget, widgetStatus, openWidgetHome } from '../utils/scheduleWidget'
import request from '../utils/request'
const form=reactive({...preferences}),showKey=ref(false),busy=ref(false),message=ref(''),failed=ref(false),status=ref({}),widgetState=ref({}),widgetBusy=ref(false),widgetAttempted=ref(false),widgetMessage=ref('')
function changeLanguage(value){setLanguage(value);form.language=value;message.value=''}
async function endpointChanged(){form.apiKey='';message.value=''}
async function clearKey(){try{await clearAIKey();form.apiKey='';message.value=t('密钥已清除','Saved key removed')}catch(e){message.value=e.message}}
async function refreshWidget(){try{widgetState.value=await widgetStatus();if(widgetAttempted.value)widgetMessage.value=widgetState.value.count?t('桌面课表已添加','Widget added'):t('桌面尚未确认添加','Launcher has not confirmed adding the widget')}catch{widgetMessage.value=t('无法读取桌面状态','Unable to read widget status')}}
async function addWidget(){widgetBusy.value=true;widgetAttempted.value=true;try{const result=await pinWidget();widgetState.value=result;widgetMessage.value=result.requested?t('添加请求已发送，请在桌面弹窗中确认。','Request sent. Confirm in the launcher dialog.'):t('当前桌面不支持直接添加，请手动添加。','This launcher requires adding the widget manually.')}catch(e){widgetMessage.value=e.message||t('请在桌面手动添加','Please add it from the launcher')}finally{widgetBusy.value=false}}
async function openHome(){try{await openWidgetHome()}catch{widgetMessage.value=t('请按手机 Home 键返回桌面','Use the Home button to open the launcher')}}
async function refresh(){try{status.value=await alarmStatus()}catch{message.value=t('无法读取闹钟状态','Unable to read alarm status')}}
async function authorize(){try{await requestAlarmAccess();await refresh();message.value=''}catch(e){message.value=e.message;failed.value=true}}
async function save(){busy.value=true;failed.value=false;try{form.language=preferences.language;await savePreferences(form);if(isAndroid){const r=await request.get('/schedule');if(!r.data?.success)throw new Error(t('课表读取失败，请重试','Unable to load schedules; please retry'));status.value=await syncAlarms(r.data.data||[])}message.value=form.alarmEnabled&&isAndroid&&(!status.value.exact||!status.value.notifications)?t('已保存，请授权闹钟','Saved. Please grant alarm access'):t('已保存','Saved')}catch(e){message.value=e.message;failed.value=true}finally{busy.value=false}}
function resumed(){if(document.visibilityState==='visible'){refresh();refreshWidget()}}
onMounted(async()=>{await preferencesReady;form.apiKey=preferences.apiKey;refresh();refreshWidget();window.addEventListener('focus',resumed);document.addEventListener('visibilitychange',resumed)})
onUnmounted(()=>{window.removeEventListener('focus',resumed);document.removeEventListener('visibilitychange',resumed)})
</script>
<style scoped>
.settings-page{max-width:1000px;margin:auto;padding:28px 24px 50px;color:#20344a}h1{font-size:27px;margin:0 0 24px}h2{font-size:17px;margin:0 0 20px}.settings-layout{display:grid;grid-template-columns:1fr 1fr;gap:20px}.setting-card{background:#fff;border:1px solid #e8edf3;border-radius:16px;padding:24px;min-width:0}.general-card{grid-column:1/-1}.general-card label{display:flex;align-items:center;justify-content:space-between;margin:0}.general-card select{width:180px;margin:0}label{display:block;font-size:13px;font-weight:600;margin-top:18px}label input,select{box-sizing:border-box;width:100%;border:1px solid #dfe7f0;border-radius:9px;padding:12px;font-size:14px;margin-top:8px;color:#20344a;background:#fbfcfe;min-width:0}input:focus,select:focus{outline:2px solid #409eff50;border-color:#409eff}.field-help{font-size:12px;line-height:1.7;color:#8495a6;margin:12px 0}.key-field{display:flex;align-items:center;gap:8px}.key-field button{background:none;border:0;color:#409eff;white-space:nowrap;font-size:12px}.text-action{border:0;background:none;color:#409eff;cursor:pointer;font-size:13px;padding:10px 0;margin-right:16px}.toggle-row{display:flex;align-items:center;justify-content:space-between;gap:12px}.toggle-row label{margin:0}.toggle-row input{appearance:none;width:42px;height:24px;border-radius:15px;background:#dfe7ef;position:relative;flex-shrink:0;cursor:pointer}.toggle-row input:before{content:"";position:absolute;width:18px;height:18px;border-radius:50%;background:#fff;left:3px;top:3px;box-shadow:0 1px 4px #20344a25}.toggle-row input:checked{background:#409eff}.toggle-row input:checked:before{transform:translateX(18px)}.toggle-row input:disabled{opacity:.5}.minutes-field{display:flex;align-items:center;gap:12px}.minutes-field input{width:110px}.minutes-field span{font-size:12px;color:#8a96a4}.minute-options{display:flex;gap:7px;flex-wrap:wrap;margin:14px 0}.minute-options button{background:#f5f7fa;border:1px solid transparent;padding:8px;border-radius:8px;font-size:12px;color:#69809a;cursor:pointer}.minute-options .selected{border-color:#409eff;background:#eaf4ff;color:#409eff}.permission-box{border-top:1px solid #edf1f6;padding-top:12px;margin-top:18px;font-size:12px;color:#8495a6}.widget-card{grid-column:1/-1}.widget-fallback{font-size:12px;color:#8495a6;line-height:1.7}.settings-footer{grid-column:1/-1;display:flex;align-items:center;justify-content:space-between;gap:15px}.settings-footer p{font-size:13px;color:#7891ab;line-height:1.7}.settings-footer .error{color:#d34d4d}.primary-action{background:#409eff;color:#fff;border:0;border-radius:9px;padding:12px 22px;font-size:14px;cursor:pointer}button:disabled{opacity:.5;cursor:default}@media(max-width:760px){.settings-page{padding:24px 16px}.settings-layout{grid-template-columns:1fr;gap:16px}.setting-card{padding:20px}.general-card select{width:145px}}
</style>
