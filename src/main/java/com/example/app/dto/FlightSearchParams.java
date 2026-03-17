package com.example.app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 航班搜索参数（内部 Mapper 使用）
 */
@Data
@Builder
public class FlightSearchParams {

    private String departureAirportCode;
    private String arrivalAirportCode;
    private LocalDate effectiveDate;
    private LocalDate returnDate;
    private String cabinClass;
    private String airlineCode;
    private Boolean directOnly;
    private Integer maxStops;
    private String sortBy;  // PRICE_ASC, DURATION_ASC, DEPARTURE_ASC, COMPOSITE
}
