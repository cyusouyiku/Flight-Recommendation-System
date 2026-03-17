package com.example.app.mapper;

import com.example.app.dto.FlightSearchParams;
import com.example.app.entity.Flight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FlightMapper {

    Flight selectById(Long id);

    List<Flight> selectAll();

    List<Flight> selectBySearch(@Param("departureAirportCode") String departureAirportCode,
                                @Param("arrivalAirportCode") String arrivalAirportCode,
                                @Param("effectiveDate") LocalDate effectiveDate);

    List<Flight> selectBySearchAdvanced(FlightSearchParams params);

    int insert(Flight flight);

    int update(Flight flight);

    int deleteById(Long id);
}
