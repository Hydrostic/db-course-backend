package org.LunaTelecom.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.AsymmetricJwk;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.EdDSAParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class JWTUtil {
    private static SecretKey key;


    /**
     * 生成 JWT Token
     * @param subject 用户标识，userId
     * @return JWT 字符串
     */
    public static String generateToken(String subject, long expirationSecs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(now + expirationSecs * 1000))
                .signWith(key)
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
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        return claimsJws.getPayload().getSubject();
    }
    public static String initConfig(String secret) throws Exception {
        if (secret == null || secret.isEmpty()) {
            key = Jwts.SIG.HS256.key().build();
            return Encoders.BASE64.encode(key.getEncoded());
        } else {
            key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            return secret;
        }
    }
}
