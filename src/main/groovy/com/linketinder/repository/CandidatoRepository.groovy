package com.linketinder.repository

import com.linketinder.model.Candidato

interface CandidatoRepository {

    void inserir(Candidato candidato)

    List<Candidato> listarTodos()

    Candidato buscarPorId(Long id)

    boolean atualizar(Candidato candidato)

    boolean excluir(Long id)
}