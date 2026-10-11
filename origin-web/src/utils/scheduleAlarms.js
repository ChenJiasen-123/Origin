import { Capacitor, registerPlugin } from '@capacitor/core'
import { preferences } from './preferences'
import { buildAlarmPlan } from './alarmPlan'
const plugin = registerPlugin('ScheduleAlarms')
export const isAndroid = Capacitor.getPlatform() === 'android'
export async function syncAlarms(schedules) {
  if (!isAndroid) return { supported: false, count: 0 }
  return plugin.sync({ items: buildAlarmPlan(schedules, preferences) })
}
export async function alarmStatus() {
  if (!isAndroid) return { supported: false, count: 0 }
  return plugin.status()
}
export async function requestAlarmAccess() {
  if (!isAndroid) return { supported: false }
  return plugin.requestAccess()
}
export async function clearAlarms() {
  if (isAndroid) await plugin.sync({ items: [] })
}
