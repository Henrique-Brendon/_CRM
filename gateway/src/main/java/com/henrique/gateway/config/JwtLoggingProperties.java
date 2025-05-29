package com.henrique.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.logging")
public class JwtLoggingProperties {
    private boolean debugEnabled;
    private boolean logUserInfo;

    public boolean isDebugEnabled() {
        return debugEnabled;
    }
    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
    public boolean isLogUserInfo() {
        return logUserInfo;
    }
    public void setLogUserInfo(boolean logUserInfo) {
        this.logUserInfo = logUserInfo;
    }
    
}