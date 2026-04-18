package com.flow.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程模型VO
 */
@Data
public class BpmModelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模型ID
     */
    private String id;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型标识
     */
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
     * 版本??
     */
    private Integer version;

    /**
     * 状态（0-草稿??-已部署）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
