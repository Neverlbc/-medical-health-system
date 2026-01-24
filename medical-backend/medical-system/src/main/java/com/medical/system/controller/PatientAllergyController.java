package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.PatientAllergy;
import com.medical.system.service.PatientAllergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 过敏史控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "过敏史管理")
@RestController
@RequestMapping("/api/v1/allergy")
public class PatientAllergyController {

    @Autowired
    private PatientAllergyService patientAllergyService;

    @Operation(summary = "创建过敏记录")
    @PostMapping
    public Result<PatientAllergy> create(@RequestBody PatientAllergy allergy) {
        Long userId = SecurityUtils.getUserId();
        return Result.success(patientAllergyService.createAllergy(allergy, userId));
    }

    @Operation(summary = "更新过敏记录")
    @PutMapping
    public Result<PatientAllergy> update(@RequestBody PatientAllergy allergy) {
        return Result.success(patientAllergyService.updateAllergy(allergy));
    }

    @Operation(summary = "删除过敏记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        patientAllergyService.deleteAllergy(id);
        return Result.success();
    }

    @Operation(summary = "获取过敏史列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<PatientAllergy>> list(@RequestParam Long patientId) {
        return Result.success(patientAllergyService.getAllergyList(patientId));
    }
}
