package com.flow.admin.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 菜单DTO
 */
@Data
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /**
     * 父菜单ID
     */
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;

    /**
     * 菜单类型（0-目录 1-菜单 2-按钮）
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

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
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

}
