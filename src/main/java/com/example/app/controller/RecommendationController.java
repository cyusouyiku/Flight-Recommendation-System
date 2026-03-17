package com.example.app.controller;

import com.example.app.common.Result;
import com.example.app.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "推荐", description = "个性化航班推荐")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    @Operation(summary = "获取个性化推荐", description = "返回热门、常搜航线、你可能喜欢等多维度推荐")
    public ResponseEntity<Result<Map<String, List<?>>>> getRecommendations() {
        Map<String, ?> raw = recommendationService.getRecommendations();
        Map<String, List<?>> result = raw.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (List<?>) e.getValue()));
        return ResponseEntity.ok(Result.success(result));
    }
}
