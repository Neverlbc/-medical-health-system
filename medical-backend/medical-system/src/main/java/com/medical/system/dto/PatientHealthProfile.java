package com.medical.system.dto;

import com.medical.system.entity.*;
import lombok.Data;
import java.util.List;

/**
 * 患者健康档案全量数据 DTO
 * 用于 AI 分析
 */
@Data
public class PatientHealthProfile {
    /**
     * 基本信息
     */
    private PatientInfo basicInfo;

    /**
     * 过敏史
     */
    private List<PatientAllergy> allergies;

    /**
     * 既往史/家族史/个人史
     */
    private List<PatientHistory> histories;

    /**
     * 近期诊断记录 (最近6个月)
     */
    private List<DiagnosisRecord> recentDiagnoses;

    /**
     * 近期生命体征 (最近30天)
     */
    private List<HealthData> recentHealthData;

    /**
     * 近期检验报告 (最近3个月)
     */
    private List<LabTest> recentLabTests;
    
    /**
     * 近期用药记录 (如有)
     */
}
