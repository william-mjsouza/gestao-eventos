package com.gestaoeventos.dominio.participante.pessoa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public class Pessoa {

    @CPF(message = "O CPF informado é inválido")
    private String cpf;

    @NotBlank(message = "O nome não pode ficar vazio")
    private String nome;

    @Email(message = "O formato do email é inválido")
    @NotBlank(message = "O Email é obrigatório")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    private Double saldo = 0.0;

    private Boolean organizador = false;

    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    private Long versao;

    public Pessoa() {}

    public Pessoa(String cpf, String nome, String email, String senha,
                  Double saldo, Boolean organizador, LocalDate dataNascimento) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.saldo = saldo;
        this.organizador = organizador;
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }

    public Boolean getOrganizador() { return organizador; }
    public void setOrganizador(Boolean organizador) { this.organizador = organizador; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}
