package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.system.entity.PatientHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 病史信息 Mapper
 *
 * @author Antigravity
 * @date 2025-11-24
 */
@Mapper
public interface PatientHistoryMapper extends BaseMapper<PatientHistory> {
}
