package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.PatientInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者信息Mapper
 *
 * @author lbc
 * @date 2025-11-06
 */
@Mapper
public interface PatientInfoMapper extends BaseMapper<PatientInfo> {
}

