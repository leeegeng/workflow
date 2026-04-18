<template>
  <div class="form-renderer">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="render-form"
    >
      <template v-for="field in fields" :key="field.id">
        <el-form-item
          :label="field.label"
          :prop="field.field"
          :rules="getFieldRules(field)"
        >
          <!-- 输入框 -->
          <el-input
            v-if="field.type === 'input'"
            v-model="formData[field.field]"
            :placeholder="field.placeholder"
            :disabled="preview"
            clearable
          />

          <!-- 文本域 -->
          <el-input
            v-else-if="field.type === 'textarea'"
            v-model="formData[field.field]"
            type="textarea"
            :rows="field.rows || 3"
            :placeholder="field.placeholder"
            :disabled="preview"
          />

          <!-- 数字输入 -->
          <el-input-number
            v-else-if="field.type === 'number'"
            v-model="formData[field.field]"
            :placeholder="field.placeholder"
            :disabled="preview"
            style="width: 100%"
          />

          <!-- 单选框 -->
          <el-radio-group
            v-else-if="field.type === 'radio'"
            v-model="formData[field.field]"
            :disabled="preview"
          >
            <el-radio
              v-for="opt in field.options"
              :key="opt.value"
              :label="opt.value"
            >
              {{ opt.label }}
            </el-radio>
          </el-radio-group>

          <!-- 多选框 -->
          <el-checkbox-group
            v-else-if="field.type === 'checkbox'"
            v-model="formData[field.field]"
            :disabled="preview"
          >
            <el-checkbox
              v-for="opt in field.options"
              :key="opt.value"
              :label="opt.value"
            >
              {{ opt.label }}
            </el-checkbox>
          </el-checkbox-group>

          <!-- 下拉选择 -->
          <el-select
            v-else-if="field.type === 'select'"
            v-model="formData[field.field]"
            :placeholder="field.placeholder"
            :disabled="preview"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="opt in field.options"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>

          <!-- 日期选择 -->
          <el-date-picker
            v-else-if="field.type === 'date'"
            v-model="formData[field.field]"
            type="date"
            :placeholder="field.placeholder"
            :disabled="preview"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />

          <!-- 日期时间 -->
          <el-date-picker
            v-else-if="field.type === 'datetime'"
            v-model="formData[field.field]"
            type="datetime"
            :placeholder="field.placeholder"
            :disabled="preview"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />

          <!-- 开关 -->
          <el-switch
            v-else-if="field.type === 'switch'"
            v-model="formData[field.field]"
            :disabled="preview"
          />
        </el-form-item>
      </template>

      <!-- 空状态 -->
      <el-empty v-if="fields.length === 0" description="暂无表单组件" />
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'

const props = defineProps({
  // 表单JSON字符串
  formJson: {
    type: String,
    default: ''
  },
  // 表单配置对象
  formConfig: {
    type: Object,
    default: () => null
  },
  // 表单数据（用于编辑时回显）
  initialData: {
    type: Object,
    default: () => ({})
  },
  // 是否预览模式
  preview: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['change'])

const formRef = ref()

// 表单字段列表
const fields = ref([])

// 表单数据
const formData = reactive({})

// 表单验证规则
const formRules = reactive({})

// 解析表单配置
const parseFormConfig = () => {
  let config = null

  if (props.formConfig) {
    config = props.formConfig
  } else if (props.formJson) {
    try {
      config = JSON.parse(props.formJson)
    } catch (e) {
      console.error('解析表单JSON失败:', e)
    }
  }

  if (config && config.fields) {
    fields.value = config.fields

    // 初始化表单数据
    fields.value.forEach(field => {
      // 设置默认值
      if (field.defaultValue !== undefined && field.defaultValue !== '') {
        if (field.type === 'checkbox') {
          formData[field.field] = Array.isArray(field.defaultValue)
            ? field.defaultValue
            : [field.defaultValue]
        } else if (field.type === 'number') {
          formData[field.field] = Number(field.defaultValue)
        } else if (field.type === 'switch') {
          formData[field.field] = field.defaultValue === 'true' || field.defaultValue === true
        } else {
          formData[field.field] = field.defaultValue
        }
      } else {
        // 根据类型设置默认值
        switch (field.type) {
          case 'checkbox':
            formData[field.field] = []
            break
          case 'number':
            formData[field.field] = 0
            break
          case 'switch':
            formData[field.field] = false
            break
          default:
            formData[field.field] = ''
        }
      }
    })

    // 合并初始数据
    if (props.initialData) {
      Object.assign(formData, props.initialData)
    }
  }
}

// 获取字段验证规则
const getFieldRules = (field) => {
  const rules = []

  if (field.required) {
    rules.push({
      required: true,
      message: `${field.label}不能为空`,
      trigger: field.type === 'input' || field.type === 'textarea' ? 'blur' : 'change'
    })
  }

  return rules
}

// 验证表单
const validate = async () => {
  try {
    await formRef.value.validate()
    return true
  } catch (error) {
    return false
  }
}

// 获取表单数据
const getFormData = () => {
  return { ...formData }
}

// 设置表单数据
const setFormData = (data) => {
  Object.assign(formData, data)
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
}

// 监听数据变化
watch(formData, (newVal) => {
  emit('change', { ...newVal })
}, { deep: true })

// 监听配置变化
watch(() => props.formJson, parseFormConfig, { immediate: true })
watch(() => props.formConfig, parseFormConfig, { immediate: true })
watch(() => props.initialData, (newVal) => {
  if (newVal) {
    Object.assign(formData, newVal)
  }
}, { deep: true })

onMounted(() => {
  parseFormConfig()
})

defineExpose({
  validate,
  getFormData,
  setFormData,
  resetForm
})
</script>

<style scoped>
.form-renderer {
  padding: 20px;
}

.render-form {
  max-width: 800px;
  margin: 0 auto;
}
</style>
