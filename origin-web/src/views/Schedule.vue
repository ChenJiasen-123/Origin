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
              :style="getEventStyle(item)"  @click="handleItemClick(item)"
              @dblclick="handleItemDblClick(item)"
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

    <div class="floating-action-group">
      <button class="floating-btn add-btn" @click="createNewSchedule" title="添加日程">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
      </button>

      <button class="floating-btn today-btn" @click="goToToday" title="回到今天">
        <span>Now</span>
      </button>
    </div>

  <!-- 左侧抽屉菜单已移除 -->

<Transition name="pop">
  <div v-if="detailEvent" class="detail-modal-mask" @click="closeModal">
    <div class="detail-modal-card" @click.stop>
      <div class="modal-line" :style="{ backgroundColor: detailEvent.color }"></div>

      <div class="modal-main">
        <div style="display: flex; justify-content: flex-end; align-items: center; gap: 12px; margin-top: -10px; margin-bottom: 5px;">

          <div @click="toggleEdit" style="cursor: pointer; display: flex; align-items: center;" title="编辑/保存">
            <svg v-if="!isEditing" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="#666" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            <span v-else style="color: #409EFF; font-weight: bold; font-size: 14px;">Done</span>
          </div>

          <div v-if="isEditing" @click="handleDelete(detailEvent)" style="cursor: pointer; display: flex; align-items: center;" title="删除日程">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="#F56C6C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="3 6 5 6 21 6"></polyline>
              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
              <line x1="10" y1="11" x2="10" y2="17"></line>
              <line x1="14" y1="11" x2="14" y2="17"></line>
            </svg>
          </div>

          <label v-else style="cursor: pointer; display: flex; align-items: center;">
            <input
              type="checkbox"
              v-model="detailEvent.is_completed"
              @change="saveSchedule(detailEvent)"
              style="width: 24px; height: 24px; cursor: pointer; accent-color: #409EFF;"
            >
          </label>
        </div>

        <div class="modal-info-row">
          <input v-if="isEditing" v-model="detailEvent.title" class="edit-input title-input" placeholder="Event Title" ref="titleInputRef" />
          <h3 v-else class="modal-title" :style="detailEvent.is_completed ? 'color: #999; text-decoration: line-through;' : ''">
            {{ detailEvent.title || 'Untitled Event' }}
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
                      :clearable="false"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="11">
                  <el-form-item label="Start">
                    <el-time-picker
                      v-model="detailEvent.start"
                      format="HH:mm"
                      value-format="HH:mm"
                      :clearable="false"
                      style="width: 100%"
                      @change="validateTime"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="2" style="display: flex; justify-content: center; align-items: flex-end; height: 50px; color: #999; padding-bottom: 10px;">
                  <span>-</span>
                </el-col>
                <el-col :span="11">
                  <el-form-item label="End">
                    <el-time-picker
                      v-model="detailEvent.end"
                      format="HH:mm"
                      value-format="HH:mm"
                      :clearable="false"
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
            <div class="display-time" style="margin-top: 4px; font-size: 14px; color: #444; font-weight: 500;">
              {{ detailEvent.start }} <span style="color: #ccc; margin: 0 4px;"> - </span> {{ detailEvent.end }}
            </div>
          </div>
        </div>

        <div class="modal-info-row">
          <span class="info-label">Location</span>
          <input v-if="isEditing" v-model="detailEvent.location" class="edit-input" placeholder="Add location" />
          <span v-else class="info-value" :style="detailEvent.is_completed ? 'color: #999' : ''">
            {{ detailEvent.location || 'No location' }}
          </span>
        </div>

        <div class="modal-info-row">
          <span class="info-label">Who</span>
          <div v-if="isEditing" class="edit-tags-container">
            <div class="modal-tags">
              <el-tag
                v-for="(name, index) in detailEvent.who"
                :key="index"
                closable
                @close="removeWho(index)"
                class="custom-blue-tag"
              >
                {{ name }}
              </el-tag>
              <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                class="new-tag-input"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
              />
              <el-button v-else class="button-new-tag" size="small" @click="showInput">+ Tag</el-button>
            </div>
          </div>
          <div v-else class="modal-tags">
            <span v-for="(name, index) in detailEvent.who" :key="index"
              class="name-tag"
              :style="{
                backgroundColor: detailEvent.is_completed ? '#e0e0e0' : detailEvent.color + '25',
                color: detailEvent.is_completed ? '#999': detailEvent.color,
                fontSize: '12px', padding: '3px 10px'
              }">
              {{ name }}
            </span>
            <span v-if="!detailEvent.who || detailEvent.who.length === 0" class="info-value" style="color:#ccc">No one</span>
          </div>
        </div>

        <div class="modal-info-row" v-if="isEditing">
          <span class="info-label">Card Color</span>
          <div style="position: relative; margin-top: 8px;">
            <div
              @click="showColorPicker = !showColorPicker"
              class="current-color-preview square-preview"
              :style="{ backgroundColor: detailEvent.color }"
            >
              <span :style="{ color: getContrastColor(detailEvent.color) }">Change Color</span>
            </div>

            <Transition name="fade">
              <div v-if="showColorPicker" class="color-picker-popover">
                <div class="color-grid">
                  <div
                    v-for="colorCode in colorPool"
                    :key="colorCode"
                    class="color-block"
                    :style="{ backgroundColor: colorCode }"
                    @click="selectColor(colorCode)"
                  >
                    <div v-if="detailEvent.color === colorCode" class="check-white"></div>
                  </div>
                </div>
                <div class="popover-arrow"></div>
              </div>
            </Transition>
          </div>
        </div>

         <div class="modal-info-row">
                  <span class="info-label">Reminder</span>
                  <div v-if="isEditing">
                    <el-select v-model="detailEvent.reminder_offset" placeholder="Select" style="width: 100%" class="blue-select">
                      <el-option label="No reminder" :value="null" />
                      <el-option label="At time of event" :value="0" />
                      <el-option label="5 minutes before" :value="5" />
                      <el-option label="15 minutes before" :value="15" />
                      <el-option label="1 hour before" :value="60" />
                      <el-option label="1 day before" :value="1440" />
                    </el-select>
                  </div>
                  <span v-else class="info-value">
                    {{ detailEvent.reminder_time ? detailEvent.reminder_time.replace('T', ' ').substring(0, 16) : 'No reminder' }}
                  </span>
                </div>

        <div class="modal-info-row" style="margin-top: 15px;">
          <span class="info-label">Note</span>
          <textarea v-if="isEditing" v-model="detailEvent.note" class="edit-textarea" placeholder="Add notes..."></textarea>
          <div v-else :style="{
                 background: '#f8f8f8',
                 padding: '10px',
                 borderRadius: '8px',
                 fontSize: '13px',
                 marginTop: '4px',
                 color: detailEvent.is_completed ? '#bbb' : '#666'
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
import request from '../utils/request'
import { LocalNotifications } from '@capacitor/local-notifications';
// 已移除左侧抽屉相关路由跳转

// --- 基础配置 ---
const hours = Array.from({ length: 24 }, (_, i) => i + 0)
const GUTTER_WIDTH = 38

// --- 响应式数据 ---
const activeEvent = ref(null)      // 用于辅助线展示
const detailEvent = ref(null)      // 用于双击弹窗展示
const scrollContainer = ref(null)
const schedules = ref([])
const startOfWeek = ref(getMonday(new Date()))
// 已移除左侧抽屉状态

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
// --- 处理点击日程块（显示辅助线） ---
const handleItemClick = (item) => {
  // 如果点击的是当前已选中的，则取消选中（再次点击消失）
  if (activeEvent.value?.id === item.id) {
    activeEvent.value = null;
  } else {
    activeEvent.value = item;
  }
};

const handleItemDblClick = (item) => {
  openDetail(item, false); // 查看模式
};

// --- API 请求 ---
const fetchSchedules = async () => {
  try {
const res = await request.get('/schedule')
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

const saveSchedule = async (item) => {
  if (!item) return false;

  let finalReminderTime = null;
  // 计算提醒时间
  if (item.reminder_offset !== null && item.reminder_offset !== undefined) {
    try {
      const startDt = new Date(`${item.date}T${item.start}:00`);
      const reminderDt = new Date(startDt.getTime() - item.reminder_offset * 60000);
      const pad = (n) => String(n).padStart(2, '0');
      finalReminderTime = `${reminderDt.getFullYear()}-${pad(reminderDt.getMonth() + 1)}-${pad(reminderDt.getDate())} ${pad(reminderDt.getHours())}:${pad(reminderDt.getMinutes())}:00`;
    } catch (e) {
      console.error("Reminder error:", e);
    }
  }

  try {
    const isNew = item.id === null;
    const url = isNew ? '/schedule' : `/schedule/${item.id}`;
    const method = isNew ? 'post' : 'put';

    const res = await request[method](url, {
      ...item,
      name: item.title,
      start_time: `${item.date} ${item.start}:00`,
      end_time: `${item.date} ${item.end}:00`,
      reminder_time: finalReminderTime
    });

    if (res.data?.success) {
      await fetchSchedules(); // 重新拉取主列表，实现界面刷新
      console.log('✅ 保存成功');
      return true;
    }
    return false;
  } catch (error) {
    alert("保存失败，请检查网络");
    return false;
  }
};

 const isEditing = ref(false)

const openDetail = (item, editMode = false) => {
  // 使用 JSON 序列化实现深拷贝，确保编辑时背景不跟着变
  detailEvent.value = JSON.parse(JSON.stringify(item));
  isEditing.value = editMode;
};

// --- 创建新日程逻辑 ---
const createNewSchedule = () => {
  const now = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  const currentDate = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;
  const startTime = `${pad(now.getHours())}:00`;
  const endTime = `${pad(Math.min(now.getHours() + 1, 23))}:00`;

  const newItem = {
    id: null, // 标记为新日程
    title: '',
    note: '',
    date: currentDate,
    start: startTime,
    end: endTime,
    who: [],
    location: '',
    color: '#409EFF',
    is_completed: false,
    reminder_offset: null,
    reminder_time: null
  };

  openDetail(newItem, true); // 直接进入编辑模式
};

const toggleEdit = async () => {
  if (isEditing.value) {
    // 处于编辑状态点击 Done -> 执行保存
    const success = await saveSchedule(detailEvent.value);
    if (success) {
      isEditing.value = false;
      detailEvent.value = null; // 只有保存成功才关闭
    }
  } else {
    // 处于查看状态点击编辑图标 -> 进入编辑
    isEditing.value = true;
    nextTick(() => {
      // 聚焦并滚动到可视区域（解决移动端键盘遮挡）
      titleInputRef.value?.focus();
      titleInputRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    });
  }
};

// --- 删除日程逻辑 ---
const handleDelete = async (item) => {
  // 1. 如果是新建还没保存的，直接关闭即可
  if (!item.id || String(item.id).startsWith('temp-')) {
    detailEvent.value = null;
    isEditing.value = false;
    return;
  }

  // 2. 二次确认防止误点
  if (!confirm('Are you sure you want to delete this schedule?')) {
    return;
  }

  try {
const res = await request.delete(`/schedule/${item.id}`);

    if (res.data?.success) {
      // 3. 删除成功后：刷新主列表并关闭弹窗
      await fetchSchedules();
      detailEvent.value = null;
      isEditing.value = false;
      activeEvent.value = null;
      console.log('🗑️ 日程已删除');
    } else {
      alert('删除失败：' + (res.data?.message || '未知错误'));
    }
  } catch (error) {
    console.error("Delete error:", error);
    alert('删除请求失败，请检查网络连接');
  }
};

const handleStatusChange = async (item) => {
  await saveSchedule(item);
};

// 7. 修改：关闭弹窗 (点击空白处)
const closeModal = () => {
  detailEvent.value = null;
  isEditing.value = false;
  showColorPicker.value = false; // 确保弹窗关闭时 Picker 也隐藏
};

const colorPool = ref([]);

const fetchColorPool = async () => {
  try {
// 路径直接写接口名，baseURL 会自动补全为 /origin/api/colorList
  const res = await request.get('/api/colorList');
  colorPool.value = res.data;
  } catch (err) {
    console.error("Fetch colors failed:", err);
    // 如果后端没好，可以先用一组默认值兜底
    colorPool.value = ['#FF0000', '#409EFF', '#67C23A', '#E6A23C', '#F56C6C'];
  }
};

onMounted(() => {
  fetchSchedules();
  fetchColorPool(); // 加载颜色
  goToToday();
});

// --- 标签管理 ---
const inputVisible = ref(false)
const inputValue = ref('')
const InputRef = ref(null)

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value?.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    if (!detailEvent.value.who) detailEvent.value.who = []
    detailEvent.value.who.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

const removeWho = (index) => {
  detailEvent.value.who.splice(index, 1)
}

// --- 提醒文本转换 ---
const getReminderLabel = (val) => {
  const map = {
    'none': 'No reminder',
    '0': 'At time of event',
    '5': '5 minutes before',
    '15': '15 minutes before',
    '60': '1 hour before',
    '1440': '1 day before'
  }
  return map[val] || 'No reminder'
}

// --- 颜色选择器逻辑 ---
const showColorPicker = ref(false);
const titleInputRef = ref(null);

// 已移除左侧抽屉路由与登出按钮

// 选择颜色并关闭弹窗
const selectColor = (code) => {
  detailEvent.value.color = code;
  showColorPicker.value = false;
};

// 辅助函数：根据背景色深浅返回黑色或白色的文字颜色（用于按钮文字可读性）
const getContrastColor = (hexcolor) => {
  if (!hexcolor) return '#fff';
  const r = parseInt(hexcolor.substring(1, 3), 16);
  const g = parseInt(hexcolor.substring(3, 5), 16);
  const b = parseInt(hexcolor.substring(5, 7), 16);
  const yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
  return (yiq >= 128) ? '#333' : '#fff';
};

</script>

<style scoped>
/* 1. 基础容器与优化 */
.origin-schedule {
position: fixed; /* 使用 fixed 代替 relative，彻底锁定在视口 */
  top: 60px;
  bottom:0px;
  left: 0;
  right: 0;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
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

scroll-snap-align: start;
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
/* 浮动按钮组容器 */
.floating-action-group {
  position: fixed; /* 核心：固定定位 */
  right: 24px;     /* 距离右侧距离 */
  bottom: 30px;    /* 距离底部距离 */
  display: flex;
  flex-direction: column; /* 垂直排列 */
  gap: 16px;       /* 按钮之间的间距 */
  z-index: 9999;   /* 确保在所有元素之上 */
  pointer-events: none; /* 防止容器遮挡下方点击，内部按钮需设为 auto */
}

/* 统一按钮基础样式 */
.floating-btn {
  pointer-events: auto; /* 恢复按钮可点击 */
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 加号按钮 - 蓝色主色 */
.add-btn {
  background-color: #409EFF;
  color: white;
}

/* Now 按钮 - 白色简约 */
.today-btn {
  background-color: #ffffff;
  color: #409EFF;
  font-weight: bold;
  font-size: 13px;
  border: 1px solid #e8e8e8;
}

/* 交互反馈 */
.floating-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3);
}

.floating-btn:active {
  transform: scale(0.92);
}

.schedule-scroll-viewport {
  flex: 1;
  overflow-x: auto;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;

  /* --- 核心添加：滚动吸附 --- */
/* 明确：只在 X 轴强制吸附 */
  scroll-snap-type: x mandatory;
  scroll-padding-left: 38px;

  /* 允许斜向滑动，不锁定轴向，保证纵向灵敏度 */
  touch-action: auto;
}

.grid-col {
  flex: 1;
  border-right: 1px solid #f2f2f2;

  /* 核心添加：让背景列在横向成为对齐点 */
  scroll-snap-align: start;
}

.schedule-full-width {
  /* 这里的 3 是指同屏显示 3 天，公式：(容器宽 - 侧边栏宽) / 3天 * 总共7天 + 侧边栏宽 */
  width: calc((100vw - 38px) / 3 * 7 + 38px);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}
/*适配电脑端*/
@media screen and (min-width: 1024px) {
  .schedule-full-width {
    width: 100%; /* 电脑端直接铺满，同屏显示 7 天 */
    min-width: 100%;
  }

  .schedule-scroll-viewport {

    scroll-snap-type: none; /* 电脑端不需要强行吸附 */
    overflow-x: hidden;    /* 电脑端通常不需要横向滚动 */
  }
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
  overscroll-behavior: contain; /* 阻止背景页面跟随滑动 */
}
.detail-modal-card {
  background: #fff;
  width: 90%;
  max-width: 320px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  /* 允许内部弹出层溢出（避免色盘被裁切） */
  overflow: visible;
  animation: modalShow 0.2s ease-out;
  user-select: none;
  touch-action: pan-y; /* 仅允许垂直滚动，避免左右滑动 */
}
.modal-line { height: 6px; width: 100%; }
.modal-main {
  padding: 24px;
  /* 移动端滚动容器，避免键盘遮挡编辑内容 */
  max-height: 85vh;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
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

/* 自定义标签颜色 */
.custom-blue-tag {
  background-color: #ecf5ff !important;
  border-color: #d9ecff !important;
  color: #409EFF !important;
  margin-right: 6px;
  margin-bottom: 6px;
}

:deep(.custom-blue-tag .el-tag__close) {
  color: #409EFF !important;
}

:deep(.custom-blue-tag .el-tag__close:hover) {
  background-color: #409EFF !important;
  color: #fff !important;
}

/* 添加按钮样式 */
.button-new-tag {
  margin-bottom: 6px;
  height: 24px;
  padding-top: 0;
  padding-bottom: 0;
  border-style: dashed;
}

.new-tag-input {
  width: 80px;
  margin-bottom: 6px;
  vertical-align: bottom;
}

/* 强制 Select 选框焦点颜色 */
.blue-select :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px #409EFF inset !important;
}

/* 统一编辑态下的 input/textarea 边框 */
.edit-input:focus,
.edit-textarea:focus,
.new-tag-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409EFF !important;
  box-shadow: 0 0 0 1px #409EFF !important;
  outline: none;
}

/* 颜色预览按钮（方形） */
.square-preview {
  height: 38px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 13px;
  font-weight: bold;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

/* 弹出层容器 */
.color-picker-popover {
  position: absolute;
  top: 48px;
  /* 居中显示，避免超出右侧裁切 */
  left: 0;
  right: 0;
  margin: 0 auto;
  z-index: 1000;
  background: white;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  border: 1px solid #eee;
  width: min(90vw, 320px); /* 移动端自适应宽度 */
  max-height: 50vh;        /* 允许色盘垂直滚动 */
  overflow-y: auto;
}

/* 弹出层小箭头 */
.popover-arrow {
  position: absolute;
  top: -6px;
  left: calc(50% - 6px);
  width: 12px;
  height: 12px;
  background: white;
  transform: rotate(45deg);
  border-left: 1px solid #eee;
  border-top: 1px solid #eee;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

/* 核心修改：方形色块 */
.color-block {
  aspect-ratio: 1 / 1;
  width: 100%;
  border-radius: 2px; /* 方形效果的关键 */
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.1s;
}

.color-block:hover {
  transform: scale(1.1);
}

/* 白色对勾 */
.check-white {
  width: 5px;
  height: 10px;
  border: 2px solid #fff;
  border-top: 0;
  border-left: 0;
  transform: rotate(45deg);
  margin-top: -2px;
}

/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
