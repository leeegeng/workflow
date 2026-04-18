package com.flow.admin.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 部门DTO
 */
@Data
public class DeptDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 父部门ID
     */
    @NotNull(message = "上级部门不能为空")
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 负责人ID
     */
    private Long leaderId;

}
