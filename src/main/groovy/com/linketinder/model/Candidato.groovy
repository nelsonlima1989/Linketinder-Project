package com.linketinder.model

import java.time.LocalDate

class Candidato extends Pessoa {
    String cpf
    LocalDate dataNascimento
    String descricaoPessoal
    String senha
    List<Skill> skills = []
}