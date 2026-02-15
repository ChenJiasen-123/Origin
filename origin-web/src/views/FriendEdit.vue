<template>
  <div class="edit-container" style="padding: 20px; max-width: 600px; margin: 0 auto;">
    <div class="button-group" style="margin-bottom: 20px; display: flex; gap: 10px;">
      <el-button @click="router.back()">Cancel</el-button>
      <el-button type="success" :loading="submitting" @click="handleSave">Save</el-button>
    </div>

    <el-card v-loading="loading">
      <template #header>
        <span style="font-weight: bold;">{{ isEdit ? 'Edit Friend' : 'Add New Friend' }}</span>
      </template>

      <el-form :model="friend" label-position="top">
        <el-form-item label="Name">
          <el-input v-model="friend.name" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Age">
              <el-input-number v-model="friend.age" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Birthday">
              <el-date-picker v-model="friend.birthday" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Phone">
          <el-input v-model="friend.number" />
        </el-form-item>

        <el-form-item label="Hometown">
          <el-input v-model="friend.hometown" />
        </el-form-item>

        <el-form-item label="Tags">
          <el-select v-model="friend.tags" multiple filterable allow-create placeholder="Select or type to create" style="width:100%">
            <el-option v-for="t in friend.tags" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>

        <el-form-item label="Note">
          <el-input v-model="friend.note" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item v-if="isEdit" label="Last Modified (Read Only)">
          <el-input v-model="friend.lastModified" disabled />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const friend = ref({
  tags: [] // 初始化 tags 防止渲染报错
})
const loading = ref(false)
const submitting = ref(false)

// 获取详情
const fetchDetail = async () => {
  if (!isEdit.value) return

  loading.value = true
  try {
    // 【修改2】使用 request 实例。baseURL 已配置为 /origin
    // 实际请求：/origin/friend/1
    const res = await request.get(`/friend/${route.params.id}`)
    friend.value = res.data.data
    if(!friend.value.tags) friend.value.tags = []
  } catch (e) {
    ElMessage.error("Failed to load details")
  } finally {
    loading.value = false
  }
}

// 处理保存（新增或编辑）
const handleSave = async () => {
  submitting.value = true
  // 【修改3】统一使用相对路径，baseURL 会自动处理前缀
  const path = '/friend'

  try {
    if (isEdit.value) {
      // 编辑：PUT /origin/friend/id
      await request.put(`${path}/${route.params.id}`, friend.value)
      ElMessage.success("Edit Successfully")
      router.back()
    } else {
      // 新增：POST /origin/friend
      await request.post(path, friend.value)
      ElMessage.success("Add Successfully")
      router.replace('/friend')
    }
  } catch (e) {
    ElMessage.error("Operation failed")
  } finally {
    submitting.value = false
  }
}

onMounted(() => fetchDetail())
</script>