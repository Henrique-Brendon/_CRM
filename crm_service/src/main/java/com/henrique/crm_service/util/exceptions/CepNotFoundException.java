package com.henrique.crm_service.util.exceptions;

public class CepNotFoundException extends RuntimeException{
    public CepNotFoundException(String cep) {
        super("CEP não encontrado: " + cep);
    }
}