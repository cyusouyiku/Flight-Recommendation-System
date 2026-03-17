package com.example.app.aspect;

import com.example.app.service.UserBehaviorService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AOP 切面：记录用户搜索行为
 */
@Aspect
@Component
public class UserBehaviorAspect {

    private final UserBehaviorService behaviorService;

    public UserBehaviorAspect(UserBehaviorService behaviorService) {
        this.behaviorService = behaviorService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            Object principal = auth.getPrincipal();
            if (principal instanceof com.example.app.security.AuthUser authUser) {
                return authUser.getUserId();
            }
        } catch (Exception ignored) {}
        return null;
    }

    @AfterReturning(
            pointcut = "execution(* com.example.app.service.FlightService.searchAdvanced(..)) && args(req)",
            returning = "result"
    )
    public void afterSearch(JoinPoint jp, Object req, Object result) {
        try {
            com.example.app.dto.FlightSearchRequest request = (com.example.app.dto.FlightSearchRequest) req;
            Long userId = getCurrentUserId();
            if (userId != null && request != null && request.getDeparture() != null && request.getArrival() != null) {
                behaviorService.recordSearch(userId, request.getDeparture(), request.getArrival(), null);
            }
        } catch (Exception ignored) {}
    }
}
