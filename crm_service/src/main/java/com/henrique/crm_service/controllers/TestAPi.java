package com.henrique.crm_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAPi {

    @GetMapping("/teste")
    public ResponseEntity<String> teste(
            @RequestHeader(value = "X-User", required = false) String user,
            @RequestHeader(value = "X-Role", required = false) String role
    ) {
        return ResponseEntity.ok("Hello, " + (user != null ? user : "Anonymous") + "! Role: " + (role != null ? role : "N/A"));
    }

}
