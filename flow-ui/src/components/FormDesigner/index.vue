<template>
  <div class="form-designer">
    <!-- 左侧组件面板 -->
    <div class="component-panel">
      <div class="panel-title">组件库</div>
      <div class="component-list">
        <div
          v-for="item in componentList"
          :key="item.type"
          class="component-item"
          draggable="true"
          @dragstart="handleDragStart(item)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </div>
      </div>
    </div>

    <!-- 中间画布区域 -->
    <div class="canvas-panel">
      <div class="panel-title">
        表单设计
        <div class="toolbar">
          <el-button type="primary" size="small" @click="handlePreview">
            <el-icon><View /></el-icon>预览
          </el-button>
          <el-button type="success" size="small" @click="handleSave">
            <el-icon><Check /></el-icon>保存
          </el-button>
          <el-button type="danger" size="small" @click="handleClear">
            <el-icon><Delete /></el-icon>清空
          </el-button>
        </div>
      </div>
      <div
        class="canvas-area"
        @dragover.prevent
        @drop="handleDrop"
      >
        <el-form
          ref="formRef"
          :model="formData"
          label-width="100px"
          class="designer-form"
        >
          <draggable
            v-model="formConfig.fields"
            item-key="id"
            class="field-list"
            ghost-class="ghost"
            @end="handleDragEnd"
          >
            <template #item="{ element, index }">
              <div
                class="form-field"
                :class="{ active: selectedField?.id === element.id }"
                @click="handleSelectField(element)"
              >
                <div class="field-actions">
                  <el-icon class="drag-handle"><Rank /></el-icon>
                  <el-icon class="delete-btn" @click.stop="handleDeleteField(index)"><Delete /></el-icon>
                </div>
                <el-form-item :label="element.label" :required="element.required">
                  <!-- 输入框 -->
                  <el-input
                    v-if="element.type === 'input'"
                    v-model="formData[element.field]"
                    :placeholder="element.placeholder"
                    :disabled="true"
                  />
                  <!-- 文本域 -->
                  <el-input
                    v-else-if="element.type === 'textarea'"
                    v-model="formData[element.field]"
                    type="textarea"
                    :rows="element.rows || 3"
                    :placeholder="element.placeholder"
                    :disabled="true"
                  />
                  <!-- 数字输入 -->
                  <el-input-number
                    v-else-if="element.type === 'number'"
                    v-model="formData[element.field]"
                    :disabled="true"
                  />
                  <!-- 单选框 -->
                  <el-radio-group
                    v-else-if="element.type === 'radio'"
                    v-model="formData[element.field]"
                    :disabled="true"
                  >
                    <el-radio
                      v-for="opt in element.options"
                      :key="opt.value"
                      :label="opt.value"
                    >
                      {{ opt.label }}
                    </el-radio>
                  </el-radio-group>
                  <!-- 多选框 -->
                  <el-checkbox-group
                    v-else-if="element.type === 'checkbox'"
                    v-model="formData[element.field]"
                    :disabled="true"
                  >
                    <el-checkbox
                      v-for="opt in element.options"
                      :key="opt.value"
                      :label="opt.value"
                    >
                      {{ opt.label }}
                    </el-checkbox>
                  </el-checkbox-group>
                  <!-- 下拉选择 -->
                  <el-select
                    v-else-if="element.type === 'select'"
                    v-model="formData[element.field]"
                    :placeholder="element.placeholder"
                    :disabled="true"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="opt in element.options"
                      :key="opt.value"
                      :label="opt.label"
                      :value="opt.value"
                    />
                  </el-select>
                  <!-- 日期选择 -->
                  <el-date-picker
                    v-else-if="element.type === 'date'"
                    v-model="formData[element.field]"
                    type="date"
                    :placeholder="element.placeholder"
                    :disabled="true"
                    style="width: 100%"
                  />
                  <!-- 日期时间 -->
                  <el-date-picker
                    v-else-if="element.type === 'datetime'"
                    v-model="formData[element.field]"
                    type="datetime"
                    :placeholder="element.placeholder"
                    :disabled="true"
                    style="width: 100%"
                  />
                  <!-- 开关 -->
                  <el-switch
                    v-else-if="element.type === 'switch'"
                    v-model="formData[element.field]"
                    :disabled="true"
                  />
                </el-form-item>
              </div>
            </template>
          </draggable>

          <div v-if="formConfig.fields.length === 0" class="empty-tip">
            <el-icon :size="48"><Plus /></el-icon>
            <p>从左侧拖拽组件到此处</p>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 右侧属性面板 -->
    <div class="property-panel">
      <div class="panel-title">属性配置</div>
      <div class="property-content" v-if="selectedField">
        <el-form label-width="80px" size="small">
          <el-form-item label="字段标识">
            <el-input v-model="selectedField.field" placeholder="请输入字段标识" />
          </el-form-item>
          <el-form-item label="字段名称">
            <el-input v-model="selectedField.label" placeholder="请输入字段名称" />
          </el-form-item>
          <el-form-item label="占位提示">
            <el-input v-model="selectedField.placeholder" placeholder="请输入占位提示" />
          </el-form-item>
          <el-form-item label="默认值">
            <el-input v-model="selectedField.defaultValue" placeholder="请输入默认值" />
          </el-form-item>
          <el-form-item label="是否必填">
            <el-switch v-model="selectedField.required" />
          </el-form-item>

          <!-- 选项配置（用于radio、checkbox、select） -->
          <template v-if="['radio', 'checkbox', 'select'].includes(selectedField.type)">
            <el-divider>选项配置</el-divider>
            <div v-for="(opt, idx) in selectedField.options" :key="idx" class="option-item">
              <el-input v-model="opt.label" placeholder="显示值" size="small" />
              <el-input v-model="opt.value" placeholder="实际值" size="small" />
              <el-button type="danger" link @click="removeOption(idx)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button type="primary" link @click="addOption">
              <el-icon><Plus /></el-icon>添加选项
            </el-button>
          </template>
        </el-form>
      </div>
      <div v-else class="empty-property">
        <el-icon :size="32"><InfoFilled /></el-icon>
        <p>请选择组件进行配置</p>
      </div>
    </div>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="表单预览" width="600px">
      <form-renderer :form-config="formConfig" :preview="true" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable'
import FormRenderer from '../FormRenderer/index.vue'

const props = defineProps({
  initialConfig: {
    type: Object,
    default: () => ({
      fields: []
    })
  }
})

const emit = defineEmits(['save'])

// 组件列表
const componentList = [
  { type: 'input', label: '单行文本', icon: 'Edit' },
  { type: 'textarea', label: '多行文本', icon: 'Document' },
  { type: 'number', label: '数字输入', icon: 'Sort' },
  { type: 'radio', label: '单选框', icon: 'CircleCheck' },
  { type: 'checkbox', label: '多选框', icon: 'Check' },
  { type: 'select', label: '下拉选择', icon: 'ArrowDown' },
  { type: 'date', label: '日期选择', icon: 'Calendar' },
  { type: 'datetime', label: '日期时间', icon: 'Clock' },
  { type: 'switch', label: '开关', icon: 'SwitchButton' }
]

// 表单配置
const formConfig = reactive({
  fields: []
})

// 表单数据（用于预览）
const formData = reactive({})

// 当前选中的字段
const selectedField = ref(null)

// 预览对话框
const previewVisible = ref(false)

// 拖拽的组件类型
const dragType = ref(null)

// 初始化配置
watch(() => props.initialConfig, (newVal) => {
  if (newVal && newVal.fields) {
    formConfig.fields = JSON.parse(JSON.stringify(newVal.fields))
  }
}, { immediate: true })

// 开始拖拽
const handleDragStart = (item) => {
  dragType.value = item.type
}

// 放置组件
const handleDrop = () => {
  if (!dragType.value) return

  const type = dragType.value
  const field = {
    id: Date.now().toString(),
    type: type,
    field: `field_${formConfig.fields.length + 1}`,
    label: getDefaultLabel(type),
    placeholder: '',
    required: false,
    defaultValue: ''
  }

  // 添加选项（用于radio、checkbox、select）
  if (['radio', 'checkbox', 'select'].includes(type)) {
    field.options = [
      { label: '选项1', value: '1' },
      { label: '选项2', value: '2' }
    ]
  }

  formConfig.fields.push(field)
  selectedField.value = field
  dragType.value = null
}

// 获取默认标签
const getDefaultLabel = (type) => {
  const labels = {
    input: '文本字段',
    textarea: '多行文本',
    number: '数字字段',
    radio: '单选字段',
    checkbox: '多选字段',
    select: '下拉字段',
    date: '日期字段',
    datetime: '日期时间',
    switch: '开关字段'
  }
  return labels[type] || '字段'
}

// 拖拽结束
const handleDragEnd = () => {
  // 可以在这里添加排序后的处理逻辑
}

// 选择字段
const handleSelectField = (field) => {
  selectedField.value = field
}

// 删除字段
const handleDeleteField = (index) => {
  formConfig.fields.splice(index, 1)
  if (selectedField.value && !formConfig.fields.find(f => f.id === selectedField.value.id)) {
    selectedField.value = null
  }
}

// 添加选项
const addOption = () => {
  if (selectedField.value) {
    selectedField.value.options.push({
      label: `选项${selectedField.value.options.length + 1}`,
      value: String(selectedField.value.options.length + 1)
    })
  }
}

// 删除选项
const removeOption = (index) => {
  if (selectedField.value && selectedField.value.options) {
    selectedField.value.options.splice(index, 1)
  }
}

// 预览
const handlePreview = () => {
  if (formConfig.fields.length === 0) {
    ElMessage.warning('请先添加表单组件')
    return
  }
  previewVisible.value = true
}

// 保存
const handleSave = () => {
  if (formConfig.fields.length === 0) {
    ElMessage.warning('表单不能为空')
    return
  }

  // 验证字段配置
  for (const field of formConfig.fields) {
    if (!field.field) {
      ElMessage.error('字段标识不能为空')
      return
    }
    if (!field.label) {
      ElMessage.error('字段名称不能为空')
      return
    }
  }

  emit('save', JSON.parse(JSON.stringify(formConfig)))
}

// 清空
const handleClear = () => {
  ElMessageBox.confirm('确定要清空所有组件吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    formConfig.fields = []
    selectedField.value = null
    ElMessage.success('已清空')
  })
}

// 获取表单配置
const getFormConfig = () => {
  return JSON.parse(JSON.stringify(formConfig))
}

// 设置表单配置
const setFormConfig = (config) => {
  if (config && config.fields) {
    formConfig.fields = JSON.parse(JSON.stringify(config.fields))
  }
}

defineExpose({
  getFormConfig,
  setFormConfig
})
</script>

<style scoped lang="scss">
.form-designer {
  display: flex;
  height: calc(100vh - 200px);
  background: #f5f5f5;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.panel-title {
  padding: 12px 16px;
  font-weight: bold;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.component-panel {
  width: 200px;
  background: #fff;
  border-right: 1px solid #e4e7ed;

  .component-list {
    padding: 10px;

    .component-item {
      display: flex;
      align-items: center;
      padding: 10px;
      margin-bottom: 8px;
      background: #f5f7fa;
      border-radius: 4px;
      cursor: move;
      transition: all 0.3s;

      &:hover {
        background: #e6f7ff;
        border-color: #1890ff;
      }

      .el-icon {
        margin-right: 8px;
        font-size: 16px;
      }
    }
  }
}

.canvas-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;

  .toolbar {
    display: flex;
    gap: 8px;
  }

  .canvas-area {
    flex: 1;
    padding: 20px;
    overflow-y: auto;

    .designer-form {
      min-height: 100%;
    }

    .field-list {
      min-height: 400px;
    }

    .form-field {
      position: relative;
      padding: 12px;
      margin-bottom: 8px;
      background: #fafafa;
      border: 2px dashed #d9d9d9;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover,
      &.active {
        border-color: #1890ff;
        background: #e6f7ff;
      }

      .field-actions {
        position: absolute;
        top: 5px;
        right: 5px;
        display: flex;
        gap: 8px;
        opacity: 0;
        transition: opacity 0.3s;

        .drag-handle {
          cursor: move;
        }

        .delete-btn {
          color: #ff4d4f;
          cursor: pointer;

          &:hover {
            color: #ff7875;
          }
        }
      }

      &:hover .field-actions {
        opacity: 1;
      }
    }

    .empty-tip {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 400px;
      color: #999;

      .el-icon {
        margin-bottom: 16px;
      }
    }
  }
}

.property-panel {
  width: 280px;
  background: #fff;
  border-left: 1px solid #e4e7ed;

  .property-content {
    padding: 16px;

    .option-item {
      display: flex;
      gap: 8px;
      margin-bottom: 8px;

      .el-input {
        flex: 1;
      }
    }
  }

  .empty-property {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 300px;
    color: #999;

    .el-icon {
      margin-bottom: 8px;
    }
  }
}

.ghost {
  opacity: 0.5;
  background: #c6ebff;
}
</style>
