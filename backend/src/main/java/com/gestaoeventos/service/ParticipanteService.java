package com.gestaoeventos.service;

import com.gestaoeventos.entity.Participante;
import com.gestaoeventos.exception.ParticipanteException;
import com.gestaoeventos.repository.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ParticipanteService {
    @Autowired
    private ParticipanteRepository participanteRepository;
    public Participante salvar(Participante participante){
        if(participanteRepository.existsById(participante.getCpf())){
            throw new ParticipanteException("CPF já cadastrado no sistema.");
        }
        if(participanteRepository.existsByEmail(participante.getEmail())){
            throw new ParticipanteException("Email já cadastrado no sistema.");
        }
        if(participante.getSaldo() != null && participante.getSaldo() < 0){
            throw new ParticipanteException("Saldo nao pode ser negativo.");
        }

        return participanteRepository.save(participante);

    }
}
