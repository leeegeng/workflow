package com.flow.admin.controller;

import com.flow.admin.dto.DeptDTO;
import com.flow.admin.service.SysDeptService;
import com.flow.admin.vo.DeptVO;
import com.flow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/system/dept")
public class DeptController {

    @Autowired
    private SysDeptService deptService;

    /**
     * 获取部门树
     */
    @GetMapping("/tree")
    public Result<List<DeptVO>> tree() {
        return deptService.getDeptTree();
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/{id}")
    public Result<DeptVO> getById(@PathVariable Long id) {
        return deptService.getDeptById(id);
    }

    /**
     * 新增部门
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Result<Void> add(@Validated @RequestBody DeptDTO deptDTO) {
        return deptService.addDept(deptDTO);
    }

    /**
     * 修改部门
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> update(@Validated @RequestBody DeptDTO deptDTO) {
        return deptService.updateDept(deptDTO);
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        return deptService.deleteDept(id);
    }

}
