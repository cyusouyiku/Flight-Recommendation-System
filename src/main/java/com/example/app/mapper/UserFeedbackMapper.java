package com.example.app.mapper;

import com.example.app.entity.UserFeedback;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户反馈Mapper
 */
@Mapper
public interface UserFeedbackMapper {

    /**
     * 根据ID查询用户反馈
     */
    UserFeedback selectById(Long id);

    /**
     * 根据用户ID查询反馈列表
     */
    List<UserFeedback> selectByUserId(Long userId);

    /**
     * 根据用户ID和反馈类型查询反馈列表
     */
    List<UserFeedback> selectByUserIdAndType(Long userId, String feedbackType);

    /**
     * 根据航班ID查询反馈列表
     */
    List<UserFeedback> selectByFlightId(Long flightId);

    /**
     * 查询所有用户反馈
     */
    List<UserFeedback> selectAll();

    /**
     * 插入用户反馈
     */
    int insert(UserFeedback userFeedback);

    /**
     * 更新用户反馈
     */
    int update(UserFeedback userFeedback);

    /**
     * 根据ID删除用户反馈
     */
    int deleteById(Long id);

    /**
     * 统计用户反馈数量
     */
    int countByUserId(Long userId);

    /**
     * 获取用户的反馈统计（按类型分组）
     */
    List<UserFeedback> selectFeedbackStatsByUserId(Long userId);

    /**
     * 获取最近的用户反馈（用于AI分析）
     */
    List<UserFeedback> selectRecentFeedbacks(int limit);
}