package com.example.app.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI分析结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAnalysisResult {

    /**
     * 分析ID
     */
    private String analysisId;

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 分析类型：FEEDBACK_ANALYSIS, RECOMMENDATION_OPTIMIZATION, PERFORMANCE_ANALYSIS
     */
    private String analysisType;

    /**
     * 分析结果摘要
     */
    private String summary;

    /**
     * 详细分析结果（JSON格式）
     */
    private String detailedResult;

    /**
     * 置信度分数（0-1）
     */
    private Double confidenceScore;

    /**
     * 建议操作
     */
    private String recommendedAction;

    /**
     * 优化建议
     */
    private String optimizationSuggestion;

    /**
     * 元数据
     */
    private String metadata;

    /**
     * 创建时间戳
     */
    private Long timestamp;
}