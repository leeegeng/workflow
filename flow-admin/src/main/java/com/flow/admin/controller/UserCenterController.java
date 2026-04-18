package com.flow.admin.controller;

import com.flow.admin.security.SecurityUser;
import com.flow.admin.service.SysUserService;
import com.flow.admin.vo.UserVO;
import com.flow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户中心控制器
 */
@RestController
@RequestMapping("/user-center")
public class UserCenterController {

    @Autowired
    private SysUserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return userService.getCurrentUser(securityUser.getUserId());
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody Map<String, String> params) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        // 这里可以实现更新个人信息的逻辑
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        return userService.changePassword(securityUser.getUserId(), oldPassword, newPassword);
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar() {
        // 这里可以实现上传头像的逻辑
        return Result.success("头像上传成功");
    }

}
