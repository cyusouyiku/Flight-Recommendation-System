package com.example.app.controller;

import com.example.app.common.Result;
import com.example.app.dto.PriceAlertRequest;
import com.example.app.entity.FlightPrice;
import com.example.app.entity.PriceAlert;
import com.example.app.security.AuthUser;
import com.example.app.service.PriceAlertService;
import com.example.app.service.PriceTrendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
@Tag(name = "价格", description = "价格趋势与低价提醒")
public class PriceController {

    private final PriceTrendService priceTrendService;
    private final PriceAlertService priceAlertService;

    public PriceController(PriceTrendService priceTrendService, PriceAlertService priceAlertService) {
        this.priceTrendService = priceTrendService;
        this.priceAlertService = priceAlertService;
    }

    @GetMapping("/trend")
    @Operation(summary = "查询价格趋势", description = "获取某航线近期价格走势")
    public ResponseEntity<Result<List<FlightPrice>>> getPriceTrend(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam(defaultValue = "7") int days) {
        List<FlightPrice> trend = priceTrendService.getPriceTrend(departure, arrival, Math.min(days, 30));
        return ResponseEntity.ok(Result.success(trend));
    }

    @PostMapping("/alert")
    @Operation(summary = "创建低价提醒", description = "设置目标航线和期望价格")
    public ResponseEntity<Result<PriceAlert>> createAlert(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody PriceAlertRequest req) {
        if (user == null) {
            return ResponseEntity.ok(Result.error(401, "请先登录"));
        }
        PriceAlert alert = priceAlertService.createAlert(
                user.getUserId(), req.getDeparture(), req.getArrival(),
                req.getTargetPrice(), req.getCabinClass());
        return ResponseEntity.ok(Result.success(alert));
    }

    @GetMapping("/alert")
    @Operation(summary = "我的低价提醒列表")
    public ResponseEntity<Result<List<PriceAlert>>> myAlerts(@AuthenticationPrincipal AuthUser user) {
        if (user == null) {
            return ResponseEntity.ok(Result.error(401, "请先登录"));
        }
        List<PriceAlert> alerts = priceAlertService.getMyAlerts(user.getUserId());
        return ResponseEntity.ok(Result.success(alerts));
    }
}
