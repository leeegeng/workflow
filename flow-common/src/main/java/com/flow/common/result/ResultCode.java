package com.flow.common.result;

import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 错误
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /**
     * token过期
     */
    TOKEN_EXPIRED(1002, "登录已过期，请重新登录"),

    /**
     * token无效
     */
    TOKEN_INVALID(1003, "无效的token"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1004, "用户不存在"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1005, "用户已被禁用"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1006, "用户名已存在"),

    /**
     * 旧密码错误
     */
    OLD_PASSWORD_ERROR(1007, "旧密码错误"),

    /**
     * 流程定义不存在
     */
    PROCESS_DEFINITION_NOT_FOUND(2001, "流程定义不存在"),

    /**
     * 流程实例不存在
     */
    PROCESS_INSTANCE_NOT_FOUND(2002, "流程实例不存在"),

    /**
     * 任务不存在
     */
    TASK_NOT_FOUND(2003, "任务不存在"),

    /**
     * 任务已被处理
     */
    TASK_ALREADY_HANDLED(2004, "任务已被处理"),

    /**
     * 没有权限处理此任务
     */
    TASK_NO_PERMISSION(2005, "没有权限处理此任务"),

    /**
     * 流程启动失败
     */
    PROCESS_START_ERROR(2006, "流程启动失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
