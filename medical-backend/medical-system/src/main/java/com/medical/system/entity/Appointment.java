package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约记录实体
 *
 * @author 刘柏城
 * @date 2025-11-06
 */
@Data
@TableName("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long patientId;

    private Long doctorId;

    private LocalDate appointmentDate;

    private String appointmentTime;

    private String department;

    private String symptoms;

    private Integer status;

    private String cancelReason;

    private String diagnosis;

    private String prescription;

    private BigDecimal fee;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String doctorName;

    @TableField(exist = false)
    private String patientName;

    @TableField(exist = false)
    private Long patientUserId;
}

