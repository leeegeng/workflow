import request from '@/utils/request'

// 获取流程发起统计
export function getProcessStartStatistics(startTime, endTime) {
  return request({
    url: '/workflow/report/process-start',
    method: 'get',
    params: { startTime, endTime }
  })
}

// 获取任务处理效率统计
export function getTaskEfficiencyStatistics(startTime, endTime) {
  return request({
    url: '/workflow/report/task-efficiency',
    method: 'get',
    params: { startTime, endTime }
  })
}

// 获取个人工作量统计
export function getPersonalWorkloadStatistics(startTime, endTime) {
  return request({
    url: '/workflow/report/personal-workload',
    method: 'get',
    params: { startTime, endTime }
  })
}

// 获取流程耗时统计
export function getProcessDurationStatistics(startTime, endTime) {
  return request({
    url: '/workflow/report/process-duration',
    method: 'get',
    params: { startTime, endTime }
  })
}

// 获取流程状态分布
export function getProcessStatusDistribution() {
  return request({
    url: '/workflow/report/status-distribution',
    method: 'get'
  })
}

// 获取月度流程趋势
export function getMonthlyProcessTrend(months) {
  return request({
    url: '/workflow/report/monthly-trend',
    method: 'get',
    params: { months }
  })
}
