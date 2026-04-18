package com.flow.admin.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 表单DTO
 */
@Data
public class BpmFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表单ID
     */
    private Long id;

    /**
     * 表单名称
     */
    @NotBlank(message = "表单名称不能为空")
    private String formName;

    /**
     * 表单标识
     */
    @NotBlank(message = "表单标识不能为空")
    private String formKey;

    /**
     * 表单描述
     */
    private String description;

    /**
     * 分类
     */
    private String category;

    /**
     * 表单JSON配置
     */
    private String formJson;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

}
