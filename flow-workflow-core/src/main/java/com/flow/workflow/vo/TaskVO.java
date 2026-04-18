package com.flow.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务VO
 */
@Data
public class TaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务定义Key
     */
    private String taskDefKey;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

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
     * 办理人ID
     */
    private Long assigneeId;

    /**
     * 办理人姓??
     */
    private String assigneeName;

    /**
     * 发起人ID
     */
    private Long startUserId;

    /**
     * 发起人姓??
     */
    private String startUserName;

    /**
     * 状态（0-待处理，1-已处理，2-已委托）
     */
    private Integer status;

    /**
     * 处理结果??-同意??-拒绝??-退回）
     */
    private Integer result;

    /**
     * 处理意见
     */
    private String comment;

    /**
     * 表单数据
     */
    private Map<String, Object> formData;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
