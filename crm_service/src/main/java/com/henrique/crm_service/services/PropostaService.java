package com.henrique.crm_service.services;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.henrique.crm_service.controllers.dtos.PropostaDTO;
import com.henrique.crm_service.entities.Proposta;
import com.henrique.crm_service.repositories.ClienteRepository;
import com.henrique.crm_service.repositories.PropostaRepository;
import com.henrique.crm_service.repositories.VendedorRepository;

@Service
public class PropostaService {

    private final PropostaRepository propostaRepository;
    private final ClienteRepository clienteRepository;
    private final VendedorRepository vendedorRepository;

    public PropostaService(
            PropostaRepository propostaRepository,
            ClienteRepository clienteRepository,
            VendedorRepository vendedorRepository) {
        this.propostaRepository = propostaRepository;
        this.clienteRepository = clienteRepository;
        this.vendedorRepository = vendedorRepository;
    }
    public Page<Proposta> findAll(Pageable pageable) {
        return propostaRepository.findAll(pageable);
    }

    public Page<PropostaDTO> getAllPropostas(Pageable pageable) {
        return propostaRepository.findAll(pageable)
                .map(PropostaDTO::fromProposta);
    }

}