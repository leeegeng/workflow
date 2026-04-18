package com.flow.admin.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flow.admin.dto.LoginDTO;
import com.flow.admin.dto.UserDTO;
import com.flow.admin.entity.SysRole;
import com.flow.admin.entity.SysUser;
import com.flow.admin.entity.SysUserRole;
import com.flow.admin.mapper.SysRoleMapper;
import com.flow.admin.mapper.SysUserMapper;
import com.flow.admin.mapper.SysUserRoleMapper;
import com.flow.admin.service.SysUserService;
import com.flow.admin.vo.LoginVO;
import com.flow.admin.vo.UserVO;
import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import com.flow.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Service实现
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public Result<LoginVO> login(LoginDTO loginDTO) {
        // 查询用户
        SysUser user = baseMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 检查用户状??
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 验证密码
        String encryptedPassword = DigestUtil.md5Hex(loginDTO.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 生成JWT Token
        String token = JwtUtils.generateToken(user.getId(), user.getUsername());

        // 获取用户角色和权??
        List<String> roles = baseMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = baseMapper.selectPermissionsByUserId(user.getId());

        // 构建返回对象
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(token);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(7 * 24 * 60 * 60L);

        LoginVO.UserInfoVO userInfo = new LoginVO.UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(roles);
        userInfo.setPermissions(permissions);
        loginVO.setUserInfo(userInfo);

        log.info("用户登录成功: {}", user.getUsername());
        return Result.success(loginVO);
    }

    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public Result<UserVO> getCurrentUser(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserVO userVO = convertToVO(user);
        return Result.success(userVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addUser(UserDTO userDTO) {
        // 检查用户名是否已存??
        SysUser existUser = getByUsername(userDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);

        // 默认密码??23456
        String defaultPassword = StringUtils.hasText(userDTO.getPassword())
                ? userDTO.getPassword() : "123456";
        user.setPassword(DigestUtil.md5Hex(defaultPassword));

        // 默认状态为正常
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        save(user);

        // 保存用户角色关联
        if (!CollectionUtils.isEmpty(userDTO.getRoleIds())) {
            saveUserRoles(user.getId(), userDTO.getRoleIds());
        }

        log.info("新增用户成功: {}", user.getUsername());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateUser(UserDTO userDTO) {
        SysUser user = getById(userDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新用户信息
        BeanUtils.copyProperties(userDTO, user, "password");
        updateById(user);

        // 更新用户角色关联
        if (userDTO.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(user.getId());
            if (!CollectionUtils.isEmpty(userDTO.getRoleIds())) {
                saveUserRoles(user.getId(), userDTO.getRoleIds());
            }
        }

        log.info("更新用户成功: {}", user.getUsername());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteUser(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 逻辑删除
        removeById(userId);

        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);

        log.info("删除用户成功: {}", user.getUsername());
        return Result.success();
    }

    @Override
    public Result<List<UserVO>> listUsers() {
        List<SysUser> userList = list();
        List<UserVO> voList = userList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<Void> resetPassword(Long userId, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(DigestUtil.md5Hex(newPassword));
        updateById(user);

        log.info("重置密码成功: {}", user.getUsername());
        return Result.success();
    }

    @Override
    public Result<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密??
        String encryptedOldPassword = DigestUtil.md5Hex(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }

        user.setPassword(DigestUtil.md5Hex(newPassword));
        updateById(user);

        log.info("修改密码成功: {}", user.getUsername());
        return Result.success();
    }

    /**
     * 保存用户角色关联
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        // 先删除旧的角色关??
        userRoleMapper.deleteByUserId(userId);
        // 插入新的角色关联
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 获取角色列表
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getId());
        List<String> roleNames = roles.stream()
                .map(SysRole::getRoleName)
                .collect(Collectors.toList());
        vo.setRoles(roleNames);

        return vo;
    }
}
