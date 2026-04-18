import request from '@/utils/request'

// 获取流程监控列表
export function getMonitorList() {
  return request({
    url: '/workflow/monitor/list',
    method: 'get'
  })
}

// 获取流程实例详情
export function getInstanceDetail(processInstanceId) {
  return request({
    url: `/workflow/monitor/instance/${processInstanceId}`,
    method: 'get'
  })
}

// 获取流程统计信息
export function getStatistics() {
  return request({
    url: '/workflow/monitor/statistics',
    method: 'get'
  })
}

// 获取流程图高亮数据
export function getHighlightData(processInstanceId) {
  return request({
    url: `/workflow/monitor/instance/${processInstanceId}/highlight`,
    method: 'get'
  })
}

// 获取流程执行历史
export function getHistory(processInstanceId) {
  return request({
    url: `/workflow/monitor/instance/${processInstanceId}/history`,
    method: 'get'
  })
}

// 获取节点耗时统计
export function getNodeStatistics(processDefinitionKey) {
  return request({
    url: `/workflow/monitor/statistics/node/${processDefinitionKey}`,
    method: 'get'
  })
}

// 强制终止流程实例
export function terminateInstance(processInstanceId, reason) {
  return request({
    url: `/workflow/monitor/instance/${processInstanceId}/terminate`,
    method: 'post',
    params: { reason }
  })
}

// 批量终止流程实例
export function batchTerminate(processInstanceIds, reason) {
  return request({
    url: '/workflow/monitor/batch-terminate',
    method: 'post',
    data: processInstanceIds,
    params: { reason },
    headers: {
      'Content-Type': 'application/json'
    }
  })
}
