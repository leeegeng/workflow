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
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.suspended ? 'danger' : 'success'">
              {{ row.suspended ? '已挂起' : '激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deploymentTime" label="部署时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="success" link @click="handleViewXml(row)">XML</el-button>
            <el-button
              :type="row.suspended ? 'success' : 'warning'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.suspended ? '激活' : '挂起' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 查看XML对话框 -->
    <el-dialog
      v-model="xmlDialogVisible"
      title="流程定义XML"
      width="800px"
      :close-on-click-modal="false"
    >
      <pre style="background-color: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto;">{{ xmlContent }}</pre>
    </el-dialog>

    <!-- 查看流程图对话框 -->
    <el-dialog
      v-model="diagramDialogVisible"
      title="流程图"
      width="900px"
      :close-on-click-modal="false"
    >
      <ProcessDiagram :process-definition-id="currentDefinitionId" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDefinitionList, activateDefinition, suspendDefinition, getDefinitionXml, deleteDefinition } from '@/api/definition'
import ProcessDiagram from '@/components/ProcessDiagram/index.vue'

const definitionList = ref([])
const loading = ref(false)
const xmlDialogVisible = ref(false)
const xmlContent = ref('')
const diagramDialogVisible = ref(false)
const currentDefinitionId = ref('')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDefinitionList()
    if (res.code === 200) {
      definitionList.value = res.data || []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取流程定义列表失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  currentDefinitionId.value = row.id
  diagramDialogVisible.value = true
}

const handleViewXml = async (row) => {
  try {
    const res = await getDefinitionXml(row.id)
    if (res.code === 200) {
      xmlContent.value = res.data
      xmlDialogVisible.value = true
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取流程定义XML失败')
  }
}

const handleToggleStatus = async (row) => {
  const action = row.suspended ? '激活' : '挂起'
  try {
    await ElMessageBox.confirm(`确定要${action}流程定义"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const api = row.suspended ? activateDefinition : suspendDefinition
    const res = await api(row.id)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除流程定义"${row.name}"吗？删除后将无法恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteDefinition(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '删除失败')
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
