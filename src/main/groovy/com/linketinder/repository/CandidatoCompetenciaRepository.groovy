package com.linketinder.repository

import com.linketinder.model.Skill

interface CandidatoCompetenciaRepository {

    void adicionarCompetencia(Long candidatoId, Long competenciaId)

    List<Skill> listarCompetenciasDoCandidato(Long candidatoId)
}