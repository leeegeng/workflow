package com.flow.admin.controller;

import com.flow.admin.dto.MenuDTO;
import com.flow.admin.service.SysMenuService;
import com.flow.admin.vo.MenuVO;
import com.flow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private SysMenuService menuService;

    /**
     * 获取菜单树
     */
    @GetMapping("/tree")
    public Result<List<MenuVO>> tree() {
        return menuService.getMenuTree();
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    public Result<MenuVO> getById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Void> add(@Validated @RequestBody MenuDTO menuDTO) {
        return menuService.addMenu(menuDTO);
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> update(@Validated @RequestBody MenuDTO menuDTO) {
        return menuService.updateMenu(menuDTO);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        return menuService.deleteMenu(id);
    }

    /**
     * 获取当前用户的菜单
     */
    @GetMapping("/user")
    public Result<List<MenuVO>> getUserMenus(@RequestParam Long userId) {
        return menuService.getMenusByUserId(userId);
    }

}
