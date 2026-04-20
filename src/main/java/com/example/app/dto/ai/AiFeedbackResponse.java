package com.example.app.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * AI用户反馈响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedbackResponse {

    /**
     * 反馈ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 航班ID
     */
    private Long flightId;

    /**
     * 反馈类型
     */
    private String feedbackType;

    /**
     * 反馈文本
     */
    private String feedbackText;

    /**
     * AI分析结果
     */
    private String aiAnalysis;

    /**
     * AI置信度分数
     */
    private Double confidenceScore;

    /**
     * 元数据
     */
    private String metadata;

    /**
     * 创建时间
     */
    private Timestamp createdAt;

    /**
     * 更新时间
     */
    private Timestamp updatedAt;
}