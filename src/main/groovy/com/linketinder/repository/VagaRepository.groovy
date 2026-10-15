package com.linketinder.repository

import com.linketinder.model.Vaga

interface VagaRepository {

    void inserir(Vaga vaga)

    List<Vaga> listarTodos()

    Vaga buscarPorId(Long id)

    boolean atualizar(Vaga vaga)

    boolean excluir(Long id)
}