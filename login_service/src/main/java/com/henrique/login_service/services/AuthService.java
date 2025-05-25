package com.henrique.login_service.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.henrique.login_service.model.Usuario;
import com.henrique.login_service.model.UsuarioRepository;
import com.henrique.login_service.services.excepitons.AuthServiceException;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> findByNome(String nome) {
        try {
            return usuarioRepository.findByNome(nome);
        } catch (Exception e) {
            throw new AuthServiceException("Erro ao buscar usuário pelo nome", e);
        }
    }

    public boolean isPasswordValid(Usuario usuario, String senha) {
        try {
            return passwordEncoder.matches(senha, usuario.getSenhaHash());
        } catch (Exception e) {
            throw new AuthServiceException("Erro ao validar senha do usuário", e);
        }
    }
}
