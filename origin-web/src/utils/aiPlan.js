import { localTimestamp } from './alarmPlan.js'
export function normalizeSchedule(raw, requireColor = true) {
  const name = String(raw.name || '').trim()
  if (!name || name.length > 200) throw new Error('日程名称为空或过长')
  const start = localTimestamp(raw.start_time), end = localTimestamp(raw.end_time)
  if (!Number.isFinite(start) || !Number.isFinite(end) || end <= start) throw new Error(`「${name}」的日期或起止时间无效`)
  if (raw.reminder_time && !Number.isFinite(localTimestamp(raw.reminder_time))) throw new Error(`「${name}」提醒时间无效`)
  if (raw.reminder_time && localTimestamp(raw.reminder_time) > start) throw new Error('提醒不能晚于日程开始')
  if (requireColor && !/^#[0-9a-f]{6}$/i.test(raw.color || '')) throw new Error(`「${name}」缺少有效颜色，请让 AI 为日程选择颜色后重试`)
  const stamp = value => value.replace('T',' ').slice(0,16)+':00'
  return { name, start_time: stamp(raw.start_time), end_time: stamp(raw.end_time), location: String(raw.location || '').slice(0,500), note: String(raw.note || '').slice(0,5000), who: Array.isArray(raw.who) ? raw.who.filter(x=>typeof x==='string').slice(0,30) : [], color: raw.color, is_completed: raw.is_completed === true, reminder_time: raw.reminder_time ? stamp(raw.reminder_time) : null }
}
export function parseAIPlan(content, schedules) {
  if (typeof content !== 'string') throw new Error('模型没有返回文本')
  let result
  try { result = JSON.parse(content.trim().replace(/^```(?:json)?\s*/,'').replace(/\s*```$/,'')) } catch { throw new Error('AI 未返回有效日程方案，请补充日期和时间后重试') }
  if (!Array.isArray(result.actions) || result.actions.length > 100) throw new Error('单次最多处理 100 条日程')
  const seen = new Set()
  const actions = result.actions.map(a => {
    if (!['create','update','delete'].includes(a.type)) throw new Error('未知的日程操作')
    const id = a.id == null ? null : String(a.id)
    const original = schedules.find(s=>String(s.id)===id)
    if (a.type !== 'create' && (!original || seen.has(id))) throw new Error('AI 引用了不存在或重复的日程，请重新生成')
    if (a.type !== 'create') seen.add(id)
    return { type:a.type, id, original:original ? structuredClone(original) : null, schedule:a.type==='delete' ? null : normalizeSchedule(a.type==='update' ? {...original,...a.schedule} : a.schedule || {}), state:'pending' }
  })
  return { reply: String(result.reply || '请检查以下日程变更。').slice(0,4000), actions }
}
export function assertPlanFresh(actions, current) {
  for (const a of actions.filter(x=>x.state==='pending' && x.type!=='create')) {
    const existing = current.find(s=>String(s.id)===a.id)
    if (!existing || JSON.stringify(normalizeSchedule(existing, false)) !== JSON.stringify(normalizeSchedule(a.original, false))) throw new Error('课表已在其他页面或设备变更，请重新生成方案')
  }
}
