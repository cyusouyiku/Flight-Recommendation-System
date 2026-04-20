package com.example.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 用户反馈实体，记录用户对航班推荐的反馈
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFeedback {

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
     * 反馈类型：LIKE（喜欢）、DISLIKE（不喜欢）、NEUTRAL（中立）
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
     * AI置信度分数（0-1）
     */
    private Double confidenceScore;

    /**
     * 额外元数据（JSON格式）
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