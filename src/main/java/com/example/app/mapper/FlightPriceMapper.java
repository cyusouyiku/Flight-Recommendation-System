package com.example.app.mapper;

import com.example.app.entity.FlightPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FlightPriceMapper {

    int insert(FlightPrice record);

    List<FlightPrice> selectByRouteAndDateRange(
            @Param("departureCode") String departureCode,
            @Param("arrivalCode") String arrivalCode,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
