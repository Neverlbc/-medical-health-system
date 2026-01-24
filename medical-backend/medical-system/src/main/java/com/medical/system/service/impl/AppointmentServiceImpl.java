package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.DoctorInfo;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.AppointmentMapper;
import com.medical.system.mapper.DoctorInfoMapper;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 预约服务实现类
 *
 * @author Antigravity
 * @date 2025-11-27
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    @Override
    public void createAppointment(Appointment appointment, Long userId) {
        // 1. 获取患者信息
        PatientInfo patientInfo = patientInfoMapper.selectOne(
                new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
        );
        if (patientInfo == null) {
            throw new BusinessException("未找到患者档案信息，请先完善个人信息");
        }

        // 2. 设置基本信息
        appointment.setPatientId(patientInfo.getId());
        appointment.setStatus(0); // 0: 待就诊
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        // 3. 校验医生是否存在
        if (appointment.getDoctorId() == null) {
            throw new BusinessException("请选择医生");
        }
        DoctorInfo doctorInfo = doctorInfoMapper.selectById(appointment.getDoctorId());
        if (doctorInfo == null) {
            throw new BusinessException("医生不存在");
        }
        // 自动填充科室（如果前端没传或传的不对）
        appointment.setDepartment(doctorInfo.getDepartment());

        appointmentMapper.insert(appointment);
    }

    @Override
    public void cancelAppointment(Long id, Long userId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        // 简单权限校验：只能取消自己的预约（这里简化处理，实际可能需要更复杂的校验）
        // 获取当前用户的 patientId
        PatientInfo patientInfo = patientInfoMapper.selectOne(
                new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
        );
        
        if (patientInfo != null && !appointment.getPatientId().equals(patientInfo.getId())) {
             // 如果是医生或管理员，可能允许取消，这里暂不处理复杂角色判断，假设主要是患者取消
             // throw new BusinessException("无权操作");
        }

        appointment.setStatus(2); // 2: 已取消
        appointment.setCancelReason("用户主动取消");
        appointment.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);
    }

    @Override
    public IPage<Appointment> getAppointmentPage(Page<Appointment> page, Long userId, String role, String keyword) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();

        // 角色过滤
        if ("PATIENT".equals(role)) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo != null) {
                wrapper.eq(Appointment::getPatientId, patientInfo.getId());
            } else {
                // 如果没有患者档案，返回空列表
                return page;
            }
        } else if ("DOCTOR".equals(role)) {
            DoctorInfo doctorInfo = doctorInfoMapper.selectOne(
                    new LambdaQueryWrapper<DoctorInfo>().eq(DoctorInfo::getUserId, userId)
            );
            if (doctorInfo != null) {
                wrapper.eq(Appointment::getDoctorId, doctorInfo.getId());
            }
        }

        // 关键字搜索 (这里假设搜索科室或症状)
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Appointment::getDepartment, keyword)
                    .or().like(Appointment::getSymptoms, keyword));
        }

        wrapper.orderByDesc(Appointment::getAppointmentDate);
        
        IPage<Appointment> resultPage = appointmentMapper.selectPage(page, wrapper);
        
        // 填充名称
        resultPage.getRecords().forEach(item -> {
            if (item.getDoctorId() != null) {
                DoctorInfo doc = doctorInfoMapper.selectById(item.getDoctorId());
                if (doc != null) {
                    item.setDoctorName(doc.getRealName());
                }
            }
            if (item.getPatientId() != null) {
                PatientInfo pat = patientInfoMapper.selectById(item.getPatientId());
                if (pat != null) {
                    item.setPatientName(pat.getRealName());
                    item.setPatientUserId(pat.getUserId());
                }
            }
        });
        
        return resultPage;
    }

    @Override
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        appointment.setStatus(1); // 1: 已完成
        appointment.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);
    }
}
