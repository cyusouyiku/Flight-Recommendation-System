package com.example.app.service.ai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OpenAI客户端包装器
 */
@Component
public class AiClient {

    private static final Logger log = LoggerFactory.getLogger(AiClient.class);
    private final OpenAiService openAiService;
    private final String model;
    private final Double temperature;
    private final Integer maxTokens;

    @Autowired
    public AiClient(OpenAiService openAiService,
                    @Value("${ai.openai.model:deepseek-chat}") String model,
                    @Value("${ai.openai.temperature:0.3}") Double temperature,
                    @Value("${ai.openai.max-tokens:1000}") Integer maxTokens) {
        this.openAiService = openAiService;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
    }

    /**
     * 调用OpenAI聊天完成API
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI响应文本
     */
    public String callChatCompletion(String systemPrompt, String userPrompt) {
        return callChatCompletion(systemPrompt, userPrompt, null);
    }

    /**
     * 调用OpenAI聊天完成API，支持自定义参数
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @param temperature  温度参数（0-2），控制随机性
     * @return AI响应文本
     */
    public String callChatCompletion(String systemPrompt, String userPrompt, Double temperature) {
        if (openAiService == null) {
            log.warn("OpenAI服务不可用，跳过AI调用");
            return null;
        }

        try {
            // 创建聊天消息
            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt);
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .temperature(temperature != null ? temperature : this.temperature)
                    .maxTokens(maxTokens)
                    .messages(List.of(systemMessage, userMessage))
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                String response = result.getChoices().get(0).getMessage().getContent();
                log.debug("OpenAI调用成功，响应长度: {}", response.length());
                return response;
            } else {
                log.warn("OpenAI调用返回空结果");
                return null;
            }
        } catch (Exception e) {
            log.error("OpenAI调用失败", e);
            return null;
        }
    }

    /**
     * 测试AI连接
     *
     * @return 连接是否成功
     */
    public boolean testConnection() {
        if (openAiService == null) {
            log.warn("OpenAI服务未初始化，AI连接测试失败");
            return false;
        }

        try {
            // 尝试调用一个简单的请求测试连接
            String response = callChatCompletion("你是一个测试助手，请回复'连接成功'", "请回复'连接成功'");
            if (response != null && !response.trim().isEmpty()) {
                log.debug("AI连接测试成功，响应长度: {}", response.length());
                return true;
            } else {
                log.warn("AI连接测试失败，响应为空或null");
                return false;
            }
        } catch (Exception e) {
            log.error("AI连接测试失败", e);
            return false;
        }
    }

    /**
     * 获取OpenAI服务状态
     *
     * @return 服务状态描述
     */
    public String getServiceStatus() {
        if (openAiService == null) {
            return "AI服务未配置";
        }
        return testConnection() ? "AI服务可用" : "AI服务连接失败";
    }
}