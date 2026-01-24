package com.medical.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.common.utils.SecurityUtils;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.DoctorInfo;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.AppointmentMapper;
import com.medical.system.mapper.DoctorInfoMapper;
import com.medical.system.mapper.PatientInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限校验服务
 *
 * @author Antigravity
 * @date 2025-11-25
 */
@Service("permissionService")
public class PermissionService {

    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private PatientInfoMapper patientInfoMapper;
    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    /**
     * 检查当前用户是否有权限访问目标患者的档案
     * @param targetPatientId 目标患者ID (patient_info.id)
     * @return true: 有权限, false: 无权限
     */
    public boolean hasPatientPermission(Long targetPatientId) {
        if (targetPatientId == null) return false;

        Long currentUserId = SecurityUtils.getUserId();
        String role = SecurityUtils.getUserRole();

        if (currentUserId == null || role == null) return false;

        if ("ADMIN".equals(role)) {
            return true;
        }

        if ("PATIENT".equals(role)) {
            // 查询当前用户的 patientId
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, currentUserId)
            );
            return patientInfo != null && patientInfo.getId().equals(targetPatientId);
        }

        if ("DOCTOR".equals(role)) {
            // 查询当前用户的 doctorId
            DoctorInfo doctorInfo = doctorInfoMapper.selectOne(
                    new LambdaQueryWrapper<DoctorInfo>().eq(DoctorInfo::getUserId, currentUserId)
            );
            if (doctorInfo == null) return false;

            // 检查是否有预约记录 (只要有过预约记录就算关联)
            Long count = appointmentMapper.selectCount(
                    new LambdaQueryWrapper<Appointment>()
                            .eq(Appointment::getDoctorId, doctorInfo.getId())
                            .eq(Appointment::getPatientId, targetPatientId)
            );
            return count > 0;
        }

        return false;
    }
}
