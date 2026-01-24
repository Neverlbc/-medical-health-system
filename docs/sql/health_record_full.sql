-- ====================================================
-- 医院级健康档案系统 - 数据库扩展脚本
-- Author: Antigravity
-- Date: 2025-11-24
-- ====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================
-- 1. 病史信息表 (patient_history)
-- 包含个人史、家族史、既往史的结构化数据
-- ====================================================
DROP TABLE IF EXISTS `patient_history`;
CREATE TABLE `patient_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `history_type` VARCHAR(20) NOT NULL COMMENT '类型(PERSONAL:个人史, FAMILY:家族史, PAST:既往史)',
  `disease_name` VARCHAR(100) DEFAULT NULL COMMENT '疾病名称(家族史/既往史)',
  `diagnosis_date` DATE DEFAULT NULL COMMENT '确诊日期(既往史)',
  `relationship` VARCHAR(50) DEFAULT NULL COMMENT '亲属关系(家族史)',
  `description` TEXT DEFAULT NULL COMMENT '详细描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:有效 0:无效)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_type` (`history_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='病史信息表';

-- ====================================================
-- 2. 过敏史表 (patient_allergy)
-- 独立的过敏记录，支持详细描述
-- ====================================================
DROP TABLE IF EXISTS `patient_allergy`;
CREATE TABLE `patient_allergy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `allergen` VARCHAR(100) NOT NULL COMMENT '过敏原',
  `allergy_type` VARCHAR(20) DEFAULT NULL COMMENT '类型(DRUG:药物, FOOD:食物, ENV:环境, OTHER:其他)',
  `reaction` VARCHAR(200) DEFAULT NULL COMMENT '过敏反应',
  `severity` VARCHAR(20) DEFAULT NULL COMMENT '严重程度(MILD:轻微, MODERATE:中度, SEVERE:严重)',
  `record_date` DATE DEFAULT NULL COMMENT '记录日期',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='过敏史表';

-- ====================================================
-- 3. 生命体征表 (vital_signs)
-- 记录血压、心率、体温、血氧、身高、体重等
-- ====================================================
DROP TABLE IF EXISTS `vital_signs`;
CREATE TABLE `vital_signs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `systolic` INT DEFAULT NULL COMMENT '收缩压(mmHg)',
  `diastolic` INT DEFAULT NULL COMMENT '舒张压(mmHg)',
  `heart_rate` INT DEFAULT NULL COMMENT '心率(bpm)',
  `temperature` DECIMAL(4,1) DEFAULT NULL COMMENT '体温(℃)',
  `spo2` INT DEFAULT NULL COMMENT '血氧饱和度(%)',
  `respiratory_rate` INT DEFAULT NULL COMMENT '呼吸频率(次/分)',
  `height` DECIMAL(5,2) DEFAULT NULL COMMENT '身高(cm)',
  `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `bmi` DECIMAL(4,2) DEFAULT NULL COMMENT 'BMI指数',
  `measure_time` DATETIME NOT NULL COMMENT '测量时间',
  `recorder` VARCHAR(50) DEFAULT NULL COMMENT '记录人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_measure_time` (`measure_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生命体征表';

-- ====================================================
-- 4. 体格检查表 (physical_exam)
-- 系统化的体格检查记录
-- ====================================================
DROP TABLE IF EXISTS `physical_exam`;
CREATE TABLE `physical_exam` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `exam_date` DATE NOT NULL COMMENT '检查日期',
  `general_condition` TEXT DEFAULT NULL COMMENT '一般情况',
  `skin_mucosa` TEXT DEFAULT NULL COMMENT '皮肤粘膜',
  `lymph_nodes` TEXT DEFAULT NULL COMMENT '淋巴结',
  `head_neck` TEXT DEFAULT NULL COMMENT '头颈部',
  `chest_lungs` TEXT DEFAULT NULL COMMENT '胸部/肺',
  `heart` TEXT DEFAULT NULL COMMENT '心脏',
  `abdomen` TEXT DEFAULT NULL COMMENT '腹部',
  `spine_limbs` TEXT DEFAULT NULL COMMENT '脊柱四肢',
  `nervous_system` TEXT DEFAULT NULL COMMENT '神经系统',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '检查医生ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_exam_date` (`exam_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='体格检查表';

-- ====================================================
-- 5. 实验室检查主表 (lab_test)
-- ====================================================
DROP TABLE IF EXISTS `lab_test`;
CREATE TABLE `lab_test` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `test_type` VARCHAR(50) NOT NULL COMMENT '检查类型(如:血常规,生化全项)',
  `test_date` DATETIME NOT NULL COMMENT '检查时间',
  `hospital` VARCHAR(100) DEFAULT NULL COMMENT '检查医院',
  `report_url` VARCHAR(255) DEFAULT NULL COMMENT '报告单图片/PDF链接',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '开单医生ID',
  `result_summary` TEXT DEFAULT NULL COMMENT '结果摘要',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_test_date` (`test_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验室检查主表';

-- ====================================================
-- 6. 实验室检查明细表 (lab_test_item)
-- ====================================================
DROP TABLE IF EXISTS `lab_test_item`;
CREATE TABLE `lab_test_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `test_id` BIGINT NOT NULL COMMENT '检查ID',
  `item_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `item_value` VARCHAR(50) NOT NULL COMMENT '测定值',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `reference_range` VARCHAR(50) DEFAULT NULL COMMENT '参考范围',
  `is_abnormal` TINYINT NOT NULL DEFAULT 0 COMMENT '是否异常(1:是 0:否)',
  `abnormal_flag` VARCHAR(10) DEFAULT NULL COMMENT '异常标识(H:高 L:低)',
  PRIMARY KEY (`id`),
  KEY `idx_test_id` (`test_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验室检查明细表';

-- ====================================================
-- 7. 诊断记录表 (diagnosis_record)
-- 支持 ICD-10 编码
-- ====================================================
DROP TABLE IF EXISTS `diagnosis_record`;
CREATE TABLE `diagnosis_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `diagnosis_type` VARCHAR(20) NOT NULL DEFAULT 'PRELIMINARY' COMMENT '类型(PRELIMINARY:初步诊断, CONFIRMED:确诊)',
  `disease_name` VARCHAR(100) NOT NULL COMMENT '疾病名称',
  `icd10_code` VARCHAR(20) DEFAULT NULL COMMENT 'ICD-10编码',
  `diagnosis_date` DATE NOT NULL COMMENT '诊断日期',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '诊断医生ID',
  `description` TEXT DEFAULT NULL COMMENT '诊断描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_diagnosis_date` (`diagnosis_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诊断记录表';

-- ====================================================
-- 8. 治疗方案表 (treatment_plan)
-- ====================================================
DROP TABLE IF EXISTS `treatment_plan`;
CREATE TABLE `treatment_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `diagnosis_id` BIGINT DEFAULT NULL COMMENT '关联诊断ID',
  `plan_name` VARCHAR(100) NOT NULL COMMENT '方案名称',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE:进行中, COMPLETED:已完成, TERMINATED:已终止)',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '制定医生ID',
  `description` TEXT DEFAULT NULL COMMENT '方案描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='治疗方案表';

-- ====================================================
-- 9. 随访记录表 (follow_up)
-- ====================================================
DROP TABLE IF EXISTS `follow_up`;
CREATE TABLE `follow_up` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '随访医生ID',
  `follow_up_date` DATE NOT NULL COMMENT '随访日期',
  `method` VARCHAR(20) DEFAULT NULL COMMENT '方式(OUTPATIENT:门诊, PHONE:电话, HOME:家访)',
  `content` TEXT DEFAULT NULL COMMENT '随访内容',
  `result` TEXT DEFAULT NULL COMMENT '随访结果/病情变化',
  `next_date` DATE DEFAULT NULL COMMENT '下次随访日期',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_follow_up_date` (`follow_up_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='随访记录表';

SET FOREIGN_KEY_CHECKS = 1;
