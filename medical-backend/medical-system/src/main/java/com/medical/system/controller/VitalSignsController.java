package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.VitalSigns;
import com.medical.system.service.VitalSignsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生命体征控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "生命体征管理")
@RestController
@RequestMapping("/api/v1/vital-signs")
public class VitalSignsController {

    @Autowired
    private VitalSignsService vitalSignsService;

    @Operation(summary = "创建生命体征记录")
    @PostMapping
    public Result<VitalSigns> create(@RequestBody VitalSigns vitalSigns) {
        Long userId = SecurityUtils.getUserId();
        return Result.success(vitalSignsService.createVitalSigns(vitalSigns, userId));
    }

    @Operation(summary = "更新生命体征记录")
    @PutMapping
    public Result<VitalSigns> update(@RequestBody VitalSigns vitalSigns) {
        return Result.success(vitalSignsService.updateVitalSigns(vitalSigns));
    }

    @Operation(summary = "删除生命体征记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        vitalSignsService.deleteVitalSigns(id);
        return Result.success();
    }

    @Operation(summary = "获取生命体征列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<VitalSigns>> list(@RequestParam Long patientId) {
        return Result.success(vitalSignsService.getVitalSignsList(patientId));
    }
}
