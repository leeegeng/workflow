import request from '@/utils/request'

// 获取流程定义列表
export function getDefinitionList() {
  return request({
    url: '/workflow/definition/list',
    method: 'get'
  })
}

// 获取流程定义详情
export function getDefinitionById(definitionId) {
  return request({
    url: `/workflow/definition/${definitionId}`,
    method: 'get'
  })
}

// 激活流程定义
export function activateDefinition(definitionId) {
  return request({
    url: `/workflow/definition/${definitionId}/activate`,
    method: 'post'
  })
}

// 挂起流程定义
export function suspendDefinition(definitionId) {
  return request({
    url: `/workflow/definition/${definitionId}/suspend`,
    method: 'post'
  })
}

// 获取流程定义XML
export function getDefinitionXml(definitionId) {
  return request({
    url: `/workflow/definition/${definitionId}/xml`,
    method: 'get'
  })
}

// 删除流程定义
export function deleteDefinition(definitionId) {
  return request({
    url: `/workflow/definition/${definitionId}`,
    method: 'delete'
  })
}
