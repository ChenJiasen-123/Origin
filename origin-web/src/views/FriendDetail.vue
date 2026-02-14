<template>
  <div class="detail-container" style="padding: 20px; max-width: 600px; margin: 0 auto;">
    <div class="button-group" style="margin-bottom: 20px; display: flex; gap: 10px;">
      <el-button @click="router.push('/friend')">Back</el-button>
      <el-button type="primary" @click="goToEdit">Edit</el-button>
    </div>

    <el-card v-loading="loading" class="info-card">
      <template #header>
        <div class="card-header">
          <span style="font-size: 1.2rem; font-weight: bold; color: #409EFF;">Friend Detail</span>
        </div>
      </template>

      <el-descriptions :column="1" border>
        <el-descriptions-item label="Name">{{ friend.name }}</el-descriptions-item>
        <el-descriptions-item label="Age">{{ friend.age }}</el-descriptions-item>
        <el-descriptions-item label="Birthday">{{ friend.birthday }}</el-descriptions-item>
        <el-descriptions-item label="Next Birthday">
          {{ friend.howLongForBirthday ? 'Only for ' + friend.howLongForBirthday + ' days' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="Phone">{{ friend.number }}</el-descriptions-item>
        <el-descriptions-item label="Hometown">{{ friend.hometown }}</el-descriptions-item>
        <el-descriptions-item label="Last Modified">{{ friend.lastModified }}</el-descriptions-item>
        <el-descriptions-item label="Tags">
          <el-tag v-for="tag in friend.tags" :key="tag" style="margin-right: 5px;">{{ tag }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Note">{{ friend.note }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const friend = ref({})
const loading = ref(false)

const goToEdit = () => {
  router.push(`/friend/edit/${route.params.id}`)
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const response = await axios.get(`http://192.168.124.9:8080/friend/${route.params.id}`)
    friend.value = response.data.data
  } catch (error) {
    console.error("加载详情失败", error)
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchDetail())
</script>