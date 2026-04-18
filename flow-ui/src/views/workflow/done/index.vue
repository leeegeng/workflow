<template>
  <div class="done-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>已办任务</span>
        </div>
      </template>

      <el-table :data="taskList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" />
        <el-table-column prop="title" label="流程标题" min-width="200" />
        <el-table-column prop="startUserName" label="发起人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="endTime" label="完成时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDoneList } from '@/api/task'

const taskList = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDoneList()
    if (res.code === 200) {
      taskList.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  // TODO: 查看任务详情
  console.log('查看任务', row)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
