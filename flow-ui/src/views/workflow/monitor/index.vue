<template>
  <div class="monitor-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ statistics.processDefinitionCount || 0 }}</div>
          <div class="stat-label">流程定义</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value text-primary">{{ statistics.runningCount || 0 }}</div>
          <div class="stat-label">运行中</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value text-success">{{ statistics.completedCount || 0 }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value text-warning">{{ statistics.todoTaskCount || 0 }}</div>
          <div class="stat-label">待办任务</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ statistics.todayStartCount || 0 }}</div>
          <div class="stat-label">今日发起</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ statistics.averageDurationFormat || '-' }}</div>
          <div class="stat-label">平均耗时</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 流程监控列表 -->
    <el-card class="monitor-table">
      <template #header>
        <div class="card-header">
          <span>流程监控</span>
          <div class="header-actions">
            <el-input
              v-model="searchKey"
              placeholder="搜索流程名称/标题"
              style="width: 200px"
              clearable
            />
            <el-button type="primary" @click="loadData">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" />
        <el-table-column prop="title" label="流程标题" min-width="200" />
        <el-table-column prop="startUserName" label="发起人" width="120" />
        <el-table-column prop="currentNodeName" label="当前节点" width="150" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="durationFormat" label="持续时间" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="success" link @click="handleViewDiagram(row)">流程图</el-button>
            <el-button
              v-if="row.status === 1"
              type="danger"
              link
              @click="handleTerminate(row)"
            >
              终止
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 流程详情对话框 -->
    <el-dialog v-model="detailVisible" title="流程详情" width="900px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="流程名称">{{ currentRow?.processDefinitionName }}</el-descriptions-item>
            <el-descriptions-item label="流程标题">{{ currentRow?.title }}</el-descriptions-item>
            <el-descriptions-item label="发起人">{{ currentRow?.startUserName }}</el-descriptions-item>
            <el-descriptions-item label="当前节点">{{ currentRow?.currentNodeName }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentRow?.status)">
                {{ currentRow?.statusName }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ currentRow?.startTime }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ currentRow?.endTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="持续时间">{{ currentRow?.durationFormat || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="执行历史" name="history">
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in historyList"
              :key="index"
              :type="item.endTime ? 'success' : 'primary'"
              :timestamp="item.startTime"
            >
              <div class="history-item">
                <div class="history-title">{{ item.activityName }}</div>
                <div class="history-info">
                  <span>办理人: {{ item.assignee || '-' }}</span>
                  <span v-if="item.duration">耗时: {{ formatDuration(item.duration) }}</span>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>

        <el-tab-pane label="流程变量" name="variables">
          <el-table :data="variableList" border>
            <el-table-column prop="name" label="变量名" />
            <el-table-column prop="value" label="变量值" />
            <el-table-column prop="type" label="类型" width="120" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 流程图对话框 -->
    <el-dialog v-model="diagramVisible" title="流程图" width="1000px">
      <process-diagram
        :process-instance-id="currentRow?.processInstanceId"
        :process-definition-id="currentRow?.processDefinitionId"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMonitorList, getStatistics, getHistory, terminateInstance } from '@/api/monitor'
import ProcessDiagram from '@/components/ProcessDiagram/index.vue'

const loading = ref(false)
const monitorList = ref([])
const statistics = ref({})
const searchKey = ref('')
const detailVisible = ref(false)
const diagramVisible = ref(false)
const activeTab = ref('basic')
const currentRow = ref(null)
const historyList = ref([])
const variableList = ref([])

const filteredList = computed(() => {
  if (!searchKey.value) return monitorList.value
  const key = searchKey.value.toLowerCase()
  return monitorList.value.filter(item =>
    (item.processDefinitionName && item.processDefinitionName.toLowerCase().includes(key)) ||
    (item.title && item.title.toLowerCase().includes(key))
  )
})

const getStatusType = (status) => {
  const types = {
    1: 'primary',
    2: 'success',
    3: 'danger',
    4: 'warning'
  }
  return types[status] || 'info'
}

const formatDuration = (millis) => {
  if (!millis) return '-'
  const seconds = Math.floor(millis / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (days > 0) return `${days}天${hours % 24}小时`
  if (hours > 0) return `${hours}小时${minutes % 60}分`
  if (minutes > 0) return `${minutes}分${seconds % 60}秒`
  return `${seconds}秒`
}

const loadData = async () => {
  loading.value = true
  try {
    const [listRes, statRes] = await Promise.all([
      getMonitorList(),
      getStatistics()
    ])
    if (listRes.code === 200) {
      monitorList.value = listRes.data
    }
    if (statRes.code === 200) {
      statistics.value = statRes.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleView = async (row) => {
  currentRow.value = row
  detailVisible.value = true
  activeTab.value = 'basic'

  // 加载历史记录
  try {
    const res = await getHistory(row.processInstanceId)
    if (res.code === 200) {
      historyList.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const handleViewDiagram = (row) => {
  currentRow.value = row
  diagramVisible.value = true
}

const handleTerminate = (row) => {
  ElMessageBox.prompt('请输入终止原因', '终止流程', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputValidator: (value) => {
      if (!value) return '请输入终止原因'
      return true
    }
  }).then(async ({ value }) => {
    try {
      const res = await terminateInstance(row.processInstanceId, value)
      if (res.code === 200) {
        ElMessage.success('终止成功')
        loadData()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;

  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 8px;

    &.text-primary {
      color: #409eff;
    }

    &.text-success {
      color: #67c23a;
    }

    &.text-warning {
      color: #e6a23c;
    }
  }

  .stat-label {
    font-size: 14px;
    color: #909399;
  }
}

.monitor-table {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }
}

.history-item {
  .history-title {
    font-weight: bold;
    margin-bottom: 4px;
  }

  .history-info {
    font-size: 12px;
    color: #909399;

    span {
      margin-right: 16px;
    }
  }
}
</style>
