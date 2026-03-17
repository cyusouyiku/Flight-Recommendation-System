package com.example.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    private Long id;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String departureAirportName;
    private String arrivalAirportName;
}
