package com.medical.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.common.result.Result;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.PatientInfoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 患者基本信息控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "患者基本信息")
@RestController
@RequestMapping("/api/v1/patient/info")
public class PatientInfoController {

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Operation(summary = "根据用户ID获取患者信息")
    @GetMapping("/user/{userId}")
    public Result<PatientInfo> getByUserId(@PathVariable Long userId) {
        PatientInfo info = patientInfoMapper.selectOne(
                new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
        );
        return Result.success(info);
    }

    @Operation(summary = "根据ID获取患者信息")
    @GetMapping("/{id}")
    public Result<PatientInfo> getById(@PathVariable Long id) {
        return Result.success(patientInfoMapper.selectById(id));
    }
}
