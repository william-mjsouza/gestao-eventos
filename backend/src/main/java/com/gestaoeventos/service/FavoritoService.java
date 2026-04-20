package com.gestaoeventos.service;

import com.gestaoeventos.entity.Evento;
import com.gestaoeventos.entity.Favorito;
import com.gestaoeventos.entity.Pessoa;
import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.FavoritoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class FavoritoService {
    @Autowired private FavoritoRepository favoritoRepository;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private EventoRepository eventoRepository;

    @Transactional
    public String toggleFavorito(String cpf, Long eventoId) {
        return favoritoRepository.findByPessoaCpfAndEventoId(cpf, eventoId)
                .map(f -> {
                    favoritoRepository.delete(f);
                    return "Evento removido dos favoritos.";
                })
                .orElseGet(() -> {
                    Pessoa p = pessoaRepository.findById(cpf).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                    Evento e = eventoRepository.findById(eventoId).orElseThrow(() -> new RuntimeException("Evento não encontrado"));

                    Favorito novo = new Favorito();
                    novo.setPessoa(p);
                    novo.setEvento(e);
                    favoritoRepository.save(novo);
                    return "Evento adicionado aos favoritos!";
                });
    }

    public List<Favorito> listarFavoritos(String cpf) {
        return favoritoRepository.findAllByPessoaCpf(cpf);
    }
}