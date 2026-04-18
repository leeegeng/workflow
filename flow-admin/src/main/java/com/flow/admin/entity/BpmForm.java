package com.flow.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flow.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bpm_form")
public class BpmForm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单标识
     */
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
