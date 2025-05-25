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

    public Optional<Usuario> authenticate(String nome, String senha) {
        Optional<Usuario> usuarioOpt =  usuarioRepository.findByNome(nome);
        if(usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if(passwordEncoder.matches(senha, usuario.getSenhaHash())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }
}
