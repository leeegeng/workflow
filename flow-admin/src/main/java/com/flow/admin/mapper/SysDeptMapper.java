package com.flow.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flow.admin.entity.SysDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门Mapper
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门树
     */
    @Select("SELECT d.*, u.real_name as leader_name FROM sys_dept d " +
            "LEFT JOIN sys_user u ON d.leader_id = u.id " +
            "WHERE d.deleted = 0 ORDER BY d.sort_order")
    List<SysDept> selectDeptTree();

    /**
     * 根据父ID查询子部门
     */
    @Select("SELECT * FROM sys_dept WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order")
    List<SysDept> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 检查部门下是否有用户
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE dept_id = #{deptId} AND deleted = 0")
    int countUserByDeptId(@Param("deptId") Long deptId);

}
