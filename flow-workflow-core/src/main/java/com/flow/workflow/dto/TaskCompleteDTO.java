package com.flow.workflow.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 任务完成DTO
 */
@Data
public class TaskCompleteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 处理意见
     */
    private String comment;

    /**
     * 处理结果??-同意??-拒绝??
     */
    private Integer result;

    /**
     * 表单数据
     */
    private Map<String, Object> formData;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;

}
