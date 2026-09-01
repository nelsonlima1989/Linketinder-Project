package com.linketinder.model

class Vaga {
    Long id
    String titulo
    String descricaoVaga
    Empresa empresa
    List<Skill> skills = []
}
