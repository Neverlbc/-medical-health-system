package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者信息实体
 *
 * @author 刘柏城
 * @date 2025-11-06
 */
@Data
@TableName("patient_info")
public class PatientInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID(外键)
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别(0:女 1:男)
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * 血型(A/B/AB/O)
     */
    private String bloodType;

    /**
     * 过敏史
     */
    private String allergies;

    /**
     * 家族病史
     */
    private String familyHistory;

    /**
     * 既往病史
     */
    private String medicalHistory;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

