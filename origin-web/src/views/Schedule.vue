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
              <div class="fixed-guide-label" :style="{ top: getPos(activeEvent.start) + 'px', backgroundColor: activeEvent.is_completed? '#666':activeEvent.color }">
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
      <div v-if="detailEvent" class="detail-modal-mask" @click="detailEvent = null">
        <div class="detail-modal-card" @click.stop>
          <div class="modal-line" :style="{ backgroundColor: detailEvent.color }"></div>
          <div class="modal-main">
          <div style="display: flex; justify-content: flex-end; margin-top: -10px;">
            <label style="cursor: pointer;">
              <input
                type="checkbox"
                v-model="detailEvent.is_completed"
                @change="updateScheduleStatus(detailEvent)"
                style="width: 24px; height: 24px; cursor: pointer; accent-color: #409EFF;"
              >
            </label>
          </div>
            <h3 class="modal-title" :style="detailEvent.is_completed ? 'color: #999; text-decoration: line-through;' : ''">
                      {{ detailEvent.title }}
                    </h3>
            <div class="modal-info-row">
              <span class="info-label">Time</span>
              <span class="info-value" :style="detailEvent.is_completed ? 'color: #999' : ''">
              {{ detailEvent.date }} {{ detailEvent.start }} - {{ detailEvent.end }}</span>
            </div>
            <div class="modal-info-row" v-if="detailEvent.location">
              <span class="info-label">Location</span>
              <span class="info-value" :style="detailEvent.is_completed ? 'color: #999' : ''">
              {{ detailEvent.location }}</span>
            </div>
            <div class="modal-info-row" v-if="detailEvent.who && detailEvent.who.length">
              <span class="info-label">Participants</span>
              <div class="modal-tags">
                <span v-for="name in detailEvent.who" :key="name" class="m-tag"
                  :style="{
                    color: detailEvent.is_completed ? '#aaa' : detailEvent.color,
                    backgroundColor: detailEvent.is_completed ? '#f0f0f0' : detailEvent.color + '20',
                    opacity: detailEvent.is_completed ? 0.7 : 1,
                  }">
                  {{ name }}
                </span>
              </div>
            </div>
            <div class="modal-info-row" style="margin-top: 15px;">
              <span class="info-label">Note</span>
              <div :style="{
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
const hours = Array.from({ length: 16 }, (_, i) => i + 8)
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
  const top = (sH - 8 + sM / 60) * rowHeight
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
  return ((h - 8) * 60) + m
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
    const res = await axios.get('http://192.168.124.8:8080/schedule')
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

const updateScheduleStatus = async (item) => {
  const originalStatus = !item.is_completed;

  try {
    // 修改点 1: 后端 Controller 使用的是 @PutMapping
    // 修改点 2: URL 必须带上 id，匹配后端 /schedule/{id}
    const res = await axios.put(`http://192.168.124.8:8080/schedule/${item.id}`, {
      // 构造符合 ScheduleDTO 结构的对象
      id: item.id,
      name: item.title,      // 注意：前端叫 title，后端 DTO 叫 name
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
  /* 禁止双击选中文字 */
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
  z-index: 9999;
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
.pop-enter-active, .pop-leave-active { transition: all 0.3s ease; }
.pop-enter-from, .pop-leave-to { opacity: 0; transform: scale(0.9); }
</style>