package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.LabTest;
import com.medical.system.entity.LabTestItem;
import com.medical.system.mapper.LabTestItemMapper;
import com.medical.system.mapper.LabTestMapper;
import com.medical.system.service.LabTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实验室检查服务实现
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Service
public class LabTestServiceImpl implements LabTestService {

    @Autowired
    private LabTestMapper labTestMapper;

    @Autowired
    private LabTestItemMapper labTestItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabTest createLabTest(LabTest labTest, List<LabTestItem> items, Long doctorId) {
        labTest.setDoctorId(doctorId);
        labTest.setCreateTime(LocalDateTime.now());
        labTestMapper.insert(labTest);

        // 插入明细
        if (items != null && !items.isEmpty()) {
            for (LabTestItem item : items) {
                item.setTestId(labTest.getId());
                labTestItemMapper.insert(item);
            }
        }

        return labTest;
    }

    @Override
    public LabTest updateLabTest(LabTest labTest) {
        LabTest exist = labTestMapper.selectById(labTest.getId());
        if (exist == null) {
            throw new BusinessException("记录不存在");
        }
        labTestMapper.updateById(labTest);
        return labTestMapper.selectById(labTest.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLabTest(Long id) {
        // 删除主记录
        labTestMapper.deleteById(id);
        // 删除明细
        LambdaQueryWrapper<LabTestItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LabTestItem::getTestId, id);
        labTestItemMapper.delete(wrapper);
    }

    @Override
    public List<LabTest> getLabTestList(Long patientId) {
        LambdaQueryWrapper<LabTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LabTest::getPatientId, patientId);
        wrapper.orderByDesc(LabTest::getTestDate);
        return labTestMapper.selectList(wrapper);
    }

    @Override
    public Page<LabTest> getLabTestPage(Page<LabTest> page, Long patientId, String keyword) {
        LambdaQueryWrapper<LabTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LabTest::getPatientId, patientId);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(LabTest::getTestType, keyword)
                    .or()
                    .like(LabTest::getHospital, keyword)
                    .or()
                    .like(LabTest::getResultSummary, keyword));
        }

        wrapper.orderByDesc(LabTest::getTestDate);
        return labTestMapper.selectPage(page, wrapper);
    }

    @Override
    public List<LabTestItem> getLabTestItems(Long testId) {
        LambdaQueryWrapper<LabTestItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LabTestItem::getTestId, testId);
        return labTestItemMapper.selectList(wrapper);
    }

    @Override
    public void addLabTestItem(LabTestItem item) {
        labTestItemMapper.insert(item);
    }

    @Override
    public void deleteLabTestItem(Long itemId) {
        labTestItemMapper.deleteById(itemId);
    }
}
