package com.example.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriceAlertRequest {

    @NotBlank(message = "出发地不能为空")
    private String departure;

    @NotBlank(message = "目的地不能为空")
    private String arrival;

    @NotNull
    @Min(1)
    @Max(99999)
    private Integer targetPrice;

    private String cabinClass = "Y";
}
