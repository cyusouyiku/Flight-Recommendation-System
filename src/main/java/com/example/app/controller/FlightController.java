package com.example.app.controller;

import com.example.app.common.Result;
import com.example.app.entity.Flight;
import com.example.app.service.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<Result<List<Flight>>> list() {
        return ResponseEntity.ok(Result.success(flightService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<Flight>> getById(@PathVariable Long id) {
        Flight flight = flightService.findById(id);
        if (flight == null) {
            return ResponseEntity.ok(Result.error(404, "航班不存在"));
        }
        return ResponseEntity.ok(Result.success(flight));
    }

    @GetMapping("/search")
    public ResponseEntity<Result<List<Flight>>> search(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Flight> flights = flightService.search(departure, arrival, date);
        return ResponseEntity.ok(Result.success(flights));
    }
}
