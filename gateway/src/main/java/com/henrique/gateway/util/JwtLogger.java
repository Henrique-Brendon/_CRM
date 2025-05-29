package com.henrique.gateway.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.henrique.gateway.config.JwtLoggingProperties;

@Component
public class JwtLogger {

    private static final Logger logger = LoggerFactory.getLogger(JwtLogger.class);

    private final JwtLoggingProperties properties;

    public JwtLogger(JwtLoggingProperties properties) {
        this.properties = properties;
    }

    public void logPath(String path) {
        if (properties.isDebugEnabled()) {
            logger.debug("[JwtFilter] Interceptando path: {}", path);
        }
    }

    public void logBypass(String path) {
        if (properties.isDebugEnabled()) {
            logger.debug("[JwtFilter] Liberando acesso ao path {}", path);
        }
    }

    public void logMissingHeader() {
        logger.warn("[JwtFilter] Header Authorization inválido ou ausente");
    }

    public void logTokenExtracted(String token) {
        if (properties.isDebugEnabled()) {
            logger.debug("[JwtFilter] Token JWT extraído (parcial): {}...", token.substring(0, Math.min(10, token.length())));
        }
    }

    public void logTokenValid(String username, String role) {
        if (properties.isLogUserInfo()) {
            logger.info("[JwtFilter] Token válido! X-User: {}, X-Role: {}", username, role);
        } else {
            logger.info("[JwtFilter] Token válido!");
        }
    }

    public void logInvalidToken(String message) {
        logger.warn("[JwtFilter] Token inválido: {}", message);
    }
}
