package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医生排班实体
 *
 * @author Antigravity
 * @date 2025-11-06
 */
@Data
@TableName("doctor_schedule")
public class DoctorSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 排班日期
     */
    private LocalDate scheduleDate;

    /**
     * 时段(上午/下午)
     */
    private String timePeriod;

    /**
     * 最大接诊数
     */
    private Integer maxPatients;

    /**
     * 已预约数
     */
    private Integer bookedPatients;

    /**
     * 状态(0:停诊 1:可预约)
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ===== 非数据库字段 =====

    @TableField(exist = false)
    private String doctorName;

    @TableField(exist = false)
    private String department;

    @TableField(exist = false)
    private String title;

    /**
     * 剩余可预约数
     */
    @TableField(exist = false)
    private Integer availableSlots;

    /**
     * 是否可预约
     */
    public boolean isAvailable() {
        return status != null && status == 1 
            && bookedPatients != null && maxPatients != null 
            && bookedPatients < maxPatients;
    }

    /**
     * 计算剩余可约数
     */
    public Integer getAvailableSlots() {
        if (maxPatients == null || bookedPatients == null) {
            return 0;
        }
        return Math.max(0, maxPatients - bookedPatients);
    }
}
