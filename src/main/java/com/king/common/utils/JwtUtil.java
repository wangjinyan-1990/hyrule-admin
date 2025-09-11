package com.king.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // 从配置文件中读取JWT密钥
    @Value("${jwt.secret}")
    private String secretKey;

    // 从配置文件中读取过期时间
    @Value("${jwt.expiration}")
    private Long expirationTime;

    // 从配置文件中读取header名称
    @Value("${jwt.header}")
    private String headerName;

    /**
     * 生成JWT token
     * @param userId 用户ID
     * @return token字符串
     */
    public String generateToken(String userId) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /**
     * 从token中解析用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("userId", String.class);
        } catch (Exception e) {
            // token解析失败，返回null
            return null;
        }
    }

    /**
     * 验证token是否有效
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查token是否过期
     * @param token JWT token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Date expiration = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取header名称
     * @return header名称
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * 获取配置的过期时间（毫秒）
     */
    public Long getExpirationTime() {
        return expirationTime;
    }
}

