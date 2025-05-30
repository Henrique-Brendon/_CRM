package com.henrique.crm_service.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class CepInfo implements Serializable{

    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String endereco;
    private String numCasa;
}