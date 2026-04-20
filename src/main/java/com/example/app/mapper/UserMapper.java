package com.example.app.mapper;

import com.example.app.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(Long id);

    User selectByUsername(String username);

    User selectByEmail(String email);

    User selectByPhone(String phone);

    List<User> selectAll();

    int insert(User user);

    int update(User user);

    int deleteById(Long id);
}
