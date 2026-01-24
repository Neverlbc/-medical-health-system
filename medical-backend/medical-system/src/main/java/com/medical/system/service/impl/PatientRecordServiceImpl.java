package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.PatientRecord;
import com.medical.system.mapper.PatientRecordMapper;
import com.medical.system.service.PatientRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PatientRecordServiceImpl extends ServiceImpl<PatientRecordMapper, PatientRecord> implements PatientRecordService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.medical.system.mapper.DoctorInfoMapper doctorInfoMapper;
    @org.springframework.beans.factory.annotation.Autowired
    private com.medical.system.mapper.AppointmentMapper appointmentMapper;
    @org.springframework.beans.factory.annotation.Autowired
    private com.medical.system.mapper.PatientInfoMapper patientInfoMapper;

    @Override
    public void create(PatientRecord record, Long currentUserId, String currentRole) {
        if ("PATIENT".equals(currentRole)) {
            record.setUserId(currentUserId);
        } else {
            if (record.getUserId() == null) {
                throw new BusinessException("请指定患者ID");
            }
        }
        save(record);
    }

    @Override
    public void update(PatientRecord record, Long currentUserId, String currentRole) {
        PatientRecord exist = getById(record.getId());
        if (exist == null) {
            throw new BusinessException("档案不存在");
        }
        
        if ("PATIENT".equals(currentRole)) {
            if (!exist.getUserId().equals(currentUserId)) {
                throw new BusinessException("无权修改他人档案");
            }
            // Patient cannot change userId
            record.setUserId(currentUserId);
        }
        
        updateById(record);
    }

    @Override
    public PatientRecord detail(Long id, Long currentUserId, String currentRole) {
        PatientRecord record = getById(id);
        if (record == null) {
            return null;
        }
        
        if ("PATIENT".equals(currentRole) && !record.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权查看他人档案");
        }
        
        return record;
    }

    @Override
    public IPage<PatientRecord> pageList(Page<?> page, String keyword, Long filterUserId, Long currentUserId, String currentRole) {
        LambdaQueryWrapper<PatientRecord> wrapper = new LambdaQueryWrapper<>();
        
        if ("PATIENT".equals(currentRole)) {
            wrapper.eq(PatientRecord::getUserId, currentUserId);
        } else if ("DOCTOR".equals(currentRole)) {
            // Doctor can only see patients they have appointments with
            com.medical.system.entity.DoctorInfo doctorInfo = doctorInfoMapper.selectOne(
                    new LambdaQueryWrapper<com.medical.system.entity.DoctorInfo>().eq(com.medical.system.entity.DoctorInfo::getUserId, currentUserId)
            );
            
            if (doctorInfo != null) {
                // Find all patients who have appointments with this doctor
                java.util.List<com.medical.system.entity.Appointment> appointments = appointmentMapper.selectList(
                        new LambdaQueryWrapper<com.medical.system.entity.Appointment>()
                                .eq(com.medical.system.entity.Appointment::getDoctorId, doctorInfo.getId())
                                .select(com.medical.system.entity.Appointment::getPatientId)
                );
                
                if (appointments.isEmpty()) {
                    // No patients, return empty page
                    return new Page<>(page.getCurrent(), page.getSize());
                }
                
                java.util.Set<Long> patientIds = appointments.stream()
                        .map(com.medical.system.entity.Appointment::getPatientId)
                        .collect(java.util.stream.Collectors.toSet());
                
                // Convert patientIds (from patient_info table) to userIds (from sys_user table)
                java.util.List<com.medical.system.entity.PatientInfo> patients = patientInfoMapper.selectList(
                        new LambdaQueryWrapper<com.medical.system.entity.PatientInfo>()
                                .in(com.medical.system.entity.PatientInfo::getId, patientIds)
                                .select(com.medical.system.entity.PatientInfo::getUserId)
                );
                
                if (patients.isEmpty()) {
                     return new Page<>(page.getCurrent(), page.getSize());
                }
                
                java.util.Set<Long> userIds = patients.stream()
                        .map(com.medical.system.entity.PatientInfo::getUserId)
                        .collect(java.util.stream.Collectors.toSet());
                
                wrapper.in(PatientRecord::getUserId, userIds);
            } else {
                 return new Page<>(page.getCurrent(), page.getSize());
            }
            
            if (StringUtils.hasText(keyword)) {
                 wrapper.like(PatientRecord::getRemark, keyword);
            }
        } else {
            // ADMIN or others
            if (filterUserId != null) {
                wrapper.eq(PatientRecord::getUserId, filterUserId);
            }
            if (StringUtils.hasText(keyword)) {
                 wrapper.like(PatientRecord::getRemark, keyword);
            }
        }
        
        wrapper.orderByDesc(PatientRecord::getCreateTime);
        Page<PatientRecord> recordPage = new Page<>(page.getCurrent(), page.getSize());
        return page(recordPage, wrapper);
    }
}
