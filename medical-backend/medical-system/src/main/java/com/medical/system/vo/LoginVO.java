package com.medical.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 *
 * @author lbc
 * @date 2025-11-06
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String nickname;

    private String role;

    private String token;

    private Long expiresIn;
}

