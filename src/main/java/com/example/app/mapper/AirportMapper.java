package com.example.app.mapper;

import com.example.app.entity.Airport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AirportMapper {

    Airport selectById(Long id);

    Airport selectByCode(String code);

    List<Airport> selectAll();

    int insert(Airport airport);

    int update(Airport airport);

    int deleteById(Long id);

    int deleteByCode(String code);
}
