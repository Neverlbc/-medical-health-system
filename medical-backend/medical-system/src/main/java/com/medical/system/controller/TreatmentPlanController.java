package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.TreatmentPlan;
import com.medical.system.service.TreatmentPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 治疗方案控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "治疗方案管理")
@RestController
@RequestMapping("/api/v1/treatment-plan")
public class TreatmentPlanController {

    @Autowired
    private TreatmentPlanService treatmentPlanService;

    @Operation(summary = "创建治疗方案")
    @PostMapping
    public Result<TreatmentPlan> create(@RequestBody TreatmentPlan plan) {
        Long doctorId = SecurityUtils.getUserId(); // 假设当前登录用户是医生
        return Result.success(treatmentPlanService.createTreatmentPlan(plan, doctorId));
    }

    @Operation(summary = "更新治疗方案")
    @PutMapping
    public Result<TreatmentPlan> update(@RequestBody TreatmentPlan plan) {
        return Result.success(treatmentPlanService.updateTreatmentPlan(plan));
    }

    @Operation(summary = "删除治疗方案")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        treatmentPlanService.deleteTreatmentPlan(id);
        return Result.success();
    }

    @Operation(summary = "获取治疗方案列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<TreatmentPlan>> list(@RequestParam Long patientId) {
        return Result.success(treatmentPlanService.getTreatmentPlanList(patientId));
    }
}
