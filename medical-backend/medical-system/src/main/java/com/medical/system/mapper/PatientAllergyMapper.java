package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.PatientAllergy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 过敏史 Mapper
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Mapper
public interface PatientAllergyMapper extends BaseMapper<PatientAllergy> {
}
