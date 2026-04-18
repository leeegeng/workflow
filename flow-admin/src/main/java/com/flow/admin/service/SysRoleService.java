package com.flow.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flow.admin.dto.RoleDTO;
import com.flow.admin.entity.SysRole;
import com.flow.admin.vo.RoleVO;
import com.flow.common.result.Result;

import java.util.List;

/**
 * 角色Service接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取角色列表
     */
    Result<List<RoleVO>> listRoles();

    /**
     * 获取角色详情
     */
    Result<RoleVO> getRoleById(Long id);

    /**
     * 新增角色
     */
    Result<Void> addRole(RoleDTO roleDTO);

    /**
     * 修改角色
     */
    Result<Void> updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     */
    Result<Void> deleteRole(Long id);

    /**
     * 分配角色菜单
     */
    Result<Void> assignRoleMenus(Long roleId, List<Long> menuIds);

}
