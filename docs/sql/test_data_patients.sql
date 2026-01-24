-- =================================================================
-- 测试数据脚本：患者数据
-- 包含 50 名患者，覆盖不同年龄段和性别
-- 密码统一为: patient123
-- =================================================================

USE medical_health;

-- 1. 清理旧的测试数据 (利用外键级联删除 patient_info)
DELETE FROM `sys_user` WHERE `username` LIKE 'patient_%';

-- 2. 插入患者用户账号 (sys_user)
-- 密码: patient123 (BCrypt加密)
-- 使用存储过程批量插入，避免SQL过长
DELIMITER $$
DROP PROCEDURE IF EXISTS `sp_generate_patients`$$
CREATE PROCEDURE `sp_generate_patients`()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE v_username VARCHAR(50);
    DECLARE v_phone VARCHAR(20);
    DECLARE v_real_name VARCHAR(50);
    DECLARE v_gender TINYINT;
    DECLARE v_age INT;
    DECLARE v_birthday DATE;
    DECLARE v_id_card VARCHAR(18);
    DECLARE v_user_id BIGINT;
    
    -- 循环插入50个患者
    WHILE i <= 50 DO
        SET v_username = CONCAT('patient_', LPAD(i, 3, '0'));
        SET v_phone = CONCAT('139', LPAD(i, 8, '0'));
        
        -- 简单的姓名生成逻辑 (仅作演示)
        SET v_real_name = CONCAT('患者', CHAR(65 + (i % 26)), i); 
        
        -- 性别交替
        SET v_gender = i % 2;
        
        -- 年龄分布 (0-90岁)
        SET v_age = FLOOR(RAND() * 90);
        SET v_birthday = DATE_SUB(CURDATE(), INTERVAL v_age YEAR);
        SET v_birthday = DATE_SUB(v_birthday, INTERVAL FLOOR(RAND() * 365) DAY);
        
        -- 身份证号 (模拟)
        SET v_id_card = CONCAT('110101', DATE_FORMAT(v_birthday, '%Y%m%d'), LPAD(i, 4, '0'));

        -- 插入 sys_user
        INSERT INTO `sys_user` (`username`, `password`, `nickname`, `phone`, `email`, `role`, `status`) 
        VALUES (v_username, '$2a$10$xQZqHcJJ0RMUj0QaWXB1O.WaLNZPSzrqKqXvXLJqGqQgYN5qTiH9u', v_real_name, v_phone, CONCAT(v_username, '@test.com'), 'PATIENT', 1);
        
        SET v_user_id = LAST_INSERT_ID();
        
        -- 插入 patient_info
        INSERT INTO `patient_info` (`user_id`, `real_name`, `gender`, `birthday`, `age`, `id_card`, `address`, `emergency_contact`, `emergency_phone`)
        VALUES (v_user_id, v_real_name, v_gender, v_birthday, v_age, v_id_card, '北京市朝阳区模拟街道', '紧急联系人', '13800000000');
        
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

-- 执行存储过程
CALL sp_generate_patients();

-- 清理存储过程
DROP PROCEDURE IF EXISTS `sp_generate_patients`;

SELECT '患者数据插入完成！共插入 50 名患者' AS message;
