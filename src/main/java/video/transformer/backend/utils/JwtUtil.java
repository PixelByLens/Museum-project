package video.transformer.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    // 生成 token
    public static String generateToken(String email,  Integer id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);
        claims.put("id", id);
        claims.put("iat", new Date());
        claims.put("exp", new Date(System.currentTimeMillis() + EXPIRATION_TIME));

        return Jwts.builder()
            .setClaims(claims)
            .signWith(SECRET_KEY)
            .compact();
    }


    // 验证 token
    public static Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("密钥过期.....");
            return null;
        }
    }
}