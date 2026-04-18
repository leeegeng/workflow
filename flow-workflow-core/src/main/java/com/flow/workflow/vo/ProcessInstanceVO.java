package com.flow.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 流程实例VO
 */
@Data
public class ProcessInstanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

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
    private Long startUserId;

    /**
     * 发起人姓??
     */
    private String startUserName;

    /**
     * 状态（1-运行中，2-已完成，3-已终止）
     */
    private Integer status;

    /**
     * 表单数据
     */
    private Map<String, Object> formData;

    /**
     * 开始时??
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 持续时间（毫秒）
     */
    private Long duration;

}
