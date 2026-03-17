package com.example.app.service;

import com.example.app.dto.FlightSearchParams;
import com.example.app.dto.FlightSearchRequest;
import com.example.app.dto.FlightSearchResponse;
import com.example.app.dto.PageResult;
import com.example.app.entity.Flight;
import com.example.app.mapper.FlightMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightMapper flightMapper;

    public FlightService(FlightMapper flightMapper) {
        this.flightMapper = flightMapper;
    }

    public List<Flight> findAll() {
        return flightMapper.selectAll();
    }

    public Flight findById(Long id) {
        return flightMapper.selectById(id);
    }

    public List<Flight> search(String departureAirportCode, String arrivalAirportCode, LocalDate effectiveDate) {
        return flightMapper.selectBySearch(departureAirportCode, arrivalAirportCode, effectiveDate);
    }

    /**
     * 高级搜索：分页、排序、过滤、比价（带缓存）
     */
    @Cacheable(value = "flightSearch", key = "#req.departure + '-' + #req.arrival + '-' + #req.departureDate + '-' + #req.page + '-' + #req.size + '-' + #req.sortBy", unless = "#result == null || #result.totalElements == 0")
    public FlightSearchResponse searchAdvanced(FlightSearchRequest req) {
        FlightSearchParams params = FlightSearchParams.builder()
                .departureAirportCode(req.getDeparture().toUpperCase())
                .arrivalAirportCode(req.getArrival().toUpperCase())
                .effectiveDate(req.getDepartureDate())
                .cabinClass(req.getCabinClass())
                .airlineCode(req.getAirlineCode())
                .directOnly(req.getDirectOnly())
                .maxStops(req.getMaxStops())
                .sortBy(req.getSortBy())
                .build();

        PageHelper.startPage(req.getPage() + 1, req.getSize());
        List<Flight> flights = flightMapper.selectBySearchAdvanced(params);
        PageInfo<Flight> pageInfo = new PageInfo<>(flights);

        // 按航司聚合比价（同航线多航司）
        List<FlightSearchResponse.RouteComparison> comparison = buildRouteComparison(flights);

        return FlightSearchResponse.builder()
                .flights(flights)
                .totalElements((int) pageInfo.getTotal())
                .totalPages(pageInfo.getPages())
                .currentPage(req.getPage())
                .pageSize(req.getSize())
                .comparison(comparison)
                .build();
    }

    private List<FlightSearchResponse.RouteComparison> buildRouteComparison(List<Flight> flights) {
        if (flights == null || flights.isEmpty()) return List.of();

        Map<String, List<Flight>> byRoute = flights.stream()
                .collect(Collectors.groupingBy(f -> f.getDepartureAirportCode() + "-" + f.getArrivalAirportCode()));

        return byRoute.entrySet().stream()
                .map(e -> {
                    List<Flight> list = e.getValue();
                    list.sort(Comparator.comparingInt(Flight::getPrice));
                    int minPrice = list.stream().mapToInt(Flight::getPrice).min().orElse(0);
                    int maxPrice = list.stream().mapToInt(Flight::getPrice).max().orElse(0);
                    Flight first = list.get(0);
                    return FlightSearchResponse.RouteComparison.builder()
                            .departureCode(first.getDepartureAirportCode())
                            .arrivalCode(first.getArrivalAirportCode())
                            .departureName(first.getDepartureAirport())
                            .arrivalName(first.getArrivalAirport())
                            .flightsByAirline(list)
                            .minPrice(minPrice)
                            .maxPrice(maxPrice)
                            .build();
                })
                .toList();
    }

    public Flight save(Flight flight) {
        if (flight.getId() == null) {
            flightMapper.insert(flight);
        } else {
            flightMapper.update(flight);
        }
        return flight;
    }
}
