import test from 'node:test'
import assert from 'node:assert/strict'
const saved=new Map()
globalThis.localStorage={getItem:k=>saved.get(k)||null,setItem:(k,v)=>saved.set(k,v),removeItem:k=>saved.delete(k)}
const {preferences,savePreferences,validateEndpoint,clearAIKey,restoreAIKey}=await import('../src/utils/preferences.js')
const {askAI}=await import('../src/utils/aiClient.js')
test('API Key 按接口长期保存，设置元数据不包含密钥，地址需 HTTPS，支持完整接口地址归一化',async()=>{
 await savePreferences({endpoint:'https://fixture.invalid/v1/chat/completions',model:'preview',apiKey:'dummy-local-key',alarmMinutes:10,alarmEnabled:false})
 assert.equal(preferences.endpoint,'https://fixture.invalid/v1');assert.equal(saved.get('origin.preferences').includes('dummy-local-key'),false)
 assert.throws(()=>validateEndpoint('http://fixture.invalid'),/HTTPS/);assert.throws(()=>validateEndpoint('https://example.invalid?api_key=test'),/HTTPS/)
 preferences.apiKey='';await restoreAIKey();assert.equal(preferences.apiKey,'dummy-local-key');await clearAIKey();assert.equal(preferences.apiKey,'')
})
test('图片和前次附件按视觉消息发送，响应需经过日程校验',async()=>{
 preferences.apiKey='dummy-local-key';const original=globalThis.fetch;let called=false
 globalThis.fetch=async(url,options)=>{called=true;assert.equal(url,'https://fixture.invalid/v1/chat/completions');const body=JSON.parse(options.body);assert.equal(body.messages.at(-1).content[1].type,'image_url');assert.equal(options.redirect,'error');assert.equal(options.credentials,'omit');return new Response(JSON.stringify({choices:[{message:{content:'{"reply":"请提供学期起始日","actions":[]}'}}]}),{status:200})}
 try{assert.equal((await askAI('请导入',{kind:'image',data:'data:image/png;base64,AAAA',name:'test.png'},[])).actions.length,0);assert.ok(called)}finally{globalThis.fetch=original;clearAIKey()}
})
test('鉴权失败和网络错误返回可读提示，不泄露服务返回内容或密钥',async()=>{
 preferences.apiKey='dummy-local-key';const original=globalThis.fetch
 try{globalThis.fetch=async()=>new Response('{"error":{"message":"dummy-local-key"}}',{status:401});await assert.rejects(askAI('hello',null,[]),/API Key 无效/);globalThis.fetch=async()=>{throw new Error('dummy-local-key')};await assert.rejects(askAI('hello',null,[]),/连接失败/)}finally{globalThis.fetch=original;clearAIKey()}
})
