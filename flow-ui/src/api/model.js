import request from '@/utils/request'

// 获取模型列表
export function getModelList() {
  return request({
    url: '/workflow/model/list',
    method: 'get'
  })
}

// 获取模型详情
export function getModelById(id) {
  return request({
    url: `/workflow/model/${id}`,
    method: 'get'
  })
}

// 创建模型
export function createModel(data) {
  return request({
    url: '/workflow/model',
    method: 'post',
    data
  })
}

// 更新模型
export function updateModel(id, data) {
  return request({
    url: `/workflow/model/${id}`,
    method: 'put',
    data
  })
}

// 删除模型
export function deleteModel(id) {
  return request({
    url: `/workflow/model/${id}`,
    method: 'delete'
  })
}

// 部署模型
export function deployModel(id) {
  return request({
    url: `/workflow/model/${id}/deploy`,
    method: 'post'
  })
}

// 获取模型BPMN XML
export function getModelBpmnXml(id) {
  return request({
    url: `/workflow/model/${id}/bpmn`,
    method: 'get'
  })
}

// 保存模型BPMN XML
export function saveModelBpmnXml(id, xml) {
  return request({
    url: `/workflow/model/${id}/bpmn`,
    method: 'post',
    data: xml,
    headers: {
      'Content-Type': 'text/plain'
    }
  })
}
