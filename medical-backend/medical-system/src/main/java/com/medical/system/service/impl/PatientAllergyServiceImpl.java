package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.PatientAllergy;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.PatientAllergyMapper;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.service.PatientAllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 过敏史服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class PatientAllergyServiceImpl implements PatientAllergyService {

    @Autowired
    private PatientAllergyMapper patientAllergyMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Override
    public PatientAllergy createAllergy(PatientAllergy allergy, Long userId) {
        if (allergy.getPatientId() == null) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo == null) {
                throw new BusinessException("患者信息不存在");
            }
            allergy.setPatientId(patientInfo.getId());
        }

        allergy.setCreateTime(LocalDateTime.now());
        patientAllergyMapper.insert(allergy);
        return allergy;
    }

    @Override
    public PatientAllergy updateAllergy(PatientAllergy allergy) {
        PatientAllergy exist = patientAllergyMapper.selectById(allergy.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }
        patientAllergyMapper.updateById(allergy);
        return patientAllergyMapper.selectById(allergy.getId());
    }

    @Override
    public void deleteAllergy(Long id) {
        patientAllergyMapper.deleteById(id);
    }

    @Override
    public List<PatientAllergy> getAllergyList(Long patientId) {
        LambdaQueryWrapper<PatientAllergy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientAllergy::getPatientId, patientId);
        wrapper.orderByDesc(PatientAllergy::getCreateTime);
        return patientAllergyMapper.selectList(wrapper);
    }
}
