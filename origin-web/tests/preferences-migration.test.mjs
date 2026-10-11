import test from 'node:test'
import assert from 'node:assert/strict'
const saved=new Map([['origin.preferences',JSON.stringify({endpoint:'https://api.openai.com/v1',model:'deepseek',alarmMinutes:10,language:'en'})]])
globalThis.localStorage={getItem:k=>saved.get(k)||null,setItem:(k,v)=>saved.set(k,v),removeItem:k=>saved.delete(k)}
const {preferences,savePreferences,preferencesReady}=await import('../src/utils/preferences.js')
await preferencesReady
test('旧OpenAI地址和DeepSeek模型混合配置升级为DeepSeek且持久保存',()=>{assert.equal(preferences.endpoint,'https://api.deepseek.com/v1');assert.equal(preferences.model,'deepseek-flash');assert.equal(preferences.language,'en');assert.equal('alarmMinutes' in preferences,false);assert.equal(JSON.parse(saved.get('origin.preferences')).endpoint,'https://api.deepseek.com/v1');assert.equal(preferences.apiKey,'')})
test('明确重新选择的自定义服务仍可保存',async()=>{await savePreferences({endpoint:'https://custom.invalid/v1',model:'custom',apiKey:'local-test-key',alarmEnabled:false,language:'zh'});assert.equal(preferences.endpoint,'https://custom.invalid/v1');assert.equal(JSON.parse(saved.get('origin.preferences')).configVersion,2)})
