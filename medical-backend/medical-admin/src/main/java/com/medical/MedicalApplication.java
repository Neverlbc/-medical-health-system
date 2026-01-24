package com.medical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧医疗健康管理系统启动类
 *
 * @author 刘柏城
 * @date 2025-11-06
 */
@SpringBootApplication
public class MedicalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalApplication.class, args);
        System.out.println("\n" +
                "========================================\n" +
                "  智慧医疗健康管理系统启动成功！\n" +
                "  API文档地址: http://localhost:8080/doc.html\n" +
                "========================================\n");
    }
}

