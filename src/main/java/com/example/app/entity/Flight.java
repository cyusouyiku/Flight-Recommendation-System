package com.example.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {
    private Long id;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int durationMinutes;
    private LocalDate effectiveDate;

    private String flightNumber;
    private String airlineCode;
    private String cabinClass;
    private String aircraftType;

    private int stops;
    private boolean isDirect;

    private String departureAirport;
    private String arrivalAirport;
    private String departureAirportCode;
    private String arrivalAirportCode;

    private int price;
    private Integer originalPrice;
    private Integer availableSeats;

    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static class InvalidTimeException extends Exception {
        public InvalidTimeException(String message) {
            super(message);
        }
    }

    /**
     * 根据起飞/到达时间计算飞行时长（分钟）
     */
    public static int calculateDurationMinutes(LocalDateTime departureTime, LocalDateTime arrivalTime) throws InvalidTimeException {
        if (departureTime == null || arrivalTime == null) {
            throw new InvalidTimeException("起飞/到达时间不能为空");
        }
        if (arrivalTime.isBefore(departureTime)) {
            throw new InvalidTimeException("到达时间不能早于起飞时间");
        }
        return (int) Duration.between(departureTime, arrivalTime).toMinutes();
    }
}
