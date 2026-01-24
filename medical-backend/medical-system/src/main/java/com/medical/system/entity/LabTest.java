package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实验室检查主表实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("lab_test")
public class LabTest implements Serializable {

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
     * 检查类型(如:血常规,生化全项)
     */
    private String testType;

    /**
     * 检查时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime testDate;

    /**
     * 检查医院
     */
    private String hospital;

    /**
     * 报告单图片/PDF链接
     */
    private String reportUrl;

    /**
     * 开单医生ID
     */
    private Long doctorId;

    /**
     * 结果摘要
     */
    private String resultSummary;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 检查项目明细列表 (非数据库字段)
     */
    @TableField(exist = false)
    private java.util.List<LabTestItem> items;
}
