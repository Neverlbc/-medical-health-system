package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.FollowUp;
import com.medical.system.service.FollowUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 随访记录控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "随访记录管理")
@RestController
@RequestMapping("/api/v1/follow-up")
public class FollowUpController {

    @Autowired
    private FollowUpService followUpService;

    @Operation(summary = "创建随访记录")
    @PostMapping
    public Result<FollowUp> create(@RequestBody FollowUp followUp) {
        Long doctorId = SecurityUtils.getUserId(); // 假设当前登录用户是医生
        return Result.success(followUpService.createFollowUp(followUp, doctorId));
    }

    @Operation(summary = "更新随访记录")
    @PutMapping
    public Result<FollowUp> update(@RequestBody FollowUp followUp) {
        return Result.success(followUpService.updateFollowUp(followUp));
    }

    @Operation(summary = "删除随访记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return Result.success();
    }

    @Operation(summary = "获取随访记录列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<FollowUp>> list(@RequestParam Long patientId) {
        return Result.success(followUpService.getFollowUpList(patientId));
    }
}
