package com.microservice.auth.utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    public String generarToken(String email, Map<String, Object> claims){
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))
                .signWith( getKey() )
                .compact();

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

    private SecretKey getKey(){
        String claveSecreta = secret;
        byte[] secretKeyBytes = claveSecreta.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public String actualizarToken(String token) {
        try {
            // Eliminar comillas simples o dobles del token si están presentes
            token = token.replaceAll("[\"']", "");
            // Parsear el token para verificar si está vencido y extraer los claims
            Jws<Claims> claims = parseJwt(token);

            // Verificar si el token está a punto de expirar (por ejemplo, dentro de los próximos 5 minutos)
            Instant tokenExpiration = claims.getBody().getExpiration().toInstant();
            Instant now = Instant.now();
            Instant fiveMinutesBeforeExpiration = tokenExpiration.minus(5, ChronoUnit.MINUTES); // Puedes ajustar el intervalo según tus necesidades

            if (now.isAfter(fiveMinutesBeforeExpiration)) {
                // El token está a punto de expirar, generar un nuevo token actualizado
                String email = claims.getBody().getSubject();
                Map<String, Object> nuevosClaims = new HashMap<>();
                // Agregar cualquier otro claim que desees transferir al nuevo token
                return generarToken(email, nuevosClaims);
            } else {
                // El token aún no está próximo a expirar, devolver el token original
                return token;
            }
        } catch (JwtException e) {
            // El token es inválido, está mal formado o ha sido manipulado
            throw new JwtException("Error al actualizar el token: " + e.getMessage());
        }
    }
}
