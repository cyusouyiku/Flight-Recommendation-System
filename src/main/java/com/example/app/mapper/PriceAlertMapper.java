package com.example.app.mapper;

import com.example.app.entity.PriceAlert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriceAlertMapper {

    int insert(PriceAlert alert);

    List<PriceAlert> selectByUserId(@Param("userId") Long userId);

    List<PriceAlert> selectActiveAlerts();  // 未通知的

    int updateNotified(@Param("id") Long id);
}
