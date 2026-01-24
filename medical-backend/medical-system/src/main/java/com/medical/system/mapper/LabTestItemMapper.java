package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.LabTestItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实验室检查明细 Mapper
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Mapper
public interface LabTestItemMapper extends BaseMapper<LabTestItem> {
}
