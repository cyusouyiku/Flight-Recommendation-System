package com.example.app.service;

import com.example.app.dto.FlightSearchParams;
import com.example.app.entity.Flight;
import com.example.app.entity.PriceAlert;
import com.example.app.entity.User;
import com.example.app.mapper.FlightMapper;
import com.example.app.mapper.PriceAlertMapper;
import com.example.app.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 低价提醒服务
 */
@Service
public class PriceAlertService {

    private final PriceAlertMapper priceAlertMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final FlightMapper flightMapper;

    public PriceAlertService(PriceAlertMapper priceAlertMapper, UserMapper userMapper,
                             NotificationService notificationService, FlightMapper flightMapper) {
        this.priceAlertMapper = priceAlertMapper;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
        this.flightMapper = flightMapper;
    }

    public PriceAlert createAlert(Long userId, String departure, String arrival, Integer targetPrice, String cabinClass) {
        PriceAlert alert = PriceAlert.builder()
                .userId(userId)
                .departureAirportCode(departure.toUpperCase())
                .arrivalAirportCode(arrival.toUpperCase())
                .targetPrice(targetPrice)
                .cabinClass(cabinClass != null ? cabinClass : "Y")
                .notified(false)
                .build();
        priceAlertMapper.insert(alert);
        return alert;
    }

    public List<PriceAlert> getMyAlerts(Long userId) {
        return priceAlertMapper.selectByUserId(userId);
    }

    /**
     * 检查并发送达标提醒（定时任务调用）
     */
    public void checkAndNotify() {
        List<PriceAlert> alerts = priceAlertMapper.selectActiveAlerts();
        LocalDate from = LocalDate.now();
        for (PriceAlert alert : alerts) {
            var params = FlightSearchParams.builder()
                    .departureAirportCode(alert.getDepartureAirportCode())
                    .arrivalAirportCode(alert.getArrivalAirportCode())
                    .effectiveDate(from)
                    .cabinClass(alert.getCabinClass())
                    .build();
            List<Flight> flights = flightMapper.selectBySearchAdvanced(params);
            int currentMin = flights.stream().mapToInt(Flight::getPrice).min().orElse(Integer.MAX_VALUE);
            if (currentMin <= alert.getTargetPrice()) {
                User user = userMapper.selectById(alert.getUserId());
                if (user != null && user.getEmail() != null) {
                    notificationService.sendPriceAlertEmail(user.getEmail(),
                            alert.getDepartureAirportCode(), alert.getArrivalAirportCode(),
                            alert.getTargetPrice(), currentMin);
                    priceAlertMapper.updateNotified(alert.getId());
                }
            }
        }
    }
}
