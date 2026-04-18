<template>
  <div class="process-diagram">
    <div ref="canvasRef" class="diagram-canvas"></div>
    <div v-if="loading" class="loading-mask">
      <el-icon class="loading-icon"><Loading /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import BpmnViewer from 'bpmn-js/lib/Viewer'
import { getHighlightData } from '@/api/monitor'
import { getDefinitionXml } from '@/api/definition'
import { ElMessage } from 'element-plus'

const props = defineProps({
  processInstanceId: {
    type: String,
    default: ''
  },
  processDefinitionId: {
    type: String,
    default: ''
  }
})

const canvasRef = ref()
const loading = ref(false)
let viewer = null

// 初始化BPMN查看器
const initViewer = () => {
  viewer = new BpmnViewer({
    container: canvasRef.value
  })
}

// 加载流程图
const loadDiagram = async () => {
  if (!props.processDefinitionId) return

  loading.value = true
  try {
    // 获取流程定义XML
    const res = await getDefinitionXml(props.processDefinitionId)
    if (res.code !== 200) {
      ElMessage.error(res.message || '获取流程定义XML失败')
      return
    }

    // 渲染流程图
    await viewer.importXML(res.data)

    // 获取高亮数据
    if (props.processInstanceId) {
      const highlightRes = await getHighlightData(props.processInstanceId)
      if (highlightRes.code === 200) {
        highlightDiagram(highlightRes.data)
      }
    }

    // 自适应画布
    const canvas = viewer.get('canvas')
    canvas.zoom('fit-viewport')
  } catch (error) {
    console.error('加载流程图失败:', error)
    ElMessage.error('加载流程图失败')
  } finally {
    loading.value = false
  }
}

// 高亮显示流程节点
const highlightDiagram = (highlightData) => {
  if (!viewer) return

  const canvas = viewer.get('canvas')
  const elementRegistry = viewer.get('elementRegistry')

  // 高亮已执行的节点
  if (highlightData.executedActivityIds) {
    highlightData.executedActivityIds.forEach(activityId => {
      const element = elementRegistry.get(activityId)
      if (element) {
        canvas.addMarker(activityId, 'completed')
      }
    })
  }

  // 高亮当前活动节点
  if (highlightData.activeActivityIds) {
    highlightData.activeActivityIds.forEach(activityId => {
      const element = elementRegistry.get(activityId)
      if (element) {
        canvas.addMarker(activityId, 'active')
      }
    })
  }
}

onMounted(() => {
  initViewer()
  loadDiagram()
})

onUnmounted(() => {
  if (viewer) {
    viewer.destroy()
  }
})

watch(() => props.processInstanceId, loadDiagram)
watch(() => props.processDefinitionId, loadDiagram)
</script>

<style scoped>
.process-diagram {
  position: relative;
  height: 500px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.diagram-canvas {
  width: 100%;
  height: 100%;
}

.loading-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-icon {
  font-size: 32px;
  color: #409eff;
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* BPMN高亮样式 */
:deep(.completed:not(.djs-connection)) {
  fill: #67c23a !important;
  stroke: #67c23a !important;
  fill-opacity: 0.2 !important;
}

:deep(.active:not(.djs-connection)) {
  fill: #409eff !important;
  stroke: #409eff !important;
  fill-opacity: 0.3 !important;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    fill-opacity: 0.3;
  }
  50% {
    fill-opacity: 0.6;
  }
  100% {
    fill-opacity: 0.3;
  }
}
</style>
