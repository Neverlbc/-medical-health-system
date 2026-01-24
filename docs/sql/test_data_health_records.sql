-- =================================================================
-- 测试数据脚本：健康档案数据
-- 为已就诊的患者生成：生命体征、诊断记录、病史等
-- 并生成 patient_record 主档案及关联的详细记录（如过敏史）
-- =================================================================

USE medical_health;

-- 1. 清理旧的健康档案数据
DELETE FROM `vital_signs`;
DELETE FROM `diagnosis_record`;
DELETE FROM `patient_history`;
DELETE FROM `patient_record`;
DELETE FROM `patient_allergy`;

DELIMITER $$
DROP PROCEDURE IF EXISTS `sp_generate_health_records`$$
CREATE PROCEDURE `sp_generate_health_records`()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE v_patient_id BIGINT;
    DECLARE v_user_id BIGINT;
    DECLARE v_doctor_id BIGINT;
    DECLARE v_appt_date DATE;
    DECLARE v_rec_count INT DEFAULT 0;
    DECLARE v_allergy VARCHAR(50);
    
    -- 游标：查找所有"已就诊"(status=1)的预约记录
    -- 同时获取 user_id (通过 patient_info 表)
    DECLARE appt_cursor CURSOR FOR 
        SELECT a.patient_id, p.user_id, a.doctor_id, a.appointment_date 
        FROM appointment a
        JOIN patient_info p ON a.patient_id = p.id
        WHERE a.status = 1;
        
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    OPEN appt_cursor;
    
    read_loop: LOOP
        FETCH appt_cursor INTO v_patient_id, v_user_id, v_doctor_id, v_appt_date;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 1. 生成生命体征 (Vital Signs)
        INSERT INTO `vital_signs` (`patient_id`, `systolic`, `diastolic`, `heart_rate`, `temperature`, `measure_time`, `create_time`)
        VALUES (v_patient_id, FLOOR(110 + RAND() * 30), FLOOR(70 + RAND() * 20), FLOOR(60 + RAND() * 40), 36.5 + RAND() * 0.7, CONCAT(v_appt_date, ' 09:00:00'), NOW());
        
        -- 2. 生成诊断记录 (Diagnosis Record)
        INSERT INTO `diagnosis_record` (`patient_id`, `doctor_id`, `diagnosis_date`, `disease_name`, `description`, `diagnosis_type`, `create_time`)
        VALUES (v_patient_id, v_doctor_id, v_appt_date, 
            ELT(FLOOR(1 + RAND() * 4), '上呼吸道感染', '高血压', '急性胃肠炎', '支气管炎'),
            '患者主诉发热、咳嗽...', 
            'PRELIMINARY', NOW());
            
        -- 3. 生成病史 (Patient History) - 仅为部分患者生成
        IF RAND() > 0.7 THEN
            INSERT INTO `patient_history` (`patient_id`, `history_type`, `disease_name`, `diagnosis_date`, `description`, `create_time`)
            VALUES (v_patient_id, 'PAST', '阑尾炎手术', DATE_SUB(CURDATE(), INTERVAL FLOOR(1 + RAND() * 10) YEAR), '行阑尾切除术', NOW());
        END IF;

        -- 4. 生成/更新 patient_record 主档案
        -- 使用 SELECT COUNT(*) 判断是否存在，避免 IF NOT EXISTS 语法问题
        SELECT COUNT(*) INTO v_rec_count FROM patient_record WHERE user_id = v_user_id;
        
        IF v_rec_count = 0 THEN
            -- 随机决定是否有过敏
            SET v_allergy = IF(RAND() > 0.8, '青霉素过敏', '无');
            
            INSERT INTO `patient_record` (`user_id`, `allergies`, `family_history`, `medical_history`, `medication_history`, `remark`, `create_time`, `update_time`, `deleted`)
            VALUES (v_user_id, 
                v_allergy,
                IF(RAND() > 0.8, '高血压家族史', '无'),
                IF(RAND() > 0.8, '曾患肺炎', '无'),
                '无近期用药',
                '自动生成的测试档案',
                NOW(), NOW(), 0);
                
            -- 如果有过敏，同步生成 patient_allergy 记录
            IF v_allergy != '无' THEN
                INSERT INTO `patient_allergy` (`patient_id`, `allergen`, `allergy_type`, `reaction`, `severity`, `record_date`, `create_time`)
                VALUES (v_patient_id, '青霉素', 'DRUG', '皮疹、瘙痒', 'MILD', CURDATE(), NOW());
            END IF;
        END IF;
        
    END LOOP;
    
    CLOSE appt_cursor;
END$$
DELIMITER ;

-- 执行存储过程
CALL sp_generate_health_records();

-- 清理存储过程
DROP PROCEDURE IF EXISTS `sp_generate_health_records`;

SELECT '健康档案数据生成完成！' AS message;
