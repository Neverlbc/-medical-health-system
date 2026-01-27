package com.medical.system.controller;

import com.medical.common.result.Result;
import com.medical.system.entity.DoctorSchedule;
import com.medical.system.service.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 医生排班控制器
 *
 * @author Antigravity
 * @date 2025-11-06
 */
@Tag(name = "医生排班管理")
@RestController
@RequestMapping("/api/v1/schedule")
public class DoctorScheduleController {

    @Autowired
    private DoctorScheduleService scheduleService;

    @Operation(summary = "获取医生排班")
    @GetMapping("/doctor/{doctorId}")
    public Result<List<DoctorSchedule>> getByDoctor(
            @PathVariable Long doctorId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(scheduleService.getScheduleByDoctor(doctorId, startDate, endDate));
    }

    @Operation(summary = "获取科室排班")
    @GetMapping("/department")
    public Result<List<DoctorSchedule>> getByDepartment(
            @Parameter(description = "科室名称") @RequestParam String department,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(scheduleService.getScheduleByDepartment(department, startDate, endDate));
    }

    @Operation(summary = "检查排班是否可预约")
    @GetMapping("/check")
    public Result<ScheduleCheckResult> checkAvailability(
            @Parameter(description = "医生ID") @RequestParam Long doctorId,
            @Parameter(description = "预约日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Parameter(description = "时段") @RequestParam String timePeriod) {
        
        DoctorSchedule schedule = scheduleService.getScheduleDetail(doctorId, date, timePeriod);
        
        ScheduleCheckResult result = new ScheduleCheckResult();
        if (schedule == null) {
            result.setAvailable(false);
            result.setMessage("该时段暂无排班");
            result.setRemainingSlots(0);
        } else if (schedule.getStatus() != 1) {
            result.setAvailable(false);
            result.setMessage("该时段停诊");
            result.setRemainingSlots(0);
        } else if (schedule.getBookedPatients() >= schedule.getMaxPatients()) {
            result.setAvailable(false);
            result.setMessage("该时段号源已满");
            result.setRemainingSlots(0);
        } else {
            result.setAvailable(true);
            result.setMessage("可预约");
            result.setRemainingSlots(schedule.getMaxPatients() - schedule.getBookedPatients());
        }
        result.setSchedule(schedule);
        
        return Result.success(result);
    }

    @Operation(summary = "生成未来7天排班")
    @PostMapping("/generate")
    public Result<Void> generateSchedule(
            @Parameter(description = "天数，默认7天") @RequestParam(defaultValue = "7") int days) {
        scheduleService.generateAllDoctorSchedule(days);
        return Result.success("排班生成成功");
    }

    // ========== 管理员排班管理 API ==========

    @Operation(summary = "查询排班列表（管理员）")
    @GetMapping("/list")
    public Result<List<DoctorSchedule>> listSchedules(
            @Parameter(description = "医生ID") @RequestParam(required = false) Long doctorId,
            @Parameter(description = "科室名称") @RequestParam(required = false) String department,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "状态：0-停诊 1-可预约") @RequestParam(required = false) Integer status) {
        
        // 默认查询最近30天
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(30);
        }
        
        return Result.success(scheduleService.listSchedules(doctorId, department, startDate, endDate, status));
    }

    @Operation(summary = "获取单个排班详情")
    @GetMapping("/{id}")
    public Result<DoctorSchedule> getScheduleById(@PathVariable Long id) {
        DoctorSchedule schedule = scheduleService.getById(id);
        if (schedule == null) {
            return Result.error("排班不存在");
        }
        return Result.success(schedule);
    }

    @Operation(summary = "创建排班")
    @PostMapping
    public Result<DoctorSchedule> createSchedule(@RequestBody DoctorSchedule schedule) {
        try {
            DoctorSchedule created = scheduleService.createSchedule(schedule);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "更新排班")
    @PutMapping("/{id}")
    public Result<DoctorSchedule> updateSchedule(@PathVariable Long id, @RequestBody DoctorSchedule schedule) {
        try {
            schedule.setId(id);
            DoctorSchedule updated = scheduleService.updateSchedule(schedule);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除排班")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        try {
            boolean deleted = scheduleService.deleteSchedule(id);
            if (deleted) {
                return Result.success("删除成功");
            } else {
                return Result.error("排班不存在");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "批量创建排班")
    @PostMapping("/batch")
    public Result<Integer> batchCreateSchedule(@RequestBody BatchCreateRequest request) {
        try {
            int count = scheduleService.batchCreateSchedule(
                request.getDoctorId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTimePeriods(),
                request.getMaxPatients()
            );
            return Result.success(count);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除排班")
    @DeleteMapping("/batch")
    public Result<Integer> batchDeleteSchedule(@RequestBody List<Long> ids) {
        int count = scheduleService.batchDeleteSchedule(ids);
        return Result.success(count);
    }

    @Operation(summary = "切换排班状态")
    @PutMapping("/{id}/status")
    public Result<DoctorSchedule> toggleStatus(
            @PathVariable Long id,
            @Parameter(description = "新状态：0-停诊 1-可预约") @RequestParam Integer status) {
        try {
            DoctorSchedule updated = scheduleService.toggleStatus(id, status);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ========== 请求/响应对象 ==========

    /**
     * 批量创建排班请求
     */
    @Data
    public static class BatchCreateRequest {
        private Long doctorId;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private List<String> timePeriods;
        private Integer maxPatients;
    }

    /**
     * 排班检查结果
     */
    public static class ScheduleCheckResult {
        private boolean available;
        private String message;
        private int remainingSlots;
        private DoctorSchedule schedule;

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getRemainingSlots() {
            return remainingSlots;
        }

        public void setRemainingSlots(int remainingSlots) {
            this.remainingSlots = remainingSlots;
        }

        public DoctorSchedule getSchedule() {
            return schedule;
        }

        public void setSchedule(DoctorSchedule schedule) {
            this.schedule = schedule;
        }
    }
}
