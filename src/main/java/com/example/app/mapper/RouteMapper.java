package com.example.app.mapper;

import com.example.app.entity.Route;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RouteMapper {

    Route selectById(Long id);

    List<Route> selectAll();

    List<Route> selectByDepartureAndArrival(@Param("departureAirportCode") String departureAirportCode, @Param("arrivalAirportCode") String arrivalAirportCode);

    int insert(Route route);

    int update(Route route);

    int deleteById(Long id);
}
