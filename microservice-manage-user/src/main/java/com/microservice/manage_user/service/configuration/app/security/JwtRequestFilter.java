package com.microservice.manage_user.service.configuration.app.security;

import com.microservice.manage_user.service.implementation.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final Map<String, String> ROLE_MAP = new HashMap<>();

    static {
        ROLE_MAP.put("/api/manage-user/**", "CLIENT");
    }

    private static final String[] PUBLIC_ROUTES = {
            "/api/request-user/**"
    };

    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        configureCorsHeaders(response);

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String requestURI = request.getRequestURI();
        String token = getToken(request);

        try {
            if (isPublicRoute(requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (isProtectedRoute(requestURI) && (token == null || !validateTokenForRole(token, requestURI))) {
                createErrorResponse("You do not have permission to access this resource", HttpServletResponse.SC_FORBIDDEN, response);
                return;
            }

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            handleJwtException(e, response);
        } catch (Exception e) {
            createErrorResponse(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        }
    }

    private void configureCorsHeaders(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, Authorization");
    }

    private boolean isPublicRoute(String requestURI) {
        for (String route : PUBLIC_ROUTES) {
            if (requestURI.startsWith(route)) {
                return true;
            }
        }
        return false;
    }

    private boolean isProtectedRoute(String requestURI) {
        return ROLE_MAP.keySet().stream().anyMatch(requestURI::startsWith);
    }

    private boolean validateTokenForRole(String token, String requestURI) {
        Jws<Claims> jws = jwtService.parseJwt(token);
        String requiredRole = ROLE_MAP.get(requestURI);
        return jws.getPayload().get("role").equals(requiredRole);
    }

    private void handleJwtException(JwtException e, HttpServletResponse response) throws IOException {
        if (e instanceof MalformedJwtException || e instanceof SignatureException) {
            createErrorResponse("The token is incorrect", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        } else if (e instanceof ExpiredJwtException) {
            createErrorResponse("Token is expired", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        } else {
            createErrorResponse("Error processing token", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        }
    }

    private void createErrorResponse(String message, int statusCode, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private String getToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}

