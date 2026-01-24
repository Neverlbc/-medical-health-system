package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 健康档案
 */
@Data
@TableName("patient_record")
public class PatientRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 归属用户ID（患者） */
    private Long userId;

    /** 过敏史 */
    private String allergies;

    /** 家族病史 */
    private String familyHistory;

    /** 既往病史 */
    private String medicalHistory;

    /** 用药史 */
    private String medicationHistory;

    /** 备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;
}

