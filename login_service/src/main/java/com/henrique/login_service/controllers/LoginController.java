package com.henrique.login_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login/hello")
    public String hello() {
        return "<H1>Hello world!</H1>";
    }
}