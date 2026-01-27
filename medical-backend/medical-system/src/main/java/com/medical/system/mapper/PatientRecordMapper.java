package com.medical.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.system.entity.PatientRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PatientRecordMapper extends com.baomidou.mybatisplus.core.mapper.BaseMapper<PatientRecord> {

    @Select("<script>" +
            "SELECT p.user_id as userId, p.real_name as patientName, p.gender, p.age, " +
            "r.id, r.allergies, r.family_history as familyHistory, r.medical_history as medicalHistory, " +
            "r.medication_history as medicationHistory, r.remark, r.create_time as createTime " +
            "FROM patient_info p " +
            "LEFT JOIN patient_record r ON p.user_id = r.user_id " +
            "WHERE 1=1 " +
            "<if test='userId != null'> AND p.user_id = #{userId} </if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (p.real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR r.remark LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR r.allergies LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY r.create_time DESC, p.id DESC" +
            "</script>")
    IPage<PatientRecord> selectPatientRecordsPage(Page<PatientRecord> page, 
                                                  @Param("keyword") String keyword, 
                                                  @Param("userId") Long userId);
}

