import random

def generate_sql():
    password_hash = '$2a$10$K8aIDyfnZYQS.UgdYiw1s.05P5qqvaTfk65hASDDDxvMAxy1vPTT6'
    
    sql_lines = [
        "SET NAMES utf8mb4;",
        "SET FOREIGN_KEY_CHECKS = 0;",
        "",
        "-- ----------------------------",
        "-- Batch Insert Test Data",
        "-- ----------------------------",
        "BEGIN;",
        ""
    ]
    
    # Doctor setup
    departments = ['内科', '外科', '儿科', '妇产科', '骨科', '口腔科', '眼科', '中医科', '皮肤科', '耳鼻喉科']
    titles = ['主任医师', '副主任医师', '主治医师', '医师']
    surnames = ['李', '张', '王', '赵', '周', '吴', '郑', '孙', '韩', '沈']
    names = ['建国', '伟', '芳', '艳', '强', '明', '勇', '军', '伟', '霞', '平', '华']
    
    # Generate 30 Doctors
    start_user_id_doctor = 100
    start_doctor_id = 100
    
    sql_lines.append("-- 插入 30 个医生用户")
    for i in range(30):
        uid = start_user_id_doctor + i
        username = f"doctor_test_{i+1}"
        nickname = random.choice(surnames) + random.choice(names)
        phone = f"138{str(i+1).zfill(8)}"
        email = f"{username}@example.com"
        sql_lines.append(f"INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `role`, `status`, `create_time`) VALUES ({uid}, '{username}', '{password_hash}', '{nickname}', '{phone}', '{email}', 'DOCTOR', 1, NOW());")
    
    sql_lines.append("")
    sql_lines.append("-- 插入 30 个医生信息")
    for i in range(30):
        uid = start_user_id_doctor + i
        did = start_doctor_id + i
        real_name = f"{random.choice(surnames)}{random.choice(names)}"
        gender = random.randint(0, 1)
        dept = random.choice(departments)
        title = random.choice(titles)
        fee = random.choice([10.00, 20.00, 30.00, 50.00])
        sql_lines.append(f"INSERT INTO `doctor_info` (`id`, `user_id`, `real_name`, `gender`, `department`, `title`, `work_years`, `consultation_fee`, `status`, `create_time`) VALUES ({did}, {uid}, '{real_name}', {gender}, '{dept}', '{title}', {random.randint(5, 40)}, {fee}, 1, NOW());")
    
    # Patient setup
    start_user_id_patient = 200
    start_patient_id = 200
    addresses = ['朝阳区', '海淀区', '东城区', '西城区', '丰台区', '石景山区', '大兴区', '通州区']
    blood_types = ['A', 'B', 'AB', 'O']
    
    sql_lines.append("")
    sql_lines.append("-- 插入 50 个患者用户")
    for i in range(50):
        uid = start_user_id_patient + i
        username = f"patient_test_{i+1}"
        nickname = random.choice(surnames) + random.choice(names)
        phone = f"139{str(i+1).zfill(8)}"
        email = f"{username}@example.com"
        sql_lines.append(f"INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `role`, `status`, `create_time`) VALUES ({uid}, '{username}', '{password_hash}', '{nickname}', '{phone}', '{email}', 'PATIENT', 1, NOW());")
        
    sql_lines.append("")
    sql_lines.append("-- 插入 50 个患者信息")
    for i in range(50):
        uid = start_user_id_patient + i
        pid = start_patient_id + i
        real_name = f"{random.choice(surnames)}{random.choice(names)}"
        gender = random.randint(0, 1)
        age = random.randint(18, 80)
        birthday = f"{2024 - age}-01-01"
        id_card = f"110101{2024-age}0101{str(i+1).zfill(4)}"
        address = f"北京市{random.choice(addresses)}XX路{i+1}号"
        blood = random.choice(blood_types)
        sql_lines.append(f"INSERT INTO `patient_info` (`id`, `user_id`, `real_name`, `gender`, `birthday`, `age`, `id_card`, `address`, `blood_type`, `create_time`) VALUES ({pid}, {uid}, '{real_name}', {gender}, '{birthday}', {age}, '{id_card}', '{address}', '{blood}', NOW());")
    
    sql_lines.append("")
    sql_lines.append("COMMIT;")
    sql_lines.append("SET FOREIGN_KEY_CHECKS = 1;")
    
    return "\n".join(sql_lines)

if __name__ == "__main__":
    content = generate_sql()
    with open(r'f:\medical-health-system\docs\sql\generate_test_data.sql', 'w', encoding='utf-8') as f:
        f.write(content)
    print("SQL file generated successfully.")
