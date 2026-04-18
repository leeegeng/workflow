package com.flow.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flow.admin.dto.DeptDTO;
import com.flow.admin.entity.SysDept;
import com.flow.admin.mapper.SysDeptMapper;
import com.flow.admin.service.SysDeptService;
import com.flow.admin.vo.DeptVO;
import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门Service实现
 */
@Slf4j
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public Result<List<DeptVO>> getDeptTree() {
        List<SysDept> deptList = baseMapper.selectDeptTree();
        List<DeptVO> voList = buildDeptTree(deptList);
        return Result.success(voList);
    }

    @Override
    public Result<DeptVO> getDeptById(Long id) {
        SysDept dept = getById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        DeptVO vo = convertToVO(dept);
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addDept(DeptDTO deptDTO) {
        // 检查父部门是否存在
        if (deptDTO.getParentId() != null && deptDTO.getParentId() != 0) {
            SysDept parentDept = getById(deptDTO.getParentId());
            if (parentDept == null) {
                throw new BusinessException("上级部门不存在");
            }
        }

        SysDept dept = new SysDept();
        BeanUtils.copyProperties(deptDTO, dept);

        if (dept.getSortOrder() == null) {
            dept.setSortOrder(0);
        }

        save(dept);
        log.info("新增部门成功: {}", dept.getDeptName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateDept(DeptDTO deptDTO) {
        SysDept dept = getById(deptDTO.getId());
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查不能将自己设为父部门
        if (deptDTO.getParentId() != null && deptDTO.getParentId().equals(deptDTO.getId())) {
            throw new BusinessException("不能将自己设为上级部门");
        }

        // 检查父部门是否存在
        if (deptDTO.getParentId() != null && deptDTO.getParentId() != 0) {
            SysDept parentDept = getById(deptDTO.getParentId());
            if (parentDept == null) {
                throw new BusinessException("上级部门不存在");
            }
        }

        BeanUtils.copyProperties(deptDTO, dept);
        updateById(dept);

        log.info("更新部门成功: {}", dept.getDeptName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteDept(Long id) {
        SysDept dept = getById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查是否有子部门
        List<SysDept> children = baseMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("该部门下存在子部门，不能删除");
        }

        // 检查部门下是否有用户
        int userCount = baseMapper.countUserByDeptId(id);
        if (userCount > 0) {
            throw new BusinessException("该部门下存在用户，不能删除");
        }

        removeById(id);
        log.info("删除部门成功: {}", dept.getDeptName());
        return Result.success();
    }

    /**
     * 构建部门树
     */
    private List<DeptVO> buildDeptTree(List<SysDept> deptList) {
        Map<Long, DeptVO> voMap = deptList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toMap(DeptVO::getId, vo -> vo));

        List<DeptVO> tree = new ArrayList<>();
        for (DeptVO vo : voMap.values()) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                tree.add(vo);
            } else {
                DeptVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }
        return tree;
    }

    /**
     * 转换为VO
     */
    private DeptVO convertToVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        BeanUtils.copyProperties(dept, vo);
        return vo;
    }
}
