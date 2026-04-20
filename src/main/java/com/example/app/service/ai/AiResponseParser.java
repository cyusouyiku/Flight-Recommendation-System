package com.example.app.service.ai;

import com.example.app.dto.ai.AiAnalysisResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * AI响应解析器，用于解析OpenAI返回的JSON响应
 */
@Component
public class AiResponseParser {

    private static final Logger log = LoggerFactory.getLogger(AiResponseParser.class);
    private final ObjectMapper objectMapper;

    public AiResponseParser() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 解析航班重新排序的响应
     *
     * @param aiResponse AI响应文本
     * @return 排序映射（航班ID -> 排序分数）
     */
    public Map<Long, Double> parseFlightRerankResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode sortedFlights = root.path("sorted_flights");

            if (!sortedFlights.isArray()) {
                log.warn("AI响应中sorted_flights不是数组格式: {}", aiResponse);
                return Collections.emptyMap();
            }

            Map<Long, Double> result = new HashMap<>();
            for (JsonNode flightNode : sortedFlights) {
                Long flightId = flightNode.path("flight_id").asLong();
                Double score = flightNode.path("score").asDouble();

                if (flightId > 0 && score >= 0 && score <= 1) {
                    result.put(flightId, score);
                } else {
                    log.debug("跳过无效的排序条目: flightId={}, score={}", flightId, score);
                }
            }

            log.info("成功解析{}个航班的排序结果", result.size());
            return result;

        } catch (JsonProcessingException e) {
            log.error("解析AI重新排序响应失败，尝试手动解析", e);

            // 尝试简单的手动解析
            try {
                return parseFlightRerankResponseManually(aiResponse);
            } catch (Exception ex) {
                log.error("手动解析也失败", ex);
                return Collections.emptyMap();
            }
        }
    }

    /**
     * 手动解析航班重新排序响应（容错处理）
     */
    private Map<Long, Double> parseFlightRerankResponseManually(String aiResponse) {
        Map<Long, Double> result = new HashMap<>();

        // 尝试提取JSON部分
        int start = aiResponse.indexOf('{');
        int end = aiResponse.lastIndexOf('}');

        if (start >= 0 && end > start) {
            String jsonStr = aiResponse.substring(start, end + 1);
            try {
                JsonNode root = objectMapper.readTree(jsonStr);
                JsonNode sortedFlights = root.path("sorted_flights");

                if (sortedFlights.isArray()) {
                    for (JsonNode flightNode : sortedFlights) {
                        JsonNode idNode = flightNode.path("flight_id");
                        JsonNode scoreNode = flightNode.path("score");

                        if (idNode.isNumber() && scoreNode.isNumber()) {
                            Long flightId = idNode.asLong();
                            Double score = scoreNode.asDouble();

                            if (flightId > 0 && score >= 0 && score <= 1) {
                                result.put(flightId, score);
                            }
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("手动提取JSON失败", e);
            }
        }

        return result;
    }

    /**
     * 解析用户反馈分析的响应
     *
     * @param aiResponse AI响应文本
     * @return AI分析结果
     */
    public AiAnalysisResult parseFeedbackAnalysisResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return createDefaultAnalysisResult();
        }

        try {
            JsonNode root = objectMapper.readTree(aiResponse);

            return AiAnalysisResult.builder()
                    .analysisId(UUID.randomUUID().toString())
                    .analysisType("FEEDBACK_ANALYSIS")
                    .summary(root.path("sentiment").asText("未知情感"))
                    .detailedResult(aiResponse)
                    .confidenceScore(root.path("confidence").asDouble(0.5))
                    .recommendedAction(root.path("improvement_suggestions").asText("暂无建议"))
                    .optimizationSuggestion(root.path("improvement_suggestions").asText("暂无优化建议"))
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("解析用户反馈分析响应失败", e);
            return createDefaultAnalysisResult();
        }
    }

    /**
     * 解析推荐效果分析的响应
     *
     * @param aiResponse AI响应文本
     * @return 分析结果映射
     */
    public Map<String, Object> parsePerformanceAnalysisResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            Map<String, Object> result = new HashMap<>();

            // 提取关键字段
            result.put("overall_assessment", root.path("overall_assessment").asText("未知"));
            result.put("confidence", root.path("confidence").asDouble(0.5));

            // 提取数组字段
            result.put("strengths", parseStringArray(root.path("strengths")));
            result.put("weaknesses", parseStringArray(root.path("weaknesses")));
            result.put("opportunities", parseStringArray(root.path("opportunities")));
            result.put("threats", parseStringArray(root.path("threats")));
            result.put("recommendations", parseStringArray(root.path("recommendations")));

            return result;

        } catch (JsonProcessingException e) {
            log.error("解析推荐效果分析响应失败", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 解析个性化推荐的响应
     *
     * @param aiResponse AI响应文本
     * @return 推荐结果映射
     */
    public Map<String, Object> parsePersonalizedRecommendationResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            Map<String, Object> result = new HashMap<>();

            result.put("summary", root.path("summary").asText("无总结"));
            result.put("user_advice", root.path("user_advice").asText("无建议"));

            // 解析推荐列表
            List<Map<String, Object>> recommendations = new ArrayList<>();
            JsonNode recommendationsNode = root.path("recommendations");

            if (recommendationsNode.isArray()) {
                for (JsonNode recNode : recommendationsNode) {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("flight_id", recNode.path("flight_id").asLong());
                    rec.put("reason", recNode.path("reason").asText("无理由"));
                    rec.put("match_score", recNode.path("match_score").asDouble(0.5));
                    rec.put("highlight", recNode.path("highlight").asText("无亮点"));
                    recommendations.add(rec);
                }
            }

            result.put("recommendations", recommendations);
            return result;

        } catch (JsonProcessingException e) {
            log.error("解析个性化推荐响应失败", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 验证AI响应是否有效
     *
     * @param aiResponse AI响应文本
     * @return 是否有效
     */
    public boolean isValidResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return false;
        }

        try {
            objectMapper.readTree(aiResponse);
            return true;
        } catch (JsonProcessingException e) {
            log.debug("AI响应不是有效JSON: {}", e.getMessage());

            // 检查是否包含关键字段
            return aiResponse.contains("{") && aiResponse.contains("}") &&
                   (aiResponse.contains("flight_id") || aiResponse.contains("sentiment") ||
                    aiResponse.contains("summary") || aiResponse.contains("confidence"));
        }
    }

    /**
     * 提取AI响应中的JSON部分
     *
     * @param aiResponse AI响应文本
     * @return 提取的JSON字符串
     */
    public String extractJsonFromResponse(String aiResponse) {
        if (aiResponse == null) {
            return "{}";
        }

        // 查找第一个{和最后一个}
        int start = aiResponse.indexOf('{');
        int end = aiResponse.lastIndexOf('}');

        if (start >= 0 && end > start) {
            return aiResponse.substring(start, end + 1);
        }

        // 如果没有找到完整JSON，尝试修复常见问题
        String cleaned = aiResponse.trim();
        if (!cleaned.startsWith("{")) {
            cleaned = "{" + cleaned;
        }
        if (!cleaned.endsWith("}")) {
            cleaned = cleaned + "}";
        }

        return cleaned;
    }

    /**
     * 解析字符串数组
     */
    private List<String> parseStringArray(JsonNode node) {
        List<String> result = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.isTextual()) {
                    result.add(item.asText());
                }
            }
        }

        return result;
    }

    /**
     * 创建默认的分析结果
     */
    private AiAnalysisResult createDefaultAnalysisResult() {
        return AiAnalysisResult.builder()
                .analysisId(UUID.randomUUID().toString())
                .analysisType("FEEDBACK_ANALYSIS")
                .summary("AI分析失败，使用默认结果")
                .detailedResult("{}")
                .confidenceScore(0.3)
                .recommendedAction("请检查AI服务配置")
                .optimizationSuggestion("优化AI提示词或检查API连接")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 测试JSON解析
     *
     * @param json JSON字符串
     * @return 是否有效JSON
     */
    public boolean testJsonParsing(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 解析基于反馈重新排序的响应
     *
     * @param aiResponse AI响应文本
     * @return 解析后的推荐映射
     */
    public Map<String, Object> parseRerankResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            Map<String, Object> result = new HashMap<>();

            // 提取分析摘要
            result.put("analysis", root.path("analysis").asText("无分析"));

            // 提取推荐映射
            Map<String, List<Long>> recommendations = new HashMap<>();
            JsonNode recommendationsNode = root.path("recommendations");

            if (recommendationsNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = recommendationsNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    List<Long> flightIds = new ArrayList<>();
                    if (entry.getValue().isArray()) {
                        for (JsonNode idNode : entry.getValue()) {
                            if (idNode.isNumber()) {
                                flightIds.add(idNode.asLong());
                            }
                        }
                    }
                    recommendations.put(entry.getKey(), flightIds);
                }
            }

            result.put("recommendations", recommendations);
            return result;

        } catch (JsonProcessingException e) {
            log.error("解析重新排序响应失败", e);
            return Collections.emptyMap();
        }
    }
}