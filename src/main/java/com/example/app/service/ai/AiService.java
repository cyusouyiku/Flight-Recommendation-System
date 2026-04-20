package com.example.app.service.ai;

import com.example.app.dto.ai.AiAnalysisResult;
import com.example.app.dto.ai.AiRecommendRequest;
import com.example.app.entity.Flight;
import com.example.app.entity.UserFeedback;

import java.util.List;
import java.util.Map;

/**
 * AI服务接口
 */
public interface AiService {

    /**
     * 检查AI服务是否可用
     */
    boolean isAiEnabled();

    /**
     * 对推荐结果进行AI重新排序
     *
     * @param recommendations 原始推荐结果
     * @param userId          用户ID
     * @return 重新排序后的推荐结果
     */
    Map<String, List<Flight>> rerankRecommendations(Map<String, List<Flight>> recommendations, Long userId);

    /**
     * 生成个性化AI推荐
     *
     * @param request AI推荐请求
     * @param userId  用户ID
     * @return AI增强的推荐结果
     */
    Map<String, List<Flight>> generatePersonalizedRecommendations(AiRecommendRequest request, Long userId);

    /**
     * 分析用户反馈
     *
     * @param feedback 用户反馈
     * @return AI分析结果
     */
    AiAnalysisResult analyzeUserFeedback(UserFeedback feedback);

    /**
     * 批量分析用户反馈
     *
     * @param feedbacks 用户反馈列表
     * @return AI分析结果列表
     */
    List<AiAnalysisResult> analyzeUserFeedbacks(List<UserFeedback> feedbacks);

    /**
     * 获取推荐效果分析
     *
     * @param period 分析周期（如：LAST_7_DAYS）
     * @return 分析结果
     */
    Map<String, Object> analyzeRecommendationPerformance(String period);

    /**
     * 优化推荐策略
     *
     * @param feedbacks 用户反馈数据
     * @return 优化结果
     */
    Map<String, Object> optimizeRecommendationStrategy(List<UserFeedback> feedbacks);

    /**
     * 测试AI连接
     *
     * @return 连接状态
     */
    boolean testAiConnection();

    /**
     * 基于用户反馈重新优化推荐
     *
     * @param feedbacks           用户反馈列表
     * @param feedbackSummary     反馈总结
     * @param recommendationType  推荐类型
     * @param userId              用户ID
     * @return 优化结果，包含重新排序的推荐和AI分析
     */
    Map<String, Object> rerankBasedOnFeedback(List<UserFeedback> feedbacks, String feedbackSummary,
                                              String recommendationType, Long userId);

    /**
     * AI助手对话
     *
     * @param message 用户消息
     * @param context 对话上下文
     * @param userId  用户ID
     * @return AI助手响应
     */
    Map<String, Object> chatWithAssistant(String message, List<Map<String, Object>> context, Long userId);

    /**
     * AI助手流式对话
     *
     * @param message 用户消息
     * @param context 对话上下文
     * @param userId  用户ID
     * @return AI助手流式响应（Server-Sent Events）
     */
    String chatWithAssistantStream(String message, List<Map<String, Object>> context, Long userId);
}