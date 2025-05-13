package com.spring.eventbooking.security;

import com.spring.eventbooking.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtilsUser extends JwtUtils {

    private String buildToken(Map<String, Object> extraClaims, User user,
                              long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        return buildToken(extraClaims, user, getJwtExpiration());
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }


    @Override
    public String getSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public long getJwtExpiration() {
        return super.getJwtExpiration();
    }

}
