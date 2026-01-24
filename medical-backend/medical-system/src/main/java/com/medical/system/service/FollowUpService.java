package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.FollowUp;

import java.util.List;

/**
 * 随访记录服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface FollowUpService {

    /**
     * 创建随访记录
     */
    FollowUp createFollowUp(FollowUp followUp, Long doctorId);

    /**
     * 更新随访记录
     */
    FollowUp updateFollowUp(FollowUp followUp);

    /**
     * 删除随访记录
     */
    void deleteFollowUp(Long id);

    /**
     * 获取患者的随访记录列表
     */
    List<FollowUp> getFollowUpList(Long patientId);

    /**
     * 分页查询随访记录
     */
    Page<FollowUp> getFollowUpPage(Page<FollowUp> page, Long patientId);
}
