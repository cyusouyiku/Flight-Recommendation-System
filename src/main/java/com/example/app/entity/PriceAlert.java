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
public class PriceAlert {
    private Long id;
    private Long userId;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private int targetPrice;
    private String cabinClass;
    private boolean notified;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
