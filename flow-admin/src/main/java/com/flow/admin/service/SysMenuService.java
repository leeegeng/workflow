package com.flow.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flow.admin.dto.MenuDTO;
import com.flow.admin.entity.SysMenu;
import com.flow.admin.vo.MenuVO;
import com.flow.common.result.Result;

import java.util.List;

/**
 * 菜单Service接口
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单树
     */
    Result<List<MenuVO>> getMenuTree();

    /**
     * 获取菜单详情
     */
    Result<MenuVO> getMenuById(Long id);

    /**
     * 新增菜单
     */
    Result<Void> addMenu(MenuDTO menuDTO);

    /**
     * 修改菜单
     */
    Result<Void> updateMenu(MenuDTO menuDTO);

    /**
     * 删除菜单
     */
    Result<Void> deleteMenu(Long id);

    /**
     * 获取用户的菜单列表
     */
    Result<List<MenuVO>> getMenusByUserId(Long userId);

}
