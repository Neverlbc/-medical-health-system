package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.HealthData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康数据Mapper
 *
 * @author lbc
 * @date 2025-11-06
 */
@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {
}

