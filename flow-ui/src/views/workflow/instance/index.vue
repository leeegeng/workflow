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

      <!-- 发起流程对话框 -->
      <el-dialog v-model="startDialogVisible" title="发起流程" width="600px">
        <el-form :model="startForm" label-width="100px" :rules="startRules" ref="startFormRef">
          <el-form-item label="选择流程" prop="processDefinitionId">
            <el-select v-model="startForm.processDefinitionId" placeholder="请选择流程" style="width: 100%">
              <el-option
                v-for="item in definitionList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="流程标题" prop="title">
            <el-input v-model="startForm.title" placeholder="请输入流程标题" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="startForm.remark" type="textarea" rows="3" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="startDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="startLoading" @click="handleStartSubmit">确定</el-button>
        </template>
      </el-dialog>

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

    <!-- 查看流程实例详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="流程实例详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程名称">{{ currentInstance.processDefinitionName }}</el-descriptions-item>
        <el-descriptions-item label="流程标题">{{ currentInstance.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ currentInstance.startUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentInstance.status)">
            {{ getStatusText(currentInstance.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentInstance.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentInstance.endTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="流程实例ID" :span="2">{{ currentInstance.processInstanceId }}</el-descriptions-item>
        <el-descriptions-item label="流程定义ID" :span="2">{{ currentInstance.processDefinitionId }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getDefinitionList } from '@/api/definition'
import { startProcessInstance, getMyStartedList, getProcessInstanceById, terminateProcessInstance } from '@/api/instance'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const instanceList = ref([])
const loading = ref(false)
const startDialogVisible = ref(false)
const startLoading = ref(false)
const definitionList = ref([])
const startFormRef = ref(null)

const startForm = reactive({
  processDefinitionId: '',
  title: '',
  remark: ''
})

const startRules = {
  processDefinitionId: [{ required: true, message: '请选择流程', trigger: 'change' }],
  title: [{ required: true, message: '请输入流程标题', trigger: 'blur' }]
}

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
  try {
    const res = await getMyStartedList()
    if (res.code === 200) {
      instanceList.value = res.data || []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取流程实例列表失败')
  } finally {
    loading.value = false
  }
}

// 加载流程定义列表
const loadDefinitionList = async () => {
  try {
    const res = await getDefinitionList()
    if (res.code === 200) {
      definitionList.value = res.data || []
    }
  } catch (error) {
    console.error(error)
  }
}

// 打开发起流程对话框
const handleStart = async () => {
  startForm.processDefinitionId = ''
  startForm.title = ''
  startForm.remark = ''
  await loadDefinitionList()
  startDialogVisible.value = true
}

// 提交发起流程
const handleStartSubmit = async () => {
  if (!startFormRef.value) return
  
  await startFormRef.value.validate(async (valid) => {
    if (valid) {
      startLoading.value = true
      try {
        const res = await startProcessInstance({
          processDefinitionId: startForm.processDefinitionId,
          variables: {
            title: startForm.title,
            remark: startForm.remark,
            startUserId: userStore.userInfo?.userId?.toString(),
            startUserName: userStore.userInfo?.username
          }
        })
        if (res.code === 200) {
          ElMessage.success('流程发起成功')
          startDialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('流程发起失败')
      } finally {
        startLoading.value = false
      }
    }
  })
}

// 查看对话框
const viewDialogVisible = ref(false)
const currentInstance = ref({})

const handleView = async (row) => {
  try {
    const res = await getProcessInstanceById(row.processInstanceId)
    if (res.code === 200) {
      currentInstance.value = res.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取流程详情失败')
  }
}

const handleTerminate = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入终止原因', '终止流程', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputValidator: (val) => val ? true : '请输入终止原因'
    })
    
    const res = await terminateProcessInstance(row.processInstanceId, value)
    if (res.code === 200) {
      ElMessage.success('终止成功')
      loadData()
    } else {
      ElMessage.error(res.message || '终止失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('终止失败')
    }
  }
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
