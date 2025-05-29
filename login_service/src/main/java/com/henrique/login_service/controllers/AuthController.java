package com.henrique.login_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
@Tag(name = "Autenticação", description = "Operações de autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.secret}")
    private String secret;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Login do usuário",
        description = "Autentica o usuário e retorna um cockie JWT para sessões autenticadas",
        requestBody = @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = LoginDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Senha incorreta"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO creds, HttpServletResponse response) {
        String nome = creds.nome();
        String senha = creds.senha();

        Optional<Usuario> usuarioOpt = authService.findByNome(nome);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Usuário não encontrado",
                "status", HttpStatus.NOT_FOUND.value()
            ));
        }

        Usuario usuario = usuarioOpt.get();
        if (!authService.isPasswordValid(usuario, senha)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Senha incorreta",
                "status", HttpStatus.UNAUTHORIZED.value()
            ));
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

        String cookieValue = "jwt=" + token + "; Path=/; HttpOnly; SameSite=Lax; Max-Age=3600";
        response.setHeader("Set-Cookie", cookieValue);

        /*
        // Ambiente de produção
        String cookieValue = "jwt=" + token + "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=3600";
        response.setHeader("Set-Cookie", cookieValue);
        */

        return ResponseEntity.ok(Map.of(
            "message", "Login realizado com sucesso"
        ));
    }

}

