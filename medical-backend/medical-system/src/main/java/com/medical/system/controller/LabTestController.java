package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.LabTest;
import com.medical.system.entity.LabTestItem;
import com.medical.system.service.LabTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实验室检查控制器
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Tag(name = "实验室检查管理")
@RestController
@RequestMapping("/api/v1/lab-test")
public class LabTestController {

    @Autowired
    private LabTestService labTestService;

    @Operation(summary = "创建实验室检查记录")
    @PostMapping
    public Result<LabTest> create(@RequestBody LabTest labTest) {
        Long doctorId = SecurityUtils.getUserId(); // 假设当前登录用户是医生
        return Result.success(labTestService.createLabTest(labTest, labTest.getItems(), doctorId));
    }

    @Operation(summary = "更新实验室检查记录")
    @PutMapping
    public Result<LabTest> update(@RequestBody LabTest labTest) {
        return Result.success(labTestService.updateLabTest(labTest));
    }

    @Operation(summary = "删除实验室检查记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        labTestService.deleteLabTest(id);
        return Result.success();
    }

    @Operation(summary = "获取实验室检查列表")
    @GetMapping("/list")
    @PreAuthorize("@permissionService.hasPatientPermission(#patientId)")
    public Result<List<LabTest>> list(@RequestParam Long patientId) {
        return Result.success(labTestService.getLabTestList(patientId));
    }

    @Operation(summary = "获取实验室检查项目明细")
    @GetMapping("/{testId}/items")
    public Result<List<LabTestItem>> getItems(@PathVariable Long testId) {
        return Result.success(labTestService.getLabTestItems(testId));
    }

    @Operation(summary = "添加实验室检查项目")
    @PostMapping("/item")
    public Result<LabTestItem> addItem(@RequestBody LabTestItem item) {
        labTestService.addLabTestItem(item);
        return Result.success(item);
    }

    @Operation(summary = "删除实验室检查项目")
    @DeleteMapping("/item/{itemId}")
    public Result<Void> deleteItem(@PathVariable Long itemId) {
        labTestService.deleteLabTestItem(itemId);
        return Result.success();
    }
}
