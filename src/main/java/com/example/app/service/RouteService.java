package com.example.app.service;

import com.example.app.entity.Flight;
import com.example.app.mapper.FlightMapper;
import com.example.app.mapper.UserFlightHistoryMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 航线服务：热门航线等
 */
@Service
public class RouteService {

    private final UserFlightHistoryMapper historyMapper;
    private final FlightMapper flightMapper;

    public RouteService(UserFlightHistoryMapper historyMapper, FlightMapper flightMapper) {
        this.historyMapper = historyMapper;
        this.flightMapper = flightMapper;
    }

    /**
     * 热门航线排行（基于搜索量，无数据时用航班量）
     */
    @Cacheable(value = "popularRoutes", key = "'list'", unless = "#result == null || #result.isEmpty()")
    public List<PopularRouteItem> getPopularRoutes(int limit) {
        List<String> routeKeys = historyMapper.selectPopularRoutesGlobal(limit);
        if (routeKeys.isEmpty()) {
            return getDefaultPopularRoutes(limit);
        }
        List<PopularRouteItem> result = new ArrayList<>();
        LocalDate from = LocalDate.now().plusDays(1);
        for (String key : routeKeys) {
            String[] parts = key.split("-", 2);
            if (parts.length != 2) continue;
            var params = com.example.app.dto.FlightSearchParams.builder()
                    .departureAirportCode(parts[0])
                    .arrivalAirportCode(parts[1])
                    .effectiveDate(from)
                    .sortBy("PRICE_ASC")
                    .build();
            List<Flight> flights = flightMapper.selectBySearchAdvanced(params);
            int minPrice = flights.stream().mapToInt(Flight::getPrice).min().orElse(0);
            result.add(new PopularRouteItem(parts[0], parts[1], minPrice, flights.size()));
        }
        return result;
    }

    private List<PopularRouteItem> getDefaultPopularRoutes(int limit) {
        List<Flight> all = flightMapper.selectAll();
        LocalDate from = LocalDate.now().plusDays(1);
        var byRoute = all.stream()
                .filter(f -> f.getEffectiveDate() != null && !f.getEffectiveDate().isBefore(from))
                .collect(Collectors.groupingBy(f -> f.getDepartureAirportCode() + "-" + f.getArrivalAirportCode()));
        return byRoute.entrySet().stream()
                .map(e -> {
                    String[] p = e.getKey().split("-", 2);
                    int minP = e.getValue().stream().mapToInt(Flight::getPrice).min().orElse(0);
                    return new PopularRouteItem(p[0], p[1], minP, e.getValue().size());
                })
                .sorted((a, b) -> Integer.compare(b.flightCount(), a.flightCount()))
                .limit(limit)
                .toList();
    }

    public record PopularRouteItem(String departureCode, String arrivalCode, int minPrice, int flightCount) {}
}
