package com.henrique.login_service.controllers.dtos;

public record ErrorResponse(
    String error,
    String message,
    Integer status
) {
    
}
