package com.medical.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.result.Result;
import com.medical.system.entity.SysUser;
import com.medical.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户查询（仅用于医生端选择患者）
 */
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "用户查询")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper userMapper;

    @GetMapping("/patients")
    @Operation(summary = "患者搜索（医生/管理员使用）")
    public Result<List<SimpleUser>> searchPatients(@RequestParam(required = false) String keyword,
                                                   @RequestParam(defaultValue = "1") long pageNum,
                                                   @RequestParam(defaultValue = "20") long pageSize) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUser::getRole, "PATIENT");
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        qw.orderByAsc(SysUser::getId);
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        Page<SysUser> result = userMapper.selectPage(page, qw);
        List<SimpleUser> list = result.getRecords().stream().map(u -> {
            SimpleUser s = new SimpleUser();
            s.setId(u.getId());
            s.setUsername(u.getUsername());
            s.setNickname(u.getNickname());
            s.setPhone(u.getPhone());
            return s;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @Data
    public static class SimpleUser {
        private Long id;
        private String username;
        private String nickname;
        private String phone;
    }
}

