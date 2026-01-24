package com.medical.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.DiagnosisRecord;
import com.medical.system.service.DiagnosisRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊断记录控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "诊断记录管理")
@RestController
@RequestMapping("/api/v1/diagnosis")
public class DiagnosisRecordController {

    @Autowired
    private DiagnosisRecordService diagnosisRecordService;

    @Operation(summary = "创建诊断记录")
    @PostMapping
    public Result<DiagnosisRecord> create(@RequestBody DiagnosisRecord diagnosis) {
        Long doctorId = SecurityUtils.getUserId(); // 假设当前登录用户是医生
        return Result.success(diagnosisRecordService.createDiagnosis(diagnosis, doctorId));
    }

    @Operation(summary = "更新诊断记录")
    @PutMapping
    public Result<DiagnosisRecord> update(@RequestBody DiagnosisRecord diagnosis) {
        return Result.success(diagnosisRecordService.updateDiagnosis(diagnosis));
    }

    @Operation(summary = "删除诊断记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        diagnosisRecordService.deleteDiagnosis(id);
        return Result.success();
    }

    @Operation(summary = "获取诊断记录列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<DiagnosisRecord>> list(@RequestParam Long patientId) {
        return Result.success(diagnosisRecordService.getDiagnosisList(patientId));
    }

    @Operation(summary = "分页查询诊断记录")
    @GetMapping("/page")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<Page<DiagnosisRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Long patientId,
            @RequestParam(required = false) String keyword) {
        return Result.success(diagnosisRecordService.getDiagnosisPage(new Page<>(pageNum, pageSize), patientId, keyword));
    }
}
