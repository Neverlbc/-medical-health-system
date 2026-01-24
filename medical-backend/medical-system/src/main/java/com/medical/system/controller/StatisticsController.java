package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.system.dto.statistics.DiseaseStatVO;
import com.medical.system.dto.statistics.OverviewVO;
import com.medical.system.dto.statistics.TrendStatVO;
import com.medical.system.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "数据分析管理")
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Operation(summary = "获取数据概览")
    @GetMapping("/overview")
    public Result<OverviewVO> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    @Operation(summary = "获取疾病分布")
    @GetMapping("/disease")
    public Result<List<DiseaseStatVO>> getDiseaseDistribution() {
        return Result.success(statisticsService.getDiseaseDistribution());
    }

    @Operation(summary = "获取预约趋势")
    @GetMapping("/trend")
    public Result<List<TrendStatVO>> getAppointmentTrend() {
        return Result.success(statisticsService.getAppointmentTrend());
    }
}
