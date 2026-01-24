package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.PhysicalExam;
import com.medical.system.service.PhysicalExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 体格检查控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "体格检查管理")
@RestController
@RequestMapping("/api/v1/physical-exam")
public class PhysicalExamController {

    @Autowired
    private PhysicalExamService physicalExamService;

    @Operation(summary = "创建体格检查记录")
    @PostMapping
    public Result<PhysicalExam> create(@RequestBody PhysicalExam exam) {
        Long doctorId = SecurityUtils.getUserId(); // 假设当前登录用户是医生
        return Result.success(physicalExamService.createPhysicalExam(exam, doctorId));
    }

    @Operation(summary = "更新体格检查记录")
    @PutMapping
    public Result<PhysicalExam> update(@RequestBody PhysicalExam exam) {
        return Result.success(physicalExamService.updatePhysicalExam(exam));
    }

    @Operation(summary = "删除体格检查记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        physicalExamService.deletePhysicalExam(id);
        return Result.success();
    }

    @Operation(summary = "获取体格检查列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<PhysicalExam>> list(@RequestParam Long patientId) {
        return Result.success(physicalExamService.getPhysicalExamList(patientId));
    }
}
