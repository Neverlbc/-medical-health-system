package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病史信息实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("patient_history")
public class PatientHistory implements Serializable {

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
     * 类型(PERSONAL:个人史, FAMILY:家族史, PAST:既往史)
     */
    private String historyType;

    /**
     * 疾病名称(家族史/既往史)
     */
    private String diseaseName;

    /**
     * 确诊日期(既往史)
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate diagnosisDate;

    /**
     * 亲属关系(家族史)
     */
    private String relationship;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 状态(1:有效 0:无效)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
