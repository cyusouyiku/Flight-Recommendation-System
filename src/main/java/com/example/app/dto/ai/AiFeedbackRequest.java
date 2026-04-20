package com.example.app.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * AI用户反馈请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedbackRequest {

    /**
     * 航班ID
     */
    @NotNull(message = "航班ID不能为空")
    private Long flightId;

    /**
     * 反馈类型：LIKE（喜欢）、DISLIKE（不喜欢）、NEUTRAL（中立）
     */
    @NotNull(message = "反馈类型不能为空")
    private String feedbackType;

    /**
     * 反馈文本（可选）
     */
    private String feedbackText;

    /**
     * 额外元数据（JSON格式）
     */
    private String metadata;
}