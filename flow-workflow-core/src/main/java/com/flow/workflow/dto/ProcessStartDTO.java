package com.flow.workflow.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 流程启动DTO
 */
@Data
public class ProcessStartDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流程定义Key
     */
    @NotBlank(message = "流程定义不能为空")
    private String processDefinitionKey;

    /**
     * 业务主键
     */
    private String businessKey;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 发起人ID
     */
    @NotNull(message = "发起人不能为空")
    private Long startUserId;

    /**
     * 表单数据
     */
    private Map<String, Object> formData;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;

}
