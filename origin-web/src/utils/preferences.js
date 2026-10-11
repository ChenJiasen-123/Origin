import { reactive } from 'vue'
import { Capacitor, registerPlugin } from '@capacitor/core'
import { syncWidgetLanguage } from './scheduleWidget.js'
const secrets = registerPlugin('OriginSecrets')
const defaults = { configVersion: 2, endpoint: 'https://api.deepseek.com/v1', model: 'deepseek-flash', alarmEnabled: false, language: 'zh' }
let stored = {}
try { stored = JSON.parse(localStorage.getItem('origin.preferences') || '{}') } catch {}
delete stored.alarmMinutes
if (stored.configVersion !== 2 && /^https:\/\/api\.openai\.com\/v1\/?$/.test(stored.endpoint || '')) { stored.endpoint = defaults.endpoint; stored.model = defaults.model }
if (/^https:\/\/api\.deepseek\.com(?:\/v1)?\/?$/.test(stored.endpoint || '') && (!stored.model || stored.model === 'deepseek')) stored.model = defaults.model
stored.configVersion = 2
try { const { apiKey, ...safe } = stored; localStorage.setItem('origin.preferences', JSON.stringify(safe)) } catch {}
export const preferences = reactive({ ...defaults, ...stored, language: stored.language === 'en' ? 'en' : 'zh', apiKey: '' })
export function validateEndpoint(value) {
  const url = new URL(value.trim())
  if (url.protocol !== 'https:' || url.username || url.password || url.search || url.hash) throw new Error('请填写 HTTPS API 地址，不要包含密钥、查询参数或账号密码')
  return url.href.replace(/\/+$/, '').replace(/\/chat\/completions$/, '')
}
function secretId(endpoint) { return `ai:${validateEndpoint(endpoint)}` }
export async function restoreAIKey() {
  const endpoint = preferences.endpoint
  try {
    const id = secretId(endpoint)
    const key = Capacitor.getPlatform() === 'android' ? (await secrets.get({ id })).value || '' : localStorage.getItem(`origin.key.${id}`) || ''
    if (preferences.endpoint === endpoint) preferences.apiKey = key
  } catch { preferences.apiKey = '' }
}
export const preferencesReady = restoreAIKey()
export async function savePreferences(value) {
  const endpoint = validateEndpoint(value.endpoint)
  const key = value.apiKey.trim(), id = secretId(endpoint)
  if (Capacitor.getPlatform() === 'android') await secrets.set({ id, value: key })
  else if (key) localStorage.setItem(`origin.key.${id}`, key)
  else localStorage.removeItem(`origin.key.${id}`)
  Object.assign(preferences, { endpoint, model: value.model.trim() || defaults.model, apiKey: key, alarmEnabled: !!value.alarmEnabled, language: value.language === 'en' ? 'en' : 'zh' })
  const { apiKey, ...persistent } = preferences
  localStorage.setItem('origin.preferences', JSON.stringify(persistent))
}
export async function clearAIKey() {
  const id = secretId(preferences.endpoint)
  if (Capacitor.getPlatform() === 'android') await secrets.set({id,value:''})
  else localStorage.removeItem(`origin.key.${id}`)
  preferences.apiKey = ''
}
export function setLanguage(value) {
  syncWidgetLanguage(value).catch(()=>{})
  preferences.language = value === 'en' ? 'en' : 'zh'
  const { apiKey, ...persistent } = preferences
  localStorage.setItem('origin.preferences', JSON.stringify(persistent))
}
