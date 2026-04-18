package com.flow.admin.controller;

import com.flow.admin.dto.UserDTO;
import com.flow.admin.service.SysUserService;
import com.flow.admin.vo.UserVO;
import com.flow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private SysUserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<List<UserVO>> list() {
        return userService.listUsers();
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:detail')")
    public Result<UserVO> getById(@PathVariable Long id) {
        return userService.getCurrentUser(id);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Void> add(@Validated @RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    /**
     * 修改用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        return userService.updateUser(userDTO);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        return userService.resetPassword(id, newPassword);
    }

}
