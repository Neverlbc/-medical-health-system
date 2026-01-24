package com.medical.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.PatientRecord;

public interface PatientRecordService {
    void create(PatientRecord record, Long currentUserId, String currentRole);
    void update(PatientRecord record, Long currentUserId, String currentRole);
    PatientRecord detail(Long id, Long currentUserId, String currentRole);
    IPage<PatientRecord> pageList(Page<?> page, String keyword, Long filterUserId, Long currentUserId, String currentRole);
}
