package com.linketinder.service

import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.dao.CandidatoDAO
import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo

class CandidatoService {

    private CandidatoDAO candidatoDAO
    private CandidatoCompetenciaDAO candidatoCompetenciaDAO

    CandidatoService(
            CandidatoDAO candidatoDAO,
            CandidatoCompetenciaDAO candidatoCompetenciaDAO
    ) {
        this.candidatoDAO = candidatoDAO
        this.candidatoCompetenciaDAO = candidatoCompetenciaDAO
    }

    List<Candidato> listarTodosCandidatos() {

        List<Candidato> candidatos = candidatoDAO.listarTodos()

        candidatos.each { candidato ->
            candidato.skills =
                    candidatoCompetenciaDAO
                            .listarCompetenciasDoCandidato(candidato.id)
        }

        return candidatos
    }

    void cadastrarCandidato(Candidato candidato) {

        candidatoDAO.inserir(candidato)

        candidato.skills.each { skill ->
            candidatoCompetenciaDAO.adicionarCompetencia(
                    candidato.id,
                    skill.id
            )
        }
    }

    Candidato buscarPorId(Long id) {

        Candidato candidato = candidatoDAO.buscarPorId(id)

        if (candidato != null) {
            candidato.skills =
                    candidatoCompetenciaDAO
                            .listarCompetenciasDoCandidato(candidato.id)
        }

        return candidato
    }

    void atualizarCandidato(Candidato candidato) {
        candidatoDAO.atualizar(candidato)
    }

    void excluirCandidato(Long id) {
        candidatoDAO.excluir(id)
    }

    List<CandidatoAnonimo> listarCandidatosAnonimos() {

        List<Candidato> candidatos = listarTodosCandidatos()

        return candidatos.collect { candidato ->

            new CandidatoAnonimo(
                    id: candidato.id,
                    estado: candidato.estado,
                    descricaoPessoal: candidato.descricaoPessoal,
                    skills: candidato.skills
            )
        }
    }
}