package org.LunaTelecom.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import java.security.KeyPair;
import java.util.Date;

public class JWTUtil {
    private static final KeyPair key = Jwts.SIG.EdDSA.keyPair().build();

    private static final long EXPIRATION_MS = 60 * 60 * 1000;

    /**
     * 生成 JWT Token
     * @param subject 用户标识，userId
     * @return JWT 字符串
     */
    public static String generateToken(String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(now + EXPIRATION_MS))
                .signWith(key.getPrivate())
                .compact();
    }

    /**
     * 解析并验证 Token
     * @param token JWT 字符串
     * @return Token 中的 subject
     * @throws JwtException 验证失败会抛出异常
     */
    public static String validateTokenAndGetSubject(String token) throws JwtException {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key.getPublic())
                .build()
                .parseSignedClaims(token);

        return claimsJws.getPayload().getSubject();
    }
}
