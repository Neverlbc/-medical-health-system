package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.PatientInfo;
import com.medical.system.entity.VitalSigns;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.mapper.VitalSignsMapper;
import com.medical.system.service.VitalSignsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生命体征服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class VitalSignsServiceImpl implements VitalSignsService {

    @Autowired
    private VitalSignsMapper vitalSignsMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Override
    public VitalSigns createVitalSigns(VitalSigns vitalSigns, Long userId) {
        if (vitalSigns.getPatientId() == null) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo == null) {
                throw new BusinessException("患者信息不存在");
            }
            vitalSigns.setPatientId(patientInfo.getId());
        }

        // 自动计算 BMI
        if (vitalSigns.getHeight() != null && vitalSigns.getWeight() != null) {
            BigDecimal heightInMeters = vitalSigns.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = vitalSigns.getWeight().divide(
                    heightInMeters.multiply(heightInMeters), 2, RoundingMode.HALF_UP);
            vitalSigns.setBmi(bmi);
        }

        if (vitalSigns.getMeasureTime() == null) {
            vitalSigns.setMeasureTime(LocalDateTime.now());
        }
        vitalSigns.setCreateTime(LocalDateTime.now());
        vitalSignsMapper.insert(vitalSigns);
        return vitalSigns;
    }

    @Override
    public VitalSigns updateVitalSigns(VitalSigns vitalSigns) {
        VitalSigns exist = vitalSignsMapper.selectById(vitalSigns.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }

        // 重新计算 BMI
        if (vitalSigns.getHeight() != null && vitalSigns.getWeight() != null) {
            BigDecimal heightInMeters = vitalSigns.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = vitalSigns.getWeight().divide(
                    heightInMeters.multiply(heightInMeters), 2, RoundingMode.HALF_UP);
            vitalSigns.setBmi(bmi);
        }

        vitalSignsMapper.updateById(vitalSigns);
        return vitalSignsMapper.selectById(vitalSigns.getId());
    }

    @Override
    public void deleteVitalSigns(Long id) {
        vitalSignsMapper.deleteById(id);
    }

    @Override
    public List<VitalSigns> getVitalSignsList(Long patientId) {
        LambdaQueryWrapper<VitalSigns> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VitalSigns::getPatientId, patientId);
        wrapper.orderByDesc(VitalSigns::getMeasureTime);
        return vitalSignsMapper.selectList(wrapper);
    }

    @Override
    public Page<VitalSigns> getVitalSignsPage(Page<VitalSigns> page, Long patientId) {
        LambdaQueryWrapper<VitalSigns> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VitalSigns::getPatientId, patientId);
        wrapper.orderByDesc(VitalSigns::getMeasureTime);
        return vitalSignsMapper.selectPage(page, wrapper);
    }
}
