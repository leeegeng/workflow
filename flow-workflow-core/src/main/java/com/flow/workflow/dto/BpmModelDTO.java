package com.flow.workflow.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 流程模型DTO
 */
@Data
public class BpmModelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    /**
     * 模型标识
     */
    @NotBlank(message = "模型标识不能为空")
    private String modelKey;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 分类
     */
    private String category;

    /**
     * 表单ID
     */
    private Long formId;

}
