<template>
  <div class="todo-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>待办任务</span>
        </div>
      </template>

      <el-table :data="taskList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" />
        <el-table-column prop="title" label="流程标题" min-width="200" />
        <el-table-column prop="startUserName" label="发起人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleProcess(row)">办理</el-button>
            <el-button type="warning" link @click="handleDelegate(row)">委托</el-button>
            <el-button type="danger" link @click="handleReject(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 办理对话框 -->
    <el-dialog v-model="processDialogVisible" title="办理任务" width="600px">
      <el-form :model="processForm" label-width="80px">
        <el-form-item label="处理意见">
          <el-input v-model="processForm.comment" type="textarea" rows="4" placeholder="请输入处理意见" />
        </el-form-item>
        <el-form-item label="处理结果">
          <el-radio-group v-model="processForm.result">
            <el-radio :label="1">同意</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleProcessSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 委托对话框 -->
    <el-dialog v-model="delegateDialogVisible" title="委托任务" width="400px">
      <el-form :model="delegateForm" label-width="100px">
        <el-form-item label="委托人">
          <el-select v-model="delegateForm.delegateToUserId" placeholder="请选择委托人" style="width: 100%">
            <el-option label="用户1" :value="1" />
            <el-option label="用户2" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="delegateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleDelegateSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTodoList, completeTask, delegateTask, rejectTask } from '@/api/task'

const taskList = ref([])
const loading = ref(false)
const processDialogVisible = ref(false)
const delegateDialogVisible = ref(false)
const submitLoading = ref(false)
const currentTask = ref(null)

const processForm = reactive({
  comment: '',
  result: 1
})

const delegateForm = reactive({
  delegateToUserId: null
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTodoList()
    if (res.code === 200) {
      taskList.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleProcess = (row) => {
  currentTask.value = row
  processForm.comment = ''
  processForm.result = 1
  processDialogVisible.value = true
}

const handleProcessSubmit = async () => {
  submitLoading.value = true
  try {
    const res = await completeTask({
      taskId: currentTask.value.taskId,
      comment: processForm.comment,
      result: processForm.result
    })
    if (res.code === 200) {
      ElMessage.success('办理成功')
      processDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelegate = (row) => {
  currentTask.value = row
  delegateForm.delegateToUserId = null
  delegateDialogVisible.value = true
}

const handleDelegateSubmit = async () => {
  if (!delegateForm.delegateToUserId) {
    ElMessage.warning('请选择委托人')
    return
  }
  submitLoading.value = true
  try {
    const res = await delegateTask(currentTask.value.taskId, delegateForm.delegateToUserId)
    if (res.code === 200) {
      ElMessage.success('委托成功')
      delegateDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleReject = (row) => {
  ElMessageBox.prompt('请输入驳回原因', '驳回任务', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea'
  }).then(async ({ value }) => {
    try {
      const res = await rejectTask(row.taskId, value)
      if (res.code === 200) {
        ElMessage.success('驳回成功')
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

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
