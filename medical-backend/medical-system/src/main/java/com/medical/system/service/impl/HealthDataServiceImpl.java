package com.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.common.exception.BusinessException;
import com.medical.system.entity.HealthData;
import com.medical.system.entity.PatientInfo;
import com.medical.system.mapper.HealthDataMapper;
import com.medical.system.mapper.PatientInfoMapper;
import com.medical.system.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 健康数据服务实现
 *
 * @author lbc
 * @date 2025-11-24
 */
@Service
public class HealthDataServiceImpl implements HealthDataService {

    @Autowired
    private HealthDataMapper healthDataMapper;

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Override
    public HealthData createHealthData(HealthData healthData, Long userId) {
        // 获取患者信息
        PatientInfo patientInfo = patientInfoMapper.selectOne(
                new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
        );
        if (patientInfo == null) {
            throw new BusinessException("患者信息不存在");
        }

        healthData.setPatientId(patientInfo.getId());
        healthData.setCreateTime(LocalDateTime.now());

        // 根据数据类型自动判断状态
        healthData.setStatus(calculateStatus(healthData, patientInfo));

        healthDataMapper.insert(healthData);
        return healthData;
    }

    @Override
    public Page<HealthData> getHealthDataPage(Integer current, Integer size, Long userId, String userRole, String dataType, Long patientId) {
        Page<HealthData> page = new Page<>(current, size);
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();

        // 权限控制（处理 ROLE_ 前缀）
        String role = userRole.startsWith("ROLE_") ? userRole.substring(5) : userRole;
        
        if ("PATIENT".equals(role)) {
            // 患者只能查看自己的数据
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo == null) {
                throw new BusinessException("患者信息不存在");
            }
            wrapper.eq(HealthData::getPatientId, patientInfo.getId());
        } else if ("DOCTOR".equals(role) || "ADMIN".equals(role)) {
            // 医生/管理员可以查看指定患者的数据
            if (patientId != null) {
                wrapper.eq(HealthData::getPatientId, patientId);
            }
        }

        // 数据类型筛选
        if (StringUtils.hasText(dataType)) {
            wrapper.eq(HealthData::getDataType, dataType);
        }

        wrapper.orderByDesc(HealthData::getMeasureTime);
        return healthDataMapper.selectPage(page, wrapper);
    }

    @Override
    public HealthData getHealthDataById(Long id, Long userId, String userRole) {
        HealthData healthData = healthDataMapper.selectById(id);
        if (healthData == null) {
            throw new BusinessException("健康数据不存在");
        }

        // 权限校验（处理 ROLE_ 前缀）
        String role = userRole.startsWith("ROLE_") ? userRole.substring(5) : userRole;
        
        if ("PATIENT".equals(role)) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo == null || !healthData.getPatientId().equals(patientInfo.getId())) {
                throw new BusinessException("无权访问该数据");
            }
        }

        return healthData;
    }

    @Override
    public void deleteHealthData(Long id, Long userId, String userRole) {
        HealthData healthData = healthDataMapper.selectById(id);
        if (healthData == null) {
            throw new BusinessException("健康数据不存在");
        }

        // 权限校验（处理 ROLE_ 前缀）
        String role = userRole.startsWith("ROLE_") ? userRole.substring(5) : userRole;
        
        if ("PATIENT".equals(role)) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo == null || !healthData.getPatientId().equals(patientInfo.getId())) {
                throw new BusinessException("无权删除该数据");
            }
        }

        healthDataMapper.deleteById(id);
    }

    @Override
    public List<HealthData> getHealthDataTrend(Long userId, String userRole, String dataType, Long patientId, Integer days) {
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();

        // 权限控制（处理 ROLE_ 前缀）
        String role = userRole.startsWith("ROLE_") ? userRole.substring(5) : userRole;
        
        if ("PATIENT".equals(role)) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            if (patientInfo == null) {
                throw new BusinessException("患者信息不存在");
            }
            wrapper.eq(HealthData::getPatientId, patientInfo.getId());
        } else if (("DOCTOR".equals(role) || "ADMIN".equals(role)) && patientId != null) {
            wrapper.eq(HealthData::getPatientId, patientId);
        }

        // 数据类型
        if (StringUtils.hasText(dataType)) {
            wrapper.eq(HealthData::getDataType, dataType);
        }

        // 时间范围
        if (days != null && days > 0) {
            LocalDateTime startTime = LocalDateTime.now().minusDays(days);
            wrapper.ge(HealthData::getMeasureTime, startTime);
        }

        wrapper.orderByAsc(HealthData::getMeasureTime);
        return healthDataMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getHealthDataStatistics(Long userId, String userRole, Long patientId) {
        Map<String, Object> statistics = new HashMap<>();
        
        System.out.println("===== 统计接口调用 =====");
        System.out.println("userId: " + userId);
        System.out.println("userRole: " + userRole);
        System.out.println("patientId 参数: " + patientId);

        Long targetPatientId = null;
        // Spring Security 会给角色加 ROLE_ 前缀，需要处理
        String role = userRole.startsWith("ROLE_") ? userRole.substring(5) : userRole;
        System.out.println("处理后的 role: " + role);
        
        if ("PATIENT".equals(role)) {
            PatientInfo patientInfo = patientInfoMapper.selectOne(
                    new LambdaQueryWrapper<PatientInfo>().eq(PatientInfo::getUserId, userId)
            );
            System.out.println("查询到的 patientInfo: " + patientInfo);
            if (patientInfo != null) {
                targetPatientId = patientInfo.getId();
                System.out.println("targetPatientId: " + targetPatientId);
            }
        } else if (("DOCTOR".equals(role) || "ADMIN".equals(role)) && patientId != null) {
            targetPatientId = patientId;
        }

        if (targetPatientId == null) {
            System.out.println("targetPatientId 为 null，返回空统计");
            return statistics;
        }

        // 统计各类型数据数量
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getPatientId, targetPatientId);
        
        Long totalCount = healthDataMapper.selectCount(wrapper);
        System.out.println("totalCount: " + totalCount);
        statistics.put("totalCount", totalCount);

        // 统计最近7天的数据
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        wrapper.ge(HealthData::getMeasureTime, sevenDaysAgo);
        Long recentCount = healthDataMapper.selectCount(wrapper);
        System.out.println("recentCount: " + recentCount);
        statistics.put("recentCount", recentCount);

        // 统计异常数据数量（状态 > 0 即为异常）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getPatientId, targetPatientId);
        wrapper.gt(HealthData::getStatus, 0); // 包含所有异常状态：1,2,3,4
        Long abnormalCount = healthDataMapper.selectCount(wrapper);
        System.out.println("abnormalCount: " + abnormalCount);
        statistics.put("abnormalCount", abnormalCount);

        System.out.println("最终返回统计: " + statistics);
        return statistics;
    }

    /**
     * 根据数据类型和值计算状态
     * 状态分级：
     * 0 - 正常
     * 1 - 轻度异常
     * 2 - 中度异常
     * 3 - 重度异常
     * 4 - 危险
     */
    private Integer calculateStatus(HealthData healthData, PatientInfo patientInfo) {
        String dataType = healthData.getDataType();
        
        if ("BLOOD_PRESSURE".equals(dataType)) {
            Integer systolic = healthData.getSystolicPressure();
            Integer diastolic = healthData.getDiastolicPressure();
            if (systolic != null && diastolic != null) {
                // 危险：收缩压 ≥ 180 或舒张压 ≥ 110
                if (systolic >= 180 || diastolic >= 110) {
                    return 4;
                }
                // 3级高血压（重度）：收缩压 160-179 或舒张压 100-109
                if (systolic >= 160 || diastolic >= 100) {
                    return 3;
                }
                // 2级高血压（中度）：收缩压 140-159 或舒张压 90-99
                if (systolic >= 140 || diastolic >= 90) {
                    return 2;
                }
                // 低血压（轻度异常）：收缩压 < 90 或舒张压 < 60
                if (systolic < 90 || diastolic < 60) {
                    return 1;
                }
            }
        } else if ("BLOOD_SUGAR".equals(dataType)) {
            BigDecimal bloodSugar = healthData.getBloodSugar();
            if (bloodSugar != null) {
                // 危险：> 11.1 mmol/L（严重高血糖）或 < 2.8 mmol/L（严重低血糖）
                if (bloodSugar.compareTo(new BigDecimal("11.1")) > 0 || 
                    bloodSugar.compareTo(new BigDecimal("2.8")) < 0) {
                    return 4;
                }
                // 糖尿病（重度）：7.1 - 11.1 mmol/L
                if (bloodSugar.compareTo(new BigDecimal("7.1")) >= 0) {
                    return 3;
                }
                // 糖尿病前期（中度）：6.2 - 7.0 mmol/L
                if (bloodSugar.compareTo(new BigDecimal("6.2")) >= 0) {
                    return 2;
                }
                // 低血糖（轻度）：< 3.9 mmol/L
                if (bloodSugar.compareTo(new BigDecimal("3.9")) < 0) {
                    return 1;
                }
            }
        } else if ("HEART_RATE".equals(dataType)) {
            Integer heartRate = healthData.getHeartRate();
            if (heartRate != null) {
                // 危险：< 40 bpm 或 > 150 bpm
                if (heartRate < 40 || heartRate > 150) {
                    return 4;
                }
                // 中度异常：40-49 bpm 或 121-150 bpm
                if ((heartRate >= 40 && heartRate <= 49) || (heartRate >= 121 && heartRate <= 150)) {
                    return 3;
                }
                // 轻度心动过速：101-120 bpm
                if (heartRate >= 101 && heartRate <= 120) {
                    return 2;
                }
                // 轻度心动过缓：50-59 bpm
                if (heartRate >= 50 && heartRate <= 59) {
                    return 1;
                }
            }
        } else if ("TEMPERATURE".equals(dataType)) {
            BigDecimal temperature = healthData.getTemperature();
            if (temperature != null) {
                // 危险：> 41.0℃（超高热）或 < 35.0℃（严重低体温）
                if (temperature.compareTo(new BigDecimal("41.0")) > 0 || 
                    temperature.compareTo(new BigDecimal("35.0")) < 0) {
                    return 4;
                }
                // 高热（重度）：39.1 - 41.0℃
                if (temperature.compareTo(new BigDecimal("39.1")) >= 0) {
                    return 3;
                }
                // 中度发热：38.1 - 39.0℃
                if (temperature.compareTo(new BigDecimal("38.1")) >= 0) {
                    return 2;
                }
                // 低热或体温偏低（轻度）：37.4 - 38.0℃ 或 35.0 - 36.0℃
                if ((temperature.compareTo(new BigDecimal("37.4")) >= 0 && 
                     temperature.compareTo(new BigDecimal("38.0")) <= 0) ||
                    (temperature.compareTo(new BigDecimal("35.0")) >= 0 && 
                     temperature.compareTo(new BigDecimal("36.0")) < 0)) {
                    return 1;
                }
            }
        } else if ("WEIGHT".equals(dataType)) {
            // 基于BMI判断体重状态（中国成人标准）
            BigDecimal weight = healthData.getWeight();
            BigDecimal height = patientInfo != null ? patientInfo.getHeight() : null;
            
            if (weight != null && height != null && height.compareTo(BigDecimal.ZERO) > 0) {
                // 计算BMI = 体重(kg) / 身高²(m²)
                BigDecimal heightInMeters = height.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal bmi = weight.divide(heightInMeters.multiply(heightInMeters), 2, BigDecimal.ROUND_HALF_UP);
                
                // 危险：BMI < 16 或 BMI > 35
                if (bmi.compareTo(new BigDecimal("16")) < 0 || bmi.compareTo(new BigDecimal("35")) > 0) {
                    return 4;
                }
                // 肥胖（重度）：BMI >= 28
                if (bmi.compareTo(new BigDecimal("28")) >= 0) {
                    return 3;
                }
                // 超重（中度）：BMI 24-27.9
                if (bmi.compareTo(new BigDecimal("24")) >= 0) {
                    return 2;
                }
                // 偏瘦（轻度）：BMI < 18.5
                if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                    return 1;
                }
                // 正常：BMI 18.5-23.9
                return 0;
            }
            // 如果没有身高数据，无法判断，返回正常
            return 0;
        }

        return 0; // 正常
    }
}
