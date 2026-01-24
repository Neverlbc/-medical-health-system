package com.medical.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.result.Result;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.Appointment;
import com.medical.system.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预约挂号控制器
 *
 * @author Antigravity
 * @date 2025-11-27
 */
@Tag(name = "预约挂号管理")
@RestController
@RequestMapping("/api/v1/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Operation(summary = "创建预约")
    @PostMapping
    public Result<Void> create(@RequestBody Appointment appointment) {
        Long userId = SecurityUtils.getUserId();
        appointmentService.createAppointment(appointment, userId);
        return Result.success("预约成功");
    }

    @Operation(summary = "取消预约")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        appointmentService.cancelAppointment(id, userId);
        return Result.success("取消成功");
    }

    @Operation(summary = "完成预约")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        appointmentService.completeAppointment(id);
        return Result.success("操作成功");
    }

    @Operation(summary = "获取预约列表")
    @GetMapping("/list")
    public Result<IPage<Appointment>> list(@RequestParam(defaultValue = "1") long pageNum,
                                           @RequestParam(defaultValue = "10") long pageSize,
                                           @RequestParam(required = false) String keyword) {
        Long userId = SecurityUtils.getUserId();
        // 获取角色
        String role = "PATIENT";
        if (SecurityUtils.getAuthentication() != null) {
            String authorities = SecurityUtils.getAuthentication().getAuthorities().toString();
            if (authorities.contains("ROLE_DOCTOR")) {
                role = "DOCTOR";
            } else if (authorities.contains("ROLE_ADMIN")) {
                role = "ADMIN";
            }
        }
        
        Page<Appointment> page = new Page<>(pageNum, pageSize);
        return Result.success(appointmentService.getAppointmentPage(page, userId, role, keyword));
    }
}
