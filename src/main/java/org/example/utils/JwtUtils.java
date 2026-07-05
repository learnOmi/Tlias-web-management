package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String SECRET_KEY = "aXRoZWltYQ=="; // 秘钥
    
    // accessToken 有效期：2 小时（毫秒）
    private static final long ACCESS_TOKEN_EXPIRATION = 2 * 60 * 60 * 1000L;
    
    // refreshToken 有效期：7 天（毫秒）
    private static final long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60 * 1000;

    /**
     * 生成访问令牌（短期有效）
     * @param claims 令牌中包含的信息
     * @return 生成的JWT访问令牌字符串
     */
    public static String generateAccessToken(Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .compact();
    }

    /**
     * 生成刷新令牌（长期有效）
     * @param claims 令牌中包含的信息
     * @return 生成的JWT刷新令牌字符串
     */
    public static String generateRefreshToken(Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token 要解析的JWT令牌字符串
     * @return 包含令牌信息的Claims对象
     * @throws Exception 如果令牌无效或已过期，则抛出异常
     */
    public static Claims parseToken(String token) throws Exception {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}