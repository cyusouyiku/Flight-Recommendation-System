package com.example.app.service;

import com.example.app.entity.Flight;
import com.example.app.entity.FlightPrice;
import com.example.app.mapper.FlightMapper;
import com.example.app.mapper.FlightPriceMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 价格趋势服务
 */
@Service
public class PriceTrendService {

    private final FlightMapper flightMapper;
    private final FlightPriceMapper flightPriceMapper;

    public PriceTrendService(FlightMapper flightMapper, FlightPriceMapper flightPriceMapper) {
        this.flightMapper = flightMapper;
        this.flightPriceMapper = flightPriceMapper;
    }

    /**
     * 查询某航线近期价格走势
     */
    public List<FlightPrice> getPriceTrend(String departure, String arrival, int days) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusDays(days);
        return flightPriceMapper.selectByRouteAndDateRange(
                departure.toUpperCase(), arrival.toUpperCase(), from, to);
    }

    /**
     * 从航班表采集价格到历史表（定时任务调用）
     */
    public void collectPrices() {
        List<Flight> flights = flightMapper.selectAll();
        if (flights == null) return;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (Flight f : flights) {
            if (f.getFlightNumber() == null || f.getDepartureAirportCode() == null || f.getArrivalAirportCode() == null) continue;
            FlightPrice fp = new FlightPrice();
            fp.setFlightNumber(f.getFlightNumber());
            fp.setDepartureAirportCode(f.getDepartureAirportCode());
            fp.setArrivalAirportCode(f.getArrivalAirportCode());
            fp.setCabinClass(f.getCabinClass() != null ? f.getCabinClass() : "Y");
            fp.setPrice(f.getPrice());
            fp.setRecordAt(now);
            flightPriceMapper.insert(fp);
        }
    }
}
