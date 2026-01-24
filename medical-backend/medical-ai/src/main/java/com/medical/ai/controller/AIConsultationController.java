package com.medical.ai.controller;

import com.medical.ai.service.DeepSeekService;
import com.medical.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * AI问诊控制器
 *
 * @author 刘柏城
 * @date 2025-11-06
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI智能问诊", description = "AI症状分析、用药指导、健康知识问答")
public class AIConsultationController {

    @Autowired
    private DeepSeekService deepSeekService;

    @PostMapping("/symptom-analysis")
    @Operation(summary = "症状分析")
    public Result<String> analyzeSymptom(@RequestBody @Valid SymptomRequest request) {
        String result = deepSeekService.analyzeSymptom(request.getSymptoms());
        return Result.success(result);
    }

    @PostMapping("/medication-guide")
    @Operation(summary = "用药指导")
    public Result<String> getMedicationGuide(@RequestBody @Valid MedicationRequest request) {
        String result = deepSeekService.getMedicationGuide(request.getMedicineName());
        return Result.success(result);
    }

    @PostMapping("/health-question")
    @Operation(summary = "健康知识问答")
    public Result<String> answerHealthQuestion(@RequestBody @Valid HealthQuestionRequest request) {
        String result = deepSeekService.answerHealthQuestion(request.getQuestion());
        return Result.success(result);
    }

    @Data
    public static class SymptomRequest {
        @NotBlank(message = "症状描述不能为空")
        private String symptoms;
    }

    @Data
    public static class MedicationRequest {
        @NotBlank(message = "药品名称不能为空")
        private String medicineName;
    }

    @Data
    public static class HealthQuestionRequest {
        @NotBlank(message = "问题不能为空")
        private String question;
    }
    @Autowired
    private com.medical.system.service.HealthProfileService healthProfileService;

    @PostMapping("/health-analysis")
    @Operation(summary = "全量健康分析")
    public Result<String> analyzeHealth(@RequestBody @Valid HealthAnalysisRequest request) {
        Long userId = com.medical.common.utils.SecurityUtils.getUserId();
        // 获取健康档案 (Service内部会进行权限校验)
        // 注意：这里我们假设当前用户就是患者。如果是医生查看患者，需要前端传 patientId，这里暂简化为查看自己
        // 如果需要支持医生查看，Request需增加 patientId 字段，并在此处判断
        Long targetId = request.getPatientId() != null ? request.getPatientId() : userId;
        
        var profile = healthProfileService.getProfile(targetId);
        String json = com.alibaba.fastjson2.JSON.toJSONString(profile);
        
        String result = deepSeekService.analyzeHealthProfile(json, request.getQuestion());
        return Result.success(result);
    }

    @GetMapping("/daily-tip")
    @Operation(summary = "每日健康小贴士")
    public Result<String> getDailyTip() {
        Long userId = com.medical.common.utils.SecurityUtils.getUserId();
        var profile = healthProfileService.getProfile(userId);
        String json = com.alibaba.fastjson2.JSON.toJSONString(profile);
        
        String result = deepSeekService.generateDailyTip(json);
        return Result.success(result);
    }

    @PostMapping("/chat")
    @Operation(summary = "智能问诊(带档案)")
    public Result<String> chat(@RequestBody @Valid ChatRequest request) {
        Long userId = com.medical.common.utils.SecurityUtils.getUserId();
        var profile = healthProfileService.getProfile(userId);
        String json = com.alibaba.fastjson2.JSON.toJSONString(profile);
        
        String result = deepSeekService.chatWithProfile(json, request.getMessage());
        return Result.success(result);
    }

    @Data
    public static class ChatRequest {
        @NotBlank(message = "消息不能为空")
        private String message;
    }

    @Data
    public static class HealthAnalysisRequest {
        private String question;
        private Long patientId; // 可选，医生查询时使用
    }
}

