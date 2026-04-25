package com.gestaoeventos.bdd.config;

import com.gestaoeventos.dominio.evento.lote.LoteServico;
import com.gestaoeventos.dominio.inscricao.carrinho.CarrinhoRepositorio;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.inscricao.TestApplication;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoRepositorio;
import com.gestaoeventos.dominio.inscricao.favorito.FavoritoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(classes = TestApplication.class)
public class CucumberSpringConfiguration {

    @MockitoBean
    private PessoaRepositorio pessoaRepositorio;

    @MockitoBean
    private EventoRepositorio eventoRepositorio;

    @MockitoBean
    private CarrinhoRepositorio carrinhoRepositorio;

    @MockitoBean
    private LoteServico loteServico;

    @MockitoBean
    private InscricaoRepositorio inscricaoRepositorio;

    @MockitoBean
    private FavoritoRepositorio favoritoRepositorio;

    @MockitoBean
    private AvaliacaoRepositorio avaliacaoRepositorio;

    @MockitoBean
    private ListaEsperaRepositorio listaEsperaRepositorio;
}
