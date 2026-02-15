<template>
  <div style="padding: 20px;">
    <h1 style="color: #409EFF;">Friends</h1>

    <div style="margin-top: 20px; max-width: 600px; display: flex; align-items: center; gap: 10px;">
      <el-input
        v-model="keyword"
        placeholder="Keyword search"
        clearable
        @input="onInputChange"
        style="flex: 1;"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>

      <div style="display: flex; gap: 8px; flex-shrink: 0;">
        <el-tooltip content="Add Friend"><el-button :icon="Plus" circle @click="router.push('/friend/add')" /></el-tooltip>
        <el-tooltip content="Edit List"><el-button :type="isManageMode ? 'warning' : ''" :icon="Setting" circle @click="isManageMode = !isManageMode" /></el-tooltip>
        <el-tooltip content="Refresh Data"><el-button :icon="Refresh" circle @click="fetchData" /></el-tooltip>
      </div>
    </div>

    <el-collapse-transition>
      <div v-if="isManageMode" style="margin-top: 15px; padding: 10px; background: #fff5f5; border-radius: 4px; display: flex; align-items: center; gap: 15px;">
        <el-button type="danger" size="small" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          Delete ({{ selectedIds.length }})
        </el-button>
        <span style="font-size: 13px; color: #f56c6c;">Already In Editing Mode</span>
      </div>
    </el-collapse-transition>

    <el-table
      v-loading="loading"
      :data="friends"
      border
      style="width: 100%; margin-top: 20px; cursor: pointer;"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
    >
      <el-table-column v-if="isManageMode" type="selection" width="55" align="center" />

      <el-table-column type="index" label="ID" width="60" align="center" />
      <el-table-column prop="name" label="Name" width="120" />
      <el-table-column prop="hometown" label="Hometown" />

      <el-table-column v-if="isManageMode" label="Actions" width="100" align="center">
        <template #default="scope">
          <el-button type="danger" link @click.stop="confirmDelete(scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { Search, Plus, Refresh, Setting } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const friends = ref([])
const keyword = ref('')
const loading = ref(false)
const isManageMode = ref(false)
const selectedIds = ref([])
let timer = null

// 搜索防抖
const onInputChange = () => {
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => fetchData(), 300)
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
// 实际发出：/origin/friend
    // Vite 转发给后端：http://localhost:8080/friend
    const res = await request.get('/friend', {
      params: { keyword: keyword.value }
    })
    friends.value = res.data.data
  } catch (error) {
    ElMessage.error("Fail")
  } finally {
    loading.value = false
  }
}

// 处理勾选
const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

// 行点击：非管理模式下跳转详情
const handleRowClick = (row) => {
  if (!isManageMode.value) {
    router.push(`/friend/${row.id}`)
  }
}

// 单个删除
const confirmDelete = (row) => {
  ElMessageBox.confirm(`Are you sure you want to delete ${row.name} ?`, 'WARNING', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => executeDelete([row.id]))
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`Are you sure you want to delete ${selectedIds.value.length} friends?`, 'WARNING', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => executeDelete(selectedIds.value))
}

// 调用删除接口
const executeDelete = async (ids) => {
  try {
// 实际发出：/origin/friend
    await request.delete('/friend', { data: ids })
    ElMessage.success("Delete Successfully")
    fetchData()
  } catch (error) {
    ElMessage.error("Fail")
  }
}

onMounted(() => fetchData())
</script>