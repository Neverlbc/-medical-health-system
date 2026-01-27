package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.DoctorInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生信息Mapper
 *
 * @author lbc
 * @date 2025-11-06
 */
@Mapper
public interface DoctorInfoMapper extends BaseMapper<DoctorInfo> {
}

