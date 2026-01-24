package com.medical.system.service;

import com.medical.system.entity.PatientAllergy;

import java.util.List;

/**
 * 过敏史服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface PatientAllergyService {

    /**
     * 创建过敏记录
     */
    PatientAllergy createAllergy(PatientAllergy allergy, Long userId);

    /**
     * 更新过敏记录
     */
    PatientAllergy updateAllergy(PatientAllergy allergy);

    /**
     * 删除过敏记录
     */
    void deleteAllergy(Long id);

    /**
     * 获取患者的所有过敏记录
     */
    List<PatientAllergy> getAllergyList(Long patientId);
}
