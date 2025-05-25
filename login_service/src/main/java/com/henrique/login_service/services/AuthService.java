package com.henrique.login_service.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.henrique.login_service.model.Usuario;
import com.henrique.login_service.model.UsuarioRepository;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> findByNome(String nome) {
        return usuarioRepository.findByNome(nome);
    }

    public boolean isPasswordValid(Usuario usuario, String senha) {
        return passwordEncoder.matches(senha, usuario.getSenhaHash());
    }
}
