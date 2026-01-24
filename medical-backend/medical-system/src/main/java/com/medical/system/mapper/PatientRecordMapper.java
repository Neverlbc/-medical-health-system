package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.PatientRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientRecordMapper extends BaseMapper<PatientRecord> {
}

