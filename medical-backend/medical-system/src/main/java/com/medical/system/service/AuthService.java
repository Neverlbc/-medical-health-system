package com.medical.system.service;

import com.medical.system.dto.LoginDTO;
import com.medical.system.dto.RegisterDTO;
import com.medical.system.vo.LoginVO;

/**
 * 认证服务接口
 *
 * @author lbc
 * @date 2025-11-06
 */
public interface AuthService {

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户退出
     */
    void logout();
}

