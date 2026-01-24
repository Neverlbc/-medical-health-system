package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.LabTest;
import com.medical.system.entity.LabTestItem;

import java.util.List;

/**
 * 实验室检查服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface LabTestService {

    /**
     * 创建实验室检查记录（含明细）
     */
    LabTest createLabTest(LabTest labTest, List<LabTestItem> items, Long doctorId);

    /**
     * 更新实验室检查记录
     */
    LabTest updateLabTest(LabTest labTest);

    /**
     * 删除实验室检查记录
     */
    void deleteLabTest(Long id);

    /**
     * 获取患者的实验室检查列表
     */
    List<LabTest> getLabTestList(Long patientId);

    /**
     * 分页查询实验室检查
     */
    Page<LabTest> getLabTestPage(Page<LabTest> page, Long patientId, String keyword);

    /**
     * 获取检查明细
     */
    List<LabTestItem> getLabTestItems(Long testId);

    /**
     * 添加检查明细
     */
    void addLabTestItem(LabTestItem item);

    /**
     * 删除检查明细
     */
    void deleteLabTestItem(Long itemId);
}
