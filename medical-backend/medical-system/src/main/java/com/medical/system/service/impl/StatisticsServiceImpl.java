package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.system.dto.statistics.DiseaseStatVO;
import com.medical.system.dto.statistics.OverviewVO;
import com.medical.system.dto.statistics.TrendStatVO;
import com.medical.system.entity.Appointment;
import com.medical.system.mapper.AppointmentMapper;
import com.medical.system.mapper.DiagnosisRecordMapper;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private PatientInfoMapper patientInfoMapper;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private DiagnosisRecordMapper diagnosisRecordMapper;

    @Override
    public OverviewVO getOverview() {
        OverviewVO vo = new OverviewVO();
        
        // 1. 总患者数
        vo.setTotalPatients(patientInfoMapper.selectCount(null));
        
        // 2. 今日待诊预约数（status=0 表示待就诊）
        vo.setTodayAppointments(appointmentMapper.selectCount(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getAppointmentDate, LocalDate.now())
                        .eq(Appointment::getStatus, 0)
        ));
        
        // 3. 总诊断记录数
        vo.setTotalDiagnoses(diagnosisRecordMapper.selectCount(null));
        
        return vo;
    }

    @Override
    public List<DiseaseStatVO> getDiseaseDistribution() {
        return diagnosisRecordMapper.selectDiseaseDistribution();
    }

    @Override
    public List<TrendStatVO> getAppointmentTrend() {
        return appointmentMapper.selectAppointmentTrend();
    }
}
