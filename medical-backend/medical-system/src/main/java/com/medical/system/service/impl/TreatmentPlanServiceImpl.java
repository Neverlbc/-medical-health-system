package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.TreatmentPlan;
import com.medical.system.mapper.TreatmentPlanMapper;
import com.medical.system.service.TreatmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 治疗方案服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class TreatmentPlanServiceImpl implements TreatmentPlanService {

    @Autowired
    private TreatmentPlanMapper treatmentPlanMapper;

    @Override
    public TreatmentPlan createTreatmentPlan(TreatmentPlan treatmentPlan, Long doctorId) {
        treatmentPlan.setDoctorId(doctorId);
        treatmentPlan.setCreateTime(LocalDateTime.now());
        if (!StringUtils.hasText(treatmentPlan.getStatus())) {
            treatmentPlan.setStatus("ACTIVE");
        }
        treatmentPlanMapper.insert(treatmentPlan);
        return treatmentPlan;
    }

    @Override
    public TreatmentPlan updateTreatmentPlan(TreatmentPlan treatmentPlan) {
        TreatmentPlan exist = treatmentPlanMapper.selectById(treatmentPlan.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }
        treatmentPlanMapper.updateById(treatmentPlan);
        return treatmentPlanMapper.selectById(treatmentPlan.getId());
    }

    @Override
    public void deleteTreatmentPlan(Long id) {
        treatmentPlanMapper.deleteById(id);
    }

    @Override
    public List<TreatmentPlan> getTreatmentPlanList(Long patientId) {
        LambdaQueryWrapper<TreatmentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TreatmentPlan::getPatientId, patientId);
        wrapper.orderByDesc(TreatmentPlan::getStartDate);
        return treatmentPlanMapper.selectList(wrapper);
    }

    @Override
    public Page<TreatmentPlan> getTreatmentPlanPage(Page<TreatmentPlan> page, Long patientId, String status) {
        LambdaQueryWrapper<TreatmentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TreatmentPlan::getPatientId, patientId);
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(TreatmentPlan::getStatus, status);
        }
        
        wrapper.orderByDesc(TreatmentPlan::getStartDate);
        return treatmentPlanMapper.selectPage(page, wrapper);
    }
}
