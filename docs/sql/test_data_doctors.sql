-- =================================================================
-- 测试数据脚本：医生数据
-- 包含 12 名医生，覆盖多个科室
-- 密码统一为: doctor123
-- =================================================================

USE medical_health;

-- 1. 清理旧的测试数据 (利用外键级联删除 doctor_info)
DELETE FROM `sys_user` WHERE `username` IN (
    'dr_wang_cardio', 'dr_li_digest', 'dr_zhang_respir', 
    'dr_zhao_surgery', 'dr_liu_ortho', 
    'dr_chen_pedia1', 'dr_wu_pedia2', 
    'dr_sun_obgyn1', 'dr_zhou_obgyn2', 
    'dr_xu_tcm', 'dr_ma_general1', 'dr_huang_general2'
);

-- 2. 插入医生用户账号 (sys_user)
-- 密码: doctor123 (BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `phone`, `email`, `role`, `status`) VALUES
('dr_wang_cardio', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '王建国', '13800001001', 'wang.cardio@hospital.com', 'DOCTOR', 1),
('dr_li_digest', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '李明华', '13800001002', 'li.digest@hospital.com', 'DOCTOR', 1),
('dr_zhang_respir', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '张伟强', '13800001003', 'zhang.respir@hospital.com', 'DOCTOR', 1),
('dr_zhao_surgery', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '赵国强', '13800001004', 'zhao.surgery@hospital.com', 'DOCTOR', 1),
('dr_liu_ortho', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '刘建军', '13800001005', 'liu.ortho@hospital.com', 'DOCTOR', 1),
('dr_chen_pedia1', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '陈芳', '13800001006', 'chen.pedia@hospital.com', 'DOCTOR', 1),
('dr_wu_pedia2', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '吴静', '13800001007', 'wu.pedia@hospital.com', 'DOCTOR', 1),
('dr_sun_obgyn1', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '孙丽娟', '13800001008', 'sun.obgyn@hospital.com', 'DOCTOR', 1),
('dr_zhou_obgyn2', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '周敏', '13800001009', 'zhou.obgyn@hospital.com', 'DOCTOR', 1),
('dr_xu_tcm', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '徐文博', '13800001010', 'xu.tcm@hospital.com', 'DOCTOR', 1),
('dr_ma_general1', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '马强', '13800001011', 'ma.general@hospital.com', 'DOCTOR', 1),
('dr_huang_general2', '$2a$10$EI0Wq9JJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', '黄丽', '13800001012', 'huang.general@hospital.com', 'DOCTOR', 1);

-- 3. 插入医生详细信息 (doctor_info)
-- 使用子查询获取 user_id，确保 ID 匹配正确
INSERT INTO `doctor_info` (`user_id`, `real_name`, `gender`, `department`, `title`, `specialty`, `introduction`, `work_years`, `consultation_fee`, `rating`, `patient_count`, `status`)
SELECT id, '王建国', 1, '内科', '主任医师', '心血管疾病、高血压、冠心病', '从事心血管内科临床工作35年，擅长冠心病、心律失常的诊治', 35, 100.00, 4.9, 2800, 1 FROM sys_user WHERE username = 'dr_wang_cardio'
UNION ALL
SELECT id, '李明华', 1, '内科', '副主任医师', '消化系统疾病、胃肠疾病', '消化内科专家，擅长胃炎、胃溃疡、肠道疾病的诊治', 20, 80.00, 4.8, 1800, 1 FROM sys_user WHERE username = 'dr_li_digest'
UNION ALL
SELECT id, '张伟强', 1, '内科', '主治医师', '呼吸系统疾病、哮喘、慢阻肺', '呼吸内科医师，擅长支气管哮喘、慢性阻塞性肺疾病', 12, 60.00, 4.7, 1200, 1 FROM sys_user WHERE username = 'dr_zhang_respir'
UNION ALL
SELECT id, '赵国强', 1, '外科', '主任医师', '普通外科、腹腔镜手术', '普外科专家，擅长腹腔镜微创手术，胆囊、阑尾等手术', 28, 120.00, 4.9, 2200, 1 FROM sys_user WHERE username = 'dr_zhao_surgery'
UNION ALL
SELECT id, '刘建军', 1, '外科', '副主任医师', '骨科、创伤骨折', '骨科专家，擅长骨折复位、关节置换手术', 18, 90.00, 4.8, 1600, 1 FROM sys_user WHERE username = 'dr_liu_ortho'
UNION ALL
SELECT id, '陈芳', 0, '儿科', '副主任医师', '儿童常见病、呼吸系统疾病', '儿科临床经验丰富，对儿童呼吸道感染、哮喘有深入研究', 22, 70.00, 4.9, 2500, 1 FROM sys_user WHERE username = 'dr_chen_pedia1'
UNION ALL
SELECT id, '吴静', 0, '儿科', '主治医师', '新生儿疾病、儿童保健', '擅长新生儿疾病诊治、儿童生长发育评估', 10, 60.00, 4.7, 1400, 1 FROM sys_user WHERE username = 'dr_wu_pedia2'
UNION ALL
SELECT id, '孙丽娟', 0, '妇产科', '主任医师', '妇科肿瘤、宫颈疾病', '妇科专家，擅长妇科肿瘤、宫颈疾病的诊治', 30, 110.00, 4.9, 2600, 1 FROM sys_user WHERE username = 'dr_sun_obgyn1'
UNION ALL
SELECT id, '周敏', 0, '妇产科', '副主任医师', '产科、高危妊娠', '产科专家，擅长高危妊娠管理、产前诊断', 16, 85.00, 4.8, 1900, 1 FROM sys_user WHERE username = 'dr_zhou_obgyn2'
UNION ALL
SELECT id, '徐文博', 1, '中医科', '主治医师', '中医内科、针灸推拿', '中医世家，擅长中医内科、针灸治疗慢性病', 15, 65.00, 4.8, 1500, 1 FROM sys_user WHERE username = 'dr_xu_tcm'
UNION ALL
SELECT id, '马强', 1, '全科', '主治医师', '全科医学、慢性病管理', '全科医师，擅长常见病、多发病诊治及慢性病管理', 8, 50.00, 4.6, 1000, 1 FROM sys_user WHERE username = 'dr_ma_general1'
UNION ALL
SELECT id, '黄丽', 0, '全科', '主治医师', '全科医学、健康管理', '全科医师，擅长健康体检、慢性病预防与管理', 9, 50.00, 4.7, 1100, 1 FROM sys_user WHERE username = 'dr_huang_general2';

SELECT '医生数据插入完成！共插入 12 名医生' AS message;
