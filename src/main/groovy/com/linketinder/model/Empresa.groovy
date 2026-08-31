package com.linketinder.model

class Empresa {
    Long id
    String cnpj
    String pais
    String estado
    String descricaoEmpresarial
    Informacoes informacoes
    List<Vaga> vagas = []
}
