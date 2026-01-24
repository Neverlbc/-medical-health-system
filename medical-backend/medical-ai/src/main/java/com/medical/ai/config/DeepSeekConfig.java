package com.medical.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DeepSeek配置
 *
 * @author 刘柏城
 * @date 2025-11-06
 */
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

