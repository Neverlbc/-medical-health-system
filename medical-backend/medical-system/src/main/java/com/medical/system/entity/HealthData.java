package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康数据实体
 *
 * @author lbc
 * @date 2025-11-06
 */
@Data
@TableName("health_data")
public class HealthData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 患者ID(外键)
     */
    private Long patientId;

    /**
     * 数据类型(BLOOD_PRESSURE/BLOOD_SUGAR/HEART_RATE/TEMPERATURE/WEIGHT)
     */
    private String dataType;

    /**
     * 收缩压(mmHg)
     */
    private Integer systolicPressure;

    /**
     * 舒张压(mmHg)
     */
    private Integer diastolicPressure;

    /**
     * 血糖值(mmol/L)
     */
    private BigDecimal bloodSugar;

    /**
     * 心率(次/分)
     */
    private Integer heartRate;

    /**
     * 体温(℃)
     */
    private BigDecimal temperature;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * 备注
     */
    private String remark;

    /**
     * 测量时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime measureTime;

    /**
     * 状态(0:正常 1:偏低 2:偏高)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

