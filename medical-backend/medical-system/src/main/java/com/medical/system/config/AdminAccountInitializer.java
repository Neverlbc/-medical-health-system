package com.medical.system.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.system.entity.SysUser;
import com.medical.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时确保默认管理员账户密码正确。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.admin-username:admin}")
    private String adminUsername;

    @Value("${app.init.admin-password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        SysUser admin = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, adminUsername)
                .last("LIMIT 1"));

        if (admin == null) {
            log.warn("未找到默认管理员账号: {}", adminUsername);
            return;
        }

        if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
            String encoded = passwordEncoder.encode(adminPassword);
            admin.setPassword(encoded);
            sysUserMapper.updateById(admin);
            log.info("已重置管理员({})密码为配置中的默认值", adminUsername);
        } else {
            log.info("管理员({})密码已匹配默认配置", adminUsername);
        }
    }
}


