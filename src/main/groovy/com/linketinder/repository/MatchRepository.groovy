package com.linketinder.repository

import com.linketinder.model.MatchAnonimo

import java.sql.Connection

interface MatchRepository {

    Long buscarMatchIdPorEmpresaECandidato(Long empresaId, Long candidatoId)

    Long buscarMatchIdPorEmpresaECandidato(Long empresaId, Long candidatoId, Connection conexao)

    Long buscarMatchIdPorCandidatoEVaga(Long candidatoId, Long vagaId)

    Long buscarMatchIdPorCandidatoEVaga(Long candidatoId, Long vagaId, Connection conexao)

    MatchAnonimo buscarPorId(Long id)

    MatchAnonimo buscarPorId(Long id, Connection conexao)

    List<MatchAnonimo> listarMatches(Long candidatoId, Long empresaId)

    List<MatchAnonimo> listarMatches(Long candidatoId, Long empresaId, Connection conexao)
}
