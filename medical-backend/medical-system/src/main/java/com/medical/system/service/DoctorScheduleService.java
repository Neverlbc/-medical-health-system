package com.medical.system.service;

import com.medical.system.entity.DoctorSchedule;

import java.time.LocalDate;
import java.util.List;

/**
 * 医生排班服务接口
 *
 * @author Antigravity
 * @date 2025-11-06
 */
public interface DoctorScheduleService {

    /**
     * 获取医生在指定日期范围内的排班
     *
     * @param doctorId  医生ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 排班列表
     */
    List<DoctorSchedule> getScheduleByDoctor(Long doctorId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取科室在指定日期范围内的排班
     *
     * @param department 科室名称
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 排班列表
     */
    List<DoctorSchedule> getScheduleByDepartment(String department, LocalDate startDate, LocalDate endDate);

    /**
     * 检查指定排班是否可预约
     *
     * @param doctorId     医生ID
     * @param scheduleDate 预约日期
     * @param timePeriod   时段
     * @return true-可预约，false-不可预约
     */
    boolean checkAvailability(Long doctorId, LocalDate scheduleDate, String timePeriod);

    /**
     * 获取指定排班详情
     *
     * @param doctorId     医生ID
     * @param scheduleDate 预约日期
     * @param timePeriod   时段
     * @return 排班详情
     */
    DoctorSchedule getScheduleDetail(Long doctorId, LocalDate scheduleDate, String timePeriod);

    /**
     * 生成未来N天的排班（如果不存在）
     *
     * @param doctorId 医生ID
     * @param days     天数
     */
    void generateScheduleIfNotExists(Long doctorId, int days);

    /**
     * 为所有医生生成未来N天的排班
     *
     * @param days 天数
     */
    void generateAllDoctorSchedule(int days);

    // ========== 管理员排班管理接口 ==========

    /**
     * 分页查询排班列表
     *
     * @param doctorId   医生ID（可选）
     * @param department 科室（可选）
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param status     状态（可选）
     * @return 排班列表
     */
    List<DoctorSchedule> listSchedules(Long doctorId, String department, LocalDate startDate, LocalDate endDate, Integer status);

    /**
     * 根据ID获取排班
     *
     * @param id 排班ID
     * @return 排班信息
     */
    DoctorSchedule getById(Long id);

    /**
     * 创建排班
     *
     * @param schedule 排班信息
     * @return 创建的排班
     */
    DoctorSchedule createSchedule(DoctorSchedule schedule);

    /**
     * 更新排班
     *
     * @param schedule 排班信息
     * @return 更新后的排班
     */
    DoctorSchedule updateSchedule(DoctorSchedule schedule);

    /**
     * 删除排班
     *
     * @param id 排班ID
     * @return 是否删除成功
     */
    boolean deleteSchedule(Long id);

    /**
     * 批量创建排班
     *
     * @param doctorId    医生ID
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @param timePeriods 时段列表
     * @param maxPatients 最大接诊数
     * @return 创建的排班数量
     */
    int batchCreateSchedule(Long doctorId, LocalDate startDate, LocalDate endDate, 
                           List<String> timePeriods, Integer maxPatients);

    /**
     * 批量删除排班
     *
     * @param ids 排班ID列表
     * @return 删除数量
     */
    int batchDeleteSchedule(List<Long> ids);

    /**
     * 切换排班状态（开诊/停诊）
     *
     * @param id     排班ID
     * @param status 新状态
     * @return 更新后的排班
     */
    DoctorSchedule toggleStatus(Long id, Integer status);
}
