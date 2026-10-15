package com.linketinder.repository

import com.linketinder.model.Skill

interface VagaCompetenciaRepository {

    void adicionarCompetencia(Long vagaId, Long competenciaId)

    List<Skill> listarCompetenciasDaVaga(Long vagaId)
}