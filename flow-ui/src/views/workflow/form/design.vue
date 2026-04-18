<template>
  <div class="form-design-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>表单设计 - {{ formInfo.formName }}</span>
          <div class="header-actions">
            <el-button @click="$router.back()">返回</el-button>
            <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
          </div>
        </div>
      </template>

      <form-designer
        ref="designerRef"
        :initial-config="formConfig"
        @save="handleDesignerSave"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFormById, updateForm } from '@/api/form'
import FormDesigner from '@/components/FormDesigner/index.vue'

const route = useRoute()
const router = useRouter()
const designerRef = ref()
const saveLoading = ref(false)

const formId = route.params.id

const formInfo = ref({
  formName: ''
})

const formConfig = ref({
  fields: []
})

// 加载表单信息
const loadFormInfo = async () => {
  try {
    const res = await getFormById(formId)
    if (res.code === 200) {
      formInfo.value = res.data
      // 解析表单JSON
      if (res.data.formJson) {
        try {
          formConfig.value = JSON.parse(res.data.formJson)
        } catch (e) {
          console.error('解析表单JSON失败:', e)
        }
      }
    }
  } catch (error) {
    console.error(error)
  }
}

// 保存表单设计
const handleSave = () => {
  const config = designerRef.value?.getFormConfig()
  if (config) {
    handleDesignerSave(config)
  }
}

const handleDesignerSave = async (config) => {
  saveLoading.value = true
  try {
    const formJson = JSON.stringify(config)
    const res = await updateForm(formId, {
      formName: formInfo.value.formName,
      formKey: formInfo.value.formKey,
      formJson: formJson
    })
    if (res.code === 200) {
      ElMessage.success('保存成功')
    }
  } catch (error) {
    console.error(error)
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  loadFormInfo()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}
</style>
