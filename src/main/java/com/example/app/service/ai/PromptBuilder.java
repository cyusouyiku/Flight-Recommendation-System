package com.example.app.service.ai;

import com.example.app.entity.Flight;
import com.example.app.entity.UserFeedback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Prompt构建器，用于构建AI提示词
 */
@Component
public class PromptBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 构建航班重新排序的Prompt
     *
     * @param flights       航班列表
     * @param userId        用户ID
     * @param userFeedbacks 用户历史反馈
     * @return 构建好的Prompt
     */
    public String buildFlightRerankPrompt(List<Flight> flights, Long userId, List<UserFeedback> userFeedbacks) {
        String formattedFlights = formatFlights(flights);
        String formattedFeedbacks = formatFeedbacks(userFeedbacks);

        return String.format("""
            你是一个机票推荐专家。请根据以下信息重新排序航班推荐：

            用户ID: %s
            用户历史反馈: %s

            待排序航班列表:
            %s

            请考虑以下因素：
            1. 价格合理性
            2. 航班时间便利性
            3. 航空公司声誉
            4. 用户历史偏好
            5. 转机次数和总时长
            6. 舱位舒适度
            7. 价格与原始价对比
            8. 可用座位情况

            请返回JSON格式的排序结果，包含以下结构：
            {
              "analysis": "对排序原因的分析摘要",
              "sorted_flights": [
                {
                  "flight_id": 航班ID,
                  "score": 排序分数（0-1，越高越好）,
                  "reason": "推荐理由"
                }
              ]
            }

            请确保排序结果基于专业判断，而不是简单的价格排序。
            """, userId, formattedFeedbacks, formattedFlights);
    }

    /**
     * 构建用户反馈分析的Prompt
     *
     * @param feedback 用户反馈
     * @return 构建好的Prompt
     */
    public String buildFeedbackAnalysisPrompt(UserFeedback feedback) {
        String feedbackContext = formatFeedback(feedback);

        return String.format("""
            你是一个用户行为分析专家。请分析以下用户反馈：

            %s

            请分析：
            1. 用户反馈的情感倾向（积极、消极、中立）
            2. 反馈中提到的关键因素（价格、时间、服务、便利性等）
            3. 用户可能的潜在需求
            4. 对推荐系统的改进建议

            请返回JSON格式的分析结果，包含以下结构：
            {
              "sentiment": "情感倾向",
              "sentiment_score": 情感分数（-1到1）,
              "key_factors": ["关键因素1", "关键因素2"],
              "potential_needs": "潜在需求",
              "improvement_suggestions": "改进建议",
              "confidence": 分析置信度（0-1）
            }
            """, feedbackContext);
    }

    /**
     * 构建推荐效果分析的Prompt
     *
     * @param performanceData 性能数据
     * @return 构建好的Prompt
     */
    public String buildPerformanceAnalysisPrompt(Map<String, Object> performanceData) {
        try {
            String jsonData = objectMapper.writeValueAsString(performanceData);
            return String.format("""
                你是一个推荐系统分析师。请分析以下推荐系统性能数据：

                %s

                请分析：
                1. 推荐系统的整体效果
                2. 不同推荐策略的优缺点
                3. 用户接受度分析
                4. 潜在问题识别
                5. 优化建议

                请返回JSON格式的分析报告，包含以下结构：
                {
                  "overall_assessment": "整体评估",
                  "strengths": ["优势1", "优势2"],
                  "weaknesses": ["弱点1", "弱点2"],
                  "opportunities": ["机会1", "机会2"],
                  "threats": ["威胁1", "威胁2"],
                  "recommendations": ["建议1", "建议2"],
                  "confidence": 分析置信度（0-1）
                }
                """, jsonData);
        } catch (JsonProcessingException e) {
            return String.format("""
                请分析推荐系统性能数据。数据格式有误，请基于你的专业知识提供一般性建议。
                """);
        }
    }

    /**
     * 构建个性化推荐生成的Prompt
     *
     * @param userPreferences 用户偏好
     * @param flights         可选航班列表
     * @return 构建好的Prompt
     */
    public String buildPersonalizedRecommendationPrompt(Map<String, Object> userPreferences, List<Flight> flights) {
        String formattedPreferences = formatUserPreferences(userPreferences);
        String formattedFlights = flights != null ? formatFlights(flights) : "无特定航班限制";

        return String.format("""
            你是一个个性化推荐专家。请根据以下用户偏好生成航班推荐：

            用户偏好:
            %s

            可选航班（如有限制）:
            %s

            请生成个性化推荐，考虑：
            1. 价格与预算的匹配度
            2. 时间便利性
            3. 行程舒适度
            4. 用户历史偏好
            5. 价值与价格比

            请返回JSON格式的推荐结果，包含以下结构：
            {
              "recommendations": [
                {
                  "flight_id": 航班ID,
                  "reason": "推荐理由",
                  "match_score": 匹配度分数（0-1）,
                  "highlight": "亮点说明"
                }
              ],
              "summary": "推荐总结",
              "user_advice": "给用户的建议"
            }
            """, formattedPreferences, formattedFlights);
    }

    /**
     * 格式化航班信息
     */
    private String formatFlights(List<Flight> flights) {
        if (flights == null || flights.isEmpty()) {
            return "无航班数据";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            sb.append(String.format("""
                航班 %d:
                  - ID: %d
                  - 航班号: %s
                  - 航空公司: %s
                  - 出发: %s (%s) 时间: %s
                  - 到达: %s (%s) 时间: %s
                  - 时长: %d分钟
                  - 舱位: %s
                  - 转机次数: %d (直飞: %s)
                  - 价格: %d元 (原价: %d元)
                  - 可用座位: %d
                  - 状态: %s
                """,
                    i + 1,
                    flight.getId(),
                    flight.getFlightNumber(),
                    flight.getAirlineCode(),
                    flight.getDepartureAirport(),
                    flight.getDepartureAirportCode(),
                    flight.getDepartureTime() != null ? flight.getDepartureTime().format(dateFormatter) : "未知",
                    flight.getArrivalAirport(),
                    flight.getArrivalAirportCode(),
                    flight.getArrivalTime() != null ? flight.getArrivalTime().format(dateFormatter) : "未知",
                    flight.getDurationMinutes(),
                    flight.getCabinClass(),
                    flight.getStops(),
                    flight.isDirect() ? "是" : "否",
                    flight.getPrice(),
                    flight.getOriginalPrice() != null ? flight.getOriginalPrice() : 0,
                    flight.getAvailableSeats() != null ? flight.getAvailableSeats() : 0,
                    flight.getStatus()
            ));
        }

        return sb.toString();
    }

    /**
     * 格式化用户反馈
     */
    private String formatFeedbacks(List<UserFeedback> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return "无历史反馈";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < feedbacks.size(); i++) {
            UserFeedback feedback = feedbacks.get(i);
            sb.append(String.format("""
                反馈 %d:
                  - 航班ID: %d
                  - 反馈类型: %s
                  - 反馈内容: %s
                  - 反馈时间: %s
                """,
                    i + 1,
                    feedback.getFlightId(),
                    feedback.getFeedbackType(),
                    feedback.getFeedbackText() != null ? feedback.getFeedbackText() : "无文本",
                    feedback.getCreatedAt() != null ? feedback.getCreatedAt().toLocalDateTime().format(dateFormatter) : "未知时间"
            ));
        }

        return sb.toString();
    }

    /**
     * 格式化单个用户反馈
     */
    private String formatFeedback(UserFeedback feedback) {
        return String.format("""
            航班ID: %d
            用户ID: %d
            反馈类型: %s
            反馈文本: %s
            反馈时间: %s
            """,
                feedback.getFlightId(),
                feedback.getUserId(),
                feedback.getFeedbackType(),
                feedback.getFeedbackText() != null ? feedback.getFeedbackText() : "无文本",
                feedback.getCreatedAt() != null ? feedback.getCreatedAt().toLocalDateTime().format(dateFormatter) : "未知时间"
        );
    }

    /**
     * 格式化用户偏好
     */
    private String formatUserPreferences(Map<String, Object> userPreferences) {
        if (userPreferences == null || userPreferences.isEmpty()) {
            return "无偏好信息";
        }

        StringBuilder sb = new StringBuilder();
        try {
            String json = objectMapper.writeValueAsString(userPreferences);
            sb.append(json);
        } catch (JsonProcessingException e) {
            sb.append(userPreferences.toString());
        }

        return sb.toString();
    }

    /**
     * 构建基于反馈重新排序的Prompt
     *
     * @param feedbacks         用户反馈列表
     * @param feedbackSummary   反馈摘要
     * @param recommendationType 推荐类型
     * @param userId            用户ID
     * @param userHistory       用户历史反馈
     * @return 构建好的Prompt
     */
    public String buildFeedbackRerankPrompt(List<UserFeedback> feedbacks, String feedbackSummary, String recommendationType, Long userId, List<UserFeedback> userHistory) {
        String formattedFeedbacks = formatFeedbacks(feedbacks);
        String formattedHistory = formatFeedbacks(userHistory);

        return String.format("""
            你是一个机票推荐优化专家。请根据以下用户反馈重新优化航班推荐：

            用户ID: %s
            推荐类型: %s
            反馈摘要: %s

            用户反馈列表:
            %s

            用户历史反馈:
            %s

            请根据反馈信息优化航班推荐，考虑：
            1. 用户满意度
            2. 反馈中提到的关键问题
            3. 用户偏好变化
            4. 推荐的相关性

            请返回JSON格式的优化结果，包含以下结构：
            {
              "analysis": "对反馈的分析摘要",
              "recommendations": {
                "推荐类别1": [航班ID1, 航班ID2, ...],
                "推荐类别2": [航班ID3, 航班ID4, ...]
              }
            }

            请确保优化后的推荐更好地满足用户需求。
            """, userId, recommendationType, feedbackSummary, formattedFeedbacks, formattedHistory);
    }
}