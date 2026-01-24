package com.medical.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.PatientHistory;

import java.util.List;

/**
 * 病史信息服务接口
 *
 * @author Antigravity
 * @date 2025-11-24
 */
public interface PatientHistoryService {

    /**
     * 创建病史记录
     */
    PatientHistory createHistory(PatientHistory history, Long userId);

    /**
     * 更新病史记录
     */
    PatientHistory updateHistory(PatientHistory history);

    /**
     * 删除病史记录
     */
    void deleteHistory(Long id);

    /**
     * 获取患者的所有病史记录
     */
    List<PatientHistory> getHistoryList(Long patientId, String type);
}
