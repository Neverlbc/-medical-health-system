/*
 Navicat Premium Dump SQL

 Source Server         : medical_health
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : medical_health

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 27/01/2026 14:17:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_consultation
-- ----------------------------
DROP TABLE IF EXISTS `ai_consultation`;
CREATE TABLE `ai_consultation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID(外键)',
  `session_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话ID',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户提问',
  `ai_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'AI回复',
  `consultation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问诊类型(SYMPTOM_ANALYSIS/MEDICATION_GUIDE/HEALTH_KNOWLEDGE)',
  `tokens_used` int NULL DEFAULT NULL COMMENT '消耗token数',
  `response_time` int NULL DEFAULT NULL COMMENT '响应时间(ms)',
  `feedback` tinyint NULL DEFAULT NULL COMMENT '反馈(1:有帮助 0:无帮助)',
  `feedback_content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '反馈内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_consultation_type`(`consultation_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_consultation_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI问诊记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_consultation
-- ----------------------------
INSERT INTO `ai_consultation` VALUES (1, 1, 'session_001', '我最近经常感觉头晕，是怎么回事？', '根据您描述的症状，头晕可能由多种原因引起：\n\n1. **血压异常**：高血压或低血压都可能导致头晕\n2. **贫血**：血红蛋白不足会引起供氧不足\n3. **颈椎问题**：颈椎病可能影响脑部供血\n4. **内耳问题**：前庭功能紊乱\n5. **睡眠不足**：休息不够也会导致头晕\n\n**建议：**\n- 测量血压，记录数值\n- 保证充足睡眠\n- 避免突然改变体位\n- 如症状持续或加重，建议就诊内科或神经内科\n\n**注意：**此建议仅供参考，不能替代专业医生的诊断。', 'SYMPTOM_ANALYSIS', 256, 1250, 1, '回答很详细，有帮助', '2026-01-26 10:12:15');
INSERT INTO `ai_consultation` VALUES (2, 1, 'session_002', '高血压患者可以吃什么水果？', '高血压患者可以适量食用以下水果，这些水果富含钾元素，有助于降低血压：\n\n**推荐水果：**\n1. **香蕉**：富含钾，有助于排钠降压\n2. **橙子**：富含维生素C和钾\n3. **猕猴桃**：营养丰富，有助于血压控制\n4. **苹果**：膳食纤维丰富\n5. **西瓜**：含有瓜氨酸，有助于扩张血管\n6. **葡萄**：含有多种有益物质\n\n**注意事项：**\n- 糖尿病患者需注意控制摄入量\n- 避免过量食用\n- 最好在两餐之间食用\n\n**禁忌：**\n- 避免柚子（可能与某些降压药产生相互作用）', 'HEALTH_KNOWLEDGE', 312, 980, 1, '很实用', '2026-01-26 10:12:15');
INSERT INTO `ai_consultation` VALUES (3, 2, 'session_003', '孩子发烧38度，需要马上吃退烧药吗？', '关于儿童发烧的处理，给您以下建议：\n\n**38℃的处理：**\n- 38℃属于低热，一般不需要立即服药\n- 可以采用物理降温方法\n\n**物理降温方法：**\n1. 温水擦浴（温度37℃左右）\n2. 保持室内通风\n3. 穿着宽松衣物\n4. 多喝水\n\n**需要用药的情况：**\n- 体温超过38.5℃\n- 孩子非常不舒服\n- 有高热惊厥史\n\n**需要立即就医的情况：**\n- 3个月以下婴儿发热\n- 持续高热不退\n- 伴有呕吐、腹泻\n- 精神状态不好\n- 出现皮疹\n\n**提醒：**儿童用药需谨慎，建议在医生指导下使用。', 'SYMPTOM_ANALYSIS', 298, 1100, NULL, NULL, '2026-01-26 10:12:15');

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID(外键)',
  `doctor_id` bigint NOT NULL COMMENT '医生ID(外键)',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `appointment_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约时段',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '就诊科室',
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '症状描述',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态(0:待就诊 1:已就诊 2:已取消)',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '诊断结果',
  `prescription` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处方',
  `fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '费用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_appointment_date`(`appointment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_appointment_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_appointment_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1, 1, 1, '2026-01-28', '上午', '内科', '最近经常感觉胸闷，偶尔有心悸的情况', 2, '用户主动取消', NULL, NULL, 80.00, '2026-01-26 10:12:15', '2026-01-26 14:16:48');
INSERT INTO `appointment` VALUES (2, 2, 2, '2026-01-27', '上午', '儿科', '孩子持续咳嗽一周', 2, '用户主动取消', NULL, NULL, 60.00, '2026-01-26 10:12:15', '2026-01-27 13:53:49');
INSERT INTO `appointment` VALUES (3, 1, 1, '2026-01-19', '下午', '内科', '头痛、疲劳', 1, NULL, '血压偏高，建议清淡饮食，注意休息', '降压药：XX品牌，每日一次，每次1片', 80.00, '2026-01-26 10:12:15', '2026-01-26 10:12:15');
INSERT INTO `appointment` VALUES (4, 2, 2, '2026-01-12', '上午', '儿科', '发烧、咳嗽', 1, NULL, '急性上呼吸道感染', '退烧药+止咳糖浆', 60.00, '2026-01-26 10:12:15', '2026-01-26 10:12:15');
INSERT INTO `appointment` VALUES (5, 1, 3, '2026-01-23', '下午', '外科', '膝盖疼痛', 2, '临时有事，改期预约', NULL, NULL, NULL, '2026-01-26 10:12:15', '2026-01-26 10:12:15');
INSERT INTO `appointment` VALUES (6, 1, 2, '2026-01-27', '上午', '儿科', '', 2, '用户主动取消', NULL, NULL, NULL, '2026-01-26 13:39:45', '2026-01-26 14:16:51');
INSERT INTO `appointment` VALUES (8, 1, 1, '2026-01-28', '上午', '内科', '', 2, '用户主动取消', NULL, NULL, NULL, '2026-01-26 14:17:00', '2026-01-27 13:53:46');
INSERT INTO `appointment` VALUES (9, 1, 2, '2026-01-28', '下午', '儿科', '', 0, NULL, NULL, NULL, NULL, '2026-01-27 13:55:44', '2026-01-27 13:55:44');

-- ----------------------------
-- Table structure for diagnosis_record
-- ----------------------------
DROP TABLE IF EXISTS `diagnosis_record`;
CREATE TABLE `diagnosis_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '诊断记录ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `diagnosis_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型(PRELIMINARY:初步诊断, CONFIRMED:确诊)',
  `disease_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '疾病名称',
  `icd10_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ICD-10编码',
  `diagnosis_date` date NOT NULL COMMENT '诊断日期',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '诊断医生ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '诊断描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_disease_name`(`disease_name` ASC) USING BTREE,
  INDEX `idx_diagnosis_date`(`diagnosis_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '诊断记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diagnosis_record
-- ----------------------------
INSERT INTO `diagnosis_record` VALUES (1, 1, 'CONFIRMED', '高血压病(1级，中危)', 'I10.x00', '2023-11-01', 3, '血压控制尚可，无靶器官损害', '2026-01-26 10:12:24');
INSERT INTO `diagnosis_record` VALUES (2, 1, 'PRELIMINARY', '上呼吸道感染', 'J06.900', '2023-11-20', 3, '咽痛、流涕2天，无发热', '2026-01-26 10:12:24');
INSERT INTO `diagnosis_record` VALUES (3, 2, 'PRELIMINARY', '高血压', '', '2026-01-27', 1, '', '2026-01-27 13:52:45');

-- ----------------------------
-- Table structure for doctor_info
-- ----------------------------
DROP TABLE IF EXISTS `doctor_info`;
CREATE TABLE `doctor_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医生ID',
  `user_id` bigint NOT NULL COMMENT '用户ID(外键)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `gender` tinyint NOT NULL COMMENT '性别(0:女 1:男)',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '科室',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称',
  `specialty` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专长',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个人简介',
  `certificate_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执业证书号',
  `work_years` int NULL DEFAULT NULL COMMENT '从业年限',
  `consultation_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '挂号费(元)',
  `rating` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '评分(0-5)',
  `patient_count` int NULL DEFAULT 0 COMMENT '接诊患者数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:停诊 1:正常)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_department`(`department` ASC) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_doctor_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of doctor_info
-- ----------------------------
INSERT INTO `doctor_info` VALUES (1, 2, '李建国', 1, '内科', '主任医师', '心血管疾病、高血压、冠心病', '从事内科临床工作30余年，擅长心血管疾病的诊断与治疗', NULL, 30, 10.00, 4.90, 1, 1, '2026-01-26 10:12:06', '2026-01-27 13:16:31');
INSERT INTO `doctor_info` VALUES (2, 3, '王芳', 0, '儿科', '副主任医师', '儿童常见病、呼吸系统疾病', '儿科临床经验丰富，对儿童呼吸系统疾病有深入研究', NULL, 15, 10.00, 4.80, 1, 1, '2026-01-26 10:12:06', '2026-01-27 13:16:45');
INSERT INTO `doctor_info` VALUES (3, 4, '张伟', 1, '外科', '主治医师', '骨科、创伤外科', '擅长骨折、关节疾病的治疗，手术经验丰富', NULL, 10, 10.00, 4.70, 0, 1, '2026-01-26 10:12:06', '2026-01-27 13:16:45');

-- ----------------------------
-- Table structure for doctor_schedule
-- ----------------------------
DROP TABLE IF EXISTS `doctor_schedule`;
CREATE TABLE `doctor_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID(外键)',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `time_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '时段(上午/下午)',
  `max_patients` int NOT NULL DEFAULT 20 COMMENT '最大接诊数',
  `booked_patients` int NOT NULL DEFAULT 0 COMMENT '已预约数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:停诊 1:可预约)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_doctor_date_period`(`doctor_id` ASC, `schedule_date` ASC, `time_period` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_schedule_date`(`schedule_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_schedule_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生排班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of doctor_schedule
-- ----------------------------
INSERT INTO `doctor_schedule` VALUES (64, 1, '2026-01-28', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (65, 1, '2026-01-28', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (66, 1, '2026-01-29', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (67, 1, '2026-01-29', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (68, 1, '2026-01-30', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (69, 1, '2026-01-30', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (70, 1, '2026-01-31', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (71, 1, '2026-01-31', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (72, 1, '2026-02-01', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (73, 1, '2026-02-01', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (74, 1, '2026-02-02', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (75, 1, '2026-02-02', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (76, 1, '2026-02-03', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (77, 1, '2026-02-03', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (78, 2, '2026-01-28', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (79, 2, '2026-01-28', '下午', 20, 2, 1, '2026-01-27 13:54:11', '2026-01-27 13:55:44');
INSERT INTO `doctor_schedule` VALUES (80, 2, '2026-01-29', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (81, 2, '2026-01-29', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (82, 2, '2026-01-30', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (83, 2, '2026-01-30', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (84, 2, '2026-01-31', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (85, 2, '2026-01-31', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (86, 2, '2026-02-01', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (87, 2, '2026-02-01', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (88, 2, '2026-02-02', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (89, 2, '2026-02-02', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (90, 2, '2026-02-03', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (91, 2, '2026-02-03', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (92, 3, '2026-01-28', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (93, 3, '2026-01-28', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (94, 3, '2026-01-29', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (95, 3, '2026-01-29', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (96, 3, '2026-01-30', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (97, 3, '2026-01-30', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (98, 3, '2026-01-31', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (99, 3, '2026-01-31', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (100, 3, '2026-02-01', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (101, 3, '2026-02-01', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (102, 3, '2026-02-02', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (103, 3, '2026-02-02', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (104, 3, '2026-02-03', '上午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');
INSERT INTO `doctor_schedule` VALUES (105, 3, '2026-02-03', '下午', 20, 0, 1, '2026-01-27 13:54:11', '2026-01-27 13:54:11');

-- ----------------------------
-- Table structure for follow_up
-- ----------------------------
DROP TABLE IF EXISTS `follow_up`;
CREATE TABLE `follow_up`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '随访医生ID',
  `follow_up_date` date NOT NULL COMMENT '随访日期',
  `method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方式(OUTPATIENT:门诊, PHONE:电话, HOME:家访)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '随访内容',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '随访结果/病情变化',
  `next_date` date NULL DEFAULT NULL COMMENT '下次随访日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_follow_up_date`(`follow_up_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '随访记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow_up
-- ----------------------------
INSERT INTO `follow_up` VALUES (1, 1, 3, '2026-01-24', 'PHONE', '询问患者近期血压控制情况及服药依从性', '患者自述血压稳定在130/80左右，无头晕头痛，按时服药', '2026-02-26', '2026-01-26 10:12:24');

-- ----------------------------
-- Table structure for health_data
-- ----------------------------
DROP TABLE IF EXISTS `health_data`;
CREATE TABLE `health_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID(外键)',
  `data_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据类型(BLOOD_PRESSURE/BLOOD_SUGAR/HEART_RATE/TEMPERATURE/WEIGHT)',
  `systolic_pressure` int NULL DEFAULT NULL COMMENT '收缩压(mmHg)',
  `diastolic_pressure` int NULL DEFAULT NULL COMMENT '舒张压(mmHg)',
  `blood_sugar` decimal(5, 2) NULL DEFAULT NULL COMMENT '血糖值(mmol/L)',
  `heart_rate` int NULL DEFAULT NULL COMMENT '心率(次/分)',
  `temperature` decimal(4, 2) NULL DEFAULT NULL COMMENT '体温(℃)',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `measure_time` datetime NOT NULL COMMENT '测量时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态(0:正常 1:轻度异常 2:中度异常 3:重度异常 4:危险)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_data_type`(`data_type` ASC) USING BTREE,
  INDEX `idx_measure_time`(`measure_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_health_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_data
-- ----------------------------
INSERT INTO `health_data` VALUES (1, 1, 'BLOOD_PRESSURE', 120, 80, NULL, 72, NULL, NULL, NULL, '2026-01-19 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (2, 1, 'BLOOD_PRESSURE', 125, 82, NULL, 75, NULL, NULL, NULL, '2026-01-20 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (3, 1, 'BLOOD_PRESSURE', 130, 85, NULL, 78, NULL, NULL, NULL, '2026-01-21 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (4, 1, 'BLOOD_PRESSURE', 135, 88, NULL, 80, NULL, NULL, NULL, '2026-01-22 10:12:15', 2, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (5, 1, 'BLOOD_PRESSURE', 128, 83, NULL, 76, NULL, NULL, NULL, '2026-01-23 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (6, 1, 'BLOOD_PRESSURE', 122, 81, NULL, 73, NULL, NULL, NULL, '2026-01-24 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (7, 1, 'BLOOD_PRESSURE', 118, 79, NULL, 70, NULL, NULL, NULL, '2026-01-25 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (8, 1, 'BLOOD_SUGAR', NULL, NULL, 5.20, NULL, NULL, NULL, NULL, '2026-01-19 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (9, 1, 'BLOOD_SUGAR', NULL, NULL, 5.50, NULL, NULL, NULL, NULL, '2026-01-21 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (10, 1, 'BLOOD_SUGAR', NULL, NULL, 6.80, NULL, NULL, NULL, NULL, '2026-01-23 10:12:15', 2, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (11, 1, 'BLOOD_SUGAR', NULL, NULL, 5.80, NULL, NULL, NULL, NULL, '2026-01-25 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (12, 1, 'WEIGHT', NULL, NULL, NULL, NULL, NULL, 72.50, NULL, '2025-12-27 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (13, 1, 'WEIGHT', NULL, NULL, NULL, NULL, NULL, 71.80, NULL, '2026-01-06 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (14, 1, 'WEIGHT', NULL, NULL, NULL, NULL, NULL, 71.20, NULL, '2026-01-16 10:12:15', 0, '2026-01-26 10:12:15');
INSERT INTO `health_data` VALUES (15, 1, 'WEIGHT', NULL, NULL, NULL, NULL, NULL, 70.50, NULL, '2026-01-26 10:12:15', 0, '2026-01-26 10:12:15');

-- ----------------------------
-- Table structure for health_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `health_knowledge`;
CREATE TABLE `health_knowledge`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类',
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:草稿 1:已发布)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE,
  FULLTEXT INDEX `ft_title_content`(`title`, `content`)
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康知识库表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_knowledge
-- ----------------------------
INSERT INTO `health_knowledge` VALUES (1, '高血压患者的饮食注意事项', '慢性病管理', '高血压,饮食,健康', NULL, '高血压是常见的慢性疾病，合理的饮食对控制血压至关重要。', '# 高血压患者的饮食注意事项\n\n## 1. 低盐饮食\n每日食盐摄入量应控制在6克以内...\n\n## 2. 多吃新鲜蔬果\n蔬菜水果富含钾元素，有助于降低血压...\n\n## 3. 控制脂肪摄入\n减少饱和脂肪和反式脂肪的摄入...', '健康管理中心', NULL, 523, 45, 1, '2026-01-26 10:12:06', '2026-01-26 10:12:15', '2026-01-26 10:12:06');
INSERT INTO `health_knowledge` VALUES (2, '糖尿病的预防与控制', '慢性病管理', '糖尿病,预防,血糖', NULL, '糖尿病是一种代谢性疾病，早期预防和控制非常重要。', '# 糖尿病的预防与控制\n\n## 预防措施\n1. 保持健康体重\n2. 规律运动\n3. 均衡饮食\n\n## 控制方法\n1. 定期监测血糖\n2. 合理用药\n3. 饮食控制...', '健康管理中心', NULL, 387, 32, 1, '2026-01-26 10:12:06', '2026-01-26 10:12:15', '2026-01-26 10:12:06');
INSERT INTO `health_knowledge` VALUES (3, '如何提高免疫力', '健康养生', '免疫力,养生,健康', NULL, '提高免疫力是预防疾病的关键，本文介绍科学的方法。', '# 如何提高免疫力\n\n## 1. 充足睡眠\n成年人每天应保证7-9小时睡眠...\n\n## 2. 均衡营养\n多摄入优质蛋白质、维生素和矿物质...\n\n## 3. 适量运动\n每周至少150分钟中等强度运动...', '健康管理中心', NULL, 612, 58, 1, '2026-01-26 10:12:06', '2026-01-26 10:12:15', '2026-01-26 10:12:06');

-- ----------------------------
-- Table structure for lab_test
-- ----------------------------
DROP TABLE IF EXISTS `lab_test`;
CREATE TABLE `lab_test`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `test_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检查类型(如:血常规,生化全项)',
  `test_date` datetime NOT NULL COMMENT '检查时间',
  `hospital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检查医院',
  `report_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '报告单图片/PDF链接',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '开单医生ID',
  `result_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '结果摘要',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_test_date`(`test_date` ASC) USING BTREE,
  INDEX `idx_test_type`(`test_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室检查主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_test
-- ----------------------------
INSERT INTO `lab_test` VALUES (1, 1, '血常规', '2026-01-25 10:12:24', '市第一人民医院', NULL, 3, '白细胞计数正常，红细胞计数正常，血小板正常', '2026-01-26 10:12:24');
INSERT INTO `lab_test` VALUES (2, 1, '生化全套', '2026-01-25 10:12:24', '市第一人民医院', NULL, 3, '血糖轻度升高，肝肾功能正常', '2026-01-26 10:12:24');

-- ----------------------------
-- Table structure for lab_test_item
-- ----------------------------
DROP TABLE IF EXISTS `lab_test_item`;
CREATE TABLE `lab_test_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `test_id` bigint NOT NULL COMMENT '检查ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `item_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '测定值',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位',
  `reference_range` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参考范围',
  `is_abnormal` tinyint NOT NULL DEFAULT 0 COMMENT '是否异常(1:是 0:否)',
  `abnormal_flag` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '异常标识(H:高 L:低)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_test_id`(`test_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室检查明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_test_item
-- ----------------------------
INSERT INTO `lab_test_item` VALUES (1, 1, '白细胞计数(WBC)', '6.5', '10^9/L', '3.5-9.5', 0, NULL);
INSERT INTO `lab_test_item` VALUES (2, 1, '红细胞计数(RBC)', '4.8', '10^12/L', '4.0-5.5', 0, NULL);
INSERT INTO `lab_test_item` VALUES (3, 1, '血红蛋白(HGB)', '145', 'g/L', '120-160', 0, NULL);
INSERT INTO `lab_test_item` VALUES (4, 1, '血小板计数(PLT)', '220', '10^9/L', '100-300', 0, NULL);
INSERT INTO `lab_test_item` VALUES (5, 1, '中性粒细胞百分比', '65', '%', '40-75', 0, NULL);
INSERT INTO `lab_test_item` VALUES (6, 2, '谷丙转氨酶(ALT)', '25', 'U/L', '0-40', 0, NULL);
INSERT INTO `lab_test_item` VALUES (7, 2, '谷草转氨酶(AST)', '28', 'U/L', '0-40', 0, NULL);
INSERT INTO `lab_test_item` VALUES (8, 2, '空腹血糖(GLU)', '6.8', 'mmol/L', '3.9-6.1', 1, 'H');
INSERT INTO `lab_test_item` VALUES (9, 2, '肌酐(Cr)', '85', 'μmol/L', '57-111', 0, NULL);
INSERT INTO `lab_test_item` VALUES (10, 2, '尿素氮(BUN)', '5.2', 'mmol/L', '2.9-8.2', 0, NULL);

-- ----------------------------
-- Table structure for medical_report
-- ----------------------------
DROP TABLE IF EXISTS `medical_report`;
CREATE TABLE `medical_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID(外键)',
  `report_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '报告类型(体检报告/检验报告/影像报告)',
  `report_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '报告名称',
  `hospital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '医院名称',
  `report_data` json NULL COMMENT '报告数据(JSON格式)',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件URL',
  `ai_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI分析结果',
  `ai_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI建议',
  `report_date` date NULL DEFAULT NULL COMMENT '报告日期',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_report_type`(`report_type` ASC) USING BTREE,
  INDEX `idx_report_date`(`report_date` ASC) USING BTREE,
  CONSTRAINT `fk_report_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医疗报告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of medical_report
-- ----------------------------
INSERT INTO `medical_report` VALUES (1, 1, '体检报告', '2024年度健康体检报告', '北京协和医院', '{\"血脂\": {\"总胆固醇\": \"5.8\", \"甘油三酯\": \"2.3\", \"低密度脂蛋白\": \"3.5\", \"高密度脂蛋白\": \"1.2\"}, \"肝功能\": {\"总胆红素\": \"18\", \"谷丙转氨酶\": \"55\", \"谷草转氨酶\": \"42\"}, \"肾功能\": {\"肌酐\": \"85\", \"尿素氮\": \"5.2\"}, \"血常规\": {\"白细胞\": \"6.5\", \"红细胞\": \"4.8\", \"血小板\": \"220\", \"血红蛋白\": \"145\"}}', NULL, '您的体检报告显示：\n\n**正常项目：**\n- 血常规各项指标正常\n- 肾功能正常\n\n**异常项目：**\n1. **肝功能**：谷丙转氨酶（55 U/L）轻度升高，正常范围0-40 U/L\n2. **血脂**：总胆固醇（5.8 mmol/L）偏高，正常范围3.1-5.2 mmol/L；甘油三酯（2.3 mmol/L）偏高，正常范围0.4-1.8 mmol/L\n\n**风险评估：**\n目前异常指标为轻度异常，属于中等风险，需要注意生活方式调整。', '[\"注意肝脏保护，避免饮酒和使用肝毒性药物\",\"控制饮食，减少高脂肪食物摄入\",\"适当运动，每周至少150分钟中等强度运动\",\"控制体重，BMI保持在正常范围\",\"3个月后复查肝功能和血脂\",\"定期体检，监测健康状况\"]', '2025-12-27', '2026-01-26 10:12:15', '2026-01-26 10:12:15');
INSERT INTO `medical_report` VALUES (2, 2, '检验报告', '血常规检查', '北京儿童医院', '{\"白细胞\": \"8.2\", \"红细胞\": \"4.5\", \"血小板\": \"250\", \"淋巴细胞\": \"30%\", \"血红蛋白\": \"130\", \"中性粒细胞\": \"65%\"}', NULL, '血常规检查结果显示：\n\n**各项指标：**\n- 白细胞：8.2（正常范围4-10）\n- 红细胞：4.5（正常范围3.8-5.1）\n- 血红蛋白：130（正常范围110-150）\n- 血小板：250（正常范围100-300）\n\n**结论：**\n所有指标均在正常范围内，未发现异常。', '[\"保持良好的生活习惯\",\"注意营养均衡\",\"如有不适及时就医\"]', '2026-01-21', '2026-01-26 10:12:15', '2026-01-26 10:12:15');



-- ----------------------------
-- Table structure for patient_allergy
-- ----------------------------
DROP TABLE IF EXISTS `patient_allergy`;
CREATE TABLE `patient_allergy`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `allergen` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '过敏原',
  `allergy_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型(DRUG:药物, FOOD:食物, ENV:环境, OTHER:其他)',
  `reaction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '过敏反应',
  `severity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '严重程度(MILD:轻微, MODERATE:中度, SEVERE:严重)',
  `record_date` date NULL DEFAULT NULL COMMENT '记录日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_allergy_type`(`allergy_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '过敏史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of patient_allergy
-- ----------------------------
INSERT INTO `patient_allergy` VALUES (1, 1, '青霉素', 'DRUG', '皮疹、瘙痒', 'MODERATE', '2010-01-01', '2026-01-26 10:12:24');
INSERT INTO `patient_allergy` VALUES (2, 1, '芒果', 'FOOD', '口唇肿胀', 'MILD', '2018-06-15', '2026-01-26 10:12:24');
INSERT INTO `patient_allergy` VALUES (3, 2, '酒精过敏', 'FOOD', '', 'MILD', NULL, '2026-01-27 13:49:56');

-- ----------------------------
-- Table structure for patient_history
-- ----------------------------
DROP TABLE IF EXISTS `patient_history`;
CREATE TABLE `patient_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `history_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型(PERSONAL:个人史, FAMILY:家族史, PAST:既往史)',
  `disease_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '疾病名称',
  `diagnosis_date` date NULL DEFAULT NULL COMMENT '确诊日期',
  `relationship` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '亲属关系(家族史)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '详细描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1:有效 0:无效)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_history_type`(`history_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '病史信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of patient_history
-- ----------------------------
INSERT INTO `patient_history` VALUES (1, 1, 'PAST', '原发性高血压', '2020-05-10', NULL, '发现血压升高3年，最高160/100mmHg', 1, '2026-01-26 10:12:24', '2026-01-26 10:12:24');
INSERT INTO `patient_history` VALUES (2, 1, 'PAST', '阑尾炎切除术', '2015-08-20', NULL, '急性阑尾炎行腹腔镜切除术', 1, '2026-01-26 10:12:24', '2026-01-26 10:12:24');
INSERT INTO `patient_history` VALUES (3, 1, 'FAMILY', '2型糖尿病', NULL, '父亲', '父亲患有2型糖尿病10余年', 1, '2026-01-26 10:12:24', '2026-01-26 10:12:24');
INSERT INTO `patient_history` VALUES (4, 1, 'FAMILY', '冠心病', NULL, '母亲', '母亲有冠心病史', 1, '2026-01-26 10:12:24', '2026-01-26 10:12:24');
INSERT INTO `patient_history` VALUES (5, 1, 'PERSONAL', NULL, NULL, NULL, '吸烟史10年，平均10支/日；偶有饮酒', 1, '2026-01-26 10:12:24', '2026-01-26 10:12:24');
INSERT INTO `patient_history` VALUES (6, 2, 'PAST', '糖尿病', '2026-01-06', '', '', 1, '2026-01-27 13:50:15', '2026-01-27 13:50:15');
INSERT INTO `patient_history` VALUES (7, 2, 'FAMILY', '高血压', NULL, '母亲', '', 1, '2026-01-27 13:50:35', '2026-01-27 13:50:35');
INSERT INTO `patient_history` VALUES (8, 2, 'PERSONAL', '', NULL, '', '免疫力低下', 1, '2026-01-27 13:50:54', '2026-01-27 13:50:54');

-- ----------------------------
-- Table structure for patient_info
-- ----------------------------
DROP TABLE IF EXISTS `patient_info`;
CREATE TABLE `patient_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `user_id` bigint NOT NULL COMMENT '用户ID(外键)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `gender` tinyint NOT NULL COMMENT '性别(0:女 1:男)',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详细地址',
  `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系电话',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '血型(A/B/AB/O)',
  `allergies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '过敏史',
  `family_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '家族病史',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '既往病史',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_id_card`(`id_card` ASC) USING BTREE,
  CONSTRAINT `fk_patient_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '患者信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of patient_info
-- ----------------------------
INSERT INTO `patient_info` VALUES (1, 5, '张三', 1, '1990-05-15', 34, '110101199005151234', '北京市朝阳区XX街道XX号', '李四', '13900000002', 175.00, 70.00, 'A', '青霉素过敏', '父亲有高血压病史', NULL, '2026-01-26 10:12:06', '2026-01-26 10:12:06');
INSERT INTO `patient_info` VALUES (2, 6, '李四', 0, '1995-08-20', 29, '110101199508202345', '北京市海淀区XX路XX号', '张三', '13900000001', 165.00, 55.00, 'B', '无', '无', NULL, '2026-01-26 10:12:06', '2026-01-26 10:12:06');

-- ----------------------------
-- Table structure for patient_record
-- ----------------------------
DROP TABLE IF EXISTS `patient_record`;
CREATE TABLE `patient_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint NOT NULL COMMENT '归属用户ID(患者)',
  `allergies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '过敏史',
  `family_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '家族病史',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '既往病史',
  `medication_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用药史',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:否 1:是)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of patient_record
-- ----------------------------

-- ----------------------------
-- Table structure for physical_exam
-- ----------------------------
DROP TABLE IF EXISTS `physical_exam`;
CREATE TABLE `physical_exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `exam_date` date NOT NULL COMMENT '检查日期',
  `general_condition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '一般情况',
  `skin_mucosa` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '皮肤粘膜',
  `lymph_nodes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '淋巴结',
  `head_neck` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '头颈部',
  `chest_lungs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '胸部/肺',
  `heart` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '心脏',
  `abdomen` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '腹部',
  `spine_limbs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '脊柱四肢',
  `nervous_system` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '神经系统',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '检查医生ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_exam_date`(`exam_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '体格检查表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of physical_exam
-- ----------------------------
INSERT INTO `physical_exam` VALUES (1, 1, '2023-11-01', '发育正常，营养中等，神志清，精神可', '皮肤无黄染，无皮疹及出血点', '浅表淋巴结未触及肿大', '头颅无畸形，结膜无充血，巩膜无黄染', '双肺呼吸音清，未闻及干湿性啰音', '心率78次/分，律齐，各瓣膜听诊区未闻及病理性杂音', '腹平软，无压痛及反跳痛，肝脾肋下未触及', '脊柱呈生理弯曲，四肢活动自如，无水肿', '生理反射存在，病理反射未引出', 3, '2026-01-26 10:12:24');
INSERT INTO `physical_exam` VALUES (2, 2, '2026-01-27', '正常', '正常', '正常', '正常', '正常', '正常', '正常', '正常', '正常', 1, '2026-01-27 13:52:14');

-- ----------------------------
-- Table structure for record_attachment
-- ----------------------------
DROP TABLE IF EXISTS `record_attachment`;
CREATE TABLE `record_attachment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `record_id` bigint NOT NULL COMMENT '档案ID',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件URL',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:否 1:是)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_record_id`(`record_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '档案附件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of record_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(加密)',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色(ADMIN/DOCTOR/PATIENT)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:正常)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:否 1:是)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$1iG5vdbBgQ0X0U2efshZS.5xI51TpTpylgDBwVZxF/9/tFPiViN7K', '系统管理员', '13800000000', 'admin@medical.com', NULL, 'ADMIN', 1, '2026-01-26 10:12:06', '2026-01-27 09:41:34', '2026-01-27 13:58:02', 0);
INSERT INTO `sys_user` VALUES (2, 'doctor1', '$2a$10$K8aIDyfnZYQS.UgdYiw1s.05P5qqvaTfk65hASDDDxvMAxy1vPTT6', '李建国', '13800000001', 'doctor1@medical.com', NULL, 'DOCTOR', 1, '2026-01-26 10:12:06', '2026-01-27 13:59:12', '2026-01-27 14:03:03', 0);
INSERT INTO `sys_user` VALUES (3, 'doctor2', '$2a$10$K8aIDyfnZYQS.UgdYiw1s.05P5qqvaTfk65hASDDDxvMAxy1vPTT6', '王芳', '13800000002', 'doctor2@medical.com', NULL, 'DOCTOR', 1, '2026-01-26 10:12:06', '2026-01-27 14:00:00', '2026-01-27 14:00:19', 0);
INSERT INTO `sys_user` VALUES (4, 'doctor3', '$2a$10$K8aIDyfnZYQS.UgdYiw1s.05P5qqvaTfk65hASDDDxvMAxy1vPTT6', '张伟', '13800000003', 'doctor3@medical.com', NULL, 'DOCTOR', 1, '2026-01-26 10:12:06', '2026-01-27 14:00:45', '2026-01-27 14:00:58', 0);
INSERT INTO `sys_user` VALUES (5, 'patient1', '$2a$10$K8aIDyfnZYQS.UgdYiw1s.05P5qqvaTfk65hASDDDxvMAxy1vPTT6', '张三', '13900000001', 'patient1@test.com', NULL, 'PATIENT', 1, '2026-01-26 10:12:06', '2026-01-27 09:41:36', '2026-01-27 13:55:10', 0);
INSERT INTO `sys_user` VALUES (6, 'patient2', '$2a$10$K8aIDyfnZYQS.UgdYiw1s.05P5qqvaTfk65hASDDDxvMAxy1vPTT6', '李四', '13900000002', 'patient2@test.com', NULL, 'PATIENT', 1, '2026-01-26 10:12:06', '2026-01-27 09:41:37', '2026-01-27 13:37:03', 0);

-- ----------------------------
-- Table structure for system_notification
-- ----------------------------
DROP TABLE IF EXISTS `system_notification`;
CREATE TABLE `system_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID(外键)',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型(SYSTEM/APPOINTMENT/MEDICATION/REPORT)',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联链接',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读(0:未读 1:已读)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_notification
-- ----------------------------
INSERT INTO `system_notification` VALUES (1, 5, '预约提醒', '您预约的李建国医生的门诊将于后天上午开始，请准时就诊。', 'APPOINTMENT', '/appointment/detail/1', 0, '2026-01-26 10:12:15', NULL);
INSERT INTO `system_notification` VALUES (3, 5, '报告已解读', '您的体检报告AI分析已完成，请查看详情。', 'REPORT', '/report/detail/1', 1, '2026-01-26 10:12:15', NULL);
INSERT INTO `system_notification` VALUES (4, 5, '系统通知', '系统将于今晚22:00-23:00进行维护，期间可能无法访问，请提前安排。', 'SYSTEM', NULL, 0, '2026-01-26 10:12:15', NULL);
INSERT INTO `system_notification` VALUES (5, 6, '预约提醒', '您预约的王芳医生的门诊将于明天上午开始，请准时就诊。', 'APPOINTMENT', NULL, 0, '2026-01-26 10:12:15', NULL);
INSERT INTO `system_notification` VALUES (6, 6, '健康提醒', '您已经3天没有记录健康数据了，请及时更新。', 'SYSTEM', NULL, 0, '2026-01-26 10:12:15', NULL);
INSERT INTO `system_notification` VALUES (7, 2, '新预约通知', '患者张三预约了您后天上午的门诊。', 'APPOINTMENT', NULL, 1, '2026-01-26 10:12:15', NULL);
INSERT INTO `system_notification` VALUES (8, 2, '排班提醒', '请及时更新下周的排班信息。', 'SYSTEM', NULL, 0, '2026-01-26 10:12:15', NULL);

-- ----------------------------
-- Table structure for treatment_plan
-- ----------------------------
DROP TABLE IF EXISTS `treatment_plan`;
CREATE TABLE `treatment_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `diagnosis_id` bigint NULL DEFAULT NULL COMMENT '关联诊断ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方案名称',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE:进行中, COMPLETED:已完成, TERMINATED:已终止)',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '制定医生ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '方案描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_diagnosis_id`(`diagnosis_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '治疗方案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of treatment_plan
-- ----------------------------
INSERT INTO `treatment_plan` VALUES (1, 1, NULL, '高血压常规治疗', '2023-11-01', NULL, 'ACTIVE', 3, '1. 低盐低脂饮食，适量运动\n2. 硝苯地平控释片 30mg qd\n3. 监测血压，每日早晚各一次', '2026-01-26 10:12:24');
INSERT INTO `treatment_plan` VALUES (2, 2, NULL, '高血压常规治疗', '2026-01-27', NULL, 'ACTIVE', 1, '', '2026-01-27 13:53:05');

-- ----------------------------
-- Table structure for vital_signs
-- ----------------------------
DROP TABLE IF EXISTS `vital_signs`;
CREATE TABLE `vital_signs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `systolic` int NULL DEFAULT NULL COMMENT '收缩压(mmHg)',
  `diastolic` int NULL DEFAULT NULL COMMENT '舒张压(mmHg)',
  `heart_rate` int NULL DEFAULT NULL COMMENT '心率(bpm)',
  `temperature` decimal(4, 2) NULL DEFAULT NULL COMMENT '体温(℃)',
  `spo2` int NULL DEFAULT NULL COMMENT '血氧饱和度(%)',
  `respiratory_rate` int NULL DEFAULT NULL COMMENT '呼吸频率(次/分)',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `bmi` decimal(5, 2) NULL DEFAULT NULL COMMENT 'BMI指数',
  `measure_time` datetime NOT NULL COMMENT '测量时间',
  `recorder` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '记录人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_measure_time`(`measure_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '生命体征表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vital_signs
-- ----------------------------
INSERT INTO `vital_signs` VALUES (1, 1, 135, 85, 78, 36.50, 98, 18, 175.00, 75.00, 24.49, '2026-01-19 10:12:24', '护士A', '2026-01-26 10:12:24');
INSERT INTO `vital_signs` VALUES (2, 1, 142, 90, 82, 36.60, 97, 19, 175.00, 76.00, 24.82, '2026-01-23 10:12:24', '护士B', '2026-01-26 10:12:24');
INSERT INTO `vital_signs` VALUES (3, 1, 128, 80, 75, 36.40, 99, 18, 175.00, 74.50, 24.33, '2026-01-26 10:12:24', '护士A', '2026-01-26 10:12:24');
INSERT INTO `vital_signs` VALUES (4, 2, 90, 140, 50, 37.00, 98, 52, 165.00, 55.00, 20.20, '2026-01-27 13:51:22', NULL, '2026-01-27 13:51:52');

-- ----------------------------
-- View structure for v_doctor_statistics
-- ----------------------------
DROP VIEW IF EXISTS `v_doctor_statistics`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_doctor_statistics` AS select `d`.`id` AS `doctor_id`,`d`.`real_name` AS `real_name`,`d`.`department` AS `department`,`d`.`title` AS `title`,`d`.`rating` AS `rating`,`d`.`patient_count` AS `patient_count`,count(distinct `a`.`id`) AS `total_appointments`,count(distinct (case when (`a`.`status` = 0) then `a`.`id` end)) AS `pending_appointments`,count(distinct (case when (`a`.`status` = 1) then `a`.`id` end)) AS `completed_appointments` from (`doctor_info` `d` left join `appointment` `a` on((`d`.`id` = `a`.`doctor_id`))) group by `d`.`id`;

-- ----------------------------
-- View structure for v_patient_health_overview
-- ----------------------------
DROP VIEW IF EXISTS `v_patient_health_overview`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_patient_health_overview` AS select `p`.`id` AS `patient_id`,`p`.`real_name` AS `real_name`,`p`.`gender` AS `gender`,`p`.`age` AS `age`,`p`.`blood_type` AS `blood_type`,`u`.`phone` AS `phone`,count(distinct `hd`.`id`) AS `health_data_count`,count(distinct `mr`.`id`) AS `report_count`,count(distinct `a`.`id`) AS `appointment_count` from ((((`patient_info` `p` left join `sys_user` `u` on((`p`.`user_id` = `u`.`id`))) left join `health_data` `hd` on((`p`.`id` = `hd`.`patient_id`))) left join `medical_report` `mr` on((`p`.`id` = `mr`.`patient_id`))) left join `appointment` `a` on((`p`.`id` = `a`.`patient_id`))) group by `p`.`id`;

-- ----------------------------
-- Triggers structure for table appointment
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_appointment_insert`;
delimiter ;;
CREATE TRIGGER `tr_appointment_insert` AFTER INSERT ON `appointment` FOR EACH ROW BEGIN
  IF NEW.status = 0 THEN
    UPDATE doctor_schedule 
    SET booked_patients = booked_patients + 1
    WHERE doctor_id = NEW.doctor_id 
      AND schedule_date = NEW.appointment_date 
      AND time_period = NEW.appointment_time;
  END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table appointment
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_appointment_update`;
delimiter ;;
CREATE TRIGGER `tr_appointment_update` AFTER UPDATE ON `appointment` FOR EACH ROW BEGIN
  IF OLD.status = 0 AND NEW.status = 2 THEN
    UPDATE doctor_schedule 
    SET booked_patients = booked_patients - 1
    WHERE doctor_id = NEW.doctor_id 
      AND schedule_date = NEW.appointment_date 
      AND time_period = NEW.appointment_time
      AND booked_patients > 0;
  END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table patient_info
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_patient_age_update`;
delimiter ;;
CREATE TRIGGER `tr_patient_age_update` BEFORE UPDATE ON `patient_info` FOR EACH ROW BEGIN
  IF NEW.birthday IS NOT NULL AND NEW.birthday != OLD.birthday THEN
    SET NEW.age = TIMESTAMPDIFF(YEAR, NEW.birthday, CURDATE());
  END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
