package com.example.app.service;

import com.example.app.common.Result;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.entity.User;
import com.example.app.mapper.UserMapper;
import com.example.app.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public Result<Map<String, Object>> register(RegisterRequest req) {
        if (userMapper.selectByUsername(req.getUsername()) != null) {
            return Result.error(400, "用户名已存在");
        }
        if (req.getEmail() != null && !req.getEmail().trim().isEmpty()) {
            if (userMapper.selectByEmail(req.getEmail()) != null) {
                return Result.error(400, "邮箱已被注册");
            }
        }
        if (req.getPhone() != null && !req.getPhone().trim().isEmpty()) {
            if (userMapper.selectByPhone(req.getPhone()) != null) {
                return Result.error(400, "手机号已被注册");
            }
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getUsername());
        Map<String, Object> userMap = buildUserMap(user);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userMap);
        return Result.success(data);
    }

    public Result<Map<String, Object>> login(LoginRequest req) {
        try {
            logger.info("尝试登录用户: {}", req.getUsername());
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            User user = userMapper.selectByUsername(auth.getName());
            if (user == null) {
                logger.warn("用户不存在: {}", req.getUsername());
                return Result.error(401, "用户不存在");
            }
            String token = jwtUtil.generateToken(auth.getName());
            Map<String, Object> userMap = buildUserMap(user);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userMap);
            logger.info("用户登录成功: {}", req.getUsername());
            return Result.success(data);
        } catch (Exception e) {
            logger.error("用户登录失败: {}, 错误: {}", req.getUsername(), e.getMessage(), e);
            return Result.error(401, "用户名或密码错误");
        }
    }

    private Map<String, Object> buildUserMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhone());
        userMap.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        userMap.put("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        return userMap;
    }
}
