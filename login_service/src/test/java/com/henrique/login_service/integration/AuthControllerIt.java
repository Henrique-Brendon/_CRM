package com.henrique.login_service.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.henrique.login_service.controllers.dtos.LoginDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIt {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornarTokenQuandoCredenciaisForemValidas() throws Exception {
        // Criar payload válido
        LoginDTO loginDTO = new LoginDTO("joao", "123");

        String url = "http://localhost:" + port + "/auth/login";

        // Executar requisição POST
        ResponseEntity<Map> response = restTemplate.postForEntity(url, loginDTO, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("token"));
        assertEquals(200, response.getBody().get("status"));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() {
        LoginDTO loginDTO = new LoginDTO("usuarioInexistente", "senhaQualquer");

        String url = "http://localhost:" + port + "/auth/login";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, loginDTO, Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuário não encontrado", response.getBody().get("error"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void deveRetornar401QuandoSenhaForInvalida() {
        LoginDTO loginDTO = new LoginDTO("joao", "senhaErrada");

        String url = "http://localhost:" + port + "/auth/login";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, loginDTO, Map.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Senha incorreta", response.getBody().get("error"));
        assertEquals(401, response.getBody().get("status"));
    }
}