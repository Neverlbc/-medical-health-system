-- =================================================================
-- 测试数据脚本：预约记录
-- 建立医患关联关系
-- =================================================================

USE medical_health;

-- 1. 清理旧的预约记录 (仅清理测试医生的预约)
-- 由于我们没有简单的方法区分测试预约，这里选择清空所有预约记录
-- 或者只清理我们生成的测试医生和测试患者的预约
-- 为了简单且安全，我们假设测试环境可以清空 appointment 表
-- 如果是生产环境，绝对不能这样做！
-- 这里我们选择清理所有预约记录，因为这是生成测试数据的脚本
DELETE FROM `appointment`;

-- 重置自增ID (可选)
ALTER TABLE `appointment` AUTO_INCREMENT = 1;

DELIMITER $$
DROP PROCEDURE IF EXISTS `sp_generate_appointments`$$
CREATE PROCEDURE `sp_generate_appointments`()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE v_doctor_id BIGINT;
    DECLARE v_patient_id BIGINT;
    DECLARE v_appt_date DATE;
    DECLARE v_appt_time VARCHAR(20);
    DECLARE v_status TINYINT;
    DECLARE v_count INT;
    DECLARE v_patient_count INT;
    DECLARE v_p_index INT;
    
    -- 游标：遍历所有医生
    DECLARE doctor_cursor CURSOR FOR SELECT id FROM doctor_info;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    OPEN doctor_cursor;
    
    doctor_loop: LOOP
        FETCH doctor_cursor INTO v_doctor_id;
        IF done THEN
            LEAVE doctor_loop;
        END IF;
        
        -- 为每个医生随机分配 5-10 个患者
        SET v_patient_count = FLOOR(5 + RAND() * 6);
        SET v_p_index = 0;
        
        WHILE v_p_index < v_patient_count DO
            -- 随机选择一个患者
            SELECT id INTO v_patient_id FROM patient_info ORDER BY RAND() LIMIT 1;
            
            -- 为这对医患生成 1-3 条预约记录
            SET v_count = FLOOR(1 + RAND() * 3);
            WHILE v_count > 0 DO
                -- 随机日期：过去30天 到 未来7天
                SET v_appt_date = DATE_ADD(CURDATE(), INTERVAL FLOOR(-30 + RAND() * 38) DAY);
                
                -- 随机时段
                IF RAND() > 0.5 THEN
                    SET v_appt_time = '上午';
                ELSE
                    SET v_appt_time = '下午';
                END IF;
                
                -- 状态
                IF v_appt_date < CURDATE() THEN
                    IF RAND() > 0.1 THEN
                        SET v_status = 1; -- 已就诊
                    ELSE
                        SET v_status = 2; -- 已取消
                    END IF;
                ELSE
                    SET v_status = 0; -- 待就诊
                END IF;
                
                -- 插入预约记录
                INSERT INTO `appointment` (`patient_id`, `doctor_id`, `appointment_date`, `appointment_time`, `department`, `status`, `create_time`)
                VALUES (v_patient_id, v_doctor_id, v_appt_date, v_appt_time, '内科', v_status, NOW());
                
                SET v_count = v_count - 1;
            END WHILE;
            
            SET v_p_index = v_p_index + 1;
        END WHILE;
        
    END LOOP;
    
    CLOSE doctor_cursor;
END$$
DELIMITER ;

-- 执行存储过程
CALL sp_generate_appointments();

-- 清理存储过程
DROP PROCEDURE IF EXISTS `sp_generate_appointments`;

SELECT '预约记录生成完成！' AS message;
