package com.linketinder.repository

import com.linketinder.model.Skill

interface CompetenciaRepository {

    void inserir(Skill skill)

    List<Skill> listarTodos()

    Skill buscarPorId(Long id)

    boolean atualizar(Skill skill)

    boolean excluir(Long id)
}