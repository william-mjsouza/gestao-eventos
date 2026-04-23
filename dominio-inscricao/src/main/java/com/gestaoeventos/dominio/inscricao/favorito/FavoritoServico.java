package com.gestaoeventos.dominio.inscricao.favorito;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoritoServico {
    @Autowired
    private FavoritoRepositorio favoritoRepositorio;
    @Autowired
    private PessoaRepositorio pessoaRepositorio;
    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Transactional
    public String toggleFavorito(String cpf, Long eventoId) {
        return favoritoRepositorio.findByPessoaCpfAndEventoId(cpf, eventoId)
                .map(f -> {
                    favoritoRepositorio.delete(f);
                    return "Evento removido dos favoritos.";
                })
                .orElseGet(() -> {
                    Pessoa p = pessoaRepositorio.findById(cpf).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                    Evento e = eventoRepositorio.findById(eventoId).orElseThrow(() -> new RuntimeException("Evento não encontrado"));

                    Favorito novo = new Favorito();
                    novo.setPessoa(p);
                    novo.setEvento(e);
                    favoritoRepositorio.save(novo);
                    return "Evento adicionado aos favoritos!";
                });
    }

    public List<Favorito> listarFavoritos(String cpf) {
        return favoritoRepositorio.findAllByPessoaCpf(cpf);
    }
}
