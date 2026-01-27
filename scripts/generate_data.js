const fs = require('fs');
const path = require('path');

/**
 * 改进版的测试数据生成脚本
 * 参考 medical_health.sql 的真实数据格式，补充排班、健康数据等信息
 */
function generateEnhancedSql() {
    const passwordHash = '$2a$10$K8aIDyfnZYQS.UgdYiw1s.05P5qqvaTfk65hASDDDxvMAxy1vPTT6'; // 123456

    let sqlLines = [
        "/*",
        "  Enhanced Test Data for Medical Health System",
        "  Includes Patients, Doctors, Records, and Diagnosis History.",
        "*/",
        "SET NAMES utf8mb4;",
        "SET FOREIGN_KEY_CHECKS = 0;",
        "",
        "BEGIN;",
        "",
        "-- 清理可能存在的旧测试数据 (Range 100-500)",
        "DELETE FROM `health_data` WHERE `patient_id` BETWEEN 100 AND 500;",
        "DELETE FROM `diagnosis_record` WHERE `patient_id` BETWEEN 100 AND 500;",
        "DELETE FROM `doctor_schedule` WHERE `doctor_id` BETWEEN 100 AND 500;",
        "DELETE FROM `patient_allergy` WHERE `patient_id` BETWEEN 100 AND 500;",
        "DELETE FROM `patient_history` WHERE `patient_id` BETWEEN 100 AND 500;",
        "DELETE FROM `patient_record` WHERE `user_id` BETWEEN 100 AND 500;",
        "DELETE FROM `doctor_info` WHERE `id` BETWEEN 100 AND 500;",
        "DELETE FROM `patient_info` WHERE `id` BETWEEN 100 AND 500;",
        "DELETE FROM `system_notification` WHERE `user_id` BETWEEN 100 AND 500;",
        "DELETE FROM `sys_user` WHERE `id` BETWEEN 100 AND 500;",
        ""
    ];

    const departments = [
        { name: '内科', specialties: '心血管疾病、高血压、糖尿病、胃炎', intro: '从事内科临床工作多年，对常见内科疾病有丰富诊治经验。' },
        { name: '外科', specialties: '普外科、微创手术、阑尾炎、胆囊结石', intro: '擅长各类外科手术，精通微创治疗技术。' },
        { name: '儿科', specialties: '小儿感冒、小儿肺炎、新生儿护理', intro: '对儿童生长发育及呼吸系统常见病有深入研究，细心呵护幼儿健康。' },
        { name: '骨科', specialties: '骨折修复、关节置换、脊柱调理', intro: '资深骨科专家，专注于骨骼健康与损伤修复。' },
        { name: '妇产科', specialties: '孕期检查、产后恢复、妇科常见病', intro: '致力于妇女生命全周期健康管理，临床经验丰富。' },
        { name: '口腔科', specialties: '牙齿正畸、植牙、根管治疗', intro: '技术精湛，操作温和，为您的笑容护航。' },
        { name: '耳鼻喉科', specialties: '过敏性鼻炎、慢性咽炎、听力恢复', intro: '专注于五官健康，推崇精准诊断与有效治疗。' },
        { name: '中医科', specialties: '针灸推拿、体质调理、中医养生', intro: '弘扬国医精髓，擅长通过望闻问切制订个性化调理方案。' }
    ];

    const titles = ['主任医师', '副主任医师', '主治医师', '医师'];
    const surnames = ['李', '张', '王', '赵', '周', '吴', '郑', '孙', '韩', '沈', '陈', '刘', '杨', '黄', '何', '徐', '朱', '林', '高', '欧阳', '司马', '上官'];
    const names = ['建国', '伟', '芳', '艳', '强', '明', '勇', '军', '霞', '平', '华', '志强', '小玲', '美英', '雪', '刚', '志明', '雅琴', '丽华', '天福', '文杰', '思思', '浩然', '欣怡', '德华', '家豪', '雨柔', '梦娇'];

    const introFooters = [
        '在临床工作中严谨细致，深受患者信赖。',
        '致力于通过精准诊疗缩短患者康复周期。',
        '推崇“以患者为中心”的服务理念，耐心解答每一位患者的疑问。',
        '多次参加国内外医学学术交流，对复杂案例有独到见解。',
        '强调中西医结合或预防与治疗并重的健康管理模式。',
        '具备扎实的医学理论基础和丰富的临床实践经验。',
        '对待患者如亲人，被广大病友亲切地称为“贴心医生”。',
        '在多篇国家级医学期刊发表论文，学术造诣颇深。'
    ];
    const allergens = ['青霉素', '磺胺类药物', '花粉', '海鲜', '油漆', '避孕套(乳胶)', '尘螨'];
    const diseases = ['原发性高血压', '2型糖尿病', '慢性胃炎', '腰椎间盘突出', '胆囊结石', '过敏性鼻炎'];

    function randomChoice(arr) { return arr[Math.floor(Math.random() * arr.length)]; }
    function randomInt(min, max) { return Math.floor(Math.random() * (max - min + 1)) + min; }
    function formatDate(date) { return date.toISOString().slice(0, 10); }

    const diagnoses = [
        { name: '高血压(1级，中危)', code: 'I10.x00', desc: '血压波动，伴有轻微头晕，建议控制盐摄入。' },
        { name: '2型糖尿病', code: 'E11.900', desc: '空腹血糖偏高，建议饮食控制并配合药物治疗。' },
        { name: '上呼吸道感染', code: 'J06.900', desc: '咳嗽、流涕3天，肺部听诊无异常。' },
        { name: '慢性胃炎', code: 'K29.500', desc: '反酸、胃胀，建议进食易消化食物，忌辛辣。' },
        { name: '腰椎间盘突出', code: 'M51.201', desc: '久坐后腰部疼痛，伴随下肢放射痛。' },
        { name: '冠状动脉粥样硬化性心脏病', code: 'I25.100', desc: '心前区阵发性疼痛，建议低盐低脂饮食。' },
        { name: '慢性支气管炎', code: 'J42.x00', desc: '反复咳嗽、咳痰，冬季加重。' },
        { name: '急性阑尾炎', code: 'K35.900', desc: '右下腹转移性痛，伴发热，建议手术。' },
        { name: '偏头痛', code: 'G43.900', desc: '单侧波动性疼痛，伴恶心。' },
        { name: '过敏性皮炎', code: 'L23.900', desc: '皮肤红斑样皮疹，伴明显瘙痒感。' },
        { name: '急性扁桃体炎', code: 'J03.900', desc: '咽痛明显，吞咽困难，伴发热。' },
        { name: '脂肪肝', code: 'K76.000', desc: '体检发现肝脏回声增强，建议控制体重。' },
        { name: '胃溃疡', code: 'K25.900', desc: '餐后腹痛明显，伴嗳气，建议胃镜复查。' },
        { name: '胆囊炎', code: 'K81.900', desc: '右上腹阵发性疼痛，伴恶心，忌油腻食物。' },
        { name: '支气管哮喘', code: 'J45.900', desc: '反复发作性喘息，伴胸闷。' },
        { name: '龋病', code: 'K02.900', desc: '牙齿冷热敏感，发现龋洞，建议补牙治疗。' },
        { name: '牙周炎', code: 'K05.300', desc: '牙龈出血，牙齿松动。' },
        { name: '肩周炎', code: 'M75.000', desc: '肩关节活动受限，夜间疼痛加重。' },
        { name: '颈椎病', code: 'M47.8xx', desc: '颈部僵硬，伴双下肢麻木。' },
        { name: '慢性鼻窦炎', code: 'J32.900', desc: '流脓涕，头痛，伴嗅觉减退。' },
        { name: '手足口病', code: 'B08.400', desc: '手掌、足底及口腔出现疱疹，伴发热。' },
        { name: '小儿腹泻', code: 'A09.900', desc: '大便次数增多，水样便，注意预防脱水。' },
        { name: '带状疱疹', code: 'B02.900', desc: '身体一侧出现簇集性水疱，伴神经痛。' },
        { name: '痛风性关节炎', code: 'M10.900', desc: '第一跖趾关节红肿痛，建议低嘌呤饮食。' }
    ];

    const doctorCount = 30;
    const patientCount = 50;
    const startId = 100; // 医生 ID 100-129
    const patentStartId = 300; // 患者 ID 300-349

    // 1. Doctors
    sqlLines.push("-- ============================================");
    sqlLines.push("-- 1. 生成医生数据");
    sqlLines.push("-- ============================================");
    for (let i = 0; i < doctorCount; i++) {
        const uid = startId + i;
        const did = startId + i;
        const username = `doctor_test_${i + 1}`;
        const realName = randomChoice(surnames) + randomChoice(names);
        const dept = randomChoice(departments);
        const years = randomInt(5, 40);
        const intro = `从事${dept.name}临床工作${years}余年。${dept.intro} ${randomChoice(introFooters)}`;
        const phone = `138${String(i + 501).padStart(8, '0')}`;
        const fee = randomChoice([30, 50, 80, 100, 150, 200]);

        sqlLines.push(`INSERT INTO \`sys_user\` (\`id\`, \`username\`, \`password\`, \`nickname\`, \`phone\`, \`email\`, \`role\`, \`status\`, \`create_time\`) VALUES (${uid}, '${username}', '${passwordHash}', '${realName}', '${phone}', '${username}@test.com', 'DOCTOR', 1, NOW());`);
        sqlLines.push(`INSERT INTO \`doctor_info\` (\`id\`, \`user_id\`, \`real_name\`, \`gender\`, \`department\`, \`title\`, \`specialty\`, \`introduction\`, \`work_years\`, \`consultation_fee\`, \`status\`, \`create_time\`) VALUES (${did}, ${uid}, '${realName}', ${randomInt(0, 1)}, '${dept.name}', '${randomChoice(titles)}', '${dept.specialties}', '${intro}', ${years}, ${fee.toFixed(2)}, 1, NOW());`);
    }

    // 2. Schedule
    sqlLines.push("\n-- ============================================");
    sqlLines.push("-- 2. 生成排班记录");
    sqlLines.push("-- ============================================");
    const periods = ['上午', '下午'];
    for (let d = 0; d < doctorCount; d++) {
        const did = startId + d;
        for (let day = 1; day <= 7; day++) {
            const date = new Date();
            date.setDate(date.getDate() + day);
            const dateStr = date.toISOString().slice(0, 10);
            periods.forEach(p => {
                sqlLines.push(`INSERT INTO \`doctor_schedule\` (\`doctor_id\`, \`schedule_date\`, \`time_period\`, \`max_patients\`, \`booked_patients\`, \`status\`) VALUES (${did}, '${dateStr}', '${p}', 20, 0, 1);`);
            });
        }
    }

    // 3. Patients & Records
    sqlLines.push("\n-- ============================================");
    sqlLines.push("-- 3. 生成患者及健康档案数据");
    sqlLines.push("-- ============================================");
    const district = ['朝阳区', '海淀区', '西城区', '东城区', '丰台区', '石景山区'];
    const bloodTypes = ['A', 'B', 'AB', 'O'];

    for (let i = 0; i < patientCount; i++) {
        const uid = patentStartId + i;
        const pid = patentStartId + i;
        const username = `patient_test_${i + 1}`;
        const realName = randomChoice(surnames) + randomChoice(names);
        const birthYear = 2024 - randomInt(18, 70);
        const phone = `139${String(i + 501).padStart(8, '0')}`;

        // User & Info
        sqlLines.push(`INSERT INTO \`sys_user\` (\`id\`, \`username\`, \`password\`, \`nickname\`, \`phone\`, \`email\`, \`role\`, \`status\`, \`create_time\`) VALUES (${uid}, '${username}', '${passwordHash}', '${realName}', '${phone}', '${username}@test.com', 'PATIENT', 1, NOW());`);
        sqlLines.push(`INSERT INTO \`patient_info\` (\`id\`, \`user_id\`, \`real_name\`, \`gender\`, \`birthday\`, \`age\`, \`id_card\`, \`address\`, \`emergency_contact\`, \`emergency_phone\`, \`blood_type\`, \`create_time\`) VALUES (${pid}, ${uid}, '${realName}', ${randomInt(0, 1)}, '${birthYear}-01-01', ${2024 - birthYear}, '110101${birthYear}0101${String(i + 1).padStart(4, '0')}', '北京市${randomChoice(['朝阳区', '海淀区', '丰台区'])}XX小区', '家属', '13012345678', '${randomChoice(['A', 'B', 'O', 'AB'])}', NOW());`);

        // Record Summary
        sqlLines.push(`INSERT INTO \`patient_record\` (\`user_id\`, \`allergies\`, \`medical_history\`, \`remark\`) VALUES (${uid}, '无', '近期体征平稳', '定期回访。');`);

        // Diagnosis Records (1 to 3 records per patient)
        const recordNum = randomInt(1, 3);
        for (let r = 0; r < recordNum; r++) {
            const diag = randomChoice(diagnoses);
            const type = randomChoice(['CONFIRMED', 'PRELIMINARY']);
            const docId = startId + randomInt(0, doctorCount - 1);
            const rDate = new Date();
            rDate.setDate(rDate.getDate() - randomInt(10, 365)); // Random date within the last year
            const rDateStr = rDate.toISOString().slice(0, 10);
            sqlLines.push(`INSERT INTO \`diagnosis_record\` (\`patient_id\`, \`diagnosis_type\`, \`disease_name\`, \`icd10_code\`, \`diagnosis_date\`, \`doctor_id\`, \`description\`) VALUES (${pid}, '${type}', '${diag.name}', '${diag.code}', '${rDateStr}', ${docId}, '${diag.desc}');`);
        }

        // Health records for some
        if (i < 15) {
            sqlLines.push(`INSERT INTO \`health_data\` (\`patient_id\`, \`data_type\`, \`systolic_pressure\`, \`diastolic_pressure\`, \`heart_rate\`, \`measure_time\`, \`status\`) VALUES (${pid}, 'BLOOD_PRESSURE', ${randomInt(115, 140)}, ${randomInt(75, 90)}, 72, NOW(), 0);`);
        }
    }

    // 5. System Notifications removed per user request
    sqlLines.push("\n-- ============================================");
    sqlLines.push("-- 4. (已移除) 生成欢迎通知");
    sqlLines.push("-- ============================================");

    sqlLines.push("\nCOMMIT;");
    sqlLines.push("SET FOREIGN_KEY_CHECKS = 1;");

    return sqlLines.join("\n");
}

const content = generateEnhancedSql();
const targetPath = path.join('f:', 'medical-health-system', 'docs', 'sql', 'generate_test_data.sql');
fs.writeFileSync(targetPath, content, 'utf8');
console.log(`Enhanced SQL file generated at: ${targetPath}`);
