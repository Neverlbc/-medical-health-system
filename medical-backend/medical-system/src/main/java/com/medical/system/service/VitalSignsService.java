package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.VitalSigns;

import java.util.List;

/**
 * 生命体征服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface VitalSignsService {

    /**
     * 创建生命体征记录
     */
    VitalSigns createVitalSigns(VitalSigns vitalSigns, Long userId);

    /**
     * 更新生命体征记录
     */
    VitalSigns updateVitalSigns(VitalSigns vitalSigns);

    /**
     * 删除生命体征记录
     */
    void deleteVitalSigns(Long id);

    /**
     * 获取患者的生命体征列表
     */
    List<VitalSigns> getVitalSignsList(Long patientId);

    /**
     * 分页查询生命体征
     */
    Page<VitalSigns> getVitalSignsPage(Page<VitalSigns> page, Long patientId);
}
