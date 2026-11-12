package com.linketinder.service

import com.linketinder.model.Candidato
import com.linketinder.model.Curtida
import com.linketinder.model.Empresa
import com.linketinder.model.Match
import com.linketinder.model.MatchAnonimo
import com.linketinder.model.TipoCurtida
import com.linketinder.repository.MatchRepository

import java.sql.Connection
import java.sql.SQLException

class MatchService {

    private final MatchRepository matchRepository
    private final List<Match> matches = []

    MatchService() {
        this.matchRepository = null
    }

    MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository
    }

    List<MatchAnonimo> listarMatches(Long candidatoId, Long empresaId, Connection conexao) {
        if (matchRepository == null) {
            return []
        }
        try {
            return matchRepository.listarMatches(candidatoId, empresaId, conexao)
        } catch (IllegalStateException e) {
            tratarExcecaoPersistencia(e, "listar matches")
            return []
        }
    }

    List<MatchAnonimo> listarMatches(Long candidatoId, Long empresaId) {
        return listarMatches(candidatoId, empresaId, null)
    }

    List listarMatches() {
        if (matchRepository != null) {
            return matchRepository.listarMatches(null, null, null)
        }
        return matches
    }

    MatchAnonimo buscarPorId(Long id, Connection conexao = null) {
        if (id == null || id <= 0L) {
            throw new IllegalArgumentException("ID do match deve ser um inteiro positivo.")
        }
        if (matchRepository == null) {
            return null
        }
        try {
            return matchRepository.buscarPorId(id, conexao)
        } catch (IllegalStateException e) {
            tratarExcecaoPersistencia(e, "buscar match por ID")
            return null
        }
    }

    Long buscarMatchIdPorEmpresaECandidato(Long empresaId, Long candidatoId, Connection conexao = null) {
        if (matchRepository == null) {
            return null
        }
        try {
            return matchRepository.buscarMatchIdPorEmpresaECandidato(empresaId, candidatoId, conexao)
        } catch (IllegalStateException e) {
            tratarExcecaoPersistencia(e, "buscar match por empresa e candidato")
            return null
        }
    }

    Long buscarMatchIdPorCandidatoEVaga(Long candidatoId, Long vagaId, Connection conexao = null) {
        if (matchRepository == null) {
            return null
        }
        try {
            return matchRepository.buscarMatchIdPorCandidatoEVaga(candidatoId, vagaId, conexao)
        } catch (IllegalStateException e) {
            tratarExcecaoPersistencia(e, "buscar match por candidato e vaga")
            return null
        }
    }

    Match verificarMatch(
            Candidato candidato,
            Empresa empresa,
            List<Curtida> curtidas
    ) {
        def curtidaCandidato = curtidas.find { curtida ->
            curtida.candidato == candidato &&
                    curtida.empresa == empresa &&
                    curtida.tipoCurtida == TipoCurtida.CANDIDATO
        }

        def curtidaEmpresa = curtidas.find { curtida ->
            curtida.candidato == candidato &&
                    curtida.empresa == empresa &&
                    curtida.tipoCurtida == TipoCurtida.EMPRESA
        }

        if (curtidaCandidato && curtidaEmpresa) {
            def matchExistente = matches.find { match ->
                match.candidato == candidato &&
                        match.empresa == empresa
            }

            if (matchExistente) {
                return matchExistente
            }

            def match = new Match(
                    candidato: candidato,
                    empresa: empresa
            )
            matches << match
            return match
        }

        return null
    }

    private void tratarExcecaoPersistencia(IllegalStateException e, String operacao) {
        Throwable causa = e.cause
        if (causa instanceof SQLException) {
            SQLException sqlEx = (SQLException) causa
            throw new IllegalStateException("Falha ao ${operacao}: ${sqlEx.message}", sqlEx)
        }
        throw e
    }
}
