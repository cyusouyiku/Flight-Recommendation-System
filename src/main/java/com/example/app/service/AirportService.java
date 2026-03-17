package com.example.app.service;

import com.example.app.entity.Airport;
import com.example.app.mapper.AirportMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    private final AirportMapper airportMapper;

    public AirportService(AirportMapper airportMapper) {
        this.airportMapper = airportMapper;
    }

    public List<Airport> findAll() {
        return airportMapper.selectAll();
    }

    public Airport findById(Long id) {
        return airportMapper.selectById(id);
    }

    public Airport findByCode(String code) {
        return airportMapper.selectByCode(code);
    }

    public Airport save(Airport airport) {
        if (airport.getId() == null) {
            airportMapper.insert(airport);
        } else {
            airportMapper.update(airport);
        }
        return airport;
    }
}
