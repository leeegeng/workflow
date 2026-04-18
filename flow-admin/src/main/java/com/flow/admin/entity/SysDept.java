package com.flow.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flow.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 负责人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String leaderName;

    /**
     * 子部门列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysDept> children;

}
