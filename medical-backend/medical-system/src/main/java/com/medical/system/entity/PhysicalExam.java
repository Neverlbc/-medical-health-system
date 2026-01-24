package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体格检查实体
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Data
@TableName("physical_exam")
public class PhysicalExam implements Serializable {

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
     * 检查日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate examDate;

    /**
     * 一般情况
     */
    private String generalCondition;

    /**
     * 皮肤粘膜
     */
    private String skinMucosa;

    /**
     * 淋巴结
     */
    private String lymphNodes;

    /**
     * 头颈部
     */
    private String headNeck;

    /**
     * 胸部/肺
     */
    private String chestLungs;

    /**
     * 心脏
     */
    private String heart;

    /**
     * 腹部
     */
    private String abdomen;

    /**
     * 脊柱四肢
     */
    private String spineLimbs;

    /**
     * 神经系统
     */
    private String nervousSystem;

    /**
     * 检查医生ID
     */
    private Long doctorId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
