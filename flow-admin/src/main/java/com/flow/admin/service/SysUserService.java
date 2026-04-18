package com.flow.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flow.admin.dto.LoginDTO;
import com.flow.admin.dto.UserDTO;
import com.flow.admin.entity.SysUser;
import com.flow.admin.vo.LoginVO;
import com.flow.admin.vo.UserVO;
import com.flow.common.result.Result;

import java.util.List;

/**
 * 用户Service接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     */
    Result<LoginVO> login(LoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 获取当前登录用户信息
     */
    Result<UserVO> getCurrentUser(Long userId);

    /**
     * 新增用户
     */
    Result<Void> addUser(UserDTO userDTO);

    /**
     * 修改用户
     */
    Result<Void> updateUser(UserDTO userDTO);

    /**
     * 删除用户
     */
    Result<Void> deleteUser(Long userId);

    /**
     * 获取用户列表
     */
    Result<List<UserVO>> listUsers();

    /**
     * 重置密码
     */
    Result<Void> resetPassword(Long userId, String newPassword);

    /**
     * 修改密码
     */
    Result<Void> changePassword(Long userId, String oldPassword, String newPassword);

}
