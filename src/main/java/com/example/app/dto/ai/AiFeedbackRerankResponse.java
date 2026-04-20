package com.example.app.dto.ai;

import com.example.app.entity.Flight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * AI反馈重新排序响应DTO
 * 返回用户反馈后的重新优化结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedbackRerankResponse {

    /**
     * 原始反馈ID（如果有保存到数据库）
     */
    private Long feedbackId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * AI分析结果
     */
    private String aiAnalysis;

    /**
     * AI置信度分数（0-1）
     */
    private Double confidenceScore;

    /**
     * 优化原因说明
     */
    private String optimizationReason;

    /**
     * 重新优化后的推荐结果
     * 格式：{"推荐类别": [航班列表], ...}
     */
    private Map<String, List<Flight>> rerankedRecommendations;

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTimeMs;

    /**
     * 处理时间戳
     */
    private Timestamp processedAt;

    /**
     * 额外信息（JSON格式）
     */
    private String additionalInfo;
}