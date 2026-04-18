import request from '@/utils/request'

// 获取表单列表
export function getFormList() {
  return request({
    url: '/workflow/form/list',
    method: 'get'
  })
}

// 获取表单详情
export function getFormById(id) {
  return request({
    url: `/workflow/form/${id}`,
    method: 'get'
  })
}

// 根据表单标识获取表单
export function getFormByKey(formKey) {
  return request({
    url: `/workflow/form/key/${formKey}`,
    method: 'get'
  })
}

// 创建表单
export function createForm(data) {
  return request({
    url: '/workflow/form',
    method: 'post',
    data
  })
}

// 更新表单
export function updateForm(id, data) {
  return request({
    url: `/workflow/form/${id}`,
    method: 'put',
    data
  })
}

// 删除表单
export function deleteForm(id) {
  return request({
    url: `/workflow/form/${id}`,
    method: 'delete'
  })
}

// 获取表单渲染数据
export function getFormRenderData(id) {
  return request({
    url: `/workflow/form/${id}/render`,
    method: 'get'
  })
}

// 验证表单数据
export function validateForm(id, data) {
  return request({
    url: `/workflow/form/${id}/validate`,
    method: 'post',
    data
  })
}
