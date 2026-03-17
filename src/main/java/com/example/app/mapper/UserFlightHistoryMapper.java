package com.example.app.mapper;

import com.example.app.entity.UserFlightHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFlightHistoryMapper {

    int insert(UserFlightHistory record);

    List<UserFlightHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    List<UserFlightHistory> selectByUserAndBehavior(@Param("userId") Long userId,
                                                     @Param("behaviorType") String behaviorType,
                                                     @Param("limit") int limit);

    List<String> selectFrequentRoutesByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    List<String> selectPopularRoutesGlobal(@Param("limit") int limit);
}
