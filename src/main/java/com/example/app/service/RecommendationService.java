package com.example.app.service;

import com.example.app.dto.FlightSearchParams;
import com.example.app.entity.Flight;
import com.example.app.mapper.FlightMapper;
import com.example.app.mapper.UserFlightHistoryMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 个性化推荐服务：热门、你可能喜欢、常搜航线
 */
@Service
public class RecommendationService {

    private final FlightMapper flightMapper;
    private final UserFlightHistoryMapper historyMapper;

    public RecommendationService(FlightMapper flightMapper, UserFlightHistoryMapper historyMapper) {
        this.flightMapper = flightMapper;
        this.historyMapper = historyMapper;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        if (auth.getPrincipal() instanceof com.example.app.security.AuthUser authUser) {
            return authUser.getUserId();
        }
        return null;
    }

    /**
     * 综合推荐：合并多种策略
     */
    public Map<String, List<Flight>> getRecommendations() {
        Map<String, List<Flight>> result = new LinkedHashMap<>();
        Long userId = getCurrentUserId();

        // 1. 热门航线
        List<Flight> popular = getPopularFlights();
        if (!popular.isEmpty()) {
            result.put("热门推荐", popular);
        }

        // 2. 常搜航线（登录用户）
        if (userId != null) {
            List<Flight> frequent = getFrequentRouteFlights(userId);
            if (!frequent.isEmpty()) {
                result.put("常搜航线", frequent);
            }
        }

        // 3. 你可能喜欢（基于常搜的相似航线）
        if (userId != null) {
            List<Flight> mayLike = getMayLikeFlights(userId);
            if (!mayLike.isEmpty()) {
                result.put("你可能喜欢", mayLike);
            }
        }

        // 未登录时补充随机推荐
        if (result.isEmpty()) {
            result.put("为您推荐", getRandomFlights());
        }

        return result;
    }

    private List<Flight> getPopularFlights() {
        List<String> popularRoutes = historyMapper.selectPopularRoutesGlobal(5);
        if (popularRoutes.isEmpty()) {
            return getDefaultHotFlights();
        }
        return getFlightsForRoutes(popularRoutes);
    }

    private List<Flight> getFrequentRouteFlights(Long userId) {
        List<String> routes = historyMapper.selectFrequentRoutesByUserId(userId, 3);
        return getFlightsForRoutes(routes);
    }

    private List<Flight> getMayLikeFlights(Long userId) {
        List<String> frequentRoutes = historyMapper.selectFrequentRoutesByUserId(userId, 2);
        if (frequentRoutes.isEmpty()) return Collections.emptyList();
        // 简化：从同出发地或同目的地的其他航线推荐
        Set<String> seen = new HashSet<>(frequentRoutes);
        List<Flight> all = flightMapper.selectAll();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return all.stream()
                .filter(f -> f.getEffectiveDate() != null && !f.getEffectiveDate().isBefore(tomorrow))
                .filter(f -> {
                    String route = f.getDepartureAirportCode() + "-" + f.getArrivalAirportCode();
                    return !seen.contains(route);
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<Flight> getFlightsForRoutes(List<String> routes) {
        if (routes.isEmpty()) return Collections.emptyList();
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = from.plusDays(14);
        List<Flight> result = new ArrayList<>();
        for (String route : routes) {
            String[] parts = route.split("-", 2);
            if (parts.length != 2) continue;
            List<Flight> flights = flightMapper.selectBySearchAdvanced(
                    FlightSearchParams.builder()
                            .departureAirportCode(parts[0])
                            .arrivalAirportCode(parts[1])
                            .effectiveDate(from)
                            .sortBy("PRICE_ASC")
                            .build());
            result.addAll(flights.stream().limit(3).toList());
        }
        return result.stream().distinct().limit(15).toList();
    }

    private List<Flight> getDefaultHotFlights() {
        // 默认热门：北京-上海、上海-广州等
        List<String> defaultRoutes = List.of("PEK-PVG", "PVG-CAN", "CAN-PVG");
        return getFlightsForRoutes(defaultRoutes);
    }

    private List<Flight> getRandomFlights() {
        List<Flight> all = flightMapper.selectAll();
        LocalDate from = LocalDate.now().plusDays(1);
        return all.stream()
                .filter(f -> f.getEffectiveDate() != null && !f.getEffectiveDate().isBefore(from))
                .limit(10)
                .toList();
    }
}
