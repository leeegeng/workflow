import request from '@/utils/request'

// 获取用户列表
export function getUserList() {
  return request({
    url: '/system/user/list',
    method: 'get'
  })
}

// 获取用户详情
export function getUserById(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

// 修改用户
export function updateUser(id, data) {
  return request({
    url: `/system/user/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id, newPassword) {
  return request({
    url: `/system/user/${id}/reset-password`,
    method: 'post',
    params: { newPassword }
  })
}
