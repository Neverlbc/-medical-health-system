package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 诊断记录实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("diagnosis_record")
public class DiagnosisRecord implements Serializable {

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
     * 类型(PRELIMINARY:初步诊断, CONFIRMED:确诊)
     */
    private String diagnosisType;

    /**
     * 疾病名称
     */
    private String diseaseName;

    /**
     * ICD-10编码
     */
    private String icd10Code;

    /**
     * 诊断日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate diagnosisDate;

    /**
     * 诊断医生ID
     */
    private Long doctorId;

    /**
     * 诊断描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
