package com.example.app.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * AI反馈重新排序请求DTO
 * 用于处理用户对推荐结果不满意时的反馈和重新优化
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedbackRerankRequest {

    /**
     * 当前推荐结果的反馈列表
     */
    @NotEmpty(message = "反馈列表不能为空")
    @Valid
    private List<FlightFeedbackItem> currentRecommendations;

    /**
     * 反馈总结（可选）
     */
    private String feedbackSummary;

    /**
     * 重新优化的方向：PRICE_FIRST（价格优先）、TIME_FIRST（时间优先）、COMFORT_FIRST（舒适优先）、COMPREHENSIVE（综合）
     */
    private String recommendationType;

    /**
     * 额外参数（JSON格式）
     */
    private String additionalParams;

    /**
     * 单个航班反馈项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FlightFeedbackItem {

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
         * 具体原因（可选）
         */
        private String reason;

        /**
         * 额外元数据（JSON格式）
         */
        private String metadata;
    }
}