package com.example.app.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;

/**
 * AI配置类，配置OpenAI客户端，支持DeepSeek API
 */
@Configuration
@ConfigurationProperties(prefix = "ai.openai")
public class AiConfig {

    private String apiKey;
    private String baseUrl = "https://api.deepseek.com/v1/";
    private String model = "deepseek-chat";
    private Double temperature = 0.3;
    private Integer maxTokens = 1000;
    private Duration timeout = Duration.ofSeconds(30);

    @Bean
    public OpenAiService openAiService() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.startsWith("sk-demo-key")) {
            // 如果API密钥未配置或是演示密钥，返回null表示AI服务不可用
            return null;
        }
        // 创建支持自定义baseUrl的OpenAiApi
        OpenAiApi api = createOpenAiApi();
        return new OpenAiService(api);
    }

    /**
     * 创建支持自定义baseUrl的OpenAiApi
     */
    private OpenAiApi createOpenAiApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .addInterceptor(chain -> {
                    // 添加Authorization头
                    var newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + apiKey)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        // 创建配置好的ObjectMapper，忽略未知属性
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        return retrofit.create(OpenAiApi.class);
    }

    // Getter和Setter方法
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
}