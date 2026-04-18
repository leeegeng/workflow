package com.flow.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flow.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单与流程模型关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_model_form")
public class BpmModelForm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 流程模型ID
     */
    private String modelId;

    /**
     * 表单ID
     */
    private Long formId;

    /**
     * 流程节点标识（为空表示全局表单）
     */
    private String nodeKey;

    /**
     * 表单类型（0-全局表单 1-节点表单）
     */
    private Integer formType;

}
