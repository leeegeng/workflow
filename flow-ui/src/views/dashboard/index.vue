<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">流程模型</div>
            <div class="stat-value">{{ stats.modelCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">流程实例</div>
            <div class="stat-value">{{ stats.instanceCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <el-icon><Bell /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">待办任务</div>
            <div class="stat-value">{{ stats.todoCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #f56c6c;">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">已办任务</div>
            <div class="stat-value">{{ stats.doneCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-links">
            <el-button type="primary" @click="$router.push('/workflow/model')">
              <el-icon><Plus /></el-icon>创建流程模型
            </el-button>
            <el-button type="success" @click="$router.push('/workflow/todo')">
              <el-icon><Bell /></el-icon>查看待办
            </el-button>
            <el-button type="warning" @click="$router.push('/workflow/instance')">
              <el-icon><List /></el-icon>我的流程
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系统公告</span>
          </template>
          <div class="notice-list">
            <div class="notice-item">
              <el-tag type="success" size="small">NEW</el-tag>
              <span>工作流管理系统V1.0正式上线</span>
            </div>
            <div class="notice-item">
              <el-tag type="info" size="small">TIP</el-tag>
              <span>欢迎使用Flowable工作流引擎</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getTodoList } from '@/api/task'
import { getModelList } from '@/api/model'

const stats = reactive({
  modelCount: 0,
  instanceCount: 0,
  todoCount: 0,
  doneCount: 0
})

const loadStats = async () => {
  try {
    // 加载待办数量
    const todoRes = await getTodoList()
    if (todoRes.code === 200) {
      stats.todoCount = todoRes.data.length
    }

    // 加载模型数量
    const modelRes = await getModelList()
    if (modelRes.code === 200) {
      stats.modelCount = modelRes.data.length
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 30px;
}

.stat-info {
  margin-left: 15px;
}

.stat-title {
  color: #909399;
  font-size: 14px;
}

.stat-value {
  color: #303133;
  font-size: 24px;
  font-weight: bold;
  margin-top: 5px;
}

.quick-links {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.notice-list {
  .notice-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }
  }
}
</style>
