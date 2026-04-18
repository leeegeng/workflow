<template>
  <div class="form-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>表单设计</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>创建表单
          </el-button>
        </div>
      </template>

      <el-table :data="formList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="formName" label="表单名称" min-width="150" />
        <el-table-column prop="formKey" label="表单标识" min-width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDesign(row)">设计</el-button>
            <el-button type="success" link @click="handlePreview(row)">预览</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="表单名称" prop="formName">
          <el-input v-model="form.formName" placeholder="请输入表单名称" />
        </el-form-item>
        <el-form-item label="表单标识" prop="formKey">
          <el-input v-model="form.formKey" placeholder="请输入表单标识" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="通用表单" value="general" />
            <el-option label="审批表单" value="approval" />
            <el-option label="申请表单" value="application" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存并设计' : '下一步' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="表单预览" width="800px">
      <form-renderer :form-json="currentForm?.formJson" :preview="true" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFormList, createForm, updateForm, deleteForm } from '@/api/form'
import FormRenderer from '@/components/FormRenderer/index.vue'

const router = useRouter()
const formList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const previewVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const currentForm = ref(null)
const currentId = ref(null)

const form = reactive({
  formName: '',
  formKey: '',
  category: 'general',
  description: '',
  status: 1
})

const rules = {
  formName: [{ required: true, message: '请输入表单名称', trigger: 'blur' }],
  formKey: [{ required: true, message: '请输入表单标识', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFormList()
    if (res.code === 200) {
      formList.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '创建表单'
  Object.assign(form, {
    formName: '',
    formKey: '',
    category: 'general',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑表单'
  currentId.value = row.id
  Object.assign(form, {
    formName: row.formName,
    formKey: row.formKey,
    category: row.category,
    description: row.description,
    status: row.status
  })
  dialogVisible.value = true
}

const handleDesign = (row) => {
  router.push(`/workflow/form/design/${row.id}`)
}

const handlePreview = (row) => {
  currentForm.value = row
  previewVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除表单"${row.formName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteForm(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updateForm(currentId.value, form)
      if (res.code === 200) {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
        // 跳转到设计器
        router.push(`/workflow/form/design/${currentId.value}`)
      }
    } else {
      res = await createForm(form)
      if (res.code === 200) {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        loadData()
        // 跳转到设计器
        router.push(`/workflow/form/design/${res.data}`)
      }
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
