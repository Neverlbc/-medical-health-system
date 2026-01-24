package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.PhysicalExam;

import java.util.List;

/**
 * 体格检查服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface PhysicalExamService {

    /**
     * 创建体格检查记录
     */
    PhysicalExam createPhysicalExam(PhysicalExam physicalExam, Long doctorId);

    /**
     * 更新体格检查记录
     */
    PhysicalExam updatePhysicalExam(PhysicalExam physicalExam);

    /**
     * 删除体格检查记录
     */
    void deletePhysicalExam(Long id);

    /**
     * 获取患者的体格检查列表
     */
    List<PhysicalExam> getPhysicalExamList(Long patientId);

    /**
     * 分页查询体格检查
     */
    Page<PhysicalExam> getPhysicalExamPage(Page<PhysicalExam> page, Long patientId);
}
