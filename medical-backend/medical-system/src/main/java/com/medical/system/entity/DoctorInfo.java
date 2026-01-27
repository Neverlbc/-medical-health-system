package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医生信息实体
 *
 * @author lbc
 * @date 2025-11-06
 */
@Data
@TableName("doctor_info")
public class DoctorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID
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
     * 科室
     */
    private String department;

    /**
     * 职称
     */
    private String title;

    /**
     * 专长
     */
    private String specialty;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 执业证书号
     */
    private String certificateNo;

    /**
     * 从业年限
     */
    private Integer workYears;

    /**
     * 挂号费(元)
     */
    private BigDecimal consultationFee;

    /**
     * 评分(0-5)
     */
    private BigDecimal rating;

    /**
     * 接诊患者数
     */
    private Integer patientCount;

    /**
     * 状态(0:停诊 1:正常)
     */
    private Integer status;

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

