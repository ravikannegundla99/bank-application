package com.example.bank_application.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration}")
    private long jwtExirationDate;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key()) // Set the signing key
                    .build()
                    .parseClaimsJws(token); // Parse and validate the token
            return true; // Token is valid
        } catch (ExpiredJwtException ex) {
            System.out.println("Token expired: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid token: " + ex.getMessage());
        } catch (SignatureException ex) {
            System.out.println("Invalid signature: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Token validation error: " + ex.getMessage());
        }
        return false; // Token is invalid
    }


}
