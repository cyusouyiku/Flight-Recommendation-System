package com.example.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFlightHistory {
    private Long id;
    private Long userId;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String behaviorType;  // SEARCH, VIEW, FAVORITE
    private String flightNumber;
    private Timestamp createdAt;
}
