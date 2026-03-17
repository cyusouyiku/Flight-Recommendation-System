package com.example.app.dto;

import com.example.app.entity.Flight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 航班搜索响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {

    private List<Flight> flights;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    /**
     * 比价模式：同航线多航司聚合
     */
    private List<RouteComparison> comparison;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteComparison {
        private String departureCode;
        private String arrivalCode;
        private String departureName;
        private String arrivalName;
        private List<Flight> flightsByAirline;
        private int minPrice;
        private int maxPrice;
    }
}
