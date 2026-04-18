package com.flow.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flow.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程模型扩展实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_model")
public class BpmModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Flowable模型ID
     */
    private String modelId;

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
     * 关联表单ID
     */
    private Long formId;

    /**
     * 状态（0-草稿，1-已部署）
     */
    private Integer status;

}
