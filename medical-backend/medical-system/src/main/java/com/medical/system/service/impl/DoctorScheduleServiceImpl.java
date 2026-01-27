package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.system.entity.DoctorInfo;
import com.medical.system.entity.DoctorSchedule;
import com.medical.system.mapper.DoctorInfoMapper;
import com.medical.system.mapper.DoctorScheduleMapper;
import com.medical.system.service.DoctorScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 医生排班服务实现类
 *
 * @author Antigravity
 * @date 2025-11-06
 */
@Slf4j
@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    @Autowired
    private DoctorScheduleMapper scheduleMapper;

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    private static final List<String> TIME_PERIODS = Arrays.asList("上午", "下午");
    private static final int DEFAULT_MAX_PATIENTS = 20;

    @Override
    public List<DoctorSchedule> getScheduleByDoctor(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return scheduleMapper.findByDoctorIdAndDateRange(doctorId, startDate, endDate);
    }

    @Override
    public List<DoctorSchedule> getScheduleByDepartment(String department, LocalDate startDate, LocalDate endDate) {
        return scheduleMapper.findByDepartmentAndDateRange(department, startDate, endDate);
    }

    @Override
    public boolean checkAvailability(Long doctorId, LocalDate scheduleDate, String timePeriod) {
        DoctorSchedule schedule = scheduleMapper.findByDoctorDatePeriod(doctorId, scheduleDate, timePeriod);
        if (schedule == null) {
            // 如果没有排班记录，自动生成
            generateScheduleForDate(doctorId, scheduleDate);
            schedule = scheduleMapper.findByDoctorDatePeriod(doctorId, scheduleDate, timePeriod);
        }
        return schedule != null && schedule.isAvailable();
    }

    @Override
    public DoctorSchedule getScheduleDetail(Long doctorId, LocalDate scheduleDate, String timePeriod) {
        DoctorSchedule schedule = scheduleMapper.findByDoctorDatePeriod(doctorId, scheduleDate, timePeriod);
        if (schedule == null) {
            // 如果没有排班记录，尝试自动生成
            generateScheduleForDate(doctorId, scheduleDate);
            schedule = scheduleMapper.findByDoctorDatePeriod(doctorId, scheduleDate, timePeriod);
        }
        return schedule;
    }

    @Override
    @Transactional
    public void generateScheduleIfNotExists(Long doctorId, int days) {
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= days; i++) {
            LocalDate date = today.plusDays(i);
            generateScheduleForDate(doctorId, date);
        }
    }

    @Override
    @Transactional
    public void generateAllDoctorSchedule(int days) {
        // 查询所有正常状态的医生
        List<DoctorInfo> doctors = doctorInfoMapper.selectList(
                new LambdaQueryWrapper<DoctorInfo>().eq(DoctorInfo::getStatus, 1)
        );
        
        for (DoctorInfo doctor : doctors) {
            generateScheduleIfNotExists(doctor.getId(), days);
        }
        
        log.info("已为 {} 位医生生成未来 {} 天的排班", doctors.size(), days);
    }

    /**
     * 为指定医生和日期生成排班
     */
    private void generateScheduleForDate(Long doctorId, LocalDate date) {
        // 检查医生是否存在且正常
        DoctorInfo doctor = doctorInfoMapper.selectById(doctorId);
        if (doctor == null || doctor.getStatus() != 1) {
            return;
        }

        for (String period : TIME_PERIODS) {
            // 检查是否已存在排班
            DoctorSchedule existing = scheduleMapper.findByDoctorDatePeriod(doctorId, date, period);
            if (existing == null) {
                // 创建新排班
                DoctorSchedule schedule = new DoctorSchedule();
                schedule.setDoctorId(doctorId);
                schedule.setScheduleDate(date);
                schedule.setTimePeriod(period);
                schedule.setMaxPatients(DEFAULT_MAX_PATIENTS);
                schedule.setBookedPatients(0);
                schedule.setStatus(1); // 默认可预约
                schedule.setCreateTime(LocalDateTime.now());
                schedule.setUpdateTime(LocalDateTime.now());
                
                scheduleMapper.insert(schedule);
                log.debug("为医生 {} 生成 {} {} 排班", doctorId, date, period);
            }
        }
    }

    // ========== 管理员排班管理接口实现 ==========

    @Override
    public List<DoctorSchedule> listSchedules(Long doctorId, String department, 
                                              LocalDate startDate, LocalDate endDate, Integer status) {
        return scheduleMapper.listSchedules(doctorId, department, startDate, endDate, status);
    }

    @Override
    public DoctorSchedule getById(Long id) {
        DoctorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule != null) {
            // 附加医生信息
            DoctorInfo doctor = doctorInfoMapper.selectById(schedule.getDoctorId());
            if (doctor != null) {
                schedule.setDoctorName(doctor.getRealName());
                schedule.setDepartment(doctor.getDepartment());
                schedule.setTitle(doctor.getTitle());
            }
        }
        return schedule;
    }

    @Override
    @Transactional
    public DoctorSchedule createSchedule(DoctorSchedule schedule) {
        // 验证医生是否存在
        DoctorInfo doctor = doctorInfoMapper.selectById(schedule.getDoctorId());
        if (doctor == null) {
            throw new RuntimeException("医生不存在");
        }

        // 检查是否已存在相同的排班
        DoctorSchedule existing = scheduleMapper.findByDoctorDatePeriod(
            schedule.getDoctorId(), schedule.getScheduleDate(), schedule.getTimePeriod());
        if (existing != null) {
            throw new RuntimeException("该时段已存在排班");
        }

        // 设置默认值
        if (schedule.getMaxPatients() == null) {
            schedule.setMaxPatients(DEFAULT_MAX_PATIENTS);
        }
        if (schedule.getBookedPatients() == null) {
            schedule.setBookedPatients(0);
        }
        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }
        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());

        scheduleMapper.insert(schedule);
        log.info("创建排班: 医生={}, 日期={}, 时段={}", schedule.getDoctorId(), 
                schedule.getScheduleDate(), schedule.getTimePeriod());
        
        return getById(schedule.getId());
    }

    @Override
    @Transactional
    public DoctorSchedule updateSchedule(DoctorSchedule schedule) {
        DoctorSchedule existing = scheduleMapper.selectById(schedule.getId());
        if (existing == null) {
            throw new RuntimeException("排班不存在");
        }

        // 不允许修改已有预约的排班的最大人数到少于已预约数
        if (schedule.getMaxPatients() != null && 
            schedule.getMaxPatients() < existing.getBookedPatients()) {
            throw new RuntimeException("最大接诊数不能少于已预约数 (" + existing.getBookedPatients() + ")");
        }

        // 更新允许修改的字段
        if (schedule.getMaxPatients() != null) {
            existing.setMaxPatients(schedule.getMaxPatients());
        }
        if (schedule.getStatus() != null) {
            existing.setStatus(schedule.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());

        scheduleMapper.updateById(existing);
        log.info("更新排班: id={}", schedule.getId());
        
        return getById(schedule.getId());
    }

    @Override
    @Transactional
    public boolean deleteSchedule(Long id) {
        DoctorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            return false;
        }

        // 检查是否有预约
        if (schedule.getBookedPatients() != null && schedule.getBookedPatients() > 0) {
            throw new RuntimeException("该排班已有预约，无法删除");
        }

        int result = scheduleMapper.deleteById(id);
        log.info("删除排班: id={}", id);
        return result > 0;
    }

    @Override
    @Transactional
    public int batchCreateSchedule(Long doctorId, LocalDate startDate, LocalDate endDate,
                                   List<String> timePeriods, Integer maxPatients) {
        // 验证医生是否存在
        DoctorInfo doctor = doctorInfoMapper.selectById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("医生不存在");
        }

        if (timePeriods == null || timePeriods.isEmpty()) {
            timePeriods = TIME_PERIODS;
        }

        int count = 0;
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            for (String period : timePeriods) {
                // 检查是否已存在
                DoctorSchedule existing = scheduleMapper.findByDoctorDatePeriod(doctorId, currentDate, period);
                if (existing == null) {
                    DoctorSchedule schedule = new DoctorSchedule();
                    schedule.setDoctorId(doctorId);
                    schedule.setScheduleDate(currentDate);
                    schedule.setTimePeriod(period);
                    schedule.setMaxPatients(maxPatients != null ? maxPatients : DEFAULT_MAX_PATIENTS);
                    schedule.setBookedPatients(0);
                    schedule.setStatus(1);
                    schedule.setCreateTime(LocalDateTime.now());
                    schedule.setUpdateTime(LocalDateTime.now());
                    scheduleMapper.insert(schedule);
                    count++;
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("批量创建排班: 医生={}, 日期范围={} 至 {}, 创建数量={}", 
                doctorId, startDate, endDate, count);
        return count;
    }

    @Override
    @Transactional
    public int batchDeleteSchedule(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Long id : ids) {
            DoctorSchedule schedule = scheduleMapper.selectById(id);
            if (schedule != null && (schedule.getBookedPatients() == null || schedule.getBookedPatients() == 0)) {
                scheduleMapper.deleteById(id);
                count++;
            }
        }
        
        log.info("批量删除排班: 请求数量={}, 实际删除={}", ids.size(), count);
        return count;
    }

    @Override
    @Transactional
    public DoctorSchedule toggleStatus(Long id, Integer status) {
        DoctorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new RuntimeException("排班不存在");
        }

        schedule.setStatus(status);
        schedule.setUpdateTime(LocalDateTime.now());
        scheduleMapper.updateById(schedule);
        
        log.info("切换排班状态: id={}, status={}", id, status);
        return getById(id);
    }
}
