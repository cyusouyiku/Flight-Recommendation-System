package com.example.app.mapper;

import com.example.app.entity.Airline;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AirlineMapper {

    Airline selectById(Long id);

    Airline selectByCode(String code);

    List<Airline> selectAll();

    int insert(Airline airline);

    int update(Airline airline);

    int deleteById(Long id);

    int deleteByCode(String code);
}
