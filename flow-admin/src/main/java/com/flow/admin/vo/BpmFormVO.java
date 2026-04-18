package com.flow.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 表单VO
 */
@Data
public class BpmFormVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表单ID
     */
    private Long id;

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
     * 状态（0-禁用??-正常??
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

    /**
     * 表单字段列表（解析formJson得到??
     */
    private List<FormFieldVO> fields;

    /**
     * 表单字段VO
     */
    @Data
    public static class FormFieldVO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 字段标识
         */
        private String field;

        /**
         * 字段名称
         */
        private String label;

        /**
         * 字段类型
         */
        private String type;

        /**
         * 是否必填
         */
        private Boolean required;

        /**
         * 默认??
         */
        private Object defaultValue;

        /**
         * 选项（用于select、radio等）
         */
        private List<OptionVO> options;

        /**
         * 验证规则
         */
        private List<RuleVO> rules;
    }

    /**
     * 选项VO
     */
    @Data
    public static class OptionVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String label;
        private Object value;
    }

    /**
     * 验证规则VO
     */
    @Data
    public static class RuleVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String type;
        private String message;
        private Object value;
    }

}
