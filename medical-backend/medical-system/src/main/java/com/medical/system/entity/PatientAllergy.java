package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 过敏史实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("patient_allergy")
public class PatientAllergy implements Serializable {

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
     * 过敏原
     */
    private String allergen;

    /**
     * 类型(DRUG:药物, FOOD:食物, ENV:环境, OTHER:其他)
     */
    private String allergyType;

    /**
     * 过敏反应
     */
    private String reaction;

    /**
     * 严重程度(MILD:轻微, MODERATE:中度, SEVERE:严重)
     */
    private String severity;

    /**
     * 记录日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
