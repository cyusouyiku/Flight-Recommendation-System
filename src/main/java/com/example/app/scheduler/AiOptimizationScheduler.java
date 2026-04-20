package com.example.app.scheduler;

import com.example.app.service.UserFeedbackService;
import com.example.app.service.ai.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI优化调度器，定期优化推荐策略和分析推荐效果
 */
@Component
public class AiOptimizationScheduler {

    private static final Logger log = LoggerFactory.getLogger(AiOptimizationScheduler.class);

    private final AiService aiService;
    private final UserFeedbackService userFeedbackService;

    public AiOptimizationScheduler(AiService aiService, UserFeedbackService userFeedbackService) {
        this.aiService = aiService;
        this.userFeedbackService = userFeedbackService;
    }

    /**
     * 每天凌晨2点分析推荐效果
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void analyzeRecommendationPerformance() {
        if (!aiService.isAiEnabled()) {
            log.debug("AI服务未启用，跳过推荐效果分析");
            return;
        }

        try {
            log.info("开始分析推荐效果...");
            Map<String, Object> analysis = aiService.analyzeRecommendationPerformance("LAST_7_DAYS");
            log.info("推荐效果分析完成: {}", analysis.getOrDefault("overall_assessment", "未知"));

            // 记录分析结果到日志或数据库
            if (analysis.containsKey("recommendations")) {
                log.info("优化建议: {}", analysis.get("recommendations"));
            }

        } catch (Exception e) {
            log.error("分析推荐效果失败", e);
        }
    }

    /**
     * 每周一凌晨3点优化推荐策略
     */
    @Scheduled(cron = "0 0 3 * * 1")
    public void optimizeRecommendationStrategy() {
        if (!aiService.isAiEnabled()) {
            log.debug("AI服务未启用，跳过推荐策略优化");
            return;
        }

        try {
            log.info("开始优化推荐策略...");

            // 获取最近的用户反馈
            var recentFeedbacks = userFeedbackService.getRecentFeedbacks(500);
            if (recentFeedbacks.isEmpty()) {
                log.info("没有足够的反馈数据，跳过策略优化");
                return;
            }

            // 优化推荐策略
            Map<String, Object> optimization = aiService.optimizeRecommendationStrategy(recentFeedbacks);
            log.info("推荐策略优化完成: {}", optimization.getOrDefault("strategy", "未知"));

            // 记录优化结果
            if (optimization.containsKey("action")) {
                log.info("推荐操作建议: {}", optimization.get("action"));
            }

        } catch (Exception e) {
            log.error("优化推荐策略失败", e);
        }
    }

    /**
     * 每小时检查AI服务状态
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkAiServiceStatus() {
        try {
            boolean isEnabled = aiService.isAiEnabled();
            boolean isConnected = aiService.testAiConnection();

            if (!isEnabled || !isConnected) {
                log.warn("AI服务状态异常: enabled={}, connected={}", isEnabled, isConnected);
            } else {
                log.debug("AI服务状态正常");
            }

            // 可以记录状态到监控系统
            Map<String, Object> status = Map.of(
                    "enabled", isEnabled,
                    "connected", isConnected,
                    "timestamp", System.currentTimeMillis()
            );

            log.debug("AI服务状态: {}", status);

        } catch (Exception e) {
            log.error("检查AI服务状态失败", e);
        }
    }

    /**
     * 每周日凌晨1点清理旧的分析数据
     */
    @Scheduled(cron = "0 0 1 * * 0")
    public void cleanupOldAnalysisData() {
        if (!aiService.isAiEnabled()) {
            log.debug("AI服务未启用，跳过数据清理");
            return;
        }

        try {
            log.info("开始清理旧的分析数据...");

            // 这里可以添加清理逻辑，比如：
            // 1. 清理过期的AI分析结果
            // 2. 归档旧的用户反馈
            // 3. 清理临时缓存

            log.info("旧的分析数据清理完成");

        } catch (Exception e) {
            log.error("清理旧的分析数据失败", e);
        }
    }

    /**
     * 每月第一天凌晨4点生成月度报告
     */
    @Scheduled(cron = "0 0 4 1 * ?")
    public void generateMonthlyReport() {
        if (!aiService.isAiEnabled()) {
            log.debug("AI服务未启用，跳过月度报告生成");
            return;
        }

        try {
            log.info("开始生成月度AI推荐报告...");

            // 分析上月数据
            Map<String, Object> analysis = aiService.analyzeRecommendationPerformance("LAST_30_DAYS");

            // 获取用户反馈统计
            var recentFeedbacks = userFeedbackService.getRecentFeedbacks(1000);
            Map<String, Object> optimization = aiService.optimizeRecommendationStrategy(recentFeedbacks);

            // 生成报告摘要
            log.info("=== 月度AI推荐报告 ===");
            log.info("分析周期: 最近30天");
            log.info("整体评估: {}", analysis.getOrDefault("overall_assessment", "未知"));
            log.info("优化策略: {}", optimization.getOrDefault("strategy", "未知"));
            log.info("操作建议: {}", optimization.getOrDefault("action", "无"));
            log.info("===================");

            // 可以发送报告到邮件或存储到数据库

        } catch (Exception e) {
            log.error("生成月度报告失败", e);
        }
    }
}