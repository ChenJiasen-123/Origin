<template>
  <div class="origin-schedule">
    <div class="week-pager">
      <button @click="offsetWeek(-7)" class="pager-btn"><<</button>
      <div class="current-week-info">{{ weekRangeText }}</div>
      <button @click="offsetWeek(7)" class="pager-btn">>></button>
    </div>

    <div class="schedule-scroll-viewport" ref="scrollContainer">
      <div class="schedule-full-width">
        <div class="timeline-header">
          <div class="time-gutter-sticky header-cell"></div>
          <div v-for="day in displayWeekDays" :key="day.dateStr" class="day-column-header" :class="{ 'is-today': day.isToday }">
            <span class="day-label">{{ day.weekName }}</span>
            <span class="date-label">{{ day.dateDisplay }}</span>
          </div>
        </div>

        <div class="schedule-body">
          <div class="time-gutter-sticky">
            <div v-for="hour in hours" :key="hour" class="hour-marker">{{ hour }}:00</div>

            <template v-if="activeEvent">
              <div class="fixed-guide-label" :style="{ top: getPos(activeEvent.start) +'px', backgroundColor: activeEvent.is_completed? '#666':activeEvent.color }">
                {{ activeEvent.start }}
              </div>
              <div class="fixed-guide-label" :style="{ top: getPos(activeEvent.end) + 'px', backgroundColor: activeEvent.is_completed? '#666':activeEvent.color }">
                {{ activeEvent.end }}
              </div>
            </template>
          </div>

          <div class="schedule-canvas" @click.self="activeEvent = null">
            <div class="canvas-grid">
              <div v-for="i in 7" :key="i" class="grid-col"></div>
            </div>

            <template v-if="activeEvent">
              <div class="guide-line" :style="{
                    top: getPos(activeEvent.start) + 'px',
                    borderColor: activeEvent.is_completed? '#ccc':activeEvent.color
                    }"></div>
              <div class="guide-line" :style="{
                    top: getPos(activeEvent.end) + 'px',
                    borderColor: activeEvent.is_completed? '#ccc':activeEvent.color
                    }"></div>
            </template>

            <div
              v-for="item in currentPageSchedules"
              :key="item.id"
              class="event-block"
              :class="{ 'is-active': activeEvent?.id === item.id }"
              :style="getEventStyle(item)"
              @click="handleItemClick(item)"
              @dblclick="detailEvent = item"
            >
              <div class="event-inner"
                :style="{
                  borderLeftColor: item.is_completed ? '#999' : item.color,
                  color: item.is_completed ? '#999' : item.color,
                  backgroundColor: item.is_completed ? '#f0f0f0' : item.color + '15',
                  opacity: 1
                }">
                <div class="event-title">{{ item.title }}</div>

                <div class="event-who-tags" v-if="item.who && item.who.length > 0">
                  <svg class="info-svg" viewBox="0 0 24 24" width="12" height="12" fill="none" stroke="currentColor" stroke-width="2.5">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                  </svg>
                  <div class="tags-wrapper">
                    <span v-for="(name, index) in item.who" :key="index" class="name-tag"
                      :style="{
                      backgroundColor: item.is_completed ? '#e0e0e0' : item.color + '25',
                      color: item.is_completed ? '#999': item.color,
                      }">
                      {{ name }}
                    </span>
                  </div>
                </div>

                <div class="event-location" v-if="item.location && item.location !== 'null'">
                  <svg class="info-svg" viewBox="0 0 24 24" width="12" height="12" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="3"></circle>
                    <path d="M19.4 15a9 9 0 1 0-14.8 2.5L12 22l7.4-4.5c.3-.2.4-.5.4-.8v-1.7z"></path>
                  </svg>
                  {{ item.location }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <button class="floating-today-btn" @click="goToToday" title="回到今天">
      <span>Now</span>
    </button>

<Transition name="pop">
  <div v-if="detailEvent" class="detail-modal-mask" @click="closeModal">
    <div class="detail-modal-card" @click.stop>
      <div class="modal-line" :style="{ backgroundColor: detailEvent.color }"></div>
      <div class="modal-main">
        <div style="display: flex; justify-content: flex-end; align-items: center; gap: 12px; margin-top: -10px; margin-bottom: 10px;">
          <div @click="toggleEdit" style="cursor: pointer; display: flex; align-items: center;" title="编辑">
            <svg v-if="!isEditing" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="#666" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            <span v-else style="color: #409EFF; font-weight: bold; font-size: 14px;">Done</span>
          </div>

          <label style="cursor: pointer; display: flex; align-items: center;">
            <input
              type="checkbox"
              v-model="detailEvent.is_completed"
              @change="updateScheduleStatus(detailEvent)"
              style="width: 24px; height: 24px; cursor: pointer; accent-color: #409EFF;"
            >
          </label>
        </div>

        <div class="modal-info-row">
          <input v-if="isEditing" v-model="detailEvent.title" class="edit-input title-input" />
          <h3 v-else class="modal-title" :style="detailEvent.is_completed ? 'color: #999; text-decoration: line-through;' : ''">
            {{ detailEvent.title }}
          </h3>
        </div>

<div class="modal-info-row">
  <div v-if="isEditing" class="edit-form-container" style="margin-top: 5px;">
    <el-form :model="detailEvent" label-position="top">

      <el-row>
        <el-col :span="24">
          <el-form-item label="Date">
            <el-date-picker
              v-model="detailEvent.date"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="Select Date"
              :clearable="false"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="10" style="margin-top: 5px;">
        <el-col :span="11">
          <el-form-item label="Start Time">
            <el-time-picker
              v-model="detailEvent.start"
              format="HH:mm"
              value-format="HH:mm"
              :clearable="false"
              placeholder="开始时间"
              style="width: 100%"
              @change="validateTime"
            />
          </el-form-item>
        </el-col>
        <el-col :span="2" style="display: flex; justify-content: center; align-items: flex-end; height: 50px; color: #999; padding-bottom: 10px;">
          <span>-</span>
        </el-col>
        <el-col :span="11">
          <el-form-item label="End Time">
            <el-time-picker
              v-model="detailEvent.end"
              format="HH:mm"
              value-format="HH:mm"
              :clearable="false"
              placeholder="结束时间"
              style="width: 100%"
              @change="validateTime"
            />
          </el-form-item>
        </el-col>
      </el-row>

    </el-form>
  </div>

  <div v-else class="display-schedule-box" :style="detailEvent.is_completed ? 'opacity: 0.6' : ''">
    <span class="info-label">Time</span>
    <div class="display-time" :style="{ marginTop: '4px', fontSize: '14px', color: '#444', fontWeight: '500' }">
      {{ detailEvent.start }} <span style="color: #ccc; margin: 0 4px;"> - </span> {{ detailEvent.end }}
    </div>
  </div>
</div>

        <div class="modal-info-row">
          <span class="info-label">Location</span>
          <input v-if="isEditing" v-model="detailEvent.location" class="edit-input" />
          <span v-else class="info-value" :style="detailEvent.is_completed ? 'color: #999' : ''">
            {{ detailEvent.location || 'None' }}
          </span>
        </div>

        <div class="modal-info-row" style="margin-top: 15px;">
          <span class="info-label">Note</span>
          <textarea v-if="isEditing" v-model="detailEvent.note" class="edit-textarea"></textarea>
          <div v-else :style="{
                 background: '#f8f8f8',
                 padding: '10px',
                 borderRadius: '8px',
                 fontSize: '13px',
                 marginTop: '4px',
                 color: detailEvent.is_completed ? '#bbb' : '#666',
                 transition: 'all 0.3s ease'
               }">
            {{ detailEvent.note || 'No notes added' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import axios from 'axios'

// --- 基础配置 ---
const hours = Array.from({ length: 24 }, (_, i) => i + 0)
const GUTTER_WIDTH = 38

// --- 响应式数据 ---
const activeEvent = ref(null)      // 用于辅助线展示
const detailEvent = ref(null)      // 用于双击弹窗展示
const scrollContainer = ref(null)
const schedules = ref([])
const startOfWeek = ref(getMonday(new Date()))

// --- 日期处理逻辑 ---
function getMonday(d) {
  const date = new Date(d)
  const day = date.getDay()
  const diff = date.getDate() - day + (day === 0 ? -6 : 1)
  return new Date(date.setDate(diff))
}

const displayWeekDays = computed(() => {
  const weekNames = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  const today = new Date()
  const formatDate = (date) => {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  }
  const todayLocalStr = formatDate(today)

  return weekNames.map((name, i) => {
    const d = new Date(startOfWeek.value)
    d.setDate(d.getDate() + i)
    const localDateStr = formatDate(d)
    return {
      weekName: name,
      dateDisplay: `${d.getMonth() + 1}/${d.getDate()}`,
      dateStr: localDateStr,
      isToday: localDateStr === todayLocalStr
    }
  })
})

const weekRangeText = computed(() => {
  const start = displayWeekDays.value[0].dateStr
  const end = displayWeekDays.value[6].dateStr
  return `${start.replace(/-/g, '.')} - ${end.split('-').slice(1).join('.')}`
})

const currentPageSchedules = computed(() => {
  const currentDays = displayWeekDays.value.map(d => d.dateStr)
  return schedules.value.filter(s => currentDays.includes(s.date))
})

// --- 样式辅助函数 ---
const getEventStyle = (item) => {
  const sDate = item.date ? item.date.replace(/\//g, '-') : ''
  const dayIndex = displayWeekDays.value.findIndex(d => d.dateStr === sDate)
  if (dayIndex === -1) return { display: 'none' }

  const getPureTime = (t) => t.includes(' ') ? t.split(' ')[1] : t
  const startTime = getPureTime(item.start)
  const endTime = getPureTime(item.end)

  const [sH, sM] = startTime.split(':').map(Number)
  const [eH, eM] = endTime.split(':').map(Number)

  const rowHeight = 60
  const top = (sH + sM / 60) * rowHeight + 10
  const duration = (eH + eM / 60) - (sH + sM / 60)
  const height = duration * rowHeight

  return {
    position: 'absolute',
    top: `${top}px`,
    height: `${height}px`,
    left: `${(100 / 7) * dayIndex}%`,
    width: `${100 / 7}%`,
    zIndex: activeEvent.value?.id === item.id ? 10 : 1,
  }
}

const getPos = (timeStr) => {
  if (!timeStr) return 0
  const time = timeStr.includes(' ') ? timeStr.split(' ')[1] : timeStr
  const [h, m] = time.split(':').map(Number)
  return ((h) * 60) + m + 10
}

// --- 交互事件 ---
const offsetWeek = (days) => {
  const newDate = new Date(startOfWeek.value)
  newDate.setDate(newDate.getDate() + days)
  startOfWeek.value = newDate
  activeEvent.value = null
}

const goToToday = () => {
  startOfWeek.value = getMonday(new Date())
  activeEvent.value = null
  nextTick(() => {
    if (scrollContainer.value) {
      if (window.innerWidth <= 768) {
        const todayIndex = displayWeekDays.value.findIndex(d => d.isToday)
        if (todayIndex !== -1) {
          const unitWidth = (window.innerWidth - GUTTER_WIDTH) / 3
          scrollContainer.value.scrollTo({ left: unitWidth * Math.min(todayIndex, 4), behavior: 'smooth' })
        }
      } else {
        scrollContainer.value.scrollTo({ left: 0, behavior: 'smooth' })
      }
    }
  })
}

const handleItemClick = (item) => {
  activeEvent.value = activeEvent.value?.id === item.id ? null : item
}

// --- API 请求 ---
const fetchSchedules = async () => {
  try {
    const res = await axios.get('http://192.168.124.9:8080/schedule')
    if (res.data?.success) {
      const remoteData = res.data.data || []
      schedules.value = remoteData.map((item, index) => {
        const rawStart = item.start_time
        const rawEnd = item.end_time
        if (!rawStart) return null
        const startParts = rawStart.split(/[\sT]/)
        const endParts = rawEnd ? rawEnd.split(/[\sT]/) : []
        return {
          ...item,
          id: item.id || `temp-${index}`,
          title: item.name,
          start: startParts[1] ? startParts[1].substring(0, 5) : "00:00",
          end: endParts[1] ? endParts[1].substring(0, 5) : "00:00",
          date: startParts[0],
          color: (item.color && item.color !== 'null') ? item.color : '#409EFF',
          is_completed: item.is_completed === true || item.is_completed === 'true',
          note: item.note || ""
        }
      }).filter(i => i !== null)
    }
  } catch (error) {
    console.error("Fetch error:", error)
  }
}

// 时间校验逻辑
const validateTime = () => {
  if (!detailEvent.value.start || !detailEvent.value.end) return

  // 将 "HH:mm" 转换为分钟总数进行比较
  const timeToMinutes = (t) => {
    const [h, m] = t.split(':').map(Number)
    return h * 60 + m
  }

  const startTotal = timeToMinutes(detailEvent.value.start)
  const endTotal = timeToMinutes(detailEvent.value.end)

  // 如果结束时间早于或等于开始时间
  if (endTotal <= startTotal) {
    console.warn('结束时间必须晚于开始时间')

    // 自动处理：将结束时间设为开始时间 + 1小时 (如果超过24点则设为23:59)
    let newEndMinutes = startTotal + 60
    if (newEndMinutes >= 1440) newEndMinutes = 1439

    const h = String(Math.floor(newEndMinutes / 60)).padStart(2, '0')
    const m = String(newEndMinutes % 60).padStart(2, '0')

    detailEvent.value.end = `${h}:${m}`

    // 可选：使用 Element Plus 的消息提示
    // ElMessage.warning('结束时间已自动调整为开始时间之后')
  }
}

const updateScheduleStatus = async (item) => {
  const originalStatus = !item.is_completed;

  try {
    const res = await axios.put(`http://192.168.124.9:8080/schedule/${item.id}`, {
      id: item.id,
      name: item.title,
      note: item.note,
      start_time: item.date + " " + item.start + ":00", // 拼回 yyyy-MM-dd HH:mm:ss
      end_time: item.date + " " + item.end + ":00",
      who: item.who,
      location: item.location,
      color: item.color,
      is_completed: item.is_completed // 关键布尔值
    });

    if (res.data?.success) {
      console.log('✅ 数据库同步成功');
    } else {
      throw new Error('后端响应失败');
    }
  } catch (error) {
    console.error("同步失败:", error);
    item.is_completed = originalStatus; // 回滚
    alert("无法保存状态，请检查网络或后端服务");
  }
}

const isEditing = ref(false)

// 切换编辑模式
const toggleEdit = () => {
  if (isEditing.value) {
    // 如果是从编辑模式退出，则保存
    updateScheduleStatus(detailEvent.value)
  }
  isEditing.value = !isEditing.value
}

// 修改关闭弹窗的方法，确保重置编辑状态
const closeModal = () => {
  if (isEditing.value) {
    updateScheduleStatus(detailEvent.value)
  }
  detailEvent.value = null
  isEditing.value = false
}

onMounted(() => {
  fetchSchedules()
  goToToday()
})
</script>

<style scoped>
/* 1. 基础容器与优化 */
.origin-schedule {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fff;
  overflow: hidden;
  touch-action: manipulation; /* 优化：消除移动端双击延迟 */
}

/* 2. 头部导航 */
.week-pager {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  z-index: 30;
}
.pager-btn {
  padding: 4px 12px;
  border: 1px solid #eee;
  background: #f9f9f9;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}
.current-week-info {
  font-weight: bold;
  font-size: 13px;
  color: #333;
}

/* 3. 滚动区域 */
.schedule-scroll-viewport {
  flex: 1;
  overflow-x: auto;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
.schedule-full-width {
  min-width: 100%;
  display: flex;
  flex-direction: column;
}
@media (max-width: 768px) {
  .schedule-full-width {
    width: calc(38px + (100vw - 38px) / 3 * 7);
  }
}

/* 4. 时间轴侧边栏 */
.time-gutter-sticky {
  width: 38px;
  flex-shrink: 0;
  position: sticky;
  left: 0;
  background: #fff;
  z-index: 20;
  border-right: 1px solid #f5f5f5;
}
.hour-marker {
  height: 60px;
  font-size: 10px;
  color: #bbb;
  text-align: center;
  line-height: 20px;
}

/* 5. 顶部周信息 */
.timeline-header {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  padding: 6px 0;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 25;
}
.day-column-header {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.day-column-header.is-today .day-label,
.day-column-header.is-today .date-label {
  color: #409EFF;
  font-weight: bold;
}
.day-label { font-size: 13px; font-weight: bold; }
.date-label { font-size: 10px; color: #999; margin-top: 2px; }

/* 6. 画布与网格 */
.schedule-body { display: flex; flex: 1; position: relative; }
.schedule-canvas { flex: 1; position: relative; background: #fafafa; min-height: 960px; }
.canvas-grid { position: absolute; inset: 0; display: flex; }
.grid-col { flex: 1; border-right: 1px solid #f2f2f2; }
.grid-col:nth-child(even) { background-color: #f9f9f9; }

/* 7. 日程块样式 */
.event-block {
  position: absolute;
  box-sizing: border-box;
  cursor: pointer;
  padding: 1px;
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  user-select: none;
}
.event-block.is-active { z-index: 10; }
.event-inner {
  height: 100%;
  width: 100%;
  border-left: 3px solid;
  border-radius: 4px;
  padding: 4px 2px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  overflow: hidden;
  box-sizing: border-box;
}
.event-title {
  font-weight: bold;
  line-height: 1.3;
  margin: 8px 2px 12px 8px;
  font-size: 15px;
  word-break: break-all;
}

/* 参与人与地点详情 */
.event-who-tags { display: flex; align-items: flex-start; gap: 4px; margin: 4px 0 4px 6px; }
.tags-wrapper { display: flex; flex-wrap: wrap; gap: 3px; }
.name-tag {
  font-size: 9px;
  padding: 1px 5px;
  border-radius: 10px;
  font-weight: 600;
  opacity: 0.9;
}
.event-location {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 6px;
  opacity: 0.9;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.info-svg {
  flex-shrink: 0;
  width: 12px;
  height: 12px;
  opacity: 0.9;
  vertical-align: middle;
}

/* 8. 辅助线与标签 */
.fixed-guide-label {
  position: absolute; left: 2px; right: 2px; transform: translateY(-50%);
  color: #fff; font-size: 9px; text-align: center; padding: 1px 0;
  border-radius: 2px; z-index: 21; font-weight: bold;
}
.guide-line {
  position: absolute; left: 0; right: 0; height: 0; border-top: 1px dashed;
  z-index: 5; pointer-events: none;
}

/* 9. 悬浮按钮 */
.floating-today-btn {
  position: fixed;
  right: 20px;
  bottom: 30px;
  width: 50px;
  height: 50px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(64,158,255,0.4);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

/* 10. 弹窗样式 */
.detail-modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.detail-modal-card {
  background: #fff;
  width: 90%;
  max-width: 320px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  overflow: hidden;
  animation: modalShow 0.2s ease-out;
  user-select: none;
}
.modal-line { height: 6px; width: 100%; }
.modal-main { padding: 24px; }
.modal-title { margin: 0 0 16px 0; font-size: 18px; color: #333; font-weight: 600; }
.modal-info-row { margin-bottom: 12px; display: flex; flex-direction: column; gap: 2px; }
.info-label { font-size: 11px; color: #999; text-transform: uppercase; }
.info-value { font-size: 14px; color: #444; font-weight: 500; }
.modal-tags { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 4px; }
.m-tag { padding: 3px 10px; border-radius: 8px; font-size: 12px; font-weight: bold; }
.modal-close-btn {
  width: 100%;
  margin-top: 20px;
  padding: 12px;
  border: none;
  background: #f5f5f5;
  color: #666;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
}

/* 动画 */
@keyframes modalShow {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.edit-input {
  width: 100%;
  padding: 6px 8px;
  border: 1px solid #eee;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  background: #fafafa;
}
.edit-input:focus {
  border-color: #409EFF;
  background: #fff;
}
.title-input {
  font-size: 18px;
  font-weight: bold;
}
.edit-input-sm {
  border: 1px solid #eee;
  padding: 4px;
  border-radius: 4px;
  font-size: 13px;
}
.edit-textarea {
  width: 100%;
  min-height: 80px;
  padding: 10px;
  border: 1px solid #dcdfe6; /* 匹配 Element 的默认边框色 */
  border-radius: 8px;
  font-size: 13px;
  margin-top: 4px;
  resize: vertical; /* 允许垂直拉伸 */
  background: #fafafa;
  transition: all 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  outline: none; /* 禁用原生蓝色轮廓 */
  box-sizing: border-box;
}

/* 关键修改：选中（焦点）时的样式 */
.edit-textarea:focus {
  background: #fff;
  border-color: #409EFF; /* Primary 蓝色 */
  box-shadow: 0 0 0 1px #409EFF; /* 模拟 Element 输入框的焦点阴影感 */
}

/* 鼠标悬浮时的微调 */
.edit-textarea:hover {
  border-color: #c0c4cc;
}
/* 深度定制 Element Plus 样式以匹配蓝色主调 */
:deep(.el-form-item__label) {
  font-size: 13px !important;
  color: #606266 !important; /* 标准灰色 */
  font-weight: bold !important;
  margin-bottom: 4px !important;
  padding: 0 !important;
}

/* 蓝色主色调覆盖 */
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409EFF inset !important; /* 蓝色焦点边框 */
}

:deep(.el-form-item) {
  margin-bottom: 8px !important; /* 紧凑间距 */
}

/* 让日期选择器图标也是蓝色 */
:deep(.el-input__icon) {
  color: #409EFF;
}

/* 展示模式样式 */
.display-time {
  font-variant-numeric: tabular-nums; /* 确保时间数字等宽 */
  display: flex;
  align-items: center;
}

:deep(.el-date-editor.el-input__wrapper) {
  width: 100% !important;
  box-sizing: border-box;
}
.pop-enter-active, .pop-leave-active { transition: all 0.3s ease; }
.pop-enter-from, .pop-leave-to { opacity: 0; transform: scale(0.9); }
</style>