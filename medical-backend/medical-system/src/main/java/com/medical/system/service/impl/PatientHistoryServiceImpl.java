package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.PatientHistory;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.PatientHistoryMapper;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.service.PatientHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 病史信息服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class PatientHistoryServiceImpl implements PatientHistoryService {

    @Autowired
    private PatientHistoryMapper patientHistoryMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Override
    public PatientHistory createHistory(PatientHistory history, Long userId) {
        // 如果没有指定 patientId，尝试从 userId 获取
        if (history.getPatientId() == null) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo == null) {
                throw new BusinessException("患者信息不存在");
            }
            history.setPatientId(patientInfo.getId());
        }

        history.setCreateTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());
        history.setStatus(1);
        patientHistoryMapper.insert(history);
        return history;
    }

    @Override
    public PatientHistory updateHistory(PatientHistory history) {
        PatientHistory exist = patientHistoryMapper.selectById(history.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }
        
        history.setUpdateTime(LocalDateTime.now());
        patientHistoryMapper.updateById(history);
        return patientHistoryMapper.selectById(history.getId());
    }

    @Override
    public void deleteHistory(Long id) {
        patientHistoryMapper.deleteById(id);
    }

    @Override
    public List<PatientHistory> getHistoryList(Long patientId, String type) {
        LambdaQueryWrapper<PatientHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientHistory::getPatientId, patientId);
        if (StringUtils.hasText(type)) {
            wrapper.eq(PatientHistory::getHistoryType, type);
        }
        wrapper.orderByDesc(PatientHistory::getCreateTime);
        return patientHistoryMapper.selectList(wrapper);
    }
}
