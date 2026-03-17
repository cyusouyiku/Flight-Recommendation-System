package com.example.app.controller;

import com.example.app.common.Result;
import com.example.app.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "航线", description = "航线相关接口")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/popular")
    @Operation(summary = "热门航线排行", description = "基于搜索量或航班量的热门航线")
    public ResponseEntity<Result<List<RouteService.PopularRouteItem>>> getPopular(
            @RequestParam(defaultValue = "10") int limit) {
        List<RouteService.PopularRouteItem> items = routeService.getPopularRoutes(Math.min(limit, 50));
        return ResponseEntity.ok(Result.success(items));
    }
}
