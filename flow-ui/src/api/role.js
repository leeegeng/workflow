import request from '@/utils/request'

// 获取角色列表
export function getRoleList() {
  return request({
    url: '/system/role/list',
    method: 'get'
  })
}

// 获取角色详情
export function getRoleById(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(id, data) {
  return request({
    url: `/system/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}

// 分配角色菜单
export function assignRoleMenus(id, menuIds) {
  return request({
    url: `/system/role/${id}/menus`,
    method: 'post',
    data: menuIds,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}
