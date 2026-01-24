package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 随访记录实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("follow_up")
public class FollowUp implements Serializable {

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
     * 随访医生ID
     */
    private Long doctorId;

    /**
     * 随访日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate followUpDate;

    /**
     * 方式(OUTPATIENT:门诊, PHONE:电话, HOME:家访)
     */
    private String method;

    /**
     * 随访内容
     */
    private String content;

    /**
     * 随访结果/病情变化
     */
    private String result;

    /**
     * 下次随访日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
