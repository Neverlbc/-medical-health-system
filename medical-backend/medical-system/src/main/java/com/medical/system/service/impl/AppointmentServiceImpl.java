package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.DoctorInfo;
import com.medical.system.entity.DoctorSchedule;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.AppointmentMapper;
import com.medical.system.mapper.DoctorInfoMapper;
import com.medical.system.mapper.DoctorScheduleMapper;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约服务实现类
 *
 * @author Antigravity
 * @date 2025-11-27
 */
@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    @Autowired
    private DoctorScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public void createAppointment(Appointment appointment, Long userId) {
        // 1. 获取患者信息
        PatientInfo patientInfo = patientInfoMapper.selectOne(
                new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
        );
        if (patientInfo == null) {
            throw new BusinessException("未找到患者档案信息，请先完善个人信息");
        }

        // 2. 校验医生是否存在
        if (appointment.getDoctorId() == null) {
            throw new BusinessException("请选择医生");
        }
        DoctorInfo doctorInfo = doctorInfoMapper.selectById(appointment.getDoctorId());
        if (doctorInfo == null) {
            throw new BusinessException("医生不存在");
        }

        // 3. 校验预约日期和时段
        if (appointment.getAppointmentDate() == null) {
            throw new BusinessException("请选择预约日期");
        }
        if (!StringUtils.hasText(appointment.getAppointmentTime())) {
            throw new BusinessException("请选择预约时段");
        }

        // 4. 检查是否重复预约（同一患者同一天同一时段只能预约一次）
        Long existingCount = appointmentMapper.selectCount(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getPatientId, patientInfo.getId())
                        .eq(Appointment::getAppointmentDate, appointment.getAppointmentDate())
                        .eq(Appointment::getAppointmentTime, appointment.getAppointmentTime())
                        .eq(Appointment::getStatus, 0) // 待就诊状态
        );
        if (existingCount > 0) {
            throw new BusinessException("您在该时段已有预约，请勿重复挂号");
        }

        // 5. 检查排班号源
        LocalDate appointmentLocalDate = appointment.getAppointmentDate();
        DoctorSchedule schedule = scheduleMapper.findByDoctorDatePeriod(
                appointment.getDoctorId(), 
                appointmentLocalDate, 
                appointment.getAppointmentTime()
        );
        
        if (schedule == null) {
            throw new BusinessException("该医生在该时段暂无排班");
        }
        if (schedule.getStatus() != 1) {
            throw new BusinessException("该时段停诊，请选择其他时段");
        }
        if (schedule.getBookedPatients() >= schedule.getMaxPatients()) {
            throw new BusinessException("该时段号源已满，请选择其他时段或医生");
        }

        // 6. 设置基本信息
        appointment.setPatientId(patientInfo.getId());
        appointment.setStatus(0); // 0: 待就诊
        appointment.setDepartment(doctorInfo.getDepartment());
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        // 7. 创建预约（使用乐观锁更新排班）
        int updated = scheduleMapper.incrementBookedPatients(
                appointment.getDoctorId(),
                appointmentLocalDate,
                appointment.getAppointmentTime()
        );
        if (updated == 0) {
            throw new BusinessException("号源已被抢完，请选择其他时段");
        }

        appointmentMapper.insert(appointment);
        log.info("预约创建成功: 患者ID={}, 医生ID={}, 日期={}, 时段={}", 
                patientInfo.getId(), appointment.getDoctorId(), 
                appointmentLocalDate, appointment.getAppointmentTime());
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id, Long userId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        // 只能取消待就诊状态的预约
        if (appointment.getStatus() != 0) {
            throw new BusinessException("只能取消待就诊的预约");
        }

        // 简单权限校验：只能取消自己的预约
        PatientInfo patientInfo = patientInfoMapper.selectOne(
                new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
        );
        
        // 如果是患者，只能取消自己的预约
        if (patientInfo != null && !appointment.getPatientId().equals(patientInfo.getId())) {
            // 检查是否是医生或管理员（可以取消自己科室的预约）
            DoctorInfo doctorInfo = doctorInfoMapper.selectOne(
                    new LambdaQueryWrapper<DoctorInfo>().eq(DoctorInfo::getUserId, userId)
            );
            if (doctorInfo == null || !appointment.getDoctorId().equals(doctorInfo.getId())) {
                throw new BusinessException("无权取消该预约");
            }
        }

        // 更新预约状态
        appointment.setStatus(2); // 2: 已取消
        appointment.setCancelReason("用户主动取消");
        appointment.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        // 恢复排班号源
        scheduleMapper.decrementBookedPatients(
                appointment.getDoctorId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        log.info("预约取消成功: ID={}, 患者ID={}, 医生ID={}", 
                id, appointment.getPatientId(), appointment.getDoctorId());
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
