package fin.kk.mcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Configuration class for OpenAI integration
 */
@Configuration
public class OpenAiConfig {

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${openai.model:gpt-4}")
    private String model;

    @Value("${openai.temperature:0.7}")
    private Double temperature;

    @Value("${openai.max-tokens:1000}")
    private Integer maxTokens;

    @Value("${openai.request-timeout:30s}")
    private Duration requestTimeout;

    @Value("${openai.connection-timeout:10s}")
    private Duration connectionTimeout;

    @Value("${openai.function-calling-enabled:true}")
    private Boolean functionCallingEnabled;

    /**
     * WebClient configured for OpenAI API calls
     */
    @Bean("openAiWebClient")
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }

    // Getters for configuration properties
    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public Boolean getFunctionCallingEnabled() {
        return functionCallingEnabled;
    }
} 