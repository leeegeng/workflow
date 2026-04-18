<template>
  <div class="design-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程设计 - {{ modelName }}</span>
          <div class="header-actions">
            <el-button @click="$router.back()">返回</el-button>
            <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
          </div>
        </div>
      </template>

      <div class="designer-wrapper">
        <div ref="canvasRef" class="canvas"></div>
        <div ref="propertiesPanelRef" class="properties-panel"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import propertiesPanelModule from 'bpmn-js-properties-panel'
import propertiesProviderModule from 'bpmn-js-properties-panel/lib/provider/camunda'
import camundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda'
import { getModelBpmnXml, saveModelBpmnXml } from '@/api/model'

import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css'
import 'bpmn-js-properties-panel/dist/assets/bpmn-js-properties-panel.css'

const route = useRoute()
const router = useRouter()
const canvasRef = ref()
const propertiesPanelRef = ref()
const modelName = ref('')
const saveLoading = ref(false)

let bpmnModeler = null
const modelId = route.params.id

// 默认的BPMN XML
const defaultXml = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                  id="Definitions_1"
                  targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="Process_1" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
      <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">
        <dc:Bounds x="152" y="152" width="36" height="36"/>
      </bpmndi:BPMNShape>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`

const initBpmnModeler = () => {
  bpmnModeler = new BpmnModeler({
    container: canvasRef.value,
    propertiesPanel: {
      parent: propertiesPanelRef.value
    },
    additionalModules: [
      propertiesPanelModule,
      propertiesProviderModule
    ],
    moddleExtensions: {
      camunda: camundaModdleDescriptor
    }
  })
}

const loadBpmnXml = async () => {
  try {
    const res = await getModelBpmnXml(modelId)
    if (res.code === 200) {
      const xml = res.data || defaultXml
      await bpmnModeler.importXML(xml)
    }
  } catch (error) {
    console.error(error)
    // 如果加载失败，使用默认XML
    await bpmnModeler.importXML(defaultXml)
  }
}

const handleSave = async () => {
  try {
    saveLoading.value = true
    const { xml } = await bpmnModeler.saveXML({ format: true })
    const res = await saveModelBpmnXml(modelId, xml)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('保存失败')
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  initBpmnModeler()
  loadBpmnXml()
})

onUnmounted(() => {
  if (bpmnModeler) {
    bpmnModeler.destroy()
  }
})
</script>

<style scoped>
.design-container {
  height: calc(100vh - 140px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.designer-wrapper {
  display: flex;
  height: calc(100vh - 200px);
}

.canvas {
  flex: 1;
  border: 1px solid #e4e7ed;
}

.properties-panel {
  width: 300px;
  border-left: 1px solid #e4e7ed;
  background: #f5f5f5;
  overflow-y: auto;
}

:deep(.bpp-properties-panel) {
  background: #f5f5f5;
}
</style>
