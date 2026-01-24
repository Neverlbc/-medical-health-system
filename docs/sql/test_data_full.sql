-- =================================================================
-- 测试数据脚本：医院级健康档案系统
-- 前置条件：
-- 1. 已执行 health_record_full.sql 创建表结构
-- 2. 假设存在 patient_id = 1 的患者 (对应 user_id 可能是 2)
-- 3. 假设存在 doctor_id = 3 的医生 (对应 user_id 可能是 3)
-- =================================================================

USE medical_health;

-- 1. 病史信息 (patient_history)
INSERT INTO patient_history (patient_id, history_type, disease_name, diagnosis_date, relationship, description, create_time) VALUES
(1, 'PAST', '原发性高血压', '2020-05-10', NULL, '发现血压升高3年，最高160/100mmHg', NOW()),
(1, 'PAST', '阑尾炎切除术', '2015-08-20', NULL, '急性阑尾炎行腹腔镜切除术', NOW()),
(1, 'FAMILY', '2型糖尿病', NULL, '父亲', '父亲患有2型糖尿病10余年', NOW()),
(1, 'FAMILY', '冠心病', NULL, '母亲', '母亲有冠心病史', NOW()),
(1, 'PERSONAL', NULL, NULL, NULL, '吸烟史10年，平均10支/日；偶有饮酒', NOW());

-- 2. 过敏史 (patient_allergy)
INSERT INTO patient_allergy (patient_id, allergen, allergy_type, reaction, severity, record_date, create_time) VALUES
(1, '青霉素', 'DRUG', '皮疹、瘙痒', 'MODERATE', '2010-01-01', NOW()),
(1, '芒果', 'FOOD', '口唇肿胀', 'MILD', '2018-06-15', NOW());

-- 3. 诊断记录 (diagnosis_record)
INSERT INTO diagnosis_record (patient_id, diagnosis_type, disease_name, icd10_code, diagnosis_date, doctor_id, description, create_time) VALUES
(1, 'CONFIRMED', '高血压病(1级，中危)', 'I10.x00', '2023-11-01', 3, '血压控制尚可，无靶器官损害', NOW()),
(1, 'PRELIMINARY', '上呼吸道感染', 'J06.900', '2023-11-20', 3, '咽痛、流涕2天，无发热', NOW());

-- 4. 生命体征 (vital_signs)
INSERT INTO vital_signs (patient_id, systolic, diastolic, heart_rate, temperature, spo2, respiratory_rate, height, weight, bmi, measure_time, recorder, create_time) VALUES
(1, 135, 85, 78, 36.5, 98, 18, 175.0, 75.0, 24.49, DATE_SUB(NOW(), INTERVAL 7 DAY), '护士A', NOW()),
(1, 142, 90, 82, 36.6, 97, 19, 175.0, 76.0, 24.82, DATE_SUB(NOW(), INTERVAL 3 DAY), '护士B', NOW()),
(1, 128, 80, 75, 36.4, 99, 18, 175.0, 74.5, 24.33, NOW(), '护士A', NOW());

-- 5. 体格检查 (physical_exam)
INSERT INTO physical_exam (patient_id, exam_date, general_condition, skin_mucosa, lymph_nodes, head_neck, chest_lungs, heart, abdomen, spine_limbs, nervous_system, doctor_id, create_time) VALUES
(1, '2023-11-01', '发育正常，营养中等，神志清，精神可', '皮肤无黄染，无皮疹及出血点', '浅表淋巴结未触及肿大', '头颅无畸形，结膜无充血，巩膜无黄染', '双肺呼吸音清，未闻及干湿性啰音', '心率78次/分，律齐，各瓣膜听诊区未闻及病理性杂音', '腹平软，无压痛及反跳痛，肝脾肋下未触及', '脊柱呈生理弯曲，四肢活动自如，无水肿', '生理反射存在，病理反射未引出', 3, NOW());

-- 6. 实验室检查 (lab_test & lab_test_item)
-- 插入主表
INSERT INTO lab_test (patient_id, test_type, test_date, hospital, report_url, doctor_id, result_summary, create_time) VALUES
(1, '血常规', DATE_SUB(NOW(), INTERVAL 1 DAY), '市第一人民医院', NULL, 3, '白细胞计数正常，红细胞计数正常，血小板正常', NOW());

-- 获取刚插入的 lab_test id (这里假设是自增ID，实际执行时可能需要手动关联，或者在应用层处理。这里为了演示直接插入关联数据，假设ID为1，请根据实际情况调整)
-- 注意：如果是在MySQL命令行执行，可以使用 LAST_INSERT_ID()，但在脚本中批量执行较难控制。
-- 这里我们使用子查询或者假设ID。为了脚本能直接运行，我使用变量。

SET @last_test_id = LAST_INSERT_ID();

INSERT INTO lab_test_item (test_id, item_name, item_value, unit, reference_range, is_abnormal, abnormal_flag) VALUES
(@last_test_id, '白细胞计数(WBC)', '6.5', '10^9/L', '3.5-9.5', 0, NULL),
(@last_test_id, '红细胞计数(RBC)', '4.8', '10^12/L', '4.0-5.5', 0, NULL),
(@last_test_id, '血红蛋白(HGB)', '145', 'g/L', '120-160', 0, NULL),
(@last_test_id, '血小板计数(PLT)', '220', '10^9/L', '100-300', 0, NULL),
(@last_test_id, '中性粒细胞百分比', '65', '%', '40-75', 0, NULL);

-- 插入另一条生化检查
INSERT INTO lab_test (patient_id, test_type, test_date, hospital, report_url, doctor_id, result_summary, create_time) VALUES
(1, '生化全套', DATE_SUB(NOW(), INTERVAL 1 DAY), '市第一人民医院', NULL, 3, '血糖轻度升高，肝肾功能正常', NOW());

SET @last_test_id_2 = LAST_INSERT_ID();

INSERT INTO lab_test_item (test_id, item_name, item_value, unit, reference_range, is_abnormal, abnormal_flag) VALUES
(@last_test_id_2, '谷丙转氨酶(ALT)', '25', 'U/L', '0-40', 0, NULL),
(@last_test_id_2, '谷草转氨酶(AST)', '28', 'U/L', '0-40', 0, NULL),
(@last_test_id_2, '空腹血糖(GLU)', '6.8', 'mmol/L', '3.9-6.1', 1, 'H'),
(@last_test_id_2, '肌酐(Cr)', '85', 'μmol/L', '57-111', 0, NULL),
(@last_test_id_2, '尿素氮(BUN)', '5.2', 'mmol/L', '2.9-8.2', 0, NULL);


-- 7. 治疗方案 (treatment_plan)
INSERT INTO treatment_plan (patient_id, diagnosis_id, plan_name, start_date, end_date, status, doctor_id, description, create_time) VALUES
(1, NULL, '高血压常规治疗', '2023-11-01', NULL, 'ACTIVE', 3, '1. 低盐低脂饮食，适量运动\n2. 硝苯地平控释片 30mg qd\n3. 监测血压，每日早晚各一次', NOW());

-- 8. 随访记录 (follow_up)
INSERT INTO follow_up (patient_id, doctor_id, follow_up_date, method, content, result, next_date, create_time) VALUES
(1, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), 'PHONE', '询问患者近期血压控制情况及服药依从性', '患者自述血压稳定在130/80左右，无头晕头痛，按时服药', DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW());
