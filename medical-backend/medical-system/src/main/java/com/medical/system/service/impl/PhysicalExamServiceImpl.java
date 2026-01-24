package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.PhysicalExam;
import com.medical.system.mapper.PhysicalExamMapper;
import com.medical.system.service.PhysicalExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 体格检查服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class PhysicalExamServiceImpl implements PhysicalExamService {

    @Autowired
    private PhysicalExamMapper physicalExamMapper;

    @Override
    public PhysicalExam createPhysicalExam(PhysicalExam physicalExam, Long doctorId) {
        physicalExam.setDoctorId(doctorId);
        physicalExam.setCreateTime(LocalDateTime.now());
        physicalExamMapper.insert(physicalExam);
        return physicalExam;
    }

    @Override
    public PhysicalExam updatePhysicalExam(PhysicalExam physicalExam) {
        PhysicalExam exist = physicalExamMapper.selectById(physicalExam.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }
        physicalExamMapper.updateById(physicalExam);
        return physicalExamMapper.selectById(physicalExam.getId());
    }

    @Override
    public void deletePhysicalExam(Long id) {
        physicalExamMapper.deleteById(id);
    }

    @Override
    public List<PhysicalExam> getPhysicalExamList(Long patientId) {
        LambdaQueryWrapper<PhysicalExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhysicalExam::getPatientId, patientId);
        wrapper.orderByDesc(PhysicalExam::getExamDate);
        return physicalExamMapper.selectList(wrapper);
    }

    @Override
    public Page<PhysicalExam> getPhysicalExamPage(Page<PhysicalExam> page, Long patientId) {
        LambdaQueryWrapper<PhysicalExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhysicalExam::getPatientId, patientId);
        wrapper.orderByDesc(PhysicalExam::getExamDate);
        return physicalExamMapper.selectPage(page, wrapper);
    }
}
