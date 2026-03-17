package com.example.app.service;

import com.example.app.entity.UserFlightHistory;
import com.example.app.mapper.UserFlightHistoryMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 用户行为记录服务（搜索、浏览、收藏）
 */
@Service
public class UserBehaviorService {

    private final UserFlightHistoryMapper historyMapper;

    public UserBehaviorService(UserFlightHistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    @Async
    public void recordSearch(Long userId, String departure, String arrival, String flightNumber) {
        if (userId == null) return;
        UserFlightHistory record = UserFlightHistory.builder()
                .userId(userId)
                .departureAirportCode(departure)
                .arrivalAirportCode(arrival)
                .behaviorType("SEARCH")
                .flightNumber(flightNumber)
                .build();
        historyMapper.insert(record);
    }

    @Async
    public void recordView(Long userId, String departure, String arrival, String flightNumber) {
        if (userId == null) return;
        UserFlightHistory record = UserFlightHistory.builder()
                .userId(userId)
                .departureAirportCode(departure)
                .arrivalAirportCode(arrival)
                .behaviorType("VIEW")
                .flightNumber(flightNumber)
                .build();
        historyMapper.insert(record);
    }

    @Async
    public void recordFavorite(Long userId, String departure, String arrival, String flightNumber) {
        if (userId == null) return;
        UserFlightHistory record = UserFlightHistory.builder()
                .userId(userId)
                .departureAirportCode(departure)
                .arrivalAirportCode(arrival)
                .behaviorType("FAVORITE")
                .flightNumber(flightNumber)
                .build();
        historyMapper.insert(record);
    }
}
