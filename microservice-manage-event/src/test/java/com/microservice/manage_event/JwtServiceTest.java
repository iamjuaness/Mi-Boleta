package com.microservice.manage_event;

import com.microservice.manage_event.service.implementation.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class JwtServiceTest {

    private JwtService jwtService; // Suponiendo que tu clase se llama JwtService
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        // Inicializa el servicio JWT
        jwtService = new JwtService();

        // Inicializa la clave secreta (puedes usar la misma lógica de tu método getKey)
        String secretKeyString = "FXiqOa5gE1Kj0kLZYXRx6IDzlb6t6JZaEScetv2VBY7D2K1eC/xY6QxdA55eX0sB0x+36aBov3br/dQTHx9s+a17sU11gjXWMRd1+3I3OoCl4sVI4Yy80KoDcwb3ifl"; // Asegúrate de que tenga al menos 256 bits
        secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        jwtService.setSecretKey(secretKeyString);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .signWith(secretKey)
                .compact();

        boolean isValid = jwtService.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token";

        boolean isValid = jwtService.validateToken(invalidToken);
        assertFalse(isValid);
    }

    @Test
    void testExtractUsername_ValidToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .signWith(secretKey)
                .compact();

        String username = jwtService.extractUsername(token);
        assertEquals("user@example.com", username);
    }

    @Test
    void testExtractUsername_ExpiredToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .expiration(new Date(System.currentTimeMillis() - 1000)) // Expirado
                .signWith(secretKey)
                .compact();

        String username = jwtService.extractUsername(token);
        assertNull(username); // Debería devolver null
    }

    @Test
    void testExtractRole_ValidToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .claim("role", "CLIENT")
                .signWith(secretKey)
                .compact();

        String role = jwtService.extractRole(token);
        assertEquals("CLIENT", role);
    }

    @Test
    void testParseJwt_ValidToken() {
        String token = Jwts.builder()
                .subject("user@example.com")
                .signWith(secretKey)
                .compact();

        Claims claims = jwtService.parseJwt(token).getPayload();
        assertNotNull(claims);
        assertEquals("user@example.com", claims.getSubject());
    }
}
