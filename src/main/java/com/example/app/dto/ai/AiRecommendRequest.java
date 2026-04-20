package com.example.app.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * AI推荐请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecommendRequest {

    /**
     * 出发机场代码（可选）
     */
    private String departureAirportCode;

    /**
     * 到达机场代码（可选）
     */
    private String arrivalAirportCode;

    /**
     * 出发日期（可选）
     */
    private LocalDate departureDate;

    /**
     * 偏好类型：PRICE_FIRST（价格优先）、TIME_FIRST（时间优先）、COMFORT_FIRST（舒适优先）、COMPREHENSIVE（综合）
     */
    private String preferenceType;

    /**
     * 价格范围上限（可选）
     */
    private Integer maxPrice;

    /**
     * 航班时间偏好：MORNING（早上）、AFTERNOON（下午）、EVENING（晚上）、ANY（任意）
     */
    private String timePreference;

    /**
     * 航空公司偏好（多个用逗号分隔）
     */
    private String airlinePreference;

    /**
     * 舱位偏好
     */
    private String cabinClass;

    /**
     * 是否直飞
     */
    private Boolean directOnly;

    /**
     * 最大转机次数
     */
    private Integer maxStops;

    /**
     * 返回结果数量
     */
    private Integer maxResults;

    /**
     * 是否考虑用户历史
     */
    private Boolean considerHistory;

    /**
     * 是否考虑用户反馈
     */
    private Boolean considerFeedback;

    /**
     * 额外参数（JSON格式）
     */
    private String additionalParams;
}