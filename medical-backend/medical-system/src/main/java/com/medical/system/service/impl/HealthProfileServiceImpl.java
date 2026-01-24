package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.common.exception.BusinessException;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.dto.PatientHealthProfile;
import com.medical.system.entity.*;
import com.medical.system.mapper.*;
import com.medical.system.service.HealthProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthProfileServiceImpl implements HealthProfileService {

    @Autowired
    private PatientInfoMapper patientInfoMapper;
    @Autowired
    private PatientAllergyMapper patientAllergyMapper;
    @Autowired
    private PatientHistoryMapper patientHistoryMapper;
    @Autowired
    private DiagnosisRecordMapper diagnosisRecordMapper;
    @Autowired
    private HealthDataMapper healthDataMapper;
    @Autowired
    private LabTestMapper labTestMapper;

    @Override
    public PatientHealthProfile getProfile(Long patientUserId) {
        // 1. 权限校验
        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getUserRole();

        if ("PATIENT".equals(role)) {
            if (!currentUserId.equals(patientUserId)) {
                throw new BusinessException(403, "无权查看他人健康档案");
            }
        } else if ("DOCTOR".equals(role)) {
            // TODO: 可以在这里添加医患关系校验
            // 目前 MVP 阶段允许医生查看任意患者（前提是知道 ID）
        }

        // 2. 获取患者基本信息 (通过 userId 查询 patient_info)
        PatientInfo patientInfo = patientInfoMapper.selectOne(
                new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, patientUserId)
        );
        
        if (patientInfo == null) {
            throw new BusinessException("患者信息不存在");
        }
        Long patientId = patientInfo.getId();

        PatientHealthProfile profile = new PatientHealthProfile();
        profile.setBasicInfo(patientInfo);

        // 3. 聚合数据
        // 3.1 过敏史
        List<PatientAllergy> allergies = patientAllergyMapper.selectList(
                new LambdaQueryWrapper<PatientAllergy>().eq(PatientAllergy::getPatientId, patientId)
        );
        profile.setAllergies(allergies);

        // 3.2 既往史/家族史
        List<PatientHistory> histories = patientHistoryMapper.selectList(
                new LambdaQueryWrapper<PatientHistory>().eq(PatientHistory::getPatientId, patientId)
        );
        profile.setHistories(histories);

        // 3.3 近期诊断 (最近6个月)
        List<DiagnosisRecord> diagnoses = diagnosisRecordMapper.selectList(
                new LambdaQueryWrapper<DiagnosisRecord>()
                        .eq(DiagnosisRecord::getPatientId, patientId)
                        .ge(DiagnosisRecord::getCreateTime, LocalDateTime.now().minusMonths(6))
                        .orderByDesc(DiagnosisRecord::getCreateTime)
        );
        profile.setRecentDiagnoses(diagnoses);

        // 3.4 近期生命体征 (最近30天)
        // 注意：HealthData 表关联的是 patientId (patient_info的主键)
        List<HealthData> healthData = healthDataMapper.selectList(
                new LambdaQueryWrapper<HealthData>()
                        .eq(HealthData::getPatientId, patientId)
                        .ge(HealthData::getMeasureTime, LocalDateTime.now().minusDays(30))
                        .orderByDesc(HealthData::getMeasureTime)
        );
        profile.setRecentHealthData(healthData);

        // 3.5 近期检验报告 (最近3个月)
        List<LabTest> labTests = labTestMapper.selectList(
                new LambdaQueryWrapper<LabTest>()
                        .eq(LabTest::getPatientId, patientId)
                        .ge(LabTest::getTestDate, LocalDateTime.now().minusMonths(3))
                        .orderByDesc(LabTest::getTestDate)
        );
        profile.setRecentLabTests(labTests);

        return profile;
    }
}
