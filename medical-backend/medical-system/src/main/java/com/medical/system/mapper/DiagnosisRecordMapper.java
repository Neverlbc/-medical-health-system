package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.DiagnosisRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 诊断记录 Mapper
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Mapper
public interface DiagnosisRecordMapper extends BaseMapper<DiagnosisRecord> {
    @org.apache.ibatis.annotations.Select("SELECT disease_name as name, COUNT(*) as value FROM diagnosis_record GROUP BY disease_name ORDER BY value DESC LIMIT 10")
    java.util.List<com.medical.system.dto.statistics.DiseaseStatVO> selectDiseaseDistribution();
}
