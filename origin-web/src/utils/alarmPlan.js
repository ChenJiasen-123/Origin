export function localTimestamp(value) {
  if (typeof value !== 'string' || !/^\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}(:\d{2})?$/.test(value)) return NaN
  const normalized = value.replace(' ', 'T')
  const time = new Date(normalized).getTime()
  if (!Number.isFinite(time)) return NaN
  const d = new Date(time), pad = n => String(n).padStart(2, '0')
  const roundTrip = `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
  return roundTrip === normalized.slice(0,16) ? time : NaN
}
export function buildAlarmPlan(schedules, settings, now = Date.now()) {
  if (!settings.alarmEnabled) return []
  return schedules.filter(s => !!s.reminder_time && s.is_completed !== true && s.is_completed !== 'true').map(s => ({
    id: String(s.id), title: s.name || '日程提醒', location: s.location || '',
    at: localTimestamp(s.reminder_time)
  })).filter(s => s.id !== 'undefined' && Number.isFinite(s.at) && s.at > now).sort((a,b) => a.at-b.at)
}
