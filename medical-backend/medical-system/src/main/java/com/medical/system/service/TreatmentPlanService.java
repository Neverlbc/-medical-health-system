package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.TreatmentPlan;

import java.util.List;

/**
 * 治疗方案服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface TreatmentPlanService {

    /**
     * 创建治疗方案
     */
    TreatmentPlan createTreatmentPlan(TreatmentPlan treatmentPlan, Long doctorId);

    /**
     * 更新治疗方案
     */
    TreatmentPlan updateTreatmentPlan(TreatmentPlan treatmentPlan);

    /**
     * 删除治疗方案
     */
    void deleteTreatmentPlan(Long id);

    /**
     * 获取患者的治疗方案列表
     */
    List<TreatmentPlan> getTreatmentPlanList(Long patientId);

    /**
     * 分页查询治疗方案
     */
    Page<TreatmentPlan> getTreatmentPlanPage(Page<TreatmentPlan> page, Long patientId, String status);
}
