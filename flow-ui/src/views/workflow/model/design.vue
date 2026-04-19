<template>
  <div class="design-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程设计 - {{ modelName }}</span>
          <div class="header-actions">
            <el-button @click="$router.back()">返回</el-button>
            <el-button type="info" @click="showTemplateDialog = true">加载模板</el-button>
            <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
          </div>
        </div>
      </template>

      <div class="designer-wrapper">
        <div ref="canvasRef" class="canvas"></div>
        <div ref="propertiesPanelRef" class="properties-panel"></div>
      </div>
    </el-card>

    <!-- 模板选择对话框 -->
    <el-dialog v-model="showTemplateDialog" title="选择流程模板" width="600px">
      <el-row :gutter="20">
        <el-col :span="12" v-for="template in templates" :key="template.key">
          <el-card class="template-card" shadow="hover" @click="loadTemplate(template)">
            <div class="template-icon">
              <el-icon :size="40"><component :is="template.icon" /></el-icon>
            </div>
            <div class="template-name">{{ template.name }}</div>
            <div class="template-desc">{{ template.description }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-dialog>
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
import { Timer, Document, Money, User } from '@element-plus/icons-vue'

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
const showTemplateDialog = ref(false)

let bpmnModeler = null
const modelId = route.params.id

// 流程模板定义
const templates = [
  {
    key: 'leave',
    name: '请假流程',
    description: '员工请假审批流程，包含部门经理和总经理审批',
    icon: Timer,
    xml: `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                  xmlns:flowable="http://flowable.org/bpmn"
                  id="Definitions_leave"
                  targetNamespace="http://flowable.org/examples">
  <bpmn:process id="leave" name="请假流程" isExecutable="true">
    <bpmn:startEvent id="start" name="开始">
      <bpmn:outgoing>flow1</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="submit-leave" name="提交请假申请" flowable:assignee="\${startUserId}">
      <bpmn:incoming>flow1</bpmn:incoming>
      <bpmn:outgoing>flow2</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="manager-approval" name="部门经理审批" flowable:candidateGroups="ROLE_MANAGER">
      <bpmn:incoming>flow2</bpmn:incoming>
      <bpmn:outgoing>flow3</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="gm-approval" name="总经理审批" flowable:candidateGroups="ROLE_GM">
      <bpmn:incoming>flow3</bpmn:incoming>
      <bpmn:outgoing>flow4</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="hr-record" name="人事备案" flowable:candidateGroups="ROLE_HR">
      <bpmn:incoming>flow4</bpmn:incoming>
      <bpmn:outgoing>flow5</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:endEvent id="end" name="结束">
      <bpmn:incoming>flow5</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="flow1" sourceRef="start" targetRef="submit-leave"/>
    <bpmn:sequenceFlow id="flow2" sourceRef="submit-leave" targetRef="manager-approval"/>
    <bpmn:sequenceFlow id="flow3" sourceRef="manager-approval" targetRef="gm-approval"/>
    <bpmn:sequenceFlow id="flow4" sourceRef="gm-approval" targetRef="hr-record"/>
    <bpmn:sequenceFlow id="flow5" sourceRef="hr-record" targetRef="end"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_leave">
    <bpmndi:BPMNPlane id="BPMNPlane_leave" bpmnElement="leave">
      <bpmndi:BPMNShape id="Shape_start" bpmnElement="start">
        <dc:Bounds x="100" y="100" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_submit-leave" bpmnElement="submit-leave">
        <dc:Bounds x="200" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_manager-approval" bpmnElement="manager-approval">
        <dc:Bounds x="350" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_gm-approval" bpmnElement="gm-approval">
        <dc:Bounds x="500" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_hr-record" bpmnElement="hr-record">
        <dc:Bounds x="650" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_end" bpmnElement="end">
        <dc:Bounds x="800" y="100" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="Edge_flow1" bpmnElement="flow1">
        <di:waypoint x="136" y="118"/>
        <di:waypoint x="200" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow2" bpmnElement="flow2">
        <di:waypoint x="300" y="120"/>
        <di:waypoint x="350" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow3" bpmnElement="flow3">
        <di:waypoint x="450" y="120"/>
        <di:waypoint x="500" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow4" bpmnElement="flow4">
        <di:waypoint x="600" y="120"/>
        <di:waypoint x="650" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow5" bpmnElement="flow5">
        <di:waypoint x="750" y="120"/>
        <di:waypoint x="800" y="118"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`
  },
  {
    key: 'expense',
    name: '报销流程',
    description: '费用报销审批流程，根据金额自动判断审批层级',
    icon: Money,
    xml: `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                  xmlns:flowable="http://flowable.org/bpmn"
                  id="Definitions_expense"
                  targetNamespace="http://flowable.org/examples">
  <bpmn:process id="expense" name="报销流程" isExecutable="true">
    <bpmn:startEvent id="start" name="开始">
      <bpmn:outgoing>flow1</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="submit-expense" name="提交报销申请" flowable:assignee="\${startUserId}">
      <bpmn:incoming>flow1</bpmn:incoming>
      <bpmn:outgoing>flow2</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="dept-approval" name="部门经理审批" flowable:candidateGroups="ROLE_MANAGER">
      <bpmn:incoming>flow2</bpmn:incoming>
      <bpmn:outgoing>flow3</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="finance-approval" name="财务审批" flowable:candidateGroups="ROLE_FINANCE">
      <bpmn:incoming>flow3</bpmn:incoming>
      <bpmn:outgoing>flow4</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:endEvent id="end" name="结束">
      <bpmn:incoming>flow4</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="flow1" sourceRef="start" targetRef="submit-expense"/>
    <bpmn:sequenceFlow id="flow2" sourceRef="submit-expense" targetRef="dept-approval"/>
    <bpmn:sequenceFlow id="flow3" sourceRef="dept-approval" targetRef="finance-approval"/>
    <bpmn:sequenceFlow id="flow4" sourceRef="finance-approval" targetRef="end"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_expense">
    <bpmndi:BPMNPlane id="BPMNPlane_expense" bpmnElement="expense">
      <bpmndi:BPMNShape id="Shape_start" bpmnElement="start">
        <dc:Bounds x="100" y="100" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_submit-expense" bpmnElement="submit-expense">
        <dc:Bounds x="200" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_dept-approval" bpmnElement="dept-approval">
        <dc:Bounds x="350" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_finance-approval" bpmnElement="finance-approval">
        <dc:Bounds x="500" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_end" bpmnElement="end">
        <dc:Bounds x="650" y="100" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="Edge_flow1" bpmnElement="flow1">
        <di:waypoint x="136" y="118"/>
        <di:waypoint x="200" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow2" bpmnElement="flow2">
        <di:waypoint x="300" y="120"/>
        <di:waypoint x="350" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow3" bpmnElement="flow3">
        <di:waypoint x="450" y="120"/>
        <di:waypoint x="500" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow4" bpmnElement="flow4">
        <di:waypoint x="600" y="120"/>
        <di:waypoint x="650" y="118"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`
  },
  {
    key: 'simple',
    name: '简单审批',
    description: '单级审批流程，适用于简单的审批场景',
    icon: Document,
    xml: `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                  xmlns:flowable="http://flowable.org/bpmn"
                  id="Definitions_simple"
                  targetNamespace="http://flowable.org/examples">
  <bpmn:process id="simple" name="简单审批" isExecutable="true">
    <bpmn:startEvent id="start" name="开始">
      <bpmn:outgoing>flow1</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:userTask id="submit" name="提交申请" flowable:assignee="\${startUserId}">
      <bpmn:incoming>flow1</bpmn:incoming>
      <bpmn:outgoing>flow2</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:userTask id="approve" name="审批" flowable:candidateGroups="ROLE_MANAGER">
      <bpmn:incoming>flow2</bpmn:incoming>
      <bpmn:outgoing>flow3</bpmn:outgoing>
    </bpmn:userTask>
    <bpmn:endEvent id="end" name="结束">
      <bpmn:incoming>flow3</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="flow1" sourceRef="start" targetRef="submit"/>
    <bpmn:sequenceFlow id="flow2" sourceRef="submit" targetRef="approve"/>
    <bpmn:sequenceFlow id="flow3" sourceRef="approve" targetRef="end"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_simple">
    <bpmndi:BPMNPlane id="BPMNPlane_simple" bpmnElement="simple">
      <bpmndi:BPMNShape id="Shape_start" bpmnElement="start">
        <dc:Bounds x="100" y="100" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_submit" bpmnElement="submit">
        <dc:Bounds x="200" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_approve" bpmnElement="approve">
        <dc:Bounds x="350" y="80" width="100" height="80"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Shape_end" bpmnElement="end">
        <dc:Bounds x="500" y="100" width="36" height="36"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="Edge_flow1" bpmnElement="flow1">
        <di:waypoint x="136" y="118"/>
        <di:waypoint x="200" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow2" bpmnElement="flow2">
        <di:waypoint x="300" y="120"/>
        <di:waypoint x="350" y="120"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Edge_flow3" bpmnElement="flow3">
        <di:waypoint x="450" y="120"/>
        <di:waypoint x="500" y="118"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`
  }
]

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
      let xml = res.data || defaultXml
      // 将 flowable 命名空间转换为 camunda 以便属性面板识别
      xml = convertFlowableToCamunda(xml)
      await bpmnModeler.importXML(xml)
    }
  } catch (error) {
    console.error(error)
    // 如果加载失败，使用默认XML
    await bpmnModeler.importXML(defaultXml)
  }
}

// 将 flowable 命名空间转换为 camunda
const convertFlowableToCamunda = (xml) => {
  return xml
    .replace(/xmlns:flowable="http:\/\/flowable.org\/bpmn"/g, 'xmlns:camunda="http://camunda.org/schema/1.0/bpmn"')
    .replace(/flowable:/g, 'camunda:')
}

// 将 camunda 命名空间转换为 flowable
const convertCamundaToFlowable = (xml) => {
  return xml
    .replace(/xmlns:camunda="http:\/\/camunda.org\/schema\/1.0\/bpmn"/g, 'xmlns:flowable="http://flowable.org/bpmn"')
    .replace(/camunda:/g, 'flowable:')
}

const handleSave = async () => {
  try {
    saveLoading.value = true
    const { xml } = await bpmnModeler.saveXML({ format: true })
    // 将 camunda 命名空间转换为 flowable 保存
    const flowableXml = convertCamundaToFlowable(xml)
    const res = await saveModelBpmnXml(modelId, flowableXml)
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

// 加载模板
const loadTemplate = async (template) => {
  try {
    // 将 flowable 命名空间转换为 camunda 以便属性面板识别
    let camundaXml = template.xml
      .replace(/xmlns:flowable="http:\/\/flowable.org\/bpmn"/g, 'xmlns:camunda="http://camunda.org/schema/1.0/bpmn"')
      .replace(/flowable:/g, 'camunda:')
    await bpmnModeler.importXML(camundaXml)
    showTemplateDialog.value = false
    ElMessage.success(`已加载模板: ${template.name}`)
  } catch (error) {
    console.error(error)
    ElMessage.error('加载模板失败')
  }
}

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

.template-card {
  cursor: pointer;
  margin-bottom: 20px;
  text-align: center;
  transition: all 0.3s;
}

.template-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.template-icon {
  margin-bottom: 10px;
  color: #409eff;
}

.template-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.template-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
