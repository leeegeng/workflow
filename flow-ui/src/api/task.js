import request from '@/utils/request'

// 获取待办任务列表
export function getTodoList() {
  return request({
    url: '/workflow/task/todo',
    method: 'get'
  })
}

// 获取已办任务列表
export function getDoneList() {
  return request({
    url: '/workflow/task/done',
    method: 'get'
  })
}

// 获取任务详情
export function getTaskById(id) {
  return request({
    url: `/workflow/task/${id}`,
    method: 'get'
  })
}

// 完成任务
export function completeTask(data) {
  return request({
    url: '/workflow/task/complete',
    method: 'post',
    data
  })
}

// 委托任务
export function delegateTask(taskId, delegateToUserId) {
  return request({
    url: `/workflow/task/${taskId}/delegate`,
    method: 'post',
    params: { delegateToUserId }
  })
}

// 转办任务
export function transferTask(taskId, transferToUserId) {
  return request({
    url: `/workflow/task/${taskId}/transfer`,
    method: 'post',
    params: { transferToUserId }
  })
}

// 驳回任务
export function rejectTask(taskId, comment) {
  return request({
    url: `/workflow/task/${taskId}/reject`,
    method: 'post',
    params: { comment }
  })
}

// 退回上一步
export function backTask(taskId, comment) {
  return request({
    url: `/workflow/task/${taskId}/back`,
    method: 'post',
    params: { comment }
  })
}
