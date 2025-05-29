package com.henrique.login_service.controllers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para login do usuário")
public record LoginDTO(
    @Schema(description = "Nome de usuário", example = "joao") String nome,
    @Schema(description = "Senha do usuário", example = "senha123") String senha
) {}