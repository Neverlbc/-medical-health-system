package com.medical.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.PatientRecord;
import com.medical.system.mapper.PatientRecordMapper;
import com.medical.system.service.impl.PatientRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 健康档案服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("健康档案服务测试")
class PatientRecordServiceTest {

    @Mock
    private PatientRecordMapper recordMapper;

    @InjectMocks
    private PatientRecordServiceImpl recordService;

    private PatientRecord testRecord;
    private final Long patientUserId = 100L;
    private final Long doctorUserId = 200L;
    private final Long otherPatientUserId = 101L;

    @BeforeEach
    void setUp() {
        testRecord = new PatientRecord();
        testRecord.setId(1L);
        testRecord.setUserId(patientUserId);
        testRecord.setAllergies("青霉素过敏");
        testRecord.setFamilyHistory("父亲有高血压");
        testRecord.setMedicalHistory("无重大疾病史");
        testRecord.setMedicationHistory("无");
        testRecord.setRemark("定期体检");
    }

    @Test
    @DisplayName("患者创建自己的档案")
    void testPatientCreateOwnRecord() {
        // Given
        PatientRecord newRecord = new PatientRecord();
        newRecord.setAllergies("无");
        
        // When
        recordService.create(newRecord, patientUserId, "PATIENT");
        
        // Then
        assertEquals(patientUserId, newRecord.getUserId());
        verify(recordMapper, times(1)).insert(any(PatientRecord.class));
    }

    @Test
    @DisplayName("医生为患者创建档案")
    void testDoctorCreateRecordForPatient() {
        // Given
        PatientRecord newRecord = new PatientRecord();
        newRecord.setUserId(patientUserId);
        newRecord.setAllergies("无");
        
        // When
        recordService.create(newRecord, doctorUserId, "DOCTOR");
        
        // Then
        assertEquals(patientUserId, newRecord.getUserId());
        verify(recordMapper, times(1)).insert(any(PatientRecord.class));
    }

    @Test
    @DisplayName("医生创建档案时未指定患者ID应抛出异常")
    void testDoctorCreateRecordWithoutUserId() {
        // Given
        PatientRecord newRecord = new PatientRecord();
        newRecord.setAllergies("无");
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            recordService.create(newRecord, doctorUserId, "DOCTOR");
        });
        
        assertEquals("请指定患者ID", exception.getMessage());
        verify(recordMapper, never()).insert(any(PatientRecord.class));
    }

    @Test
    @DisplayName("患者更新自己的档案")
    void testPatientUpdateOwnRecord() {
        // Given
        when(recordMapper.selectById(1L)).thenReturn(testRecord);
        testRecord.setAllergies("青霉素、磺胺类过敏");
        
        // When
        recordService.update(testRecord, patientUserId, "PATIENT");
        
        // Then
        verify(recordMapper, times(1)).updateById(testRecord);
    }

    @Test
    @DisplayName("患者尝试更新他人档案应抛出异常")
    void testPatientUpdateOthersRecord() {
        // Given
        when(recordMapper.selectById(1L)).thenReturn(testRecord);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            recordService.update(testRecord, otherPatientUserId, "PATIENT");
        });
        
        assertEquals("无权修改他人档案", exception.getMessage());
        verify(recordMapper, never()).updateById(any(PatientRecord.class));
    }

    @Test
    @DisplayName("医生更新任意档案")
    void testDoctorUpdateAnyRecord() {
        // Given
        when(recordMapper.selectById(1L)).thenReturn(testRecord);
        testRecord.setRemark("医生备注：需要定期复查");
        
        // When
        recordService.update(testRecord, doctorUserId, "DOCTOR");
        
        // Then
        verify(recordMapper, times(1)).updateById(testRecord);
    }

    @Test
    @DisplayName("更新不存在的档案应抛出异常")
    void testUpdateNonExistentRecord() {
        // Given
        when(recordMapper.selectById(999L)).thenReturn(null);
        testRecord.setId(999L);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            recordService.update(testRecord, patientUserId, "PATIENT");
        });
        
        assertEquals("档案不存在", exception.getMessage());
    }

    @Test
    @DisplayName("患者查看自己的档案")
    void testPatientViewOwnRecord() {
        // Given
        when(recordMapper.selectById(1L)).thenReturn(testRecord);
        
        // When
        PatientRecord result = recordService.detail(1L, patientUserId, "PATIENT");
        
        // Then
        assertNotNull(result);
        assertEquals(patientUserId, result.getUserId());
    }

    @Test
    @DisplayName("患者尝试查看他人档案应抛出异常")
    void testPatientViewOthersRecord() {
        // Given
        when(recordMapper.selectById(1L)).thenReturn(testRecord);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            recordService.detail(1L, otherPatientUserId, "PATIENT");
        });
        
        assertEquals("无权查看他人档案", exception.getMessage());
    }

    @Test
    @DisplayName("医生查看任意档案")
    void testDoctorViewAnyRecord() {
        // Given
        when(recordMapper.selectById(1L)).thenReturn(testRecord);
        
        // When
        PatientRecord result = recordService.detail(1L, doctorUserId, "DOCTOR");
        
        // Then
        assertNotNull(result);
        assertEquals(patientUserId, result.getUserId());
    }

    @Test
    @DisplayName("查看不存在的档案应返回null")
    void testViewNonExistentRecord() {
        // Given
        when(recordMapper.selectById(999L)).thenReturn(null);
        
        // When
        PatientRecord result = recordService.detail(999L, patientUserId, "PATIENT");
        
        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("患者分页查询只能看到自己的档案")
    void testPatientPageListOnlyOwnRecords() {
        // Given
        Page<PatientRecord> page = new Page<>(1, 10);
        
        // When
        // Note: 由于涉及 MyBatis-Plus 的复杂查询，这里主要测试方法调用
        // 实际的分页逻辑需要集成测试来验证
        assertDoesNotThrow(() -> {
            recordService.pageList(page, null, null, patientUserId, "PATIENT");
        });
    }

    @Test
    @DisplayName("医生分页查询可以看到所有档案")
    void testDoctorPageListAllRecords() {
        // Given
        Page<PatientRecord> page = new Page<>(1, 10);
        
        // When
        assertDoesNotThrow(() -> {
            recordService.pageList(page, null, null, doctorUserId, "DOCTOR");
        });
    }

    @Test
    @DisplayName("医生按患者ID过滤档案")
    void testDoctorFilterByUserId() {
        // Given
        Page<PatientRecord> page = new Page<>(1, 10);
        
        // When
        assertDoesNotThrow(() -> {
            recordService.pageList(page, null, patientUserId, doctorUserId, "DOCTOR");
        });
    }

    @Test
    @DisplayName("医生按关键词搜索档案")
    void testDoctorSearchByKeyword() {
        // Given
        Page<PatientRecord> page = new Page<>(1, 10);
        String keyword = "高血压";
        
        // When
        assertDoesNotThrow(() -> {
            recordService.pageList(page, keyword, null, doctorUserId, "DOCTOR");
        });
    }
}
