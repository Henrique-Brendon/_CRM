package com.henrique.crm_service.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.henrique.crm_service.controllers.dtos.CepDTO;
import com.henrique.crm_service.services.exceptions.CepFormatoInvalidoException;

@Service
public class CepService {
    
    public CepDTO verificarEndereco(String endereco) {
        if (endereco == null) {
            throw new NullPointerException("O objeto não pode estar nulo.");
        }
    
        return parse(endereco);
    }

    private CepDTO parse(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            
            return new CepDTO(
                jsonObject.getString("cep"),
                jsonObject.getString("uf"),
                jsonObject.getString("localidade"),
                jsonObject.getString("bairro"),
                jsonObject.getString("logradouro")
            );

        } catch (JSONException e) {
            throw new CepFormatoInvalidoException("JSON inválido ou campos obrigatórios ausentes.");
        }
    }

}