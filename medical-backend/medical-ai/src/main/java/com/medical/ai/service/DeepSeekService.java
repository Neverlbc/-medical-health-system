package com.medical.ai.service;

import com.alibaba.fastjson2.JSON;
import com.medical.ai.config.DeepSeekConfig;
import com.medical.ai.model.DeepSeekRequest;
import com.medical.ai.model.DeepSeekResponse;
import com.medical.common.exception.BusinessException;
import com.medical.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek AI服务
 *
 * @author lbc
 * @date 2025-11-06
 */
@Slf4j
@Service
public class DeepSeekService {

    @Autowired
    private DeepSeekConfig config;

    private final OkHttpClient client;

    public DeepSeekService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 发送聊天请求
     */
    public DeepSeekResponse chat(DeepSeekRequest request) {
        try {
            String requestBody = JSON.toJSONString(request);
            log.debug("DeepSeek请求: {}", requestBody);

            Request httpRequest = new Request.Builder()
                    .url(config.getApiUrl())
                    .addHeader("Authorization", "Bearer " + config.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("DeepSeek API调用失败: {}", response.code());
                    throw new BusinessException(ResultCode.AI_SERVICE_ERROR);
                }

                String responseBody = response.body().string();
                log.debug("DeepSeek响应: {}", responseBody);

                return JSON.parseObject(responseBody, DeepSeekResponse.class);
            }
        } catch (Exception e) {
            log.error("DeepSeek API调用异常", e);
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR.getMessage(), e);
        }
    }

    /**
     * 构建请求对象
     */
    public DeepSeekRequest buildRequest(String systemPrompt, String userMessage, List<DeepSeekRequest.Message> history) {
        DeepSeekRequest request = new DeepSeekRequest();
        request.setModel(config.getModel());
        request.setMaxTokens(config.getMaxTokens());
        request.setTemperature(config.getTemperature());
        request.setTopP(config.getTopP());
        request.setStream(false);

        List<DeepSeekRequest.Message> messages = new ArrayList<>();

        // 系统提示词
        if (StringUtils.hasText(systemPrompt)) {
            messages.add(new DeepSeekRequest.Message("system", systemPrompt));
        }

        // 历史对话
        if (history != null && !history.isEmpty()) {
            messages.addAll(history);
        }

        // 用户消息
        messages.add(new DeepSeekRequest.Message("user", userMessage));

        request.setMessages(messages);
        return request;
    }

    /**
     * 症状分析
     */
    public String analyzeSymptom(String symptoms) {
        String systemPrompt = config.getSystemPrompts().get("symptom-analysis");
        DeepSeekRequest request = buildRequest(systemPrompt, symptoms, null);
        DeepSeekResponse response = chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 用药指导
     */
    public String getMedicationGuide(String medicineName) {
        String systemPrompt = config.getSystemPrompts().get("medication-guide");
        String userMessage = "请介绍一下" + medicineName + "的用法、用量、注意事项和禁忌。";
        DeepSeekRequest request = buildRequest(systemPrompt, userMessage, null);
        DeepSeekResponse response = chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 健康知识问答
     */
    public String answerHealthQuestion(String question) {
        String systemPrompt = config.getSystemPrompts().get("health-knowledge");
        DeepSeekRequest request = buildRequest(systemPrompt, question, null);
        DeepSeekResponse response = chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 全量健康档案分析
     */
    public String analyzeHealthProfile(String profileJson, String userQuestion) {
        String systemPrompt = "你是一位经验丰富的私人医生。我将提供患者的全量健康数据（JSON格式），包括基本信息、病史、近期诊断、生命体征和检验报告。" +
                "请根据这些数据，结合用户的提问，进行深度的健康分析。" +
                "请注意：1. 关注异常指标和潜在风险。2. 结合病史给出个性化建议。3. 如果数据正常，给予肯定和鼓励。" +
                "输出格式要求：使用Markdown格式，包含【健康状况评分】、【关键发现】、【风险预警】、【生活建议】等板块。";
        
        String userMessage = "我的健康数据如下：\n" + profileJson + "\n\n我的问题是：" + (StringUtils.hasText(userQuestion) ? userQuestion : "请帮我全面评估一下我的健康状况。");
        
        DeepSeekRequest request = buildRequest(systemPrompt, userMessage, null);
        // 分析任务通常需要更长的输出
        request.setMaxTokens(2000); 
        
        DeepSeekResponse response = chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 生成每日健康小贴士
     */
    public String generateDailyTip(String profileJson) {
        String systemPrompt = "你是一位贴心的家庭医生。请根据患者的健康档案（JSON格式），为他生成一条今日健康小贴士。" +
                "要求：1. 简短精炼（100字以内）。2. 语气温馨亲切。3. 必须结合患者的具体情况（如高血压、过敏等）给出针对性建议。4. 如果患者健康状况良好，则给出通用的养生建议。";
        
        String userMessage = "我的健康数据如下：\n" + profileJson + "\n\n请给我今天的健康建议。";
        
        DeepSeekRequest request = buildRequest(systemPrompt, userMessage, null);
        request.setMaxTokens(200); // 小贴士不需要太长
        
        DeepSeekResponse response = chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 带健康档案的智能问诊
     */
    public String chatWithProfile(String profileJson, String question) {
        String systemPrompt = "你是一位专业的医疗AI助手。以下是当前患者的健康档案（JSON格式）：" + profileJson + 
                "。请结合档案回答患者的问题。如果问题与档案无关，则正常回答。请注意：1. 如果患者询问用药，务必检查过敏史。2. 回答要严谨、专业，但语气要亲切。";
        
        DeepSeekRequest request = buildRequest(systemPrompt, question, null);
        
        DeepSeekResponse response = chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }
}

