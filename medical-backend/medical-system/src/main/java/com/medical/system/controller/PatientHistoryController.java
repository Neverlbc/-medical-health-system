package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.PatientHistory;
import com.medical.system.service.PatientHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 病史信息控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "病史信息管理")
@RestController
@RequestMapping("/api/v1/history")
public class PatientHistoryController {

    @Autowired
    private PatientHistoryService patientHistoryService;

    @Operation(summary = "创建病史记录")
    @PostMapping
    @PreAuthorize("@permissionService.hasPatientPermission(#history.patientId)")
    public Result<PatientHistory> create(@RequestBody PatientHistory history) {
        Long userId = SecurityUtils.getUserId();
        return Result.success(patientHistoryService.createHistory(history, userId));
    }

    @Operation(summary = "更新病史记录")
    @PutMapping
    @PreAuthorize("@permissionService.hasPatientPermission(#history.patientId)")
    public Result<PatientHistory> update(@RequestBody PatientHistory history) {
        return Result.success(patientHistoryService.updateHistory(history));
    }

    @Operation(summary = "删除病史记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        patientHistoryService.deleteHistory(id);
        return Result.success();
    }

    @Operation(summary = "获取病史列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<PatientHistory>> list(@RequestParam Long patientId, @RequestParam(required = false) String type) {
        return Result.success(patientHistoryService.getHistoryList(patientId, type));
    }
}
