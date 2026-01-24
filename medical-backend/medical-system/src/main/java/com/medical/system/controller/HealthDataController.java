package com.medical.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.result.Result;
import com.medical.system.entity.HealthData;
import com.medical.system.service.HealthDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 健康数据控制器
 *
 * @author 刘柏城
 * @date 2025-11-24
 */
@Tag(name = "HealthData", description = "健康数据管理")
@RestController
@RequestMapping("/api/v1/health-data")
public class HealthDataController {

    @Autowired
    private HealthDataService healthDataService;

    /**
     * 创建健康数据记录
     */
    @Operation(summary = "创建健康数据记录")
    @PostMapping
    public Result<HealthData> createHealthData(@RequestBody HealthData healthData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        
        HealthData created = healthDataService.createHealthData(healthData, userId);
        return Result.success(created);
    }

    /**
     * 分页查询健康数据
     */
    @Operation(summary = "分页查询健康数据")
    @GetMapping("/list")
    public Result<Page<HealthData>> getHealthDataPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "数据类型") @RequestParam(required = false) String dataType,
            @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        String userRole = auth.getAuthorities().iterator().next().getAuthority();
        
        Page<HealthData> page = healthDataService.getHealthDataPage(current, size, userId, userRole, dataType, patientId);
        return Result.success(page);
    }

    /**
     * 获取健康数据详情
     */
    @Operation(summary = "获取健康数据详情")
    @GetMapping("/{id}")
    public Result<HealthData> getHealthDataById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        String userRole = auth.getAuthorities().iterator().next().getAuthority();
        
        HealthData healthData = healthDataService.getHealthDataById(id, userId, userRole);
        return Result.success(healthData);
    }

    /**
     * 删除健康数据
     */
    @Operation(summary = "删除健康数据")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHealthData(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        String userRole = auth.getAuthorities().iterator().next().getAuthority();
        
        healthDataService.deleteHealthData(id, userId, userRole);
        return Result.success();
    }

    /**
     * 获取健康数据趋势（用于图表）
     */
    @Operation(summary = "获取健康数据趋势")
    @GetMapping("/trend")
    public Result<List<HealthData>> getHealthDataTrend(
            @Parameter(description = "数据类型") @RequestParam String dataType,
            @Parameter(description = "最近天数") @RequestParam(defaultValue = "30") Integer days,
            @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        String userRole = auth.getAuthorities().iterator().next().getAuthority();
        
        List<HealthData> trend = healthDataService.getHealthDataTrend(userId, userRole, dataType, patientId, days);
        return Result.success(trend);
    }

    /**
     * 获取健康数据统计信息
     */
    @Operation(summary = "获取健康数据统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getHealthDataStatistics(
            @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        String userRole = auth.getAuthorities().iterator().next().getAuthority();
        
        Map<String, Object> statistics = healthDataService.getHealthDataStatistics(userId, userRole, patientId);
        return Result.success(statistics);
    }
}
