import test from 'node:test'
import assert from 'node:assert/strict'
import { buildAlarmPlan, localTimestamp } from '../src/utils/alarmPlan.js'
import { parseAIPlan, assertPlanFresh } from '../src/utils/aiPlan.js'
const course={id:7,name:'高数',start_time:'2027-03-01 00:05:00',end_time:'2027-03-01 01:00:00',location:'A101',note:'保留备注',who:['老师'],color:'#409EFF',is_completed:false,reminder_time:null}
test('提醒默认关闭；单条提前时间跨日期；完成/过去/无效日程不响铃',()=>{
 const plan=buildAlarmPlan([{...course,reminder_time:'2027-02-28 23:55:00'},{...course,id:8,is_completed:true},{...course,id:9,reminder_time:'2027-02-28 23:00:00'},{...course,id:10,start_time:'2027-02-30 10:00:00'},{...course,id:11,start_time:'2000-01-01 08:00:00'}],{alarmEnabled:true,alarmMinutes:10},localTimestamp('2027-02-28 22:00:00'))
 assert.deepEqual(plan.map(p=>p.id),['9','7']);assert.equal(plan[1].at,localTimestamp('2027-02-28 23:55:00'))
 assert.deepEqual(buildAlarmPlan([course],{alarmEnabled:false},0),[]);assert.deepEqual(buildAlarmPlan([course],{alarmEnabled:true,alarmMinutes:10},0),[])
})
test('部分修改保留备注、地点等字段；删除只允许已有 ID',()=>{
 const plan=parseAIPlan(JSON.stringify({reply:'改到明天',actions:[{type:'update',id:'7',schedule:{start_time:'2027-03-02 09:00:00',end_time:'2027-03-02 10:00:00'}}]}),[course])
 assert.equal(plan.actions[0].schedule.note,'保留备注');assert.equal(plan.actions[0].schedule.location,'A101')
 assert.throws(()=>parseAIPlan('{"actions":[{"type":"delete","id":"999"}]}',[course]),/不存在/)
})
test('拒绝无效日期、倒序时间和重复修改；课表变更需重新生成',()=>{
 for(const patch of [{start_time:'2027-02-30 10:00:00'},{end_time:'2027-02-28 10:00:00'}])assert.throws(()=>parseAIPlan(JSON.stringify({actions:[{type:'create',schedule:{...course,...patch}}]}),[]),/无效/)
 assert.throws(()=>parseAIPlan('{"actions":[{"type":"delete","id":"7"},{"type":"delete","id":"7"}]}',[course]),/重复/)
 const {actions}=parseAIPlan('{"actions":[{"type":"delete","id":"7"}]}',[course]);assertPlanFresh(actions,[course]);assert.throws(()=>assertPlanFresh(actions,[{...course,name:'已修改'}]),/变更/)
})
test('澄清问题不产生变更，拒绝非 JSON 返回',()=>{assert.deepEqual(parseAIPlan('```json\n{"reply":"学期何时开始？","actions":[]}\n```',[]).actions,[]);assert.throws(()=>parseAIPlan('好的已修改',[]),/未返回有效/);})

test('AI 新日程颜色必填，不再自动补蓝色；修改其他字段保留已有颜色',()=>{
 for(const color of [undefined,null,'','blue','#fff','#12345g']) {
  assert.throws(()=>parseAIPlan(JSON.stringify({actions:[{type:'create',schedule:{...course,color}}]}),[]),/颜色/)
 }
 const valid=parseAIPlan(JSON.stringify({actions:[{type:'create',schedule:{...course,color:'#67C23A'}}]}),[])
 assert.equal(valid.actions[0].schedule.color,'#67C23A')
 const updated=parseAIPlan(JSON.stringify({actions:[{type:'update',id:'7',schedule:{name:'数学复习'}}]}),[{...course,color:'#E6A23C'}])
 assert.equal(updated.actions[0].schedule.color,'#E6A23C')
 assert.throws(()=>parseAIPlan(JSON.stringify({actions:[{type:'update',id:'7',schedule:{color:''}}]}),[course]),/颜色/)
})

test('删除没有颜色的历史日程不受新建颜色必填规则影响',()=>{
 const legacy={...course,color:null}
 const plan=parseAIPlan(JSON.stringify({actions:[{type:'delete',id:'7'}]}),[legacy])
 assert.doesNotThrow(()=>assertPlanFresh(plan.actions,[legacy]))
 assert.throws(()=>assertPlanFresh(plan.actions,[{...legacy,name:'其他'}]),/变更/)
})
