package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.DiagnosisRecord;

import java.util.List;

/**
 * 诊断记录服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface DiagnosisRecordService {

    /**
     * 创建诊断记录
     */
    DiagnosisRecord createDiagnosis(DiagnosisRecord diagnosis, Long doctorId);

    /**
     * 更新诊断记录
     */
    DiagnosisRecord updateDiagnosis(DiagnosisRecord diagnosis);

    /**
     * 删除诊断记录
     */
    void deleteDiagnosis(Long id);

    /**
     * 获取患者的诊断记录列表
     */
    List<DiagnosisRecord> getDiagnosisList(Long patientId);

    /**
     * 分页查询诊断记录
     */
    Page<DiagnosisRecord> getDiagnosisPage(Page<DiagnosisRecord> page, Long patientId, String keyword);
}
