package com.henrique.login_service.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.henrique.login_service.model.Usuario;
import com.henrique.login_service.model.UsuarioRepository;
import com.henrique.login_service.services.excepitons.AuthServiceException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void deveRetornarUsuarioQuandoNomeExistir() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNome("joao");

        when(usuarioRepository.findByNome("joao")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = authService.findByNome("joao");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("joao", resultado.get().getNome());
        verify(usuarioRepository).findByNome("joao");
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioQuandoRepositorioFalha() {
        // Arrange
        when(usuarioRepository.findByNome(anyString())).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        AuthServiceException ex = assertThrows(AuthServiceException.class, () -> {
            authService.findByNome("joao");
        });

        assertTrue(ex.getMessage().contains("Erro ao buscar usuário pelo nome"));
    }

    @Test
    void deveRetornarTrueQuandoSenhaForValida() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setSenhaHash("hashed");

        when(passwordEncoder.matches("123", "hashed")).thenReturn(true);

        // Act
        boolean resultado = authService.isPasswordValid(usuario, "123");

        // Assert
        assertTrue(resultado);
        verify(passwordEncoder).matches("123", "hashed");
    }

    @Test
    void deveRetornarFalseQuandoSenhaForInvalida() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setSenhaHash("hashed");

        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        // Act
        boolean resultado = authService.isPasswordValid(usuario, "wrong");

        // Assert
        assertFalse(resultado);
    }

    @Test
    void deveLancarExcecaoAoValidarSenhaQuandoEncoderFalha() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setSenhaHash("qualquer");

        when(passwordEncoder.matches(anyString(), anyString()))
            .thenThrow(new RuntimeException("Encoder error"));

        // Act & Assert
        AuthServiceException ex = assertThrows(AuthServiceException.class, () -> {
            authService.isPasswordValid(usuario, "senha");
        });

        assertTrue(ex.getMessage().contains("Erro ao validar senha do usuário"));
    }
}
