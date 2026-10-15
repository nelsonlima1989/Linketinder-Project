package com.linketinder.service

import com.linketinder.repository.CandidatoCompetenciaRepository
import com.linketinder.repository.CandidatoRepository
import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo

class CandidatoService {

    private final CandidatoRepository candidatoDAO
    private final CandidatoCompetenciaRepository candidatoCompetenciaDAO

    CandidatoService(
            CandidatoRepository candidatoDAO,
            CandidatoCompetenciaRepository candidatoCompetenciaDAO
    ) {
        this.candidatoDAO = candidatoDAO
        this.candidatoCompetenciaDAO = candidatoCompetenciaDAO
    }

    List<Candidato> listarTodosCandidatos() {

        List<Candidato> candidatos = candidatoDAO.listarTodos()

        candidatos.each { candidato ->
            carregarCompetencias(candidato)
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

        if (candidato) {
            carregarCompetencias(candidato)
        }

        return candidato
    }

    boolean atualizarCandidato(Candidato candidato) {
        return candidatoDAO.atualizar(candidato)
    }

    boolean excluirCandidato(Long id) {
        return candidatoDAO.excluir(id)
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


    private void carregarCompetencias(Candidato candidato) {
        candidato.skills =
                candidatoCompetenciaDAO
                        .listarCompetenciasDoCandidato(candidato.id)
    }


}