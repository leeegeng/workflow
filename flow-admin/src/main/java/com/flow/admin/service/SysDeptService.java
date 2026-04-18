package com.flow.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flow.admin.dto.DeptDTO;
import com.flow.admin.entity.SysDept;
import com.flow.admin.vo.DeptVO;
import com.flow.common.result.Result;

import java.util.List;

/**
 * 部门Service接口
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 获取部门树
     */
    Result<List<DeptVO>> getDeptTree();

    /**
     * 获取部门详情
     */
    Result<DeptVO> getDeptById(Long id);

    /**
     * 新增部门
     */
    Result<Void> addDept(DeptDTO deptDTO);

    /**
     * 修改部门
     */
    Result<Void> updateDept(DeptDTO deptDTO);

    /**
     * 删除部门
     */
    Result<Void> deleteDept(Long id);

}
