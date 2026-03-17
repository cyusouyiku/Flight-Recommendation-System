package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 航班搜索请求 DTO
 */
@Data
public class FlightSearchRequest {

    @NotBlank(message = "出发地代码不能为空")
    private String departure;

    @NotBlank(message = "目的地代码不能为空")
    private String arrival;

    @NotNull(message = "出发日期不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate departureDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate returnDate;  // 返程日期（可选）

    private String cabinClass;     // 舱位过滤：Y/E/C/F
    private String airlineCode;   // 航司过滤
    private Boolean directOnly;   // 仅直飞
    private Integer maxStops;     // 最大经停数

    // 分页
    private Integer page = 0;
    private Integer size = 20;

    // 排序：PRICE_ASC, DURATION_ASC, DEPARTURE_ASC
    private String sortBy = "PRICE_ASC";
}
