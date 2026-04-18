package com.flow.admin.controller;

import com.flow.admin.dto.RoleDTO;
import com.flow.admin.service.SysRoleService;
import com.flow.admin.vo.RoleVO;
import com.flow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private SysRoleService roleService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<List<RoleVO>> list() {
        return roleService.listRoles();
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:detail')")
    public Result<RoleVO> getById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Void> add(@Validated @RequestBody RoleDTO roleDTO) {
        return roleService.addRole(roleDTO);
    }

    /**
     * 修改角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        return roleService.updateRole(roleDTO);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }

    /**
     * 分配角色菜单
     */
    @PostMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:assignMenu')")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        return roleService.assignRoleMenus(id, menuIds);
    }

}
