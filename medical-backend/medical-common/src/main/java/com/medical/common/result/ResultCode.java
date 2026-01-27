package com.medical.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author lbc
 * @date 2025-11-06
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(400, "操作失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "没有权限访问该资源"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "请求的资源不存在"),

    /**
     * 参数错误
     */
    PARAM_ERROR(40001, "参数错误"),

    /**
     * 参数验证失败
     */
    VALIDATE_FAILED(40002, "参数验证失败"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(40003, "用户名或密码错误"),

    /**
     * Token无效
     */
    TOKEN_INVALID(40004, "Token无效或已过期"),

    /**
     * 账号已被禁用
     */
    ACCOUNT_DISABLED(40005, "账号已被禁用"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(40006, "用户名已存在"),

    /**
     * 手机号已存在
     */
    PHONE_EXISTS(40007, "手机号已存在"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(50001, "业务处理异常"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * AI服务异常
     */
    AI_SERVICE_ERROR(50002, "AI服务暂时不可用");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

