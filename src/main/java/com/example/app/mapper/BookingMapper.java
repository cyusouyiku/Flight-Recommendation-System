package com.example.app.mapper;

import com.example.app.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookingMapper {

    Booking selectById(Long id);

    List<Booking> selectByUserId(@Param("userId") Long userId);

    List<Booking> selectAll();

    int insert(Booking booking);

    int update(Booking booking);

    int deleteById(Long id);
}
