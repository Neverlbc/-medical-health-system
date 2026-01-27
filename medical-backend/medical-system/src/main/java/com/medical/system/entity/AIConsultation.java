package com.medical.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI问诊记录实体
 *
 * @author lbc
 * @date 2025-11-06
 */
@Data
@TableName("ai_consultation")
public class AIConsultation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 患者ID(外键)
     */
    private Long patientId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户提问
     */
    private String question;

    /**
     * AI回复
     */
    private String aiResponse;

    /**
     * 问诊类型(SYMPTOM_ANALYSIS/MEDICATION_GUIDE/HEALTH_KNOWLEDGE)
     */
    private String consultationType;

    /**
     * 消耗token数
     */
    private Integer tokensUsed;

    /**
     * 响应时间(ms)
     */
    private Integer responseTime;

    /**
     * 反馈(1:有帮助 0:无帮助)
     */
    private Integer feedback;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

