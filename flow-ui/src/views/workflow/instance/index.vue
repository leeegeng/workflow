<template>
  <div class="instance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程实例</span>
          <el-button type="primary" @click="handleStart">
            <el-icon><Plus /></el-icon>发起流程
          </el-button>
        </div>
      </template>

      <el-table :data="instanceList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" />
        <el-table-column prop="title" label="流程标题" min-width="200" />
        <el-table-column prop="startUserName" label="发起人" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 1" type="danger" link @click="handleTerminate(row)">终止</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const instanceList = ref([])
const loading = ref(false)

const getStatusType = (status) => {
  const types = {
    1: 'primary',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    1: '运行中',
    2: '已完成',
    3: '已终止'
  }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  // TODO: 调用流程实例列表API
  setTimeout(() => {
    instanceList.value = []
    loading.value = false
  }, 500)
}

const handleStart = () => {
  // TODO: 发起流程
}

const handleView = (row) => {
  // TODO: 查看流程实例
  console.log('查看流程实例', row)
}

const handleTerminate = (row) => {
  ElMessageBox.prompt('请输入终止原因', '终止流程', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea'
  }).then(({ value }) => {
    // TODO: 终止流程
    ElMessage.success('终止成功')
    loadData()
  })
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
