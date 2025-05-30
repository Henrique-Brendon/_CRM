package com.henrique.gateway.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.henrique.gateway.util.JwtLogger;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

@Component
@Order(1)
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String secret;

    private final JwtLogger jwtLogger;

    public JwtAuthenticationFilter(JwtLogger jwtLogger) {
        this.jwtLogger = jwtLogger;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        jwtLogger.logPath(path);

        if (path.startsWith("/auth")) {
            jwtLogger.logBypass(path);
            return chain.filter(exchange);
        }

        String token = null;

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            jwtLogger.logTokenExtracted(token);
        } else {
            HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");
            if (cookie != null) {
                token = cookie.getValue();
                jwtLogger.logTokenExtracted(token);
            }
        }

        if (token == null) {
            jwtLogger.logMissingToken();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            var claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            jwtLogger.logTokenValid(username, role);

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(builder -> builder
                            .headers(httpHeaders -> {
                                httpHeaders.set("X-User", username);
                                httpHeaders.set("X-Role", role);
                            })
                    ).build();

            return chain.filter(modifiedExchange);

        } catch (JwtException e) {
            jwtLogger.logInvalidToken(e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}