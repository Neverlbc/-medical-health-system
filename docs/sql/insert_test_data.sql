-- ====================================================
-- 插入测试数据脚本
-- 用于开发和测试环境
-- ====================================================

USE medical_health;

-- ====================================================
-- 1. 插入健康数据测试数据
-- ====================================================

-- 患者1（张三）的血压数据
INSERT INTO health_data (patient_id, data_type, systolic_pressure, diastolic_pressure, heart_rate, measure_time, status) VALUES
(1, 'BLOOD_PRESSURE', 120, 80, 72, DATE_SUB(NOW(), INTERVAL 7 DAY), 0),
(1, 'BLOOD_PRESSURE', 125, 82, 75, DATE_SUB(NOW(), INTERVAL 6 DAY), 0),
(1, 'BLOOD_PRESSURE', 130, 85, 78, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
(1, 'BLOOD_PRESSURE', 135, 88, 80, DATE_SUB(NOW(), INTERVAL 4 DAY), 2),
(1, 'BLOOD_PRESSURE', 128, 83, 76, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
(1, 'BLOOD_PRESSURE', 122, 81, 73, DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
(1, 'BLOOD_PRESSURE', 118, 79, 70, DATE_SUB(NOW(), INTERVAL 1 DAY), 0);

-- 患者1的血糖数据
INSERT INTO health_data (patient_id, data_type, blood_sugar, measure_time, status) VALUES
(1, 'BLOOD_SUGAR', 5.2, DATE_SUB(NOW(), INTERVAL 7 DAY), 0),
(1, 'BLOOD_SUGAR', 5.5, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
(1, 'BLOOD_SUGAR', 6.8, DATE_SUB(NOW(), INTERVAL 3 DAY), 2),
(1, 'BLOOD_SUGAR', 5.8, DATE_SUB(NOW(), INTERVAL 1 DAY), 0);

-- 患者1的体重数据
INSERT INTO health_data (patient_id, data_type, weight, measure_time, status) VALUES
(1, 'WEIGHT', 72.5, DATE_SUB(NOW(), INTERVAL 30 DAY), 0),
(1, 'WEIGHT', 71.8, DATE_SUB(NOW(), INTERVAL 20 DAY), 0),
(1, 'WEIGHT', 71.2, DATE_SUB(NOW(), INTERVAL 10 DAY), 0),
(1, 'WEIGHT', 70.5, NOW(), 0);

-- 患者2（李四）的健康数据
INSERT INTO health_data (patient_id, data_type, systolic_pressure, diastolic_pressure, heart_rate, measure_time, status) VALUES
(2, 'BLOOD_PRESSURE', 110, 70, 68, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
(2, 'BLOOD_PRESSURE', 108, 68, 65, DATE_SUB(NOW(), INTERVAL 3 DAY), 1),
(2, 'BLOOD_PRESSURE', 112, 72, 70, DATE_SUB(NOW(), INTERVAL 1 DAY), 0);

INSERT INTO health_data (patient_id, data_type, temperature, measure_time, status) VALUES
(2, 'TEMPERATURE', 36.5, DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
(2, 'TEMPERATURE', 36.8, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
(2, 'TEMPERATURE', 37.2, NOW(), 0);

-- ====================================================
-- 2. 插入预约记录测试数据
-- ====================================================

-- 待就诊预约
INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, department, symptoms, status, fee) VALUES
(1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', '内科', '最近经常感觉胸闷，偶尔有心悸的情况', 0, 80.00),
(2, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '儿科', '孩子持续咳嗽一周', 0, 60.00);

-- 已就诊预约
INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, department, symptoms, status, diagnosis, prescription, fee) VALUES
(1, 1, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '下午', '内科', '头痛、疲劳', 1, '血压偏高，建议清淡饮食，注意休息', '降压药：XX品牌，每日一次，每次1片', 80.00),
(2, 2, DATE_SUB(CURDATE(), INTERVAL 14 DAY), '上午', '儿科', '发烧、咳嗽', 1, '急性上呼吸道感染', '退烧药+止咳糖浆', 60.00);

-- 已取消预约
INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, department, symptoms, status, cancel_reason) VALUES
(1, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '下午', '外科', '膝盖疼痛', 2, '临时有事，改期预约');

-- ====================================================
-- 3. 插入AI问诊记录测试数据
-- ====================================================

-- 症状分析问诊
INSERT INTO ai_consultation (patient_id, session_id, question, ai_response, consultation_type, tokens_used, response_time, feedback, feedback_content) VALUES
(1, 'session_001', '我最近经常感觉头晕，是怎么回事？', 
'根据您描述的症状，头晕可能由多种原因引起：

1. **血压异常**：高血压或低血压都可能导致头晕
2. **贫血**：血红蛋白不足会引起供氧不足
3. **颈椎问题**：颈椎病可能影响脑部供血
4. **内耳问题**：前庭功能紊乱
5. **睡眠不足**：休息不够也会导致头晕

**建议：**
- 测量血压，记录数值
- 保证充足睡眠
- 避免突然改变体位
- 如症状持续或加重，建议就诊内科或神经内科

**注意：**此建议仅供参考，不能替代专业医生的诊断。', 
'SYMPTOM_ANALYSIS', 256, 1250, 1, '回答很详细，有帮助'),

(1, 'session_002', '高血压患者可以吃什么水果？', 
'高血压患者可以适量食用以下水果，这些水果富含钾元素，有助于降低血压：

**推荐水果：**
1. **香蕉**：富含钾，有助于排钠降压
2. **橙子**：富含维生素C和钾
3. **猕猴桃**：营养丰富，有助于血压控制
4. **苹果**：膳食纤维丰富
5. **西瓜**：含有瓜氨酸，有助于扩张血管
6. **葡萄**：含有多种有益物质

**注意事项：**
- 糖尿病患者需注意控制摄入量
- 避免过量食用
- 最好在两餐之间食用

**禁忌：**
- 避免柚子（可能与某些降压药产生相互作用）', 
'HEALTH_KNOWLEDGE', 312, 980, 1, '很实用'),

(2, 'session_003', '孩子发烧38度，需要马上吃退烧药吗？', 
'关于儿童发烧的处理，给您以下建议：

**38℃的处理：**
- 38℃属于低热，一般不需要立即服药
- 可以采用物理降温方法

**物理降温方法：**
1. 温水擦浴（温度37℃左右）
2. 保持室内通风
3. 穿着宽松衣物
4. 多喝水

**需要用药的情况：**
- 体温超过38.5℃
- 孩子非常不舒服
- 有高热惊厥史

**需要立即就医的情况：**
- 3个月以下婴儿发热
- 持续高热不退
- 伴有呕吐、腹泻
- 精神状态不好
- 出现皮疹

**提醒：**儿童用药需谨慎，建议在医生指导下使用。', 
'SYMPTOM_ANALYSIS', 298, 1100, NULL, NULL);

-- ====================================================
-- 4. 插入医疗报告测试数据
-- ====================================================

-- 患者1的体检报告
INSERT INTO medical_report (patient_id, report_type, report_name, hospital, report_data, report_date, ai_analysis, ai_suggestions) VALUES
(1, '体检报告', '2024年度健康体检报告', '北京协和医院', 
'{"血常规":{"白细胞":"6.5","红细胞":"4.8","血红蛋白":"145","血小板":"220"},"肝功能":{"谷丙转氨酶":"55","谷草转氨酶":"42","总胆红素":"18"},"肾功能":{"肌酐":"85","尿素氮":"5.2"},"血脂":{"总胆固醇":"5.8","甘油三酯":"2.3","高密度脂蛋白":"1.2","低密度脂蛋白":"3.5"}}',
DATE_SUB(CURDATE(), INTERVAL 30 DAY),
'您的体检报告显示：

**正常项目：**
- 血常规各项指标正常
- 肾功能正常

**异常项目：**
1. **肝功能**：谷丙转氨酶（55 U/L）轻度升高，正常范围0-40 U/L
2. **血脂**：总胆固醇（5.8 mmol/L）偏高，正常范围3.1-5.2 mmol/L；甘油三酯（2.3 mmol/L）偏高，正常范围0.4-1.8 mmol/L

**风险评估：**
目前异常指标为轻度异常，属于中等风险，需要注意生活方式调整。',
'["注意肝脏保护，避免饮酒和使用肝毒性药物","控制饮食，减少高脂肪食物摄入","适当运动，每周至少150分钟中等强度运动","控制体重，BMI保持在正常范围","3个月后复查肝功能和血脂","定期体检，监测健康状况"]'),

(2, '检验报告', '血常规检查', '北京儿童医院',
'{"白细胞":"8.2","红细胞":"4.5","血红蛋白":"130","血小板":"250","中性粒细胞":"65%","淋巴细胞":"30%"}',
DATE_SUB(CURDATE(), INTERVAL 5 DAY),
'血常规检查结果显示：

**各项指标：**
- 白细胞：8.2（正常范围4-10）
- 红细胞：4.5（正常范围3.8-5.1）
- 血红蛋白：130（正常范围110-150）
- 血小板：250（正常范围100-300）

**结论：**
所有指标均在正常范围内，未发现异常。',
'["保持良好的生活习惯","注意营养均衡","如有不适及时就医"]');

-- ====================================================
-- 5. 插入用药提醒测试数据
-- ====================================================

-- 患者1的用药提醒（进行中）
INSERT INTO medication_reminder (patient_id, medicine_name, dosage, frequency, usage_method, start_date, end_date, reminder_times, remark, status) VALUES
(1, '阿莫西林胶囊', '500mg', '每日3次', '饭后服用', 
DATE_SUB(CURDATE(), INTERVAL 2 DAY), 
DATE_ADD(CURDATE(), INTERVAL 5 DAY), 
'08:00,12:00,18:00', 
'消炎药，连续服用7天', 1),

(1, '降压药（XX品牌）', '1片', '每日1次', '晨起空腹服用', 
DATE_SUB(CURDATE(), INTERVAL 30 DAY), 
DATE_ADD(CURDATE(), INTERVAL 60 DAY), 
'07:00', 
'长期服用，控制血压', 1);

-- 患者1的用药提醒（已完成）
INSERT INTO medication_reminder (patient_id, medicine_name, dosage, frequency, usage_method, start_date, end_date, reminder_times, remark, status) VALUES
(1, '感冒灵颗粒', '1袋', '每日3次', '温水冲服', 
DATE_SUB(CURDATE(), INTERVAL 14 DAY), 
DATE_SUB(CURDATE(), INTERVAL 10 DAY), 
'08:00,14:00,20:00', 
'治疗感冒', 2);

-- ====================================================
-- 6. 插入用药记录测试数据
-- ====================================================

-- 为第一个用药提醒生成记录
INSERT INTO medication_record (reminder_id, patient_id, medicine_name, planned_time, actual_time, status, remark) VALUES
-- 前天
(1, 1, '阿莫西林胶囊', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 08:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 08:15:00'), 1, '按时服用'),
(1, 1, '阿莫西林胶囊', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 12:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 12:10:00'), 1, '按时服用'),
(1, 1, '阿莫西林胶囊', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 18:20:00'), 1, '按时服用'),
-- 昨天
(1, 1, '阿莫西林胶囊', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 08:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 08:05:00'), 1, '按时服用'),
(1, 1, '阿莫西林胶囊', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 12:00:00'), NULL, 0, '忘记服用'),
(1, 1, '阿莫西林胶囊', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 19:30:00'), 1, '延迟服用'),
-- 今天
(1, 1, '阿莫西林胶囊', CONCAT(CURDATE(), ' 08:00:00'), CONCAT(CURDATE(), ' 08:10:00'), 1, '按时服用'),
(1, 1, '阿莫西林胶囊', CONCAT(CURDATE(), ' 12:00:00'), NULL, 0, NULL),
(1, 1, '阿莫西林胶囊', CONCAT(CURDATE(), ' 18:00:00'), NULL, 0, NULL);

-- 为第二个用药提醒生成最近7天的记录
INSERT INTO medication_record (reminder_id, patient_id, medicine_name, planned_time, actual_time, status) VALUES
(2, 1, '降压药（XX品牌）', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 07:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 07:05:00'), 1),
(2, 1, '降压药（XX品牌）', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 07:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 07:10:00'), 1),
(2, 1, '降压药（XX品牌）', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 07:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 07:00:00'), 1),
(2, 1, '降压药（XX品牌）', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 07:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 07:15:00'), 1),
(2, 1, '降压药（XX品牌）', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 07:00:00'), NULL, 0),
(2, 1, '降压药（XX品牌）', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 07:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 07:20:00'), 1),
(2, 1, '降压药（XX品牌）', CONCAT(CURDATE(), ' 07:00:00'), CONCAT(CURDATE(), ' 07:05:00'), 1);

-- ====================================================
-- 7. 插入系统通知测试数据
-- ====================================================

-- 患者1的通知
INSERT INTO system_notification (user_id, title, content, type, link_url, is_read) VALUES
(5, '预约提醒', '您预约的李建国医生的门诊将于后天上午开始，请准时就诊。', 'APPOINTMENT', '/appointment/detail/1', 0),
(5, '用药提醒', '该服用阿莫西林胶囊了，请按时服药。', 'MEDICATION', '/medication/reminder/1', 1),
(5, '报告已解读', '您的体检报告AI分析已完成，请查看详情。', 'REPORT', '/report/detail/1', 1),
(5, '系统通知', '系统将于今晚22:00-23:00进行维护，期间可能无法访问，请提前安排。', 'SYSTEM', NULL, 0);

-- 患者2的通知
INSERT INTO system_notification (user_id, title, content, type, is_read) VALUES
(6, '预约提醒', '您预约的王芳医生的门诊将于明天上午开始，请准时就诊。', 'APPOINTMENT', 0),
(6, '健康提醒', '您已经3天没有记录健康数据了，请及时更新。', 'SYSTEM', 0);

-- 医生1的通知
INSERT INTO system_notification (user_id, title, content, type, is_read) VALUES
(2, '新预约通知', '患者张三预约了您后天上午的门诊。', 'APPOINTMENT', 1),
(2, '排班提醒', '请及时更新下周的排班信息。', 'SYSTEM', 0);

-- ====================================================
-- 8. 更新医生排班的已预约数（根据预约记录）
-- ====================================================

UPDATE doctor_schedule ds
SET booked_patients = (
  SELECT COUNT(*) 
  FROM appointment a 
  WHERE a.doctor_id = ds.doctor_id 
    AND a.appointment_date = ds.schedule_date 
    AND a.appointment_time = ds.time_period
    AND a.status = 0
)
WHERE ds.schedule_date >= CURDATE();

-- ====================================================
-- 9. 更新医生的接诊患者数
-- ====================================================

UPDATE doctor_info d
SET patient_count = (
  SELECT COUNT(DISTINCT patient_id)
  FROM appointment
  WHERE doctor_id = d.id AND status = 1
);

-- ====================================================
-- 10. 更新健康知识的浏览量和点赞数（模拟数据）
-- ====================================================

UPDATE health_knowledge SET view_count = 523, like_count = 45 WHERE id = 1;
UPDATE health_knowledge SET view_count = 387, like_count = 32 WHERE id = 2;
UPDATE health_knowledge SET view_count = 612, like_count = 58 WHERE id = 3;

-- ====================================================
-- 测试数据插入完成
-- ====================================================

SELECT '测试数据插入完成！' AS message;

-- 显示各表记录数
SELECT 
  '用户' AS table_name, COUNT(*) AS record_count FROM sys_user
UNION ALL
SELECT '患者信息', COUNT(*) FROM patient_info
UNION ALL
SELECT '医生信息', COUNT(*) FROM doctor_info
UNION ALL
SELECT '健康数据', COUNT(*) FROM health_data
UNION ALL
SELECT '医疗报告', COUNT(*) FROM medical_report
UNION ALL
SELECT 'AI问诊记录', COUNT(*) FROM ai_consultation
UNION ALL
SELECT '预约记录', COUNT(*) FROM appointment
UNION ALL
SELECT '医生排班', COUNT(*) FROM doctor_schedule
UNION ALL
SELECT '用药提醒', COUNT(*) FROM medication_reminder
UNION ALL
SELECT '用药记录', COUNT(*) FROM medication_record
UNION ALL
SELECT '系统通知', COUNT(*) FROM system_notification
UNION ALL
SELECT '健康知识', COUNT(*) FROM health_knowledge;

