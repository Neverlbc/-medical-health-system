package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.HealthData;

import java.util.List;
import java.util.Map;

/**
 * 健康数据服务接口
 *
 * @author 刘柏城
 * @date 2025-11-24
 */
public interface HealthDataService {

    /**
     * 创建健康数据记录
     *
     * @param healthData 健康数据
     * @param userId 当前用户ID
     * @return 创建的健康数据
     */
    HealthData createHealthData(HealthData healthData, Long userId);

    /**
     * 分页查询健康数据
     *
     * @param current 当前页
     * @param size 每页大小
     * @param userId 当前用户ID
     * @param userRole 用户角色
     * @param dataType 数据类型（可选）
     * @param patientId 患者ID（医生查询时使用）
     * @return 分页结果
     */
    Page<HealthData> getHealthDataPage(Integer current, Integer size, Long userId, String userRole, String dataType, Long patientId);

    /**
     * 根据ID获取健康数据详情
     *
     * @param id 数据ID
     * @param userId 当前用户ID
     * @param userRole 用户角色
     * @return 健康数据
     */
    HealthData getHealthDataById(Long id, Long userId, String userRole);

    /**
     * 删除健康数据
     *
     * @param id 数据ID
     * @param userId 当前用户ID
     * @param userRole 用户角色
     */
    void deleteHealthData(Long id, Long userId, String userRole);

    /**
     * 获取健康数据趋势（用于图表）
     *
     * @param userId 当前用户ID
     * @param userRole 用户角色
     * @param dataType 数据类型
     * @param patientId 患者ID（医生查询时使用）
     * @param days 最近天数（7/30/90）
     * @return 趋势数据列表
     */
    List<HealthData> getHealthDataTrend(Long userId, String userRole, String dataType, Long patientId, Integer days);

    /**
     * 获取健康数据统计信息
     *
     * @param userId 当前用户ID
     * @param userRole 用户角色
     * @param patientId 患者ID（医生查询时使用）
     * @return 统计信息
     */
    Map<String, Object> getHealthDataStatistics(Long userId, String userRole, Long patientId);
}
