package com.medical.system.service;

import com.medical.system.dto.PatientHealthProfile;

/**
 * 健康档案聚合服务接口
 */
public interface HealthProfileService {
    
    /**
     * 获取患者全量健康档案
     * @param patientUserId 患者的用户ID (sys_user表的ID)
     * @return 健康档案数据
     */
    PatientHealthProfile getProfile(Long patientUserId);
}
