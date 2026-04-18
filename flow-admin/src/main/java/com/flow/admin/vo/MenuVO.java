package com.flow.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单VO
 */
@Data
public class MenuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单类型??-目录??-菜单??-按钮??
     */
    private Integer menuType;

    /**
     * 菜单类型名称
     */
    private String menuTypeName;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 排序??
     */
    private Integer sortOrder;

    /**
     * 状态（0-禁用??-正常??
     */
    private Integer status;

    /**
     * 子菜单列??
     */
    private List<MenuVO> children;

}
