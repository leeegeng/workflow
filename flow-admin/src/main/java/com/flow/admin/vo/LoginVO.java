package com.flow.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录返回VO
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 过期时间（秒??
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    /**
     * 用户信息VO
     */
    @Data
    public static class UserInfoVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String username;
        private String realName;
        private String avatar;
        private String deptName;
        private List<String> roles;
        private List<String> permissions;
    }

}
