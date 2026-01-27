package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * 医生排班 Mapper
 *
 * @author Antigravity
 * @date 2025-11-06
 */
@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {

    /**
     * 根据医生ID和日期范围查询排班
     */
    @Select("SELECT ds.*, di.real_name AS doctor_name, di.department, di.title " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor_info di ON ds.doctor_id = di.id " +
            "WHERE ds.doctor_id = #{doctorId} " +
            "AND ds.schedule_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY ds.schedule_date, ds.time_period")
    List<DoctorSchedule> findByDoctorIdAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 根据科室和日期范围查询排班
     */
    @Select("SELECT ds.*, di.real_name AS doctor_name, di.department, di.title " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor_info di ON ds.doctor_id = di.id " +
            "WHERE di.department = #{department} " +
            "AND ds.schedule_date BETWEEN #{startDate} AND #{endDate} " +
            "AND ds.status = 1 " +
            "ORDER BY ds.schedule_date, di.real_name, ds.time_period")
    List<DoctorSchedule> findByDepartmentAndDateRange(
            @Param("department") String department,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 查询指定医生、日期、时段的排班
     */
    @Select("SELECT * FROM doctor_schedule " +
            "WHERE doctor_id = #{doctorId} " +
            "AND schedule_date = #{scheduleDate} " +
            "AND time_period = #{timePeriod}")
    DoctorSchedule findByDoctorDatePeriod(
            @Param("doctorId") Long doctorId,
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("timePeriod") String timePeriod
    );

    /**
     * 增加已预约数
     */
    @Update("UPDATE doctor_schedule " +
            "SET booked_patients = booked_patients + 1, update_time = NOW() " +
            "WHERE doctor_id = #{doctorId} " +
            "AND schedule_date = #{scheduleDate} " +
            "AND time_period = #{timePeriod} " +
            "AND booked_patients < max_patients")
    int incrementBookedPatients(
            @Param("doctorId") Long doctorId,
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("timePeriod") String timePeriod
    );

    /**
     * 减少已预约数
     */
    @Update("UPDATE doctor_schedule " +
            "SET booked_patients = booked_patients - 1, update_time = NOW() " +
            "WHERE doctor_id = #{doctorId} " +
            "AND schedule_date = #{scheduleDate} " +
            "AND time_period = #{timePeriod} " +
            "AND booked_patients > 0")
    int decrementBookedPatients(
            @Param("doctorId") Long doctorId,
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("timePeriod") String timePeriod
    );

    /**
     * 综合查询排班列表（管理员用）
     */
    @Select("<script>" +
            "SELECT ds.*, di.real_name AS doctor_name, di.department, di.title " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor_info di ON ds.doctor_id = di.id " +
            "WHERE 1=1 " +
            "<if test='doctorId != null'> AND ds.doctor_id = #{doctorId} </if>" +
            "<if test='department != null and department != \"\"'> AND di.department = #{department} </if>" +
            "<if test='startDate != null'> AND ds.schedule_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND ds.schedule_date &lt;= #{endDate} </if>" +
            "<if test='status != null'> AND ds.status = #{status} </if>" +
            "ORDER BY ds.schedule_date DESC, di.department, di.real_name, ds.time_period" +
            "</script>")
    List<DoctorSchedule> listSchedules(
            @Param("doctorId") Long doctorId,
            @Param("department") String department,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Integer status
    );
}
