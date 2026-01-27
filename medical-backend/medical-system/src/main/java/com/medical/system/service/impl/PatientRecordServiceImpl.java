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
        if ("PATIENT".equals(currentRole)) {
            // 患者本人只能看到自己的
            LambdaQueryWrapper<PatientRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PatientRecord::getUserId, currentUserId);
            wrapper.orderByDesc(PatientRecord::getCreateTime);
            return page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        }
        
        // 管理员和医生：使用自定义的关联查询，展示所有患者及其档案状态
        Page<PatientRecord> recordPage = new Page<>(page.getCurrent(), page.getSize());
        return baseMapper.selectPatientRecordsPage(recordPage, keyword, filterUserId);
    }
}
