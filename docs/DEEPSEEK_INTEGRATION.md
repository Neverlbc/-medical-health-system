# DeepSeek API 集成指南

## 概述

本文档详细介绍如何在智慧医疗健康管理系统中集成DeepSeek AI接口，实现智能问诊、健康建议等功能。

## DeepSeek API简介

DeepSeek是一个强大的大语言模型API服务，提供类似OpenAI的接口格式，支持多轮对话、上下文理解、专业领域问答等功能。

### 官方文档
- 官网: https://www.deepseek.com
- API文档: https://platform.deepseek.com/api-docs
- 模型列表: deepseek-chat, deepseek-coder

## API配置

### 1. 获取API Key

1. 访问 [DeepSeek平台](https://platform.deepseek.com)
2. 注册并登录账号
3. 进入"API Keys"页面
4. 创建新的API Key并保存

### 2. 项目配置

在 `application.yml` 中添加配置：

```yaml
# DeepSeek AI配置
deepseek:
  # API密钥
  api-key: sk-your-api-key-here
  
  # API接口地址
  api-url: https://api.deepseek.com/v1/chat/completions
  
  # 使用的模型
  model: deepseek-chat
  
  # 最大token数
  max-tokens: 2000
  
  # 温度参数（0-2，越高越随机）
  temperature: 0.7
  
  # 采样参数
  top-p: 0.9
  
  # 请求超时时间（秒）
  timeout: 30
  
  # 是否启用流式响应
  stream: false
  
  # 系统提示词模板
  system-prompts:
    symptom-analysis: "你是一位专业的医疗健康助手，擅长分析患者症状并给出专业建议。请注意，你的建议仅供参考，不能替代专业医生的诊断。"
    medication-guide: "你是一位专业的药师，熟悉各类药物的用法、用量、注意事项和禁忌。"
    health-knowledge: "你是一位健康科普专家，能够用通俗易懂的语言讲解医疗健康知识。"
```

### 3. 配置类实现

```java
@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {
    
    private String apiKey;
    private String apiUrl;
    private String model;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Integer timeout;
    private Boolean stream;
    private Map<String, String> systemPrompts;
}
```

## 核心实现

### 1. DeepSeek客户端封装

```java
@Slf4j
@Service
public class DeepSeekClient {
    
    @Autowired
    private DeepSeekConfig config;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 发送聊天请求
     */
    public DeepSeekResponse chat(DeepSeekRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<DeepSeekResponse> response = restTemplate.exchange(
                config.getApiUrl(),
                HttpMethod.POST,
                entity,
                DeepSeekResponse.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("DeepSeek API调用失败", e);
            throw new BusinessException("AI服务暂时不可用，请稍后重试");
        }
    }
    
    /**
     * 构建请求对象
     */
    public DeepSeekRequest buildRequest(String systemPrompt, String userMessage, List<Message> history) {
        DeepSeekRequest request = new DeepSeekRequest();
        request.setModel(config.getModel());
        request.setMaxTokens(config.getMaxTokens());
        request.setTemperature(config.getTemperature());
        request.setTopP(config.getTopP());
        
        List<Message> messages = new ArrayList<>();
        
        // 系统提示词
        if (StringUtils.isNotBlank(systemPrompt)) {
            messages.add(new Message("system", systemPrompt));
        }
        
        // 历史对话
        if (CollectionUtils.isNotEmpty(history)) {
            messages.addAll(history);
        }
        
        // 用户消息
        messages.add(new Message("user", userMessage));
        
        request.setMessages(messages);
        return request;
    }
}
```

### 2. 请求和响应模型

```java
@Data
public class DeepSeekRequest {
    private String model;
    private List<Message> messages;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Boolean stream;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;      // system, user, assistant
    private String content;
}

@Data
public class DeepSeekResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    public static class Choice {
        private Integer index;
        private Message message;
        private String finishReason;
    }
    
    @Data
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}
```

### 3. AI服务层实现

```java
@Slf4j
@Service
public class AIService {
    
    @Autowired
    private DeepSeekClient deepSeekClient;
    
    @Autowired
    private DeepSeekConfig config;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String CHAT_HISTORY_KEY = "ai:chat:history:";
    private static final int HISTORY_MAX_SIZE = 10;
    
    /**
     * 症状分析
     */
    public AIConsultationResult analyzeSymptom(String sessionId, String symptoms) {
        String systemPrompt = config.getSystemPrompts().get("symptom-analysis");
        
        // 获取历史对话
        List<Message> history = getChatHistory(sessionId);
        
        // 构建请求
        DeepSeekRequest request = deepSeekClient.buildRequest(systemPrompt, symptoms, history);
        
        // 调用API
        long startTime = System.currentTimeMillis();
        DeepSeekResponse response = deepSeekClient.chat(request);
        long responseTime = System.currentTimeMillis() - startTime;
        
        // 提取回复内容
        String aiResponse = response.getChoices().get(0).getMessage().getContent();
        int tokensUsed = response.getUsage().getTotalTokens();
        
        // 保存对话历史
        saveChatHistory(sessionId, symptoms, aiResponse);
        
        // 构建结果
        AIConsultationResult result = new AIConsultationResult();
        result.setQuestion(symptoms);
        result.setAiResponse(aiResponse);
        result.setTokensUsed(tokensUsed);
        result.setResponseTime((int) responseTime);
        
        // 提取建议（通过关键词解析）
        result.setSuggestions(extractSuggestions(aiResponse));
        
        return result;
    }
    
    
    /**
     * 用药指导
     */
    public MedicationGuideResult getMedicationGuide(String medicineName) {
        String systemPrompt = config.getSystemPrompts().get("medication-guide");
        String userMessage = "请介绍一下" + medicineName + "的用法、用量、注意事项和禁忌。";
        
        // 先检查缓存
        String cacheKey = "medication:guide:" + medicineName;
        MedicationGuideResult cached = (MedicationGuideResult) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 构建请求
        DeepSeekRequest request = deepSeekClient.buildRequest(systemPrompt, userMessage, null);
        
        // 调用API
        DeepSeekResponse response = deepSeekClient.chat(request);
        String aiResponse = response.getChoices().get(0).getMessage().getContent();
        
        // 构建结果
        MedicationGuideResult result = new MedicationGuideResult();
        result.setMedicineName(medicineName);
        result.setGuideContent(aiResponse);
        
        // 缓存结果（药品信息相对稳定，缓存7天）
        redisTemplate.opsForValue().set(cacheKey, result, 7, TimeUnit.DAYS);
        
        return result;
    }
    
    /**
     * 健康知识问答
     */
    public String answerHealthQuestion(String question) {
        String systemPrompt = config.getSystemPrompts().get("health-knowledge");
        
        // 构建请求
        DeepSeekRequest request = deepSeekClient.buildRequest(systemPrompt, question, null);
        
        // 调用API
        DeepSeekResponse response = deepSeekClient.chat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }
    
    /**
     * 获取对话历史
     */
    private List<Message> getChatHistory(String sessionId) {
        String key = CHAT_HISTORY_KEY + sessionId;
        List<Object> history = redisTemplate.opsForList().range(key, 0, HISTORY_MAX_SIZE - 1);
        
        if (CollectionUtils.isEmpty(history)) {
            return new ArrayList<>();
        }
        
        return history.stream()
            .map(obj -> (Message) obj)
            .collect(Collectors.toList());
    }
    
    /**
     * 保存对话历史
     */
    private void saveChatHistory(String sessionId, String userMessage, String aiResponse) {
        String key = CHAT_HISTORY_KEY + sessionId;
        
        // 保存用户消息
        redisTemplate.opsForList().rightPush(key, new Message("user", userMessage));
        
        // 保存AI回复
        redisTemplate.opsForList().rightPush(key, new Message("assistant", aiResponse));
        
        // 限制历史记录数量
        redisTemplate.opsForList().trim(key, -HISTORY_MAX_SIZE, -1);
        
        // 设置过期时间（1小时）
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }
    
    /**
     * 提取建议（简单实现，可根据实际情况优化）
     */
    private List<String> extractSuggestions(String aiResponse) {
        List<String> suggestions = new ArrayList<>();
        
        // 按行分割
        String[] lines = aiResponse.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            // 提取"建议"相关的内容
            if (line.matches("^\\d+\\..*") || line.startsWith("- ") || line.startsWith("• ")) {
                suggestions.add(line.replaceFirst("^[\\d\\.\\-•]\\s*", ""));
            }
        }
        
        return suggestions;
    }
    
    /**
     * 格式化报告数据
     */
    private String formatReportData(Map<String, Object> reportData) {
        StringBuilder sb = new StringBuilder();
        
        reportData.forEach((key, value) -> {
            sb.append(key).append(": ").append(value).append("\n");
        });
        
        return sb.toString();
    }
    
    
}
```

## 应用场景

### 1. 智能症状分析

```java
@RestController
@RequestMapping("/api/v1/ai/consultation")
public class AIConsultationController {
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private AIConsultationService consultationService;
    
    @PostMapping("/chat")
    public Result<AIConsultationVO> chat(@RequestBody @Validated ChatRequest request) {
        // 调用AI服务
        AIConsultationResult result = aiService.analyzeSymptom(
            request.getSessionId(), 
            request.getQuestion()
        );
        
        // 保存记录
        Long consultationId = consultationService.saveConsultation(
            SecurityUtils.getUserId(),
            request.getSessionId(),
            request.getQuestion(),
            result
        );
        
        // 构建响应
        AIConsultationVO vo = new AIConsultationVO();
        vo.setConsultationId(consultationId);
        vo.setSessionId(request.getSessionId());
        vo.setQuestion(request.getQuestion());
        vo.setAiResponse(result.getAiResponse());
        vo.setSuggestions(result.getSuggestions());
        vo.setTokensUsed(result.getTokensUsed());
        vo.setResponseTime(result.getResponseTime());
        
        return Result.success(vo);
    }
}
```


## 最佳实践

### 1. 错误处理

```java
try {
    DeepSeekResponse response = deepSeekClient.chat(request);
    return processResponse(response);
} catch (HttpClientErrorException e) {
    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        log.error("DeepSeek API Key无效");
        throw new BusinessException("AI服务配置错误");
    } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
        log.warn("DeepSeek API调用频率超限");
        throw new BusinessException("AI服务繁忙，请稍后重试");
    }
    throw new BusinessException("AI服务异常");
} catch (Exception e) {
    log.error("DeepSeek API调用失败", e);
    throw new BusinessException("AI服务暂时不可用");
}
```

### 2. 限流控制

```java
@Service
public class AIRateLimiter {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String RATE_LIMIT_KEY = "ai:rate:limit:";
    private static final int MAX_REQUESTS_PER_HOUR = 100;
    
    public boolean checkRateLimit(Long userId) {
        String key = RATE_LIMIT_KEY + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
        
        return count <= MAX_REQUESTS_PER_HOUR;
    }
}
```

### 3. 内容过滤

```java
public boolean isValidInput(String input) {
    // 长度检查
    if (input.length() > 2000) {
        return false;
    }
    
    // 敏感词检查
    if (containsSensitiveWords(input)) {
        return false;
    }
    
    return true;
}
```

### 4. 成本监控

```java
@Aspect
@Component
public class AIUsageMonitor {
    
    @Autowired
    private AIUsageService usageService;
    
    @AfterReturning(pointcut = "@annotation(aiMethod)", returning = "result")
    public void monitorUsage(JoinPoint joinPoint, AIMethod aiMethod, Object result) {
        if (result instanceof AIConsultationResult) {
            AIConsultationResult aiResult = (AIConsultationResult) result;
            
            // 记录使用情况
            usageService.recordUsage(
                SecurityUtils.getUserId(),
                aiMethod.type(),
                aiResult.getTokensUsed(),
                aiResult.getResponseTime()
            );
        }
    }
}
```

## 注意事项

1. **API密钥安全**: 
   - 不要在代码中硬编码API Key
   - 使用环境变量或配置中心管理
   - 定期轮换API Key

2. **调用频率限制**:
   - DeepSeek有API调用频率限制
   - 实现客户端限流机制
   - 合理使用缓存减少调用

3. **错误处理**:
   - 妥善处理API调用失败的情况
   - 提供友好的错误提示
   - 实现降级方案

4. **成本控制**:
   - 监控Token使用量
   - 设置单次请求的max_tokens上限
   - 对常见问题使用缓存

5. **内容安全**:
   - 过滤敏感信息
   - 验证输入内容
   - AI回复需要添加免责声明

6. **用户体验**:
   - 显示加载状态
   - 提供反馈机制
   - 支持多轮对话

## 测试示例

```java
@SpringBootTest
public class AIServiceTest {
    
    @Autowired
    private AIService aiService;
    
    @Test
    public void testSymptomAnalysis() {
        String sessionId = "test_session_" + System.currentTimeMillis();
        String symptoms = "我最近经常头痛，还伴有恶心的症状";
        
        AIConsultationResult result = aiService.analyzeSymptom(sessionId, symptoms);
        
        assertNotNull(result);
        assertNotNull(result.getAiResponse());
        assertTrue(result.getTokensUsed() > 0);
        
        System.out.println("AI回复: " + result.getAiResponse());
        System.out.println("建议: " + result.getSuggestions());
    }
}
```

---

**最后更新时间**: 2025-11-06

