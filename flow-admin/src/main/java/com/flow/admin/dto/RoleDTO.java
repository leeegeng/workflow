package com.flow.admin.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 角色DTO
 */
@Data
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态（0-禁用，1-正常）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;

}
