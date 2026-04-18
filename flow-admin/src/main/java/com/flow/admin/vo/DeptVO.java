package com.flow.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 部门VO
 */
@Data
public class DeptVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 排序??
     */
    private Integer sortOrder;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 负责人姓??
     */
    private String leaderName;

    /**
     * 子部门列??
     */
    private List<DeptVO> children;

}
