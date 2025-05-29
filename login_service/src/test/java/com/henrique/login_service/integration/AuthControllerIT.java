package com.henrique.login_service.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.henrique.login_service.config.TestSecurityConfig;
import com.henrique.login_service.controllers.dtos.LoginDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@Sql(scripts = "/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/remove_usuario.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("it")
public class AuthControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<Map> postLogin(LoginDTO loginDTO) {
        return restTemplate.postForEntity("/auth/login", new HttpEntity<>(loginDTO), Map.class);
    }

    @Test
    void login_ComCredenciaisValidas_DeveRetornarToken() {
        LoginDTO loginDTO = new LoginDTO("joao", "123");

        ResponseEntity<Map> response = postLogin(loginDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
        assertThat(response.getBody()).containsEntry("status", 200);
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() {
        LoginDTO loginDTO = new LoginDTO("usuarioInexistente", "senhaQualquer");

        ResponseEntity<Map> response = postLogin(loginDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("error", "Usuário não encontrado");
        assertThat(response.getBody()).containsEntry("status", 404);
    }

    @Test
    void deveRetornar401QuandoSenhaForInvalida() {
        LoginDTO loginDTO = new LoginDTO("joao", "senhaErrada");

        ResponseEntity<Map> response = postLogin(loginDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("error", "Senha incorreta");
        assertThat(response.getBody()).containsEntry("status", 401);
    }
}
