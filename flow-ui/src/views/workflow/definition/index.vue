<template>
  <div class="definition-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程定义</span>
        </div>
      </template>

      <el-table :data="definitionList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="流程名称" min-width="150" />
        <el-table-column prop="key" label="流程标识" min-width="150" />
        <el-table-column prop="version" label="版本" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.suspended ? 'danger' : 'success'">
              {{ row.suspended ? '已挂起' : '激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deploymentTime" label="部署时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="warning" link @click="handleToggleStatus(row)">
              {{ row.suspended ? '激活' : '挂起' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const definitionList = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  // TODO: 调用流程定义列表API
  setTimeout(() => {
    definitionList.value = []
    loading.value = false
  }, 500)
}

const handleView = (row) => {
  // TODO: 查看流程定义
  console.log('查看流程定义', row)
}

const handleToggleStatus = (row) => {
  // TODO: 切换流程定义状态
  console.log('切换状态', row)
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
