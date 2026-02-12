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
              <div class="fixed-guide-label" :style="{ top: getPos(activeEvent.start) + 'px', backgroundColor: activeEvent.color }">
                {{ activeEvent.start }}
              </div>
              <div class="fixed-guide-label" :style="{ top: getPos(activeEvent.end) + 'px', backgroundColor: activeEvent.color }">
                {{ activeEvent.end }}
              </div>
            </template>
          </div>

          <div class="schedule-canvas" @click.self="activeEvent = null">
            <div class="canvas-grid">
              <div v-for="i in 7" :key="i" class="grid-col">
                <div v-for="hour in hours" :key="hour" class="grid-cell"></div>
              </div>
            </div>

            <template v-if="activeEvent">
              <div class="guide-line" :style="{ top: getPos(activeEvent.start) + 'px', borderColor: activeEvent.color }"></div>
              <div class="guide-line" :style="{ top: getPos(activeEvent.end) + 'px', borderColor: activeEvent.color }"></div>
            </template>

            <div
              v-for="item in currentPageSchedules"
              :key="item.id"
              class="event-block"
              :class="{ 'is-active': activeEvent?.id === item.id }"
              :style="getEventStyle(item)"
              @click="handleItemClick(item)"
            >
              <div class="event-inner" :style="{ borderLeftColor: item.color, backgroundColor: item.color + '15' }">
                <div class="event-title">{{ item.title }}</div>
                <div class="event-location" v-if="item.location">{{ item.location }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <button class="floating-today-btn" @click="goToToday" title="回到今天">
      <span>Now</span>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'

const hours = Array.from({ length: 15 }, (_, i) => i + 8)
const activeEvent = ref(null)
const scrollContainer = ref(null)
const GUTTER_WIDTH = 38

const startOfWeek = ref(getMonday(new Date()))

function getMonday(d) {
  const date = new Date(d)
  const day = date.getDay()
  const diff = date.getDate() - day + (day === 0 ? -6 : 1)
  return new Date(date.setDate(diff))
}

const displayWeekDays = computed(() => {
  const days = []
  const weekNames = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  const todayStr = new Date().toDateString()
  for (let i = 0; i < 7; i++) {
    const d = new Date(startOfWeek.value)
    d.setDate(d.getDate() + i)
    days.push({
      weekName: weekNames[i],
      dateDisplay: `${d.getMonth() + 1}/${d.getDate()}`,
      dateStr: d.toISOString().split('T')[0],
      isToday: d.toDateString() === todayStr
    })
  }
  return days
})

const weekRangeText = computed(() => {
  const start = displayWeekDays.value[0].dateStr
  const end = displayWeekDays.value[6].dateStr
  return `${start.replace(/-/g, '.')} - ${end.split('-').slice(1).join('.')}`
})

const schedules = ref([
  { id: 1, date: '2026-02-12', start: '08:30', end: '10:15', title: '高等数学', location: '教A302', color: '#409EFF' },
  { id: 2, date: '2026-02-13', start: '14:00', end: '15:30', title: '羽毛球', location: '体育馆', color: '#67C23A' }
])

const currentPageSchedules = computed(() => {
  const currentDays = displayWeekDays.value.map(d => d.dateStr)
  return schedules.value.filter(s => currentDays.includes(s.date))
})

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
          scrollContainer.value.scrollTo({
            left: unitWidth * Math.min(todayIndex, 4),
            behavior: 'smooth'
          })
        }
      } else {
        scrollContainer.value.scrollTo({ left: 0, behavior: 'smooth' })
      }
    }
  })
}

const getEventStyle = (item) => {
  const dateObj = new Date(item.date)
  const day = dateObj.getDay()
  const dayIndex = (day === 0 ? 6 : day - 1)
  const top = getPos(item.start)
  const duration = getPos(item.end) - top
  return {
    left: `calc((100% / 7) * ${dayIndex})`,
    width: `calc(100% / 7)`,
    top: `${top}px`,
    height: `${duration}px`,
    padding: '0 2px',
    color: item.color
  }
}

const getPos = (timeStr) => {
  const [h, m] = timeStr.split(':').map(Number)
  return ((h - 8) * 60) + m
}

const handleItemClick = (item) => {
  activeEvent.value = activeEvent.value?.id === item.id ? null : item
}

onMounted(() => {
  goToToday()
})
</script>

<style scoped>
.origin-schedule { position: relative; display: flex; flex-direction: column; height: 100vh; background: #fff; overflow: hidden; }

/* 悬浮按钮样式 */
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
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  transition: transform 0.2s, background-color 0.2s;
  outline: none;
  -webkit-tap-highlight-color: transparent;
}

.floating-today-btn:active {
  transform: scale(0.9);
  background-color: #337ecc;
}

.floating-today-btn span { font-size: 18px; font-weight: bold; }

@media (max-width: 768px) {
  .floating-today-btn {
    right: 16px;
    bottom: calc(20px + env(safe-area-inset-bottom));
    width: 46px;
    height: 46px;
  }
}

/* 翻页栏样式 */
.week-pager {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; background: #fff; border-bottom: 1px solid #f0f0f0;
}
.pager-btn {
  padding: 4px 12px; border: 1px solid #eee; background: #f9f9f9; border-radius: 4px; font-size: 12px; cursor: pointer;
}
.current-week-info { font-weight: bold; font-size: 13px; color: #333; }

.schedule-scroll-viewport { flex: 1; overflow-x: auto; overflow-y: auto; -webkit-overflow-scrolling: touch; }
.schedule-full-width { min-width: 100%; display: flex; flex-direction: column; }

@media (max-width: 768px) {
  .schedule-full-width { width: calc(38px + (100vw - 38px) / 3 * 7); }
}

.time-gutter-sticky {
  width: 38px; flex-shrink: 0; position: sticky; left: 0; background: #fff; z-index: 20; border-right: 1px solid #f5f5f5;
}

.timeline-header { display: flex; border-bottom: 1px solid #f0f0f0; padding: 6px 0; background: #fff; }
.day-column-header { flex: 1; display: flex; flex-direction: column; align-items: center; }
.day-column-header.is-today .day-label,
.day-column-header.is-today .date-label { color: #409EFF; font-weight: bold; }

.day-label { font-size: 13px; font-weight: bold; }
.date-label { font-size: 10px; color: #999; margin-top: 2px; }

.schedule-body { display: flex; flex: 1; }
.hour-marker {
  height: 60px; font-size: 10px; color: #bbb;
  text-align: center; line-height: 20px;
}

.schedule-canvas { flex: 1; position: relative; background: #fafafa; }
.canvas-grid { position: absolute; inset: 0; display: flex; }
.grid-col { flex: 1; border-right: 1px solid #f2f2f2; }
.grid-col:nth-child(even) { background-color: #f9f9f9; }

.fixed-guide-label {
  position: absolute; left: 2px; right: 2px; transform: translateY(-50%);
  color: #fff; font-size: 9px; text-align: center; padding: 1px 0;
  border-radius: 2px; z-index: 21; font-weight: bold;
}

.guide-line {
  position: absolute; left: 0; right: 0; height: 0; border-top: 1px dashed;
  z-index: 5; pointer-events: none; transform: translateY(0.5px);
}

.event-block { position: absolute; box-sizing: border-box; cursor: pointer; padding: 0 1px; }
.event-block.is-active { z-index: 25; }
.event-inner {
  height: 100%; width: 100%; border-left: 2px solid; border-radius: 3px;
  padding: 4px 2px; font-size: 11px; box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  overflow: hidden; box-sizing: border-box;
}
.event-title { font-weight: bold; line-height: 1.1; margin-bottom: 2px; }
.event-location { font-size: 9px; opacity: 0.8; white-space: nowrap; }
</style>