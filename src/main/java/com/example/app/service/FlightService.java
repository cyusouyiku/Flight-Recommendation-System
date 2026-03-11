package com.example.app.service;

import com.example.app.entity.Flight;
import com.example.app.mapper.FlightMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public Flight save(Flight flight) {
        if (flight.getId() == null) {
            flightMapper.insert(flight);
        } else {
            flightMapper.update(flight);
        }
        return flight;
    }
}
