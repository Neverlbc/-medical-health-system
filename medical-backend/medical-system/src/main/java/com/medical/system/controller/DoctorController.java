package com.medical.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.common.result.Result;
import com.medical.system.entity.DoctorInfo;
import com.medical.system.mapper.DoctorInfoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医生信息控制器
 *
 * @author Antigravity
 * @date 2025-11-25
 */
@Tag(name = "医生信息管理")
@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorController {

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    @Operation(summary = "获取所有医生列表")
    @GetMapping("/list")
    public Result<List<DoctorInfo>> list() {
        return Result.success(doctorInfoMapper.selectList(new LambdaQueryWrapper<DoctorInfo>()
                .eq(DoctorInfo::getStatus, 1)));
    }
}
