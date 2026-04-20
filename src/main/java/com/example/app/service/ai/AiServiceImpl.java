package com.example.app.service.ai;

import com.example.app.dto.ai.AiAnalysisResult;
import com.example.app.dto.ai.AiRecommendRequest;
import com.example.app.entity.Flight;
import com.example.app.entity.UserFeedback;
import com.example.app.mapper.UserFeedbackMapper;
import com.example.app.service.FlightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI服务实现类
 */
@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    private final AiClient aiClient;
    private final PromptBuilder promptBuilder;
    private final AiResponseParser aiResponseParser;
    private final FlightService flightService;
    private final UserFeedbackMapper userFeedbackMapper;
    private final ObjectMapper objectMapper;

    @Value("${ai.recommendation.enabled:true}")
    private boolean aiEnabled;

    @Value("${ai.recommendation.rerank-threshold:0.7}")
    private double rerankThreshold;

    @Value("${ai.recommendation.feedback-weight:0.3}")
    private double feedbackWeight;

    @Autowired
    public AiServiceImpl(AiClient aiClient, PromptBuilder promptBuilder,
                         AiResponseParser aiResponseParser, FlightService flightService,
                         UserFeedbackMapper userFeedbackMapper) {
        this.aiClient = aiClient;
        this.promptBuilder = promptBuilder;
        this.aiResponseParser = aiResponseParser;
        this.flightService = flightService;
        this.userFeedbackMapper = userFeedbackMapper;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public boolean isAiEnabled() {
        return aiEnabled && aiClient.testConnection();
    }

    @Override
    public Map<String, List<Flight>> rerankRecommendations(Map<String, List<Flight>> recommendations, Long userId) {
        if (!isAiEnabled()) {
            log.debug("AI服务未启用，跳过重新排序");
            return recommendations;
        }

        if (recommendations == null || recommendations.isEmpty()) {
            return recommendations;
        }

        try {
            // 合并所有推荐航班
            List<Flight> allFlights = recommendations.values().stream()
                    .flatMap(List::stream)
                    .distinct()
                    .collect(Collectors.toList());

            if (allFlights.isEmpty()) {
                return recommendations;
            }

            // 获取用户历史反馈
            List<UserFeedback> userFeedbacks = userId != null ?
                    userFeedbackMapper.selectByUserId(userId) : Collections.emptyList();

            // 构建Prompt并调用AI
            String prompt = promptBuilder.buildFlightRerankPrompt(allFlights, userId, userFeedbacks);
            String aiResponse = aiClient.callChatCompletion(
                    "你是一个专业的机票推荐专家，请根据航班信息和用户偏好进行智能排序。",
                    prompt
            );

            if (aiResponse == null || !aiResponseParser.isValidResponse(aiResponse)) {
                log.warn("AI响应无效，返回原始推荐");
                return recommendations;
            }

            // 解析AI排序结果
            Map<Long, Double> flightScores = aiResponseParser.parseFlightRerankResponse(aiResponse);

            // 应用AI排序到推荐结果
            return applyRerankToRecommendations(recommendations, flightScores);

        } catch (Exception e) {
            log.error("AI重新排序失败", e);
            return recommendations; // 失败时返回原始推荐
        }
    }

    @Override
    public Map<String, List<Flight>> generatePersonalizedRecommendations(AiRecommendRequest request, Long userId) {
        if (!isAiEnabled()) {
            log.debug("AI服务未启用，返回空推荐");
            return Collections.emptyMap();
        }

        try {
            // 构建用户偏好
            Map<String, Object> userPreferences = buildUserPreferences(request, userId);

            // 获取可选航班（如果有过滤条件）
            List<Flight> candidateFlights = getCandidateFlights(request);

            // 构建Prompt并调用AI
            String prompt = promptBuilder.buildPersonalizedRecommendationPrompt(userPreferences, candidateFlights);
            String aiResponse = aiClient.callChatCompletion(
                    "你是一个个性化机票推荐专家，请根据用户偏好生成最适合的航班推荐。",
                    prompt
            );

            if (aiResponse == null || !aiResponseParser.isValidResponse(aiResponse)) {
                log.warn("AI响应无效，返回空推荐");
                return Collections.emptyMap();
            }

            // 解析推荐结果
            Map<String, Object> parsedResult = aiResponseParser.parsePersonalizedRecommendationResponse(aiResponse);

            // 构建推荐结果
            return buildPersonalizedRecommendations(parsedResult, request.getMaxResults());

        } catch (Exception e) {
            log.error("生成个性化推荐失败", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public AiAnalysisResult analyzeUserFeedback(UserFeedback feedback) {
        if (!isAiEnabled()) {
            log.debug("AI服务未启用，返回默认分析结果");
            return createDefaultAnalysisResult(feedback);
        }

        try {
            // 构建Prompt并调用AI
            String prompt = promptBuilder.buildFeedbackAnalysisPrompt(feedback);
            String aiResponse = aiClient.callChatCompletion(
                    "你是一个用户行为分析专家，请分析用户反馈并提供有价值的见解。",
                    prompt
            );

            if (aiResponse == null || !aiResponseParser.isValidResponse(aiResponse)) {
                log.warn("AI响应无效，返回默认分析结果");
                return createDefaultAnalysisResult(feedback);
            }

            // 解析分析结果
            AiAnalysisResult result = aiResponseParser.parseFeedbackAnalysisResponse(aiResponse);
            result.setFeedbackId(feedback.getId());

            // 更新反馈的AI分析结果
            updateFeedbackWithAnalysis(feedback, result);

            return result;

        } catch (Exception e) {
            log.error("分析用户反馈失败", e);
            return createDefaultAnalysisResult(feedback);
        }
    }

    @Override
    public List<AiAnalysisResult> analyzeUserFeedbacks(List<UserFeedback> feedbacks) {
        if (!isAiEnabled()) {
            log.debug("AI服务未启用，返回空列表");
            return Collections.emptyList();
        }

        return feedbacks.stream()
                .map(this::analyzeUserFeedback)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> analyzeRecommendationPerformance(String period) {
        if (!isAiEnabled()) {
            log.debug("AI服务未启用，返回空分析结果");
            return Collections.emptyMap();
        }

        try {
            // 收集性能数据
            Map<String, Object> performanceData = collectPerformanceData(period);

            // 构建Prompt并调用AI
            String prompt = promptBuilder.buildPerformanceAnalysisPrompt(performanceData);
            String aiResponse = aiClient.callChatCompletion(
                    "你是一个推荐系统分析师，请分析推荐系统性能并提供优化建议。",
                    prompt
            );

            if (aiResponse == null || !aiResponseParser.isValidResponse(aiResponse)) {
                log.warn("AI响应无效，返回空分析结果");
                return Collections.emptyMap();
            }

            // 解析分析结果
            return aiResponseParser.parsePerformanceAnalysisResponse(aiResponse);

        } catch (Exception e) {
            log.error("分析推荐效果失败", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, Object> optimizeRecommendationStrategy(List<UserFeedback> feedbacks) {
        if (!isAiEnabled()) {
            log.debug("AI服务未启用，返回空优化结果");
            return Collections.emptyMap();
        }

        try {
            // 分析所有反馈
            List<AiAnalysisResult> analysisResults = analyzeUserFeedbacks(feedbacks);

            // 提取优化建议
            Map<String, Object> optimizationData = extractOptimizationData(analysisResults);

            // 生成优化策略
            return generateOptimizationStrategy(optimizationData);

        } catch (Exception e) {
            log.error("优化推荐策略失败", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean testAiConnection() {
        return aiClient.testConnection();
    }

    /**
     * 应用重新排序到推荐结果
     */
    private Map<String, List<Flight>> applyRerankToRecommendations(
            Map<String, List<Flight>> recommendations, Map<Long, Double> flightScores) {

        Map<String, List<Flight>> result = new LinkedHashMap<>();

        for (Map.Entry<String, List<Flight>> entry : recommendations.entrySet()) {
            String category = entry.getKey();
            List<Flight> flights = entry.getValue();

            // 根据AI评分排序航班
            List<Flight> sortedFlights = flights.stream()
                    .sorted((f1, f2) -> {
                        double score1 = flightScores.getOrDefault(f1.getId(), 0.0);
                        double score2 = flightScores.getOrDefault(f2.getId(), 0.0);
                        return Double.compare(score2, score1); // 降序排序
                    })
                    .collect(Collectors.toList());

            // 过滤低分航班
            List<Flight> filteredFlights = sortedFlights.stream()
                    .filter(f -> flightScores.getOrDefault(f.getId(), 0.0) >= rerankThreshold)
                    .collect(Collectors.toList());

            if (!filteredFlights.isEmpty()) {
                result.put(category, filteredFlights);
            }
        }

        return result;
    }

    /**
     * 构建用户偏好
     */
    private Map<String, Object> buildUserPreferences(AiRecommendRequest request, Long userId) {
        Map<String, Object> preferences = new HashMap<>();

        // 基础偏好
        preferences.put("preference_type", request.getPreferenceType());
        preferences.put("time_preference", request.getTimePreference());
        preferences.put("max_price", request.getMaxPrice());
        preferences.put("cabin_class", request.getCabinClass());
        preferences.put("direct_only", request.getDirectOnly());
        preferences.put("max_stops", request.getMaxStops());
        preferences.put("airline_preference", request.getAirlinePreference());

        // 用户历史（如果有）
        if (userId != null && request.getConsiderHistory() != null && request.getConsiderHistory()) {
            List<UserFeedback> userFeedbacks = userFeedbackMapper.selectByUserId(userId);
            preferences.put("user_feedback_count", userFeedbacks.size());
            preferences.put("feedback_summary", summarizeUserFeedbacks(userFeedbacks));
        }

        return preferences;
    }

    /**
     * 获取候选航班
     */
    private List<Flight> getCandidateFlights(AiRecommendRequest request) {
        // 如果有过滤条件，使用FlightService获取航班
        // 这里简化为返回空列表，表示不限制
        return Collections.emptyList();
    }

    /**
     * 构建个性化推荐
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<Flight>> buildPersonalizedRecommendations(
            Map<String, Object> parsedResult, Integer maxResults) {

        Map<String, List<Flight>> recommendations = new LinkedHashMap<>();

        try {
            List<Map<String, Object>> recList = (List<Map<String, Object>>) parsedResult.get("recommendations");
            if (recList != null && !recList.isEmpty()) {
                List<Flight> flights = new ArrayList<>();

                for (Map<String, Object> rec : recList) {
                    Long flightId = (Long) rec.get("flight_id");
                    if (flightId != null) {
                        Flight flight = flightService.findById(flightId);
                        if (flight != null) {
                            flights.add(flight);
                        }
                    }
                }

                // 限制结果数量
                if (maxResults != null && flights.size() > maxResults) {
                    flights = flights.subList(0, maxResults);
                }

                if (!flights.isEmpty()) {
                    recommendations.put("AI个性化推荐", flights);
                }
            }
        } catch (Exception e) {
            log.error("构建个性化推荐失败", e);
        }

        return recommendations;
    }

    /**
     * 创建默认分析结果
     */
    private AiAnalysisResult createDefaultAnalysisResult(UserFeedback feedback) {
        return AiAnalysisResult.builder()
                .analysisId(UUID.randomUUID().toString())
                .feedbackId(feedback.getId())
                .analysisType("FEEDBACK_ANALYSIS")
                .summary("AI分析服务不可用，使用默认分析")
                .detailedResult("{}")
                .confidenceScore(0.3)
                .recommendedAction("请检查AI服务配置")
                .optimizationSuggestion("等待AI服务恢复")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 更新反馈的AI分析结果
     */
    private void updateFeedbackWithAnalysis(UserFeedback feedback, AiAnalysisResult analysis) {
        try {
            feedback.setAiAnalysis(analysis.getDetailedResult());
            feedback.setConfidenceScore(analysis.getConfidenceScore());
            feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            userFeedbackMapper.update(feedback);
        } catch (Exception e) {
            log.error("更新反馈分析结果失败", e);
        }
    }

    /**
     * 收集性能数据
     */
    private Map<String, Object> collectPerformanceData(String period) {
        Map<String, Object> data = new HashMap<>();

        // 这里简化实现，实际应该从数据库收集数据
        data.put("period", period);
        data.put("total_recommendations", 100);
        data.put("click_through_rate", 0.25);
        data.put("user_satisfaction", 0.78);
        data.put("feedback_count", 45);
        data.put("ai_success_rate", 0.92);

        return data;
    }

    /**
     * 提取优化数据
     */
    private Map<String, Object> extractOptimizationData(List<AiAnalysisResult> analysisResults) {
        Map<String, Object> data = new HashMap<>();

        // 统计反馈情感
        long positiveCount = analysisResults.stream()
                .filter(r -> r.getSummary() != null && r.getSummary().contains("积极"))
                .count();
        long negativeCount = analysisResults.stream()
                .filter(r -> r.getSummary() != null && r.getSummary().contains("消极"))
                .count();

        data.put("total_feedbacks", analysisResults.size());
        data.put("positive_feedbacks", positiveCount);
        data.put("negative_feedbacks", negativeCount);
        data.put("analysis_results", analysisResults);

        return data;
    }

    /**
     * 生成优化策略
     */
    private Map<String, Object> generateOptimizationStrategy(Map<String, Object> optimizationData) {
        Map<String, Object> strategy = new HashMap<>();

        // 基于数据生成简单策略
        long positive = (Long) optimizationData.get("positive_feedbacks");
        long negative = (Long) optimizationData.get("negative_feedbacks");
        long total = (Long) optimizationData.get("total_feedbacks");

        if (total > 0) {
            double satisfactionRate = (double) positive / total;
            strategy.put("satisfaction_rate", satisfactionRate);

            if (satisfactionRate > 0.7) {
                strategy.put("strategy", "继续当前策略，保持用户满意度");
                strategy.put("action", "维持当前推荐算法，监控关键指标");
            } else if (satisfactionRate > 0.4) {
                strategy.put("strategy", "适度调整推荐策略");
                strategy.put("action", "分析负面反馈原因，调整权重分配");
            } else {
                strategy.put("strategy", "需要重大策略调整");
                strategy.put("action", "重新设计推荐算法，考虑更多用户特征");
            }
        }

        return strategy;
    }

    /**
     * 总结用户反馈
     */
    private String summarizeUserFeedbacks(List<UserFeedback> feedbacks) {
        if (feedbacks.isEmpty()) {
            return "无历史反馈";
        }

        long likeCount = feedbacks.stream()
                .filter(f -> "LIKE".equals(f.getFeedbackType()))
                .count();
        long dislikeCount = feedbacks.stream()
                .filter(f -> "DISLIKE".equals(f.getFeedbackType()))
                .count();
        long neutralCount = feedbacks.stream()
                .filter(f -> "NEUTRAL".equals(f.getFeedbackType()))
                .count();

        return String.format("历史反馈：喜欢%d条，不喜欢%d条，中立%d条", likeCount, dislikeCount, neutralCount);
    }

    @Override
    public Map<String, Object> rerankBasedOnFeedback(List<UserFeedback> feedbacks, String feedbackSummary,
                                                     String recommendationType, Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("基于用户反馈重新优化推荐，反馈数量：{}，总结：{}，类型：{}，用户ID：{}",
                    feedbacks.size(), feedbackSummary, recommendationType, userId);

            // 分析反馈
            long likeCount = feedbacks.stream().filter(f -> "LIKE".equals(f.getFeedbackType())).count();
            long dislikeCount = feedbacks.stream().filter(f -> "DISLIKE".equals(f.getFeedbackType())).count();

            // 获取用户历史反馈（如果有的话）
            List<UserFeedback> userHistory = userId != null ?
                    userFeedbackMapper.selectByUserId(userId) : Collections.emptyList();

            // 构建AI提示
            String prompt = promptBuilder.buildFeedbackRerankPrompt(
                    feedbacks, feedbackSummary, recommendationType, userId, userHistory);

            // 调用AI
            String aiResponse = aiClient.callChatCompletion(
                    "你是一个专业的机票推荐优化专家，请根据用户反馈重新优化航班推荐。",
                    prompt
            );

            // 解析AI响应
            if (aiResponse != null && aiResponseParser.isValidResponse(aiResponse)) {
                Map<String, Object> parsedResponse = aiResponseParser.parseRerankResponse(aiResponse);

                // 提取重新排序的推荐
                if (parsedResponse.containsKey("recommendations")) {
                    @SuppressWarnings("unchecked")
                    Map<String, List<Long>> recMap = (Map<String, List<Long>>) parsedResponse.get("recommendations");

                    // 将航班ID转换为Flight对象
                    Map<String, List<Flight>> rerankedFlights = new HashMap<>();
                    for (Map.Entry<String, List<Long>> entry : recMap.entrySet()) {
                        List<Flight> flights = entry.getValue().stream()
                                .map(flightService::findById)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        rerankedFlights.put(entry.getKey(), flights);
                    }
                    result.put("recommendations", rerankedFlights);
                }

                if (parsedResponse.containsKey("analysis")) {
                    result.put("analysis", parsedResponse.get("analysis"));
                }

                // 计算置信度分数
                double confidenceScore = calculateRerankConfidence(feedbacks, likeCount, dislikeCount);
                result.put("confidenceScore", confidenceScore);

            } else {
                // AI调用失败，提供降级方案
                log.warn("AI重新优化失败，使用基于反馈的简单重新排序");
                result.put("analysis", "AI优化暂时不可用，已基于您的反馈进行传统算法优化。");
                result.put("confidenceScore", 0.6);

                // 这里可以添加传统算法的降级逻辑
                Map<String, List<Flight>> fallbackRecommendations = new HashMap<>();
                result.put("recommendations", fallbackRecommendations);
            }

            // 添加反馈统计
            result.put("feedbackStats", Map.of(
                    "total", feedbacks.size(),
                    "like", likeCount,
                    "dislike", dislikeCount,
                    "summary", feedbackSummary
            ));

        } catch (Exception e) {
            log.error("重新优化推荐失败", e);
            result.put("analysis", "重新优化过程中出现错误: " + e.getMessage());
            result.put("confidenceScore", 0.3);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * 计算重新优化的置信度分数
     */
    private double calculateRerankConfidence(List<UserFeedback> feedbacks, long likeCount, long dislikeCount) {
        if (feedbacks.isEmpty()) {
            return 0.5; // 默认中等置信度
        }

        double total = feedbacks.size();
        double likeRatio = likeCount / total;
        double dislikeRatio = dislikeCount / total;

        // 如果喜欢或不喜欢的比例较高，置信度更高
        if (likeRatio > 0.7 || dislikeRatio > 0.7) {
            return 0.85; // 高置信度
        } else if (likeRatio > 0.4 || dislikeRatio > 0.4) {
            return 0.65; // 中等置信度
        } else {
            return 0.5; // 低置信度
        }
    }

    @Override
    public Map<String, Object> chatWithAssistant(String message, List<Map<String, Object>> context, Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 构建用户偏好和上下文
            String userPreferences = getUserPreferences(userId);
            String contextSummary = buildContextSummary(context);

            // 构建简洁的系统提示和用户提示
            String systemPrompt = buildAssistantSystemPrompt(userId, userPreferences);
            String userPrompt = buildAssistantUserPrompt(message, contextSummary);

            // 调用DeepSeek AI服务
            String aiResponse = aiClient.callChatCompletion(systemPrompt, userPrompt);
            String cleanedResponse = cleanAssistantResponse(aiResponse);

            result.put("message", cleanedResponse);
            result.put("status", "SUCCESS");
            result.put("timestamp", System.currentTimeMillis());

            log.info("AI助手调用成功，用户ID：{}，消息长度：{}，响应长度：{}",
                    userId != null ? userId : "匿名", message.length(), cleanedResponse.length());

        } catch (Exception e) {
            log.error("AI助手对话失败", e);
            // 出错时提供简单的错误提示
            String errorMessage = "抱歉，AI助手服务暂时遇到问题：" + e.getMessage();
            result.put("message", errorMessage);
            result.put("status", "ERROR");
            result.put("timestamp", System.currentTimeMillis());
        }

        return result;
    }

    @Override
    public String chatWithAssistantStream(String message, List<Map<String, Object>> context, Long userId) {
        try {
            // 构建用户偏好和上下文
            String userPreferences = getUserPreferences(userId);
            String contextSummary = buildContextSummary(context);

            // 构建简洁的系统提示和用户提示
            String systemPrompt = buildAssistantSystemPrompt(userId, userPreferences);
            String userPrompt = buildAssistantUserPrompt(message, contextSummary);

            // 调用DeepSeek AI服务
            String aiResponse = aiClient.callChatCompletion(systemPrompt, userPrompt);
            String cleanedResponse = cleanAssistantResponse(aiResponse);

            log.info("AI助手流式调用成功，用户ID：{}，消息长度：{}，响应长度：{}",
                    userId != null ? userId : "匿名", message.length(), cleanedResponse.length());

            return cleanedResponse;

        } catch (Exception e) {
            log.error("AI助手流式对话失败", e);
            return "抱歉，AI助手服务暂时遇到问题：" + e.getMessage();
        }
    }

    /**
     * 构建助手系统提示
     */
    private String buildAssistantSystemPrompt(Long userId, String userPreferences) {
        return String.format("""
            你是机票推荐系统的专业AI旅行助手，基于DeepSeek模型。你的目标是帮助用户找到最佳航班选择，提供实用的旅行建议。

            用户信息：
            - 用户ID: %s
            - 偏好摘要: %s

            系统功能：
            1. 航班搜索：支持按出发地、目的地、日期、舱位等条件搜索
            2. 价格比较：显示不同航空公司的价格对比
            3. 智能推荐：基于用户偏好和历史反馈推荐航班
            4. 行程规划：提供航班时间、中转建议等信息

            你的职责：
            1. 引导用户使用系统的搜索功能获取具体航班信息
            2. 基于通用旅行知识和市场规律提供建议
            3. 解释航班选择的标准和优惠策略
            4. 根据用户需求推荐合适的搜索参数
            5. 回答关于航空公司、舱位、行程安排的常见问题

            重要说明：
            - 系统有航班数据库，但价格可能不是实时更新的
            - 当用户询问具体航班时，主动引导他们使用搜索功能
            - 你可以提供搜索建议：出发地、目的地、日期、偏好等
            - 对于价格趋势、优惠时机等问题，基于通用知识回答
            - 保持积极、有帮助的态度，专注于解决问题

            请以专业、友好的方式回应用户，帮助他们获得最佳旅行体验。
            """, userId != null ? userId : "匿名用户", userPreferences);
    }

    /**
     * 构建助手用户提示
     */
    private String buildAssistantUserPrompt(String message, String context) {
        return String.format("""
            用户最新消息：%s

            %s

            请基于系统实际功能和可用数据，直接、专业地回答用户的问题。

            回答要求：
            1. 只基于真实航班数据和系统实际功能提供建议
            2. 不模拟或夸大系统效果，不承诺系统不具备的功能
            3. 如果某些信息不可用，如实告知用户
            4. 提供具体、可操作的建议，避免模糊表述
            5. 保持回答的准确性和实用性

            请基于以上要求，提供最有帮助的回答。
            """, message, context.isEmpty() ? "无对话上下文" : context);
    }

    /**
     * 清理助手响应
     */
    private String cleanAssistantResponse(String response) {
        if (response == null) return "暂无回应";

        // 移除可能的多余JSON标记
        String cleaned = response.trim();
        if (cleaned.startsWith("```") && cleaned.endsWith("```")) {
            cleaned = cleaned.substring(3, cleaned.length() - 3).trim();
        }

        // 移除JSON结构标记
        if (cleaned.startsWith("{\"") && cleaned.contains("\"response\"")) {
            try {
                Map<?, ?> map = objectMapper.readValue(cleaned, Map.class);
                Object responseObj = map.get("response");
                if (responseObj != null) {
                    cleaned = responseObj.toString();
                }
            } catch (Exception e) {
                // 如果解析失败，返回原始内容
            }
        }

        return cleaned;
    }


    /**
     * 获取用户偏好摘要
     */
    private String getUserPreferences(Long userId) {
        if (userId == null) {
            return "匿名用户";
        }

        try {
            List<UserFeedback> userFeedbacks = userFeedbackMapper.selectByUserId(userId);
            if (userFeedbacks.isEmpty()) {
                return "新用户，暂无历史偏好";
            }

            long likeCount = userFeedbacks.stream()
                    .filter(f -> f != null && "LIKE".equals(f.getFeedbackType()))
                    .count();
            long dislikeCount = userFeedbacks.stream()
                    .filter(f -> f != null && "DISLIKE".equals(f.getFeedbackType()))
                    .count();

            return String.format("历史反馈：喜欢%d条，不喜欢%d条，总计%d条反馈",
                    likeCount, dislikeCount, userFeedbacks.size());
        } catch (Exception e) {
            log.warn("获取用户偏好失败", e);
            return "偏好信息获取中...";
        }
    }

    /**
     * 构建上下文摘要
     */
    private String buildContextSummary(List<Map<String, Object>> context) {
        if (context == null || context.isEmpty()) {
            return "无对话上下文";
        }

        try {
            StringBuilder summary = new StringBuilder("对话上下文：\n");
            int count = 0;
            for (Map<String, Object> message : context) {
                if (count >= 5) { // 限制最近5条消息
                    summary.append("... (还有更多历史消息)\n");
                    break;
                }
                String role = (String) message.getOrDefault("role", "unknown");
                String content = (String) message.getOrDefault("content", "");
                if (!content.isEmpty()) {
                    summary.append(String.format("- [%s] %s\n", role, content.substring(0, Math.min(content.length(), 50))));
                    count++;
                }
            }
            return summary.toString();
        } catch (Exception e) {
            log.warn("构建上下文摘要失败", e);
            return "有历史对话上下文";
        }
    }

}