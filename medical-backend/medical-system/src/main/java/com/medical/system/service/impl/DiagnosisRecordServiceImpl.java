package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.DiagnosisRecord;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.DiagnosisRecordMapper;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.service.DiagnosisRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 诊断记录服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class DiagnosisRecordServiceImpl implements DiagnosisRecordService {

    @Autowired
    private DiagnosisRecordMapper diagnosisRecordMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Override
    public DiagnosisRecord createDiagnosis(DiagnosisRecord diagnosis, Long doctorId) {
        // 诊断记录通常由医生创建，doctorId 必填
        diagnosis.setDoctorId(doctorId);
        diagnosis.setCreateTime(LocalDateTime.now());
        diagnosisRecordMapper.insert(diagnosis);
        return diagnosis;
    }

    @Override
    public DiagnosisRecord updateDiagnosis(DiagnosisRecord diagnosis) {
        DiagnosisRecord exist = diagnosisRecordMapper.selectById(diagnosis.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }
        diagnosisRecordMapper.updateById(diagnosis);
        return diagnosisRecordMapper.selectById(diagnosis.getId());
    }

    @Override
    public void deleteDiagnosis(Long id) {
        diagnosisRecordMapper.deleteById(id);
    }

    @Override
    public List<DiagnosisRecord> getDiagnosisList(Long patientId) {
        LambdaQueryWrapper<DiagnosisRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiagnosisRecord::getPatientId, patientId);
        wrapper.orderByDesc(DiagnosisRecord::getDiagnosisDate);
        return diagnosisRecordMapper.selectList(wrapper);
    }

    @Override
    public Page<DiagnosisRecord> getDiagnosisPage(Page<DiagnosisRecord> page, Long patientId, String keyword) {
        LambdaQueryWrapper<DiagnosisRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiagnosisRecord::getPatientId, patientId);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(DiagnosisRecord::getDiseaseName, keyword)
                    .or()
                    .like(DiagnosisRecord::getIcd10Code, keyword)
                    .or()
                    .like(DiagnosisRecord::getDescription, keyword));
        }
        
        wrapper.orderByDesc(DiagnosisRecord::getDiagnosisDate);
        return diagnosisRecordMapper.selectPage(page, wrapper);
    }
}
