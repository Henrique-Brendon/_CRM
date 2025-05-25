package com.henrique.login_service.controllers.excepitons;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.henrique.login_service.controllers.dtos.ErrorResponse;
import com.henrique.login_service.services.excepitons.AuthServiceException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthServiceException(AuthServiceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            "Erro interno de autenticação",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}