package br.com.pdv.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.expiration}")
    private Integer expiration;
    @Value("${security.jwt.key}")
    private String key;

    public String genareteToken(String username) {

        Calendar currentTimeNow = Calendar.getInstance();
        currentTimeNow.add(Calendar.MINUTE, expiration);
        Date dataExpiration = currentTimeNow.getTime();

        SecretKey secretKey = getSecretKey();

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(dataExpiration)
                .signWith(secretKey)
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    private Claims getClaims(String token) {
        SecretKey secretKey = getSecretKey();

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserName(String token) {
        return getClaims(token).getSubject();
    }
}