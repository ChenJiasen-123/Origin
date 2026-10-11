import test from 'node:test'
import assert from 'node:assert/strict'
function storage() { const data = new Map(); return {getItem:k=>data.get(k)??null,setItem:(k,v)=>data.set(k,String(v)),removeItem:k=>data.delete(k)} }
globalThis.localStorage=storage();globalThis.sessionStorage=storage()
let version=0
async function fresh() { return import(`../src/utils/chatHistory.js?test=${++version}`) }
test('聊天切换页面保留同一会话，重启恢复消息、草稿和变更状态，账号隔离',async()=>{
 sessionStorage.setItem('origin.chatOwner','user-a')
 const first=await fresh();const a=first.useChatHistory()
 a.messages.value.push({role:'user',text:'明天下午有什么课',state:'sent'})
 a.messages.value.push({role:'assistant',text:'有两节课',actions:[],state:'pending'})
 a.draft.value='把第二节改到晚上'
 assert.equal(first.useChatHistory(),a)
 const reopened=(await fresh()).useChatHistory()
 assert.equal(reopened.messages.value.length,2)
 assert.equal(reopened.draft.value,'把第二节改到晚上')
 assert.equal(reopened.messages.value[1].state,'pending')
 sessionStorage.setItem('origin.chatOwner','user-b')
 assert.equal(first.useChatHistory().messages.value.length,0)
 sessionStorage.setItem('origin.chatOwner','user-a')
 assert.equal(first.useChatHistory().messages.value[0].text,'明天下午有什么课')
})
test('意外退出后的发送状态可重试，损坏记录不会阻止打开助手',async()=>{
 sessionStorage.setItem('origin.chatOwner','interrupted')
 localStorage.setItem('origin.chat.v1:interrupted',JSON.stringify({messages:[{role:'user',text:'查询',state:'sending'}]}))
 assert.equal((await fresh()).useChatHistory().messages.value[0].state,'failed')
 sessionStorage.setItem('origin.chatOwner','broken');localStorage.setItem('origin.chat.v1:broken','{')
 assert.deepEqual((await fresh()).useChatHistory().messages.value,[])
})
