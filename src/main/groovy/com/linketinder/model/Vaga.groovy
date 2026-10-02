package com.linketinder.model

class Vaga {
    Long id
    String titulo
    String descricaoVaga
    String estado
    String cidade
    Empresa empresa
    List<Skill> skills = []
}
