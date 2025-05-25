package com.henrique.login_service.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.henrique.login_service.config.TestSecurityConfig;
import com.henrique.login_service.controllers.dtos.LoginDTO;
import com.henrique.login_service.model.Usuario;
import com.henrique.login_service.services.AuthService;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Value("${jwt.secret}")
    private String secret = "umaChaveSuperSecreta123456789012345678901234";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornarTokenQuandoCredenciaisForemValidas() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("joao");
        usuario.setSenhaHash(new BCryptPasswordEncoder().encode("123"));

        when(authService.findByNome("joao")).thenReturn(Optional.of(usuario));
        when(authService.isPasswordValid(usuario, "123")).thenReturn(true);

        LoginDTO loginDTO = new LoginDTO("joao", "123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(authService.findByNome("naoexiste")).thenReturn(Optional.empty());

        LoginDTO loginDTO = new LoginDTO("naoexiste", "123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Usuário não encontrado"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar401QuandoSenhaForInvalida() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("joao");
        usuario.setSenhaHash(new BCryptPasswordEncoder().encode("123"));

        when(authService.findByNome("joao")).thenReturn(Optional.of(usuario));
        when(authService.isPasswordValid(usuario, "senhaerrada")).thenReturn(false);

        LoginDTO loginDTO = new LoginDTO("joao", "senhaerrada");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Senha incorreta"))
            .andExpect(jsonPath("$.status").value(401));
    }
}
