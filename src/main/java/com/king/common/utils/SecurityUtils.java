package com.king.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 */
@Component
public class SecurityUtils {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public String getUserId() {
        try {
            // 从请求头中获取token
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String token = getTokenFromRequest(request);
            
            if (token == null) {
                return null;
            }
            
            // 先尝试从Redis获取用户信息
            Object obj = redisTemplate.opsForValue().get(token);
            if (obj != null) {
                // 从Redis中解析用户ID
                return parseUserIdFromRedis(obj);
            }
            
            // 再尝试从JWT获取用户ID
            return jwtUtil.getUserIdFromToken(token);
            
        } catch (Exception e) {
            // 获取失败时返回null
            return null;
        }
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从Authorization头获取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 从X-Token头获取
        String xToken = request.getHeader("X-Token");
        if (xToken != null && !xToken.trim().isEmpty()) {
            return xToken;
        }
        
        // 从请求参数获取
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.trim().isEmpty()) {
            return tokenParam;
        }
        
        return null;
    }

    /**
     * 从Redis对象中解析用户ID
     */
    private String parseUserIdFromRedis(Object obj) {
        try {
            // 假设Redis中存储的是用户对象的JSON字符串
            String userJson = obj.toString();
            // 这里需要根据实际的用户对象结构来解析
            // 暂时返回null，需要根据实际情况调整
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
