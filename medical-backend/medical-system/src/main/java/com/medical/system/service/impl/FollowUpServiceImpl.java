package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.FollowUp;
import com.medical.system.mapper.FollowUpMapper;
import com.medical.system.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 随访记录服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class FollowUpServiceImpl implements FollowUpService {

    @Autowired
    private FollowUpMapper followUpMapper;

    @Override
    public FollowUp createFollowUp(FollowUp followUp, Long doctorId) {
        followUp.setDoctorId(doctorId);
        followUp.setCreateTime(LocalDateTime.now());
        followUpMapper.insert(followUp);
        return followUp;
    }

    @Override
    public FollowUp updateFollowUp(FollowUp followUp) {
        FollowUp exist = followUpMapper.selectById(followUp.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }
        followUpMapper.updateById(followUp);
        return followUpMapper.selectById(followUp.getId());
    }

    @Override
    public void deleteFollowUp(Long id) {
        followUpMapper.deleteById(id);
    }

    @Override
    public List<FollowUp> getFollowUpList(Long patientId) {
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowUp::getPatientId, patientId);
        wrapper.orderByDesc(FollowUp::getFollowUpDate);
        return followUpMapper.selectList(wrapper);
    }

    @Override
    public Page<FollowUp> getFollowUpPage(Page<FollowUp> page, Long patientId) {
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowUp::getPatientId, patientId);
        wrapper.orderByDesc(FollowUp::getFollowUpDate);
        return followUpMapper.selectPage(page, wrapper);
    }
}
