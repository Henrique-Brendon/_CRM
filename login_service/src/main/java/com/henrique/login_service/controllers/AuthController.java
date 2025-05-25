package com.henrique.login_service.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.login_service.controllers.dtos.LoginDTO;
import com.henrique.login_service.model.Usuario;
import com.henrique.login_service.services.AuthService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.secret}")
    private String secret;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO creds) {
        String nome = creds.nome();
        String senha = creds.senha();

        Optional<Usuario> usuarioOpt = authService.findByNome(nome);
        if (usuarioOpt.isEmpty()) {
            Map<String, Object> body = Map.of(
                "error", "Usuário não encontrado",
                "status", HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        Usuario usuario = usuarioOpt.get();
        if (!authService.isPasswordValid(usuario, senha)) {
            Map<String, Object> body = Map.of(
                "error", "Senha incorreta",
                "status", HttpStatus.UNAUTHORIZED.value()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setSubject(nome)
                .claim("role", "user")
                .setIssuer("meu-sistema")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();

        Map<String, Object> tokenMap = Map.of(
            "token", token,
            "status", HttpStatus.OK.value()
        );

        return ResponseEntity.ok(tokenMap);
    }

    }
