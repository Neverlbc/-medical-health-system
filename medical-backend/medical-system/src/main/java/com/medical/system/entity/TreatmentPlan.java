package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 治疗方案实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("treatment_plan")
public class TreatmentPlan implements Serializable {

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
     * 关联诊断ID
     */
    private Long diagnosisId;

    /**
     * 方案名称
     */
    private String planName;

    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * 状态(ACTIVE:进行中, COMPLETED:已完成, TERMINATED:已终止)
     */
    private String status;

    /**
     * 制定医生ID
     */
    private Long doctorId;

    /**
     * 方案描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
