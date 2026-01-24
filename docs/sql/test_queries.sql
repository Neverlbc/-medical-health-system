-- ====================================================
-- 测试查询SQL脚本
-- 用于测试数据库功能和数据完整性
-- ====================================================

USE medical_health;

-- ====================================================
-- 1. 基础数据查询测试
-- ====================================================

-- 1.1 查询所有用户及其角色
SELECT 
  id,
  username,
  nickname,
  phone,
  email,
  role,
  status,
  create_time
FROM sys_user
ORDER BY id;

-- 1.2 查询医生信息（包含用户信息）
SELECT 
  d.id AS doctor_id,
  d.real_name,
  d.gender,
  d.department,
  d.title,
  d.specialty,
  d.consultation_fee,
  d.rating,
  d.patient_count,
  u.username,
  u.phone,
  u.email
FROM doctor_info d
LEFT JOIN sys_user u ON d.user_id = u.id
ORDER BY d.department, d.title DESC;

-- 1.3 查询患者信息（包含用户信息）
SELECT 
  p.id AS patient_id,
  p.real_name,
  p.gender,
  p.age,
  p.blood_type,
  p.height,
  p.weight,
  p.allergies,
  u.username,
  u.phone,
  u.email
FROM patient_info p
LEFT JOIN sys_user u ON p.user_id = u.id
ORDER BY p.id;

-- ====================================================
-- 2. 医生排班查询测试
-- ====================================================

-- 2.1 查询所有医生的排班情况
SELECT 
  d.real_name AS doctor_name,
  d.department,
  d.title,
  s.schedule_date,
  s.time_period,
  s.max_patients,
  s.booked_patients,
  (s.max_patients - s.booked_patients) AS available_slots,
  s.status
FROM doctor_schedule s
LEFT JOIN doctor_info d ON s.doctor_id = d.id
WHERE s.schedule_date >= CURDATE()
ORDER BY s.schedule_date, d.department, s.time_period;

-- 2.2 查询特定日期的可预约排班
SELECT 
  d.id AS doctor_id,
  d.real_name AS doctor_name,
  d.department,
  d.title,
  d.consultation_fee,
  s.schedule_date,
  s.time_period,
  (s.max_patients - s.booked_patients) AS available_slots
FROM doctor_schedule s
LEFT JOIN doctor_info d ON s.doctor_id = d.id
WHERE s.schedule_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
  AND s.status = 1
  AND s.booked_patients < s.max_patients
ORDER BY d.department, s.time_period;

-- 2.3 查询医生的工作负荷
SELECT 
  d.real_name AS doctor_name,
  d.department,
  COUNT(s.id) AS total_shifts,
  SUM(s.booked_patients) AS total_booked,
  SUM(s.max_patients - s.booked_patients) AS total_available,
  ROUND(AVG(s.booked_patients * 100.0 / s.max_patients), 2) AS avg_occupancy_rate
FROM doctor_info d
LEFT JOIN doctor_schedule s ON d.id = s.doctor_id
WHERE s.schedule_date >= CURDATE()
GROUP BY d.id
ORDER BY avg_occupancy_rate DESC;

-- ====================================================
-- 3. 健康数据统计测试
-- ====================================================

-- 3.1 患者健康数据概览（需要先插入测试数据）
SELECT 
  p.real_name AS patient_name,
  COUNT(DISTINCT hd.id) AS data_count,
  COUNT(DISTINCT CASE WHEN hd.data_type = 'BLOOD_PRESSURE' THEN hd.id END) AS bp_count,
  COUNT(DISTINCT CASE WHEN hd.data_type = 'BLOOD_SUGAR' THEN hd.id END) AS bs_count,
  COUNT(DISTINCT CASE WHEN hd.status = 2 THEN hd.id END) AS abnormal_count,
  MAX(hd.measure_time) AS last_measure_time
FROM patient_info p
LEFT JOIN health_data hd ON p.id = hd.patient_id
GROUP BY p.id;

-- 3.2 健康数据异常统计
SELECT 
  data_type,
  status,
  COUNT(*) AS count,
  CASE status
    WHEN 0 THEN '正常'
    WHEN 1 THEN '偏低'
    WHEN 2 THEN '偏高'
  END AS status_name
FROM health_data
GROUP BY data_type, status
ORDER BY data_type, status;

-- ====================================================
-- 4. AI问诊统计测试
-- ====================================================

-- 4.1 AI问诊总体统计
SELECT 
  COUNT(*) AS total_consultations,
  COUNT(DISTINCT patient_id) AS unique_patients,
  COUNT(DISTINCT session_id) AS unique_sessions,
  AVG(tokens_used) AS avg_tokens,
  AVG(response_time) AS avg_response_time_ms,
  SUM(CASE WHEN feedback = 1 THEN 1 ELSE 0 END) AS positive_feedback,
  SUM(CASE WHEN feedback = 0 THEN 1 ELSE 0 END) AS negative_feedback
FROM ai_consultation;

-- 4.2 按问诊类型统计
SELECT 
  consultation_type,
  COUNT(*) AS count,
  AVG(tokens_used) AS avg_tokens,
  AVG(response_time) AS avg_response_time_ms,
  ROUND(SUM(CASE WHEN feedback = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS satisfaction_rate
FROM ai_consultation
WHERE feedback IS NOT NULL
GROUP BY consultation_type
ORDER BY count DESC;

-- 4.3 患者AI问诊历史
SELECT 
  p.real_name AS patient_name,
  ac.session_id,
  ac.consultation_type,
  LEFT(ac.question, 50) AS question_preview,
  ac.tokens_used,
  ac.feedback,
  ac.create_time
FROM ai_consultation ac
LEFT JOIN patient_info p ON ac.patient_id = p.id
ORDER BY ac.create_time DESC
LIMIT 20;

-- ====================================================
-- 5. 预约统计测试
-- ====================================================

-- 5.1 预约统计概览
SELECT 
  COUNT(*) AS total_appointments,
  COUNT(CASE WHEN status = 0 THEN 1 END) AS pending,
  COUNT(CASE WHEN status = 1 THEN 1 END) AS completed,
  COUNT(CASE WHEN status = 2 THEN 1 END) AS cancelled,
  ROUND(COUNT(CASE WHEN status = 2 THEN 1 END) * 100.0 / COUNT(*), 2) AS cancel_rate
FROM appointment;

-- 5.2 医生预约统计
SELECT 
  d.real_name AS doctor_name,
  d.department,
  COUNT(a.id) AS total_appointments,
  COUNT(CASE WHEN a.status = 0 THEN 1 END) AS pending,
  COUNT(CASE WHEN a.status = 1 THEN 1 END) AS completed,
  COUNT(CASE WHEN a.status = 2 THEN 1 END) AS cancelled,
  SUM(a.fee) AS total_revenue
FROM doctor_info d
LEFT JOIN appointment a ON d.id = a.doctor_id
GROUP BY d.id
ORDER BY total_appointments DESC;

-- 5.3 患者预约记录
SELECT 
  p.real_name AS patient_name,
  d.real_name AS doctor_name,
  d.department,
  a.appointment_date,
  a.appointment_time,
  a.symptoms,
  CASE a.status
    WHEN 0 THEN '待就诊'
    WHEN 1 THEN '已就诊'
    WHEN 2 THEN '已取消'
  END AS status_name,
  a.create_time
FROM appointment a
LEFT JOIN patient_info p ON a.patient_id = p.id
LEFT JOIN doctor_info d ON a.doctor_id = d.id
ORDER BY a.appointment_date DESC, a.create_time DESC;

-- ====================================================
-- 6. 用药管理统计测试
-- ====================================================

-- 6.1 用药提醒统计
SELECT 
  p.real_name AS patient_name,
  COUNT(mr.id) AS reminder_count,
  SUM(CASE WHEN mr.status = 1 THEN 1 ELSE 0 END) AS active_reminders,
  SUM(CASE WHEN mr.status = 2 THEN 1 ELSE 0 END) AS completed_reminders
FROM patient_info p
LEFT JOIN medication_reminder mr ON p.id = mr.patient_id
GROUP BY p.id;

-- 6.2 用药依从性统计
SELECT 
  p.real_name AS patient_name,
  COUNT(mrc.id) AS total_records,
  SUM(CASE WHEN mrc.status = 1 THEN 1 ELSE 0 END) AS taken_count,
  SUM(CASE WHEN mrc.status = 0 THEN 1 ELSE 0 END) AS missed_count,
  SUM(CASE WHEN mrc.status = 2 THEN 1 ELSE 0 END) AS ignored_count,
  ROUND(SUM(CASE WHEN mrc.status = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(mrc.id), 2) AS compliance_rate
FROM patient_info p
LEFT JOIN medication_record mrc ON p.id = mrc.patient_id
GROUP BY p.id
HAVING total_records > 0;

-- ====================================================
-- 7. 报告统计测试
-- ====================================================

-- 7.1 医疗报告统计
SELECT 
  p.real_name AS patient_name,
  COUNT(mr.id) AS report_count,
  COUNT(CASE WHEN mr.ai_analysis IS NOT NULL THEN 1 END) AS analyzed_count,
  MAX(mr.report_date) AS last_report_date
FROM patient_info p
LEFT JOIN medical_report mr ON p.id = mr.patient_id
GROUP BY p.id;

-- 7.2 按报告类型统计
SELECT 
  report_type,
  COUNT(*) AS count,
  COUNT(CASE WHEN ai_analysis IS NOT NULL THEN 1 END) AS analyzed_count,
  ROUND(COUNT(CASE WHEN ai_analysis IS NOT NULL THEN 1 END) * 100.0 / COUNT(*), 2) AS analysis_rate
FROM medical_report
GROUP BY report_type;

-- ====================================================
-- 8. 通知统计测试
-- ====================================================

-- 8.1 通知总体统计
SELECT 
  type,
  COUNT(*) AS total_count,
  SUM(CASE WHEN is_read = 0 THEN 1 ELSE 0 END) AS unread_count,
  SUM(CASE WHEN is_read = 1 THEN 1 ELSE 0 END) AS read_count,
  ROUND(SUM(CASE WHEN is_read = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS read_rate
FROM system_notification
GROUP BY type
ORDER BY total_count DESC;

-- 8.2 用户通知统计
SELECT 
  u.username,
  u.nickname,
  u.role,
  COUNT(sn.id) AS total_notifications,
  SUM(CASE WHEN sn.is_read = 0 THEN 1 ELSE 0 END) AS unread_count
FROM sys_user u
LEFT JOIN system_notification sn ON u.id = sn.user_id
GROUP BY u.id
ORDER BY unread_count DESC;

-- ====================================================
-- 9. 健康知识库统计测试
-- ====================================================

-- 9.1 知识库文章统计
SELECT 
  category,
  COUNT(*) AS article_count,
  SUM(view_count) AS total_views,
  SUM(like_count) AS total_likes,
  ROUND(AVG(view_count), 2) AS avg_views,
  ROUND(AVG(like_count), 2) AS avg_likes
FROM health_knowledge
WHERE status = 1
GROUP BY category
ORDER BY total_views DESC;

-- 9.2 热门文章排行
SELECT 
  id,
  title,
  category,
  view_count,
  like_count,
  publish_time
FROM health_knowledge
WHERE status = 1
ORDER BY view_count DESC
LIMIT 10;

-- ====================================================
-- 10. 视图查询测试
-- ====================================================

-- 10.1 患者健康概览视图
SELECT * FROM v_patient_health_overview;

-- 10.2 医生工作统计视图
SELECT * FROM v_doctor_statistics
ORDER BY total_appointments DESC;

-- ====================================================
-- 11. 数据完整性检查
-- ====================================================

-- 11.1 检查孤立记录（没有对应用户的患者/医生）
SELECT '孤立的患者记录' AS check_type, COUNT(*) AS count
FROM patient_info p
LEFT JOIN sys_user u ON p.user_id = u.id
WHERE u.id IS NULL

UNION ALL

SELECT '孤立的医生记录' AS check_type, COUNT(*) AS count
FROM doctor_info d
LEFT JOIN sys_user u ON d.user_id = u.id
WHERE u.id IS NULL;

-- 11.2 检查数据一致性
SELECT 
  '患者年龄不一致' AS check_type,
  COUNT(*) AS count
FROM patient_info
WHERE birthday IS NOT NULL 
  AND age != TIMESTAMPDIFF(YEAR, birthday, CURDATE());

-- 11.3 检查排班数据一致性
SELECT 
  '已预约数超过最大接诊数' AS check_type,
  COUNT(*) AS count
FROM doctor_schedule
WHERE booked_patients > max_patients;

-- ====================================================
-- 12. 性能测试查询
-- ====================================================

-- 12.1 查询执行计划（EXPLAIN）
EXPLAIN SELECT 
  p.real_name,
  COUNT(hd.id) AS data_count
FROM patient_info p
LEFT JOIN health_data hd ON p.id = hd.patient_id
GROUP BY p.id;

-- 12.2 索引使用情况
SHOW INDEX FROM sys_user;
SHOW INDEX FROM health_data;
SHOW INDEX FROM appointment;

-- ====================================================
-- 13. 数据库状态检查
-- ====================================================

-- 13.1 表信息统计
SELECT 
  table_name,
  table_rows,
  ROUND(data_length / 1024 / 1024, 2) AS data_size_mb,
  ROUND(index_length / 1024 / 1024, 2) AS index_size_mb,
  ROUND((data_length + index_length) / 1024 / 1024, 2) AS total_size_mb
FROM information_schema.tables
WHERE table_schema = DATABASE()
ORDER BY (data_length + index_length) DESC;

-- 13.2 数据库总大小
SELECT 
  ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS total_db_size_mb
FROM information_schema.tables
WHERE table_schema = DATABASE();

-- ====================================================
-- 测试完成
-- ====================================================
SELECT '所有测试查询执行完成！' AS message;

