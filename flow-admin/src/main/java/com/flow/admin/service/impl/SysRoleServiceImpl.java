package com.flow.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flow.admin.dto.RoleDTO;
import com.flow.admin.entity.SysRole;
import com.flow.admin.entity.SysRoleMenu;
import com.flow.admin.mapper.SysRoleMapper;
import com.flow.admin.mapper.SysRoleMenuMapper;
import com.flow.admin.service.SysRoleService;
import com.flow.admin.vo.RoleVO;
import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public Result<List<RoleVO>> listRoles() {
        List<SysRole> roleList = list();
        List<RoleVO> voList = roleList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<RoleVO> getRoleById(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        RoleVO vo = convertToVO(role);
        // 获取角色关联的菜单ID列表
        vo.setMenuIds(roleMenuMapper.selectMenuIdsByRoleId(id));
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addRole(RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        SysRole existRole = baseMapper.selectByRoleCode(roleDTO.getRoleCode());
        if (existRole != null) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);

        if (role.getStatus() == null) {
            role.setStatus(1);
        }

        save(role);

        // 保存角色菜单关联
        if (!CollectionUtils.isEmpty(roleDTO.getMenuIds())) {
            assignRoleMenus(role.getId(), roleDTO.getMenuIds());
        }

        log.info("新增角色成功: {}", role.getRoleName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateRole(RoleDTO roleDTO) {
        SysRole role = getById(roleDTO.getId());
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 如果修改了roleCode，检查是否与其他角色冲突
        if (!role.getRoleCode().equals(roleDTO.getRoleCode())) {
            SysRole existRole = baseMapper.selectByRoleCode(roleDTO.getRoleCode());
            if (existRole != null) {
                throw new BusinessException("角色编码已存在");
            }
        }

        BeanUtils.copyProperties(roleDTO, role);
        updateById(role);

        // 更新角色菜单关联
        if (roleDTO.getMenuIds() != null) {
            roleMenuMapper.deleteByRoleId(role.getId());
            if (!CollectionUtils.isEmpty(roleDTO.getMenuIds())) {
                assignRoleMenus(role.getId(), roleDTO.getMenuIds());
            }
        }

        log.info("更新角色成功: {}", role.getRoleName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteRole(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 删除角色菜单关联
        roleMenuMapper.deleteByRoleId(id);

        removeById(id);
        log.info("删除角色成功: {}", role.getRoleName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignRoleMenus(Long roleId, List<Long> menuIds) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 删除旧的角色菜单关联
        roleMenuMapper.deleteByRoleId(roleId);

        // 插入新的角色菜单关联
        if (!CollectionUtils.isEmpty(menuIds)) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }

        log.info("分配角色菜单成功: roleId={}, menuCount={}", roleId, menuIds != null ? menuIds.size() : 0);
        return Result.success();
    }

    /**
     * 转换为VO
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }

}
