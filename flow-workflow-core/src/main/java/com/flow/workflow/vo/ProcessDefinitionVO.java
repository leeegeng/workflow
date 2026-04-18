package com.flow.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程定义VO
 */
@Data
public class ProcessDefinitionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流程定义ID
     */
    private String id;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程标识
     */
    private String key;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 分类
     */
    private String category;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否挂起
     */
    private Boolean suspended;

    /**
     * 部署ID
     */
    private String deploymentId;

    /**
     * 部署时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deploymentTime;

}
