<template>
  <div class="model-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程模型</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>创建模型
          </el-button>
        </div>
      </template>

      <el-table :data="modelList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="modelName" label="模型名称" min-width="150" />
        <el-table-column prop="modelKey" label="模型标识" min-width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="version" label="版本" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已部署' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDesign(row)">设计</el-button>
            <el-button type="success" link @click="handleDeploy(row)" :disabled="row.status === 1">部署</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="form.modelName" placeholder="请输入模型名称" />
        </el-form-item>
        <el-form-item label="模型标识" prop="modelKey">
          <el-input v-model="form.modelKey" placeholder="请输入模型标识" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="关联表单" prop="formId">
          <el-select v-model="form.formId" placeholder="请选择关联表单" clearable style="width: 100%">
            <el-option
              v-for="item in formList"
              :key="item.id"
              :label="item.formName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getModelList, createModel, updateModel, deleteModel, deployModel } from '@/api/model'
import { getFormList } from '@/api/form'

const router = useRouter()
const modelList = ref([])
const formList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const currentId = ref('')

const form = reactive({
  modelName: '',
  modelKey: '',
  category: '',
  description: '',
  formId: null
})

const rules = {
  modelName: [
    { required: true, message: '请输入模型名称', trigger: 'blur' }
  ],
  modelKey: [
    { required: true, message: '请输入模型标识', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const [modelRes, formRes] = await Promise.all([
      getModelList(),
      getFormList()
    ])
    if (modelRes.code === 200) {
      modelList.value = modelRes.data
    }
    if (formRes.code === 200) {
      formList.value = formRes.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '创建模型'
  Object.assign(form, {
    modelName: '',
    modelKey: '',
    category: '',
    description: '',
    formId: null
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑模型'
  currentId.value = row.id
  Object.assign(form, {
    modelName: row.modelName,
    modelKey: row.modelKey,
    category: row.category,
    description: row.description,
    formId: row.formId
  })
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除模型"${row.modelName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteModel(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDeploy = (row) => {
  ElMessageBox.confirm(`确定要部署模型"${row.modelName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deployModel(row.id)
      if (res.code === 200) {
        ElMessage.success('部署成功')
        loadData()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDesign = (row) => {
  router.push(`/workflow/model/design/${row.id}`)
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const api = isEdit.value ? () => updateModel(currentId.value, form) : () => createModel(form)
    const res = await api()
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '创建成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
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
