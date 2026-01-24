package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约记录Mapper
 *
 * @author 刘柏城
 * @date 2025-11-06
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
    @org.apache.ibatis.annotations.Select("SELECT DATE_FORMAT(appointment_date, '%Y-%m-%d') as date, COUNT(*) as count FROM appointment WHERE appointment_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY date ORDER BY date")
    java.util.List<com.medical.system.dto.statistics.TrendStatVO> selectAppointmentTrend();
}

