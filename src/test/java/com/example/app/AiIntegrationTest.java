package com.example.app;

import com.example.app.service.ai.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AI集成测试，验证应用上下文和AI服务配置
 */
@SpringBootTest
@TestPropertySource(properties = {
    "ai.openai.api-key=test-key",
    "ai.openai.model=deepseek-chat",
    "ai.openai.temperature=0.3",
    "ai.openai.max-tokens=1000",
    "ai.openai.timeout=30s",
    "ai.recommendation.enabled=true",
    "ai.recommendation.rerank-threshold=0.7",
    "ai.recommendation.feedback-weight=0.3"
})
class AiIntegrationTest {

    @Autowired(required = false)
    private AiService aiService;

    @Test
    void contextLoads() {
        // 验证应用上下文加载成功
        assertThat(aiService).isNotNull();
    }

    @Test
    void aiServiceShouldBeConfigured() {
        // 验证AI服务已配置
        assertThat(aiService).isNotNull();

        // 测试AI服务是否可用（使用测试密钥时可能不可用，但至少应该配置）
        // 注意：使用演示密钥时AI服务会返回null
    }
}