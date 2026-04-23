package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.inscricao.favorito.Favorito;
import com.gestaoeventos.dominio.inscricao.favorito.FavoritoRepositorio;
import com.gestaoeventos.dominio.inscricao.favorito.FavoritoServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FavoritoSteps {

    @Autowired
    private FavoritoServico favoritoServico;

    @Autowired
    private FavoritoRepositorio favoritoRepositorio;
    @Autowired
    private PessoaRepositorio pessoaRepositorio;
    @Autowired
    private EventoRepositorio eventoRepositorio;

    private Pessoa usuario;
    private Evento evento;
    private Favorito favoritoExistente;
    private String mensagemRetorno;
    private List<Favorito> listaFavoritosRetornada;

    @Dado("que o usuário está navegando logado na plataforma")
    public void usuario_navegando_logado() {
        if (this.usuario == null) {
            this.usuario = new Pessoa();
            this.usuario.setCpf("12345678900");
        }
        when(pessoaRepositorio.findById(this.usuario.getCpf())).thenReturn(Optional.of(this.usuario));
    }

    @Quando("ele clica no ícone de favoritar em um evento")
    public void clica_icone_favoritar_evento() {
        if (this.evento == null) {
            this.evento = new Evento();
            this.evento.setId(10L);
        }
        when(eventoRepositorio.findById(this.evento.getId())).thenReturn(Optional.of(this.evento));

        when(favoritoRepositorio.findByPessoaCpfAndEventoId(this.usuario.getCpf(), this.evento.getId()))
                .thenReturn(Optional.empty());

        mensagemRetorno = favoritoServico.toggleFavorito(this.usuario.getCpf(), this.evento.getId());
    }

    @Entao("o evento é salvo em sua área de {string}")
    public void evento_salvo_na_area(String area) {
        verify(favoritoRepositorio, times(1)).save(any(Favorito.class));
        assertEquals("Meus Favoritos", area);
    }

    @Dado("que o evento já consta na lista de favoritos do usuário")
    public void evento_ja_consta_nos_favoritos() {
        if (this.usuario == null) {
            usuario_navegando_logado();
        }
        if (this.evento == null) {
            this.evento = new Evento();
            this.evento.setId(10L);
            when(eventoRepositorio.findById(this.evento.getId())).thenReturn(Optional.of(this.evento));
        }

        this.favoritoExistente = new Favorito();
        this.favoritoExistente.setId(1L);
        this.favoritoExistente.setPessoa(this.usuario);
        this.favoritoExistente.setEvento(this.evento);
        this.favoritoExistente.setDataFavoritado(LocalDateTime.now());

        when(favoritoRepositorio.findByPessoaCpfAndEventoId(this.usuario.getCpf(), this.evento.getId()))
                .thenReturn(Optional.of(this.favoritoExistente));
    }

    @Quando("ele clica novamente no ícone de favoritar")
    public void clica_novamente_no_icone() {
        mensagemRetorno = favoritoServico.toggleFavorito(this.usuario.getCpf(), this.evento.getId());
    }

    @Entao("o evento deve ser removido de sua lista")
    public void evento_removido_da_lista() {
        verify(favoritoRepositorio, times(1)).delete(this.favoritoExistente);
    }

    @Dado("que um usuário válido existe")
    public void usuario_valido_existe() {
        usuario_navegando_logado();
    }

    @E("o usuário possui eventos favoritados")
    public void usuario_possui_eventos_favoritados() {
        evento_ja_consta_nos_favoritos();
        when(favoritoRepositorio.findAllByPessoaCpf(this.usuario.getCpf()))
                .thenReturn(List.of(this.favoritoExistente));
    }

    @Quando("o usuário solicita a lista de favoritos")
    public void usuario_solicita_lista() {
        listaFavoritosRetornada = favoritoServico.listarFavoritos(this.usuario.getCpf());
    }

    @Entao("o sistema deve retornar a lista contendo os eventos")
    public void sistema_retorna_lista_eventos() {
        assertNotNull(listaFavoritosRetornada);
        assertFalse(listaFavoritosRetornada.isEmpty());
        assertEquals(1, listaFavoritosRetornada.size());
        assertEquals(this.evento.getId(), listaFavoritosRetornada.get(0).getEvento().getId());
    }

    @E("retornar a mensagem {string}")
    public void retornar_mensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, mensagemRetorno);
    }
}
