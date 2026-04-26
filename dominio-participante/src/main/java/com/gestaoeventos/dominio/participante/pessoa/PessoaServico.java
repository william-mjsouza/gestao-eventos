package com.gestaoeventos.dominio.participante.pessoa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessoaServico {

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @Transactional
    public Pessoa salvar(Pessoa pessoa) {
        if (pessoaRepositorio.existsById(pessoa.getCpf())) {
            throw new ParticipanteException("CPF já cadastrado no sistema.");
        }
        if (pessoaRepositorio.existsByEmail(pessoa.getEmail())) {
            throw new ParticipanteException("Email já cadastrado no sistema.");
        }
        if (pessoa.getSaldo() != null && pessoa.getSaldo() < 0) {
            throw new ParticipanteException("Saldo nao pode ser negativo.");
        }

        return pessoaRepositorio.save(pessoa);
    }

    @Transactional
    public void debitarSaldo(String cpf, Double valorBase, TipoPagamento tipoPagamento) {
        Pessoa participante = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new ParticipanteException("Participante não encontrado."));

        double taxa = (tipoPagamento == TipoPagamento.CARTAO) ? 0.05 : 0.0;
        double valorTotal = valorBase + (valorBase * taxa);

        if (participante.getSaldo() < valorTotal) {
            throw new ParticipanteException("Saldo insuficiente para concluir a compra.");
        }

        participante.setSaldo(participante.getSaldo() - valorTotal);
        pessoaRepositorio.save(participante);
    }

    @Transactional
    public void estornarSaldo(String cpf, Double valor) {
        Pessoa participante = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new ParticipanteException("Participante não encontrado."));

        participante.setSaldo(participante.getSaldo() + valor);
        pessoaRepositorio.save(participante);
    }
}