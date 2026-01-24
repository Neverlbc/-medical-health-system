package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生命体征实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("vital_signs")
public class VitalSigns implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 收缩压(mmHg)
     */
    private Integer systolic;

    /**
     * 舒张压(mmHg)
     */
    private Integer diastolic;

    /**
     * 心率(bpm)
     */
    private Integer heartRate;

    /**
     * 体温(℃)
     */
    private BigDecimal temperature;

    /**
     * 血氧饱和度(%)
     */
    private Integer spo2;

    /**
     * 呼吸频率(次/分)
     */
    private Integer respiratoryRate;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * BMI指数
     */
    private BigDecimal bmi;

    /**
     * 测量时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime measureTime;

    /**
     * 记录人
     */
    private String recorder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
