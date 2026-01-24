package com.medical.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.Appointment;

/**
 * 预约服务接口
 *
 * @author Antigravity
 * @date 2025-11-27
 */
public interface AppointmentService {

    /**
     * 创建预约
     *
     * @param appointment 预约信息
     * @param userId      当前用户ID
     */
    void createAppointment(Appointment appointment, Long userId);

    /**
     * 取消预约
     *
     * @param id     预约ID
     * @param userId 当前用户ID
     */
    void cancelAppointment(Long id, Long userId);

    /**
     * 分页查询预约列表
     *
     * @param page    分页参数
     * @param userId  当前用户ID
     * @param role    当前用户角色
     * @param keyword 搜索关键字
     * @return 分页结果
     */
    IPage<Appointment> getAppointmentPage(Page<Appointment> page, Long userId, String role, String keyword);

    /**
     * 完成预约
     * @param id 预约ID
     */
    void completeAppointment(Long id);
}
