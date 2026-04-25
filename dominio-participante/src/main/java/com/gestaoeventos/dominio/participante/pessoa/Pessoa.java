package com.gestaoeventos.dominio.participante.pessoa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PESSOA")
public class Pessoa {

    @Id
    @Column(length = 11)
    @CPF(message = "O CPF informado é inválido")
    private String cpf;

    @Column(nullable = false)
    @NotBlank(message = "O nome não pode ficar vazio")
    private String nome;

    @Column(nullable = false, unique = true)
    @Email(message = "O formato do email é inválido")
    @NotBlank(message = "O Email é obrigatório")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    @Column(nullable = false)
    private Double saldo = 0.0;

    @Column(nullable = false)
    private Boolean organizador = false;

    @Column(nullable = false)
    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate dataNascimento;
}
