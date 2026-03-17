package com.example.app.controller;

import com.example.app.common.Result;
import com.example.app.dto.FlightSearchRequest;
import com.example.app.dto.FlightSearchResponse;
import com.example.app.entity.Flight;
import com.example.app.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@Tag(name = "航班", description = "航班查询与搜索接口")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    @Operation(summary = "获取航班列表")
    public ResponseEntity<Result<List<Flight>>> list() {
        return ResponseEntity.ok(Result.success(flightService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取航班详情")
    public ResponseEntity<Result<Flight>> getById(@PathVariable Long id) {
        Flight flight = flightService.findById(id);
        if (flight == null) {
            return ResponseEntity.ok(Result.error(404, "航班不存在"));
        }
        return ResponseEntity.ok(Result.success(flight));
    }

    @GetMapping("/search")
    @Operation(summary = "航班搜索（高级）", description = "支持出发地、目的地、日期、分页、排序、过滤")
    public ResponseEntity<Result<FlightSearchResponse>> search(@Valid FlightSearchRequest req) {
        FlightSearchResponse response = flightService.searchAdvanced(req);
        return ResponseEntity.ok(Result.success(response));
    }
}
