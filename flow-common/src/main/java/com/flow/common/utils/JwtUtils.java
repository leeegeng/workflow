package com.flow.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtils {

    /**
     * 密钥（至少256位，用于HS256算法）
     */
    private static final String SECRET = "flow-workflow-secret-key-2024-very-long-and-secure-key-32bytes";

    /**
     * 过期时间（毫秒）- 默认7天
     */
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    /**
     * 获取签名密钥
     */
    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(SECRET.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return Token字符串
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims);
    }

    /**
     * 生成JWT Token（带额外数据）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param extra    额外数据
     * @return Token字符串
     */
    public static String generateToken(Long userId, String username, Map<String, Object> extra) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        if (extra != null) {
            claims.putAll(extra);
        }
        return createToken(claims);
    }

    /**
     * 创建Token
     */
    private static String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION);

        JwtBuilder builder = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey());

        return builder.compact();
    }

    /**
     * 解析Token
     *
     * @param token Token字符串
     * @return Claims
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token字符串
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断Token是否过期
     */
    private static boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 从Token中获取用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            return (Long) userId;
        }
        return null;
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return (String) claims.get("username");
        }
        return null;
    }

    /**
     * 获取Token过期时间
     */
    public static Date getExpirationDate(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.getExpiration();
        }
        return null;
    }
}
