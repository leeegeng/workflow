import request from '@/utils/request'

// 获取我的发起列表
export function getMyStartedList() {
  return request({
    url: '/workflow/instance/my-started',
    method: 'get'
  })
}

// 启动流程实例
export function startProcessInstance(data) {
  return request({
    url: '/workflow/instance/start',
    method: 'post',
    data
  })
}

// 获取流程实例详情
export function getProcessInstanceById(processInstanceId) {
  return request({
    url: `/workflow/instance/${processInstanceId}`,
    method: 'get'
  })
}

// 终止流程实例
export function terminateProcessInstance(processInstanceId, reason) {
  return request({
    url: `/workflow/instance/${processInstanceId}/terminate`,
    method: 'post',
    params: { reason }
  })
}

// 删除流程实例
export function deleteProcessInstance(processInstanceId, reason) {
  return request({
    url: `/workflow/instance/${processInstanceId}`,
    method: 'delete',
    params: { reason }
  })
}
