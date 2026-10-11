import test from 'node:test'
import assert from 'node:assert/strict'
import {selectAIContext} from '../src/utils/aiContext.js'
const now=new Date(2026,9,11,15)
const courses=Array.from({length:1200},(_,id)=>({id,name:'课程'+id,start_time:`2026-10-${String(id%30+1).padStart(2,'0')} 14:00:00`,end_time:`2026-10-${String(id%30+1).padStart(2,'0')} 15:00:00`,note:'x'.repeat(1000)}))
test('1200条课程查询今天下午不拒绝且仅选择今天',()=>{const r=selectAIContext('今天下午的日程是什么',courses,now);assert.equal(r.schedules.length,40);assert.ok(r.schedules.every(s=>s.start_time.startsWith('2026-10-11')));assert.equal(r.scope.total,1200);assert.equal(r.scope.truncated,false)})
test('截图中的中文混合日期可准确选择',()=>{const r=selectAIContext('2026.10月10号下午的日程是什么',courses,now);assert.equal(r.scope.from,'2026-10-10');assert.equal(r.schedules.length,40)})
test('周范围、日期范围及空日期不混入其他课程',()=>{assert.equal(selectAIContext('this week',courses,now).scope.from,'2026-10-05');assert.equal(selectAIContext('2027-01-01',courses,now).schedules.length,0);const r=selectAIContext('2026-10-10到2026-10-12',courses,now);assert.equal(r.schedules.length,120)})
test('范围太大时明确标记截断且限定上下文大小',()=>{const r=selectAIContext('这个月',courses,now);assert.equal(r.scope.truncated,true);assert.ok(JSON.stringify(r.schedules).length<67000)})
