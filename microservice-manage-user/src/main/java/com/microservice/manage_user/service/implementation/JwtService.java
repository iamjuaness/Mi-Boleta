package com.microservice.manage_user.service.implementation;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Boolean validateToken(String token) {
        try {
            // The parser verifies the signature and token format.
            Jwts.parser()
                    .verifyWith(getKey()) // The secret must be the same as the one used to sign the token.
                    .build()
                    .parseSignedClaims(token); // If the token is invalid, it will throw an exception
            return true; // If all is well, return true
        } catch (JwtException e){
            return false;
        }
    }

    public Jws<Claims> parseJwt(String jwtString) {
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith( getKey() ).build();
            return jwtParser.parseSignedClaims(jwtString);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            e.printStackTrace(); // Imprimir la traza de la excepción para depurar
            // Manejar la excepción según sea necesario
            return null; // O devuelve un valor predeterminado o lanza una nueva excepción
        }
    }


    public String extractUsername(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
    public String extractRole(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();
            return claims.get("role", String.class);
        } catch (JwtException e) {
            return null;
        }
    }
    public SecretKey getKey() {
        String secretKey1 = secretKey;
        byte[] secretKeyBytes = secretKey1.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }


}

