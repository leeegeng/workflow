package com.flow.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flow.admin.dto.MenuDTO;
import com.flow.admin.entity.SysMenu;
import com.flow.admin.mapper.SysMenuMapper;
import com.flow.admin.service.SysMenuService;
import com.flow.admin.vo.MenuVO;
import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单Service实现
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public Result<List<MenuVO>> getMenuTree() {
        List<SysMenu> menuList = baseMapper.selectMenuTree();
        List<MenuVO> voList = buildMenuTree(menuList);
        return Result.success(voList);
    }

    @Override
    public Result<MenuVO> getMenuById(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        return Result.success(convertToVO(menu));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addMenu(MenuDTO menuDTO) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(menuDTO, menu);

        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }

        save(menu);
        log.info("新增菜单成功: {}", menu.getMenuName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateMenu(MenuDTO menuDTO) {
        SysMenu menu = getById(menuDTO.getId());
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 检查不能将自己设为父菜单
        if (menuDTO.getParentId() != null && menuDTO.getParentId().equals(menuDTO.getId())) {
            throw new BusinessException("不能将自己设为上级菜单");
        }

        BeanUtils.copyProperties(menuDTO, menu);
        updateById(menu);

        log.info("更新菜单成功: {}", menu.getMenuName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteMenu(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 检查是否有子菜单
        List<SysMenu> children = baseMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("该菜单下存在子菜单，不能删除");
        }

        removeById(id);
        log.info("删除菜单成功: {}", menu.getMenuName());
        return Result.success();
    }

    @Override
    public Result<List<MenuVO>> getMenusByUserId(Long userId) {
        List<SysMenu> menuList = baseMapper.selectMenusByUserId(userId);
        List<MenuVO> voList = buildMenuTree(menuList);
        return Result.success(voList);
    }

    /**
     * 构建菜单树
     */
    private List<MenuVO> buildMenuTree(List<SysMenu> menuList) {
        Map<Long, MenuVO> voMap = menuList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toMap(MenuVO::getId, vo -> vo));

        List<MenuVO> tree = new ArrayList<>();
        for (MenuVO vo : voMap.values()) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                tree.add(vo);
            } else {
                MenuVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }
        return tree;
    }

    /**
     * 转换为VO
     */
    private MenuVO convertToVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);

        // 设置菜单类型名称
        String[] typeNames = {"", "目录", "菜单", "按钮"};
        if (menu.getMenuType() != null && menu.getMenuType() >= 1 && menu.getMenuType() <= 3) {
            vo.setMenuTypeName(typeNames[menu.getMenuType()]);
        }

        return vo;
    }
}
