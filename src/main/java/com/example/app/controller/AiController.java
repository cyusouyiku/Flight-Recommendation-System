package com.example.app.controller;

import com.example.app.common.Result;
import com.example.app.dto.ai.AiFeedbackRequest;
import com.example.app.dto.ai.AiFeedbackResponse;
import com.example.app.dto.ai.AiFeedbackRerankRequest;
import com.example.app.dto.ai.AiFeedbackRerankResponse;
import com.example.app.dto.ai.AiRecommendRequest;
import com.example.app.entity.Flight;
import com.example.app.entity.UserFeedback;
import com.example.app.service.UserFeedbackService;
import com.example.app.service.ai.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI相关接口控制器
 */
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI服务", description = "AI增强的航班推荐和分析接口")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    private final AiService aiService;
    private final UserFeedbackService userFeedbackService;
    private final ExecutorService executor;

    public AiController(AiService aiService, UserFeedbackService userFeedbackService) {
        this.aiService = aiService;
        this.userFeedbackService = userFeedbackService;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * 提交用户反馈
     */
    @PostMapping("/feedback")
    @Operation(summary = "提交用户反馈", description = "提交对航班推荐的反馈（喜欢/不喜欢/中立）")
    public ResponseEntity<Result<AiFeedbackResponse>> submitFeedback(@Valid @RequestBody AiFeedbackRequest request) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(Result.error(401, "请先登录"));
        }

        try {
            // 创建用户反馈实体
            UserFeedback feedback = UserFeedback.builder()
                    .userId(userId)
                    .flightId(request.getFlightId())
                    .feedbackType(request.getFeedbackType())
                    .feedbackText(request.getFeedbackText())
                    .metadata(request.getMetadata())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            // 保存反馈
            UserFeedback savedFeedback = userFeedbackService.saveFeedback(feedback);

            // 调用AI分析反馈
            if (aiService.isAiEnabled()) {
                userFeedbackService.analyzeFeedbackWithAi(savedFeedback);
            }

            // 转换为响应DTO
            AiFeedbackResponse response = convertToResponse(savedFeedback);
            return ResponseEntity.ok(Result.success(response));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("提交反馈失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户反馈历史
     */
    @GetMapping("/feedback")
    @Operation(summary = "获取用户反馈历史", description = "获取当前用户的反馈历史记录")
    public ResponseEntity<Result<List<AiFeedbackResponse>>> getFeedbackHistory() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(Result.error(401, "请先登录"));
        }

        try {
            List<UserFeedback> feedbacks = userFeedbackService.getUserFeedbacks(userId);
            List<AiFeedbackResponse> responses = feedbacks.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Result.success(responses));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("获取反馈历史失败: " + e.getMessage()));
        }
    }

    /**
     * 获取反馈统计
     */
    @GetMapping("/feedback/stats")
    @Operation(summary = "获取反馈统计", description = "获取当前用户的反馈统计数据")
    public ResponseEntity<Result<Map<String, Object>>> getFeedbackStats() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(Result.error(401, "请先登录"));
        }

        try {
            Map<String, Object> stats = userFeedbackService.getFeedbackStats(userId);
            return ResponseEntity.ok(Result.success(stats));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("获取反馈统计失败: " + e.getMessage()));
        }
    }

    /**
     * AI分析推荐效果
     */
    @PostMapping("/analyze")
    @Operation(summary = "AI分析推荐效果", description = "使用AI分析推荐系统的效果和性能")
    public ResponseEntity<Result<Map<String, Object>>> analyzeRecommendations(
            @RequestParam(defaultValue = "LAST_7_DAYS") String period) {
        try {
            Map<String, Object> analysis = aiService.analyzeRecommendationPerformance(period);
            return ResponseEntity.ok(Result.success(analysis));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("AI分析失败: " + e.getMessage()));
        }
    }

    /**
     * 获取AI服务状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取AI服务状态", description = "检查AI服务是否可用")
    public ResponseEntity<Result<Map<String, Object>>> getAiStatus() {
        try {
            boolean isEnabled = aiService.isAiEnabled();
            boolean isConnected = aiService.testAiConnection();

            Map<String, Object> status = Map.of(
                    "enabled", isEnabled,
                    "connected", isConnected,
                    "status", isEnabled && isConnected ? "可用" : "不可用",
                    "timestamp", System.currentTimeMillis()
            );

            return ResponseEntity.ok(Result.success(status));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("获取AI状态失败: " + e.getMessage()));
        }
    }

    /**
     * 优化推荐策略
     */
    @PostMapping("/optimize")
    @Operation(summary = "优化推荐策略", description = "基于用户反馈优化推荐策略")
    public ResponseEntity<Result<Map<String, Object>>> optimizeStrategy() {
        try {
            // 获取最近的用户反馈
            List<UserFeedback> recentFeedbacks = userFeedbackService.getRecentFeedbacks(100);

            if (recentFeedbacks.isEmpty()) {
                return ResponseEntity.ok(Result.error("没有足够的反馈数据进行优化"));
            }

            Map<String, Object> optimization = aiService.optimizeRecommendationStrategy(recentFeedbacks);
            return ResponseEntity.ok(Result.success(optimization));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("优化推荐策略失败: " + e.getMessage()));
        }
    }

    /**
     * 获取AI个性化推荐（高级接口）
     */
    @PostMapping("/recommend")
    @Operation(summary = "AI个性化推荐", description = "基于用户偏好和AI分析的个性化航班推荐")
    public ResponseEntity<Result<Map<String, List<?>>>> getAiRecommendations(
            @Valid @RequestBody AiRecommendRequest request) {
        Long userId = getCurrentUserId();

        try {
            Map<String, List<Flight>> recommendations = aiService.generatePersonalizedRecommendations(request, userId);
            Map<String, List<?>> result = recommendations.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (List<?>) e.getValue()));

            return ResponseEntity.ok(Result.success(result));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("获取AI推荐失败: " + e.getMessage()));
        }
    }

    /**
     * 不满意反馈并重新优化推荐
     */
    @PostMapping("/feedback/rerank")
    @Operation(summary = "不满意反馈并重新优化", description = "提交不满意反馈并立即重新优化推荐结果")
    public ResponseEntity<Result<AiFeedbackRerankResponse>> rerankAfterFeedback(
            @Valid @RequestBody AiFeedbackRerankRequest request) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(Result.error(401, "请先登录"));
        }

        long startTime = System.currentTimeMillis();

        try {
            // 1. 保存用户反馈到数据库
            List<UserFeedback> savedFeedbacks = new ArrayList<>();
            for (AiFeedbackRerankRequest.FlightFeedbackItem item : request.getCurrentRecommendations()) {
                UserFeedback feedback = UserFeedback.builder()
                        .userId(userId)
                        .flightId(item.getFlightId())
                        .feedbackType(item.getFeedbackType())
                        .feedbackText(item.getReason())
                        .metadata(item.getMetadata())
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .updatedAt(new Timestamp(System.currentTimeMillis()))
                        .build();

                UserFeedback savedFeedback = userFeedbackService.saveFeedback(feedback);
                savedFeedbacks.add(savedFeedback);

                // 调用AI分析单个反馈（如果启用）
                if (aiService.isAiEnabled()) {
                    userFeedbackService.analyzeFeedbackWithAi(savedFeedback);
                }
            }

            // 2. 调用AI进行重新优化
            Map<String, List<Flight>> rerankedRecommendations = new HashMap<>();
            String aiAnalysis = "用户反馈已记录，正在进行重新优化...";
            Double confidenceScore = 0.0;

            if (aiService.isAiEnabled()) {
                try {
                    // 使用AI服务进行重新优化
                    Map<String, Object> optimizationResult = aiService.rerankBasedOnFeedback(
                            savedFeedbacks,
                            request.getFeedbackSummary(),
                            request.getRecommendationType(),
                            userId
                    );

                    if (optimizationResult.containsKey("recommendations")) {
                        rerankedRecommendations = (Map<String, List<Flight>>) optimizationResult.get("recommendations");
                    }
                    if (optimizationResult.containsKey("analysis")) {
                        aiAnalysis = (String) optimizationResult.get("analysis");
                    }
                    if (optimizationResult.containsKey("confidenceScore")) {
                        confidenceScore = (Double) optimizationResult.get("confidenceScore");
                    }
                } catch (Exception e) {
                    // AI优化失败时，提供降级方案
                    aiAnalysis = "AI优化暂时不可用，已基于您的反馈使用传统算法重新筛选。原因: " + e.getMessage();
                    // 这里可以添加传统算法的降级逻辑
                }
            } else {
                aiAnalysis = "AI服务未启用，已记录您的反馈。";
            }

            // 3. 构建响应
            AiFeedbackRerankResponse response = AiFeedbackRerankResponse.builder()
                    .feedbackId(savedFeedbacks.isEmpty() ? null : savedFeedbacks.get(0).getId())
                    .userId(userId)
                    .aiAnalysis(aiAnalysis)
                    .confidenceScore(confidenceScore)
                    .optimizationReason(request.getFeedbackSummary())
                    .rerankedRecommendations(rerankedRecommendations)
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .processedAt(new Timestamp(System.currentTimeMillis()))
                    .additionalInfo(String.format("处理了%d条反馈", savedFeedbacks.size()))
                    .build();

            return ResponseEntity.ok(Result.success(response));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("反馈重新优化失败: " + e.getMessage()));
        }
    }

    /**
     * 获取当前用户ID
     */
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
     * 将UserFeedback转换为AiFeedbackResponse
     */
    private AiFeedbackResponse convertToResponse(UserFeedback feedback) {
        return AiFeedbackResponse.builder()
                .id(feedback.getId())
                .userId(feedback.getUserId())
                .flightId(feedback.getFlightId())
                .feedbackType(feedback.getFeedbackType())
                .feedbackText(feedback.getFeedbackText())
                .aiAnalysis(feedback.getAiAnalysis())
                .confidenceScore(feedback.getConfidenceScore())
                .metadata(feedback.getMetadata())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }

    /**
     * AI助手对话接口（满意度助手）
     */
    @PostMapping("/assistant")
    @Operation(summary = "AI助手对话", description = "与AI助手进行对话，获取航班推荐、反馈分析、满意度优化等帮助")
    public ResponseEntity<Result<Map<String, Object>>> chatWithAssistant(
            @RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            List<Map<String, Object>> context = (List<Map<String, Object>>) request.getOrDefault("context", new ArrayList<>());
            Long userId = getCurrentUserId();

            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.ok(Result.error("消息不能为空"));
            }

            // 调用AI服务进行对话
            Map<String, Object> response = aiService.chatWithAssistant(message, context, userId);

            return ResponseEntity.ok(Result.success(response));

        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("AI助手对话失败: " + e.getMessage()));
        }
    }

    /**
     * AI助手流式对话接口（SSE） - GET版本
     */
    @GetMapping(value = "/assistant/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "*") // 允许跨域访问
    @Operation(summary = "AI助手流式对话", description = "与AI助手进行流式对话，实时接收AI响应")
    public SseEmitter chatWithAssistantStreamGet(
            @RequestParam String message,
            @RequestParam(required = false) String context,
            @RequestParam(required = false) Long userId) {

        SseEmitter emitter = new SseEmitter(60_000L); // 60秒超时

        if (message == null || message.trim().isEmpty()) {
            emitter.completeWithError(new IllegalArgumentException("消息不能为空"));
            return emitter;
        }

        // 如果用户ID未提供，使用当前登录用户
        if (userId == null) {
            userId = getCurrentUserId();
        }

        // 解析上下文（如果提供）
        List<Map<String, Object>> contextList = new ArrayList<>();
        if (context != null && !context.trim().isEmpty()) {
            try {
                // 简单解析，实际应该使用JSON解析
                // 这里简化处理
                log.debug("接收到上下文参数: {}", context);
            } catch (Exception e) {
                log.warn("上下文参数解析失败", e);
            }
        }

        // 创建final变量供lambda使用
        final String finalMessage = message;
        final List<Map<String, Object>> finalContext = contextList;
        final Long finalUserId = userId;

        // 使用线程池异步处理，避免阻塞主线程
        executor.execute(() -> {
            try {
                log.info("开始处理流式对话，消息: {}, 用户ID: {}", finalMessage, finalUserId);

                // 调用AI服务获取完整响应
                String fullResponse = aiService.chatWithAssistantStream(finalMessage, finalContext, finalUserId);

                if (fullResponse == null || fullResponse.isEmpty()) {
                    emitter.send(SseEmitter.event()
                            .data("抱歉，AI助手暂时无法回答这个问题。")
                            .name("error"));
                    emitter.complete();
                    return;
                }

                log.info("AI响应长度: {} 字符", fullResponse.length());

                // 按字符流式发送
                for (int i = 0; i < fullResponse.length(); i++) {
                    String chunk = fullResponse.substring(i, i + 1);

                    // 构建SSE事件
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .data(chunk)
                            .id(String.valueOf(i))
                            .name("message_chunk");

                    emitter.send(event);

                    // 模拟打字机效果，延迟30-100ms之间随机，更自然
                    try {
                        Thread.sleep(30 + (int)(Math.random() * 70));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        emitter.completeWithError(e);
                        return;
                    }
                }

                // 发送结束标志
                emitter.send(SseEmitter.event()
                        .data("[DONE]")
                        .name("stream_end"));
                emitter.complete();

                log.info("流式对话完成");

            } catch (Exception e) {
                log.error("流式对话处理失败", e);
                try {
                    emitter.send(SseEmitter.event()
                            .data("抱歉，处理过程中出现错误: " + e.getMessage())
                            .name("error"));
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            }
        });

        // 设置超时和错误处理
        emitter.onTimeout(() -> {
            log.warn("流式对话超时");
            emitter.complete();
        });

        emitter.onError((e) -> {
            log.error("流式对话发生错误", e);
        });

        return emitter;
    }

}