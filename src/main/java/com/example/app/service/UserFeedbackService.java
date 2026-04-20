package com.example.app.service;

import com.example.app.dto.ai.AiAnalysisResult;
import com.example.app.entity.UserFeedback;
import com.example.app.mapper.UserFeedbackMapper;
import com.example.app.service.ai.AiService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户反馈服务
 */
@Service
public class UserFeedbackService {

    private final UserFeedbackMapper userFeedbackMapper;
    private final AiService aiService;

    public UserFeedbackService(UserFeedbackMapper userFeedbackMapper, AiService aiService) {
        this.userFeedbackMapper = userFeedbackMapper;
        this.aiService = aiService;
    }

    /**
     * 保存用户反馈
     *
     * @param feedback 用户反馈
     * @return 保存后的反馈
     */
    public UserFeedback saveFeedback(UserFeedback feedback) {
        // 设置时间戳
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (feedback.getCreatedAt() == null) {
            feedback.setCreatedAt(now);
        }
        feedback.setUpdatedAt(now);

        // 插入数据库
        if (feedback.getId() == null) {
            userFeedbackMapper.insert(feedback);
        } else {
            userFeedbackMapper.update(feedback);
        }

        return feedback;
    }

    /**
     * 使用AI分析反馈
     *
     * @param feedback 用户反馈
     * @return AI分析结果
     */
    public AiAnalysisResult analyzeFeedbackWithAi(UserFeedback feedback) {
        if (!aiService.isAiEnabled()) {
            return null;
        }

        try {
            AiAnalysisResult analysisResult = aiService.analyzeUserFeedback(feedback);
            if (analysisResult != null) {
                // 更新反馈的AI分析结果
                feedback.setAiAnalysis(analysisResult.getDetailedResult());
                feedback.setConfidenceScore(analysisResult.getConfidenceScore());
                feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                userFeedbackMapper.update(feedback);
            }

            return analysisResult;

        } catch (Exception e) {
            // AI分析失败不影响反馈保存
            return null;
        }
    }

    /**
     * 获取用户的所有反馈
     *
     * @param userId 用户ID
     * @return 用户反馈列表
     */
    public List<UserFeedback> getUserFeedbacks(Long userId) {
        return userFeedbackMapper.selectByUserId(userId);
    }

    /**
     * 根据用户ID和反馈类型获取反馈
     *
     * @param userId       用户ID
     * @param feedbackType 反馈类型
     * @return 用户反馈列表
     */
    public List<UserFeedback> getUserFeedbacksByType(Long userId, String feedbackType) {
        return userFeedbackMapper.selectByUserIdAndType(userId, feedbackType);
    }

    /**
     * 根据航班ID获取反馈
     *
     * @param flightId 航班ID
     * @return 反馈列表
     */
    public List<UserFeedback> getFeedbacksByFlightId(Long flightId) {
        return userFeedbackMapper.selectByFlightId(flightId);
    }

    /**
     * 获取最近的反馈
     *
     * @param limit 数量限制
     * @return 最近的反馈列表
     */
    public List<UserFeedback> getRecentFeedbacks(int limit) {
        return userFeedbackMapper.selectRecentFeedbacks(limit);
    }

    /**
     * 删除用户反馈
     *
     * @param id 反馈ID
     * @return 是否成功
     */
    public boolean deleteFeedback(Long id) {
        try {
            int result = userFeedbackMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取反馈统计
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    public Map<String, Object> getFeedbackStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 总反馈数
        int total = userFeedbackMapper.countByUserId(userId);
        stats.put("total_feedbacks", total);

        // 按类型统计
        List<UserFeedback> typeStats = userFeedbackMapper.selectFeedbackStatsByUserId(userId);
        Map<String, Integer> typeCounts = new HashMap<>();

        if (typeStats != null) {
            // 注意：selectFeedbackStatsByUserId返回的是List<UserFeedback>，但实际应该是Map
            // 这里简化处理，实际应该调整Mapper方法返回Map
            Map<String, Long> tempMap = typeStats.stream()
                    .collect(Collectors.groupingBy(UserFeedback::getFeedbackType, Collectors.counting()));

            tempMap.forEach((type, count) -> typeCounts.put(type, count.intValue()));
        }

        stats.put("feedback_by_type", typeCounts);

        // AI分析覆盖率
        List<UserFeedback> allFeedbacks = userFeedbackMapper.selectByUserId(userId);
        long aiAnalyzedCount = allFeedbacks.stream()
                .filter(f -> f.getAiAnalysis() != null && !f.getAiAnalysis().isEmpty())
                .count();

        double aiCoverage = total > 0 ? (double) aiAnalyzedCount / total : 0.0;
        stats.put("ai_analysis_coverage", aiCoverage);
        stats.put("ai_analyzed_count", aiAnalyzedCount);

        return stats;
    }

    /**
     * 批量分析用户反馈
     *
     * @param feedbacks 反馈列表
     * @return 分析结果列表
     */
    public List<AiAnalysisResult> analyzeFeedbacksWithAi(List<UserFeedback> feedbacks) {
        if (!aiService.isAiEnabled()) {
            return List.of();
        }

        return feedbacks.stream()
                .map(this::analyzeFeedbackWithAi)
                .filter(result -> result != null)
                .collect(Collectors.toList());
    }

    /**
     * 更新反馈的AI分析结果
     *
     * @param feedbackId    反馈ID
     * @param aiAnalysis    AI分析结果
     * @param confidenceScore 置信度分数
     * @return 是否成功
     */
    public boolean updateFeedbackAnalysis(Long feedbackId, String aiAnalysis, Double confidenceScore) {
        try {
            UserFeedback feedback = userFeedbackMapper.selectById(feedbackId);
            if (feedback == null) {
                return false;
            }

            feedback.setAiAnalysis(aiAnalysis);
            feedback.setConfidenceScore(confidenceScore);
            feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            int result = userFeedbackMapper.update(feedback);
            return result > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据ID获取反馈
     *
     * @param id 反馈ID
     * @return 反馈
     */
    public UserFeedback getFeedbackById(Long id) {
        return userFeedbackMapper.selectById(id);
    }

    /**
     * 获取所有反馈
     *
     * @return 所有反馈
     */
    public List<UserFeedback> getAllFeedbacks() {
        return userFeedbackMapper.selectAll();
    }
}