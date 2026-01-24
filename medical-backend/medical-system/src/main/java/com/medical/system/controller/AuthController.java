package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.system.dto.LoginDTO;
import com.medical.system.dto.RegisterDTO;
import com.medical.system.service.AuthService;
import com.medical.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Auth controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Register, Login, Logout")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user")
    public Result<Void> register(@RequestBody @Validated RegisterDTO dto) {
        authService.register(dto);
        return Result.success("register ok");
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public Result<LoginVO> login(@RequestBody @Validated LoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return Result.success("login ok", vo);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success("logout ok");
    }
}

