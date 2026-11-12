package com.linketinder.service

import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Candidato
import com.linketinder.model.Curtida
import com.linketinder.model.CurtidaResultadoDTO
import com.linketinder.model.Empresa
import com.linketinder.model.TipoCurtida
import com.linketinder.model.Vaga
import com.linketinder.repository.CurtidaRepository
import com.linketinder.repository.MatchRepository

import java.sql.Connection
import java.sql.SQLException
import java.time.OffsetDateTime

class CurtidaService {

    private final CurtidaRepository curtidaRepository
    private final MatchRepository matchRepository
    private final ConexaoProvider conexaoProvider

    private EmpresaService empresaService
    private MatchService matchService
    private final List<Curtida> curtidas = []

    CurtidaService(
            EmpresaService empresaService,
            MatchService matchService
    ) {
        this.empresaService = empresaService
        this.matchService = matchService
        this.curtidaRepository = null
        this.matchRepository = null
        this.conexaoProvider = null
    }

    CurtidaService(
            CurtidaRepository curtidaRepository,
            MatchRepository matchRepository,
            ConexaoProvider conexaoProvider,
            EmpresaService empresaService = null,
            MatchService matchService = null
    ) {
        this.curtidaRepository = curtidaRepository
        this.matchRepository = matchRepository
        this.conexaoProvider = conexaoProvider
        this.empresaService = empresaService
        this.matchService = matchService
    }

    CurtidaResultadoDTO curtirVaga(Long candidatoId, Long vagaId, Connection conexao = null) {
        if (candidatoId == null || candidatoId <= 0L) {
            throw new IllegalArgumentException("candidatoId deve ser um inteiro positivo.")
        }
        if (vagaId == null || vagaId <= 0L) {
            throw new IllegalArgumentException("vagaId deve ser um inteiro positivo.")
        }

        if (curtidaRepository == null || matchRepository == null) {
            throw new IllegalStateException("Repositório de persistência não configurado.")
        }

        try {
            return executarTransacional(conexao) { Connection conn ->
                OffsetDateTime dataCurtida = curtidaRepository.inserirCurtidaCandidatoVaga(candidatoId, vagaId, conn)
                Long matchId = matchRepository.buscarMatchIdPorCandidatoEVaga(candidatoId, vagaId, conn)

                return new CurtidaResultadoDTO(
                        candidatoId: candidatoId,
                        vagaId: vagaId,
                        dataCurtida: dataCurtida,
                        match: matchId != null,
                        matchId: matchId,
                        mensagem: "Curtida registrada com sucesso."
                )
            }
        } catch (IllegalStateException e) {
            tratarExcecaoPersistencia(e, "registrar curtida do candidato na vaga")
            return null
        }
    }

    CurtidaResultadoDTO curtirCandidato(Long empresaId, Long candidatoId, Long recrutadorId, Connection conexao = null) {
        if (empresaId == null || empresaId <= 0L) {
            throw new IllegalArgumentException("empresaId deve ser um inteiro positivo.")
        }
        if (candidatoId == null || candidatoId <= 0L) {
            throw new IllegalArgumentException("candidatoId deve ser um inteiro positivo.")
        }
        if (recrutadorId == null || recrutadorId <= 0L) {
            throw new IllegalArgumentException("recrutadorId deve ser um inteiro positivo.")
        }

        if (curtidaRepository == null || matchRepository == null) {
            throw new IllegalStateException("Repositório de persistência não configurado.")
        }

        try {
            return executarTransacional(conexao) { Connection conn ->
                OffsetDateTime dataCurtida = curtidaRepository.inserirCurtidaEmpresaCandidato(empresaId, candidatoId, recrutadorId, conn)
                Long matchId = matchRepository.buscarMatchIdPorEmpresaECandidato(empresaId, candidatoId, conn)

                return new CurtidaResultadoDTO(
                        empresaId: empresaId,
                        candidatoId: candidatoId,
                        recrutadorId: recrutadorId,
                        dataCurtida: dataCurtida,
                        match: matchId != null,
                        matchId: matchId,
                        mensagem: "Curtida registrada com sucesso."
                )
            }
        } catch (IllegalStateException e) {
            tratarExcecaoPersistencia(e, "registrar curtida da empresa no candidato")
            return null
        }
    }

    void curtirVaga(Candidato candidato, Vaga vaga) {
        if (curtidaRepository != null && conexaoProvider != null && candidato?.id != null && vaga?.id != null) {
            curtirVaga(candidato.id, vaga.id, null)
            return
        }

        Empresa empresa = null
        if (empresaService != null) {
            empresa = empresaService.listarTodasEmpresas().find { emp ->
                emp.vagas?.contains(vaga)
            }
        }

        Curtida curtida = new Curtida(
                candidato: candidato,
                vaga: vaga,
                empresa: empresa,
                tipoCurtida: TipoCurtida.CANDIDATO
        )
        curtidas << curtida

        if (matchService != null) {
            matchService.verificarMatch(candidato, empresa, curtidas)
        }
    }

    void curtirCandidato(Empresa empresa, Candidato candidato) {
        if (curtidaRepository != null) {
            throw new IllegalArgumentException("Recrutador responsável é obrigatório para registrar curtida da empresa.")
        }

        Curtida curtida = new Curtida(
                candidato: candidato,
                empresa: empresa,
                tipoCurtida: TipoCurtida.EMPRESA
        )
        curtidas << curtida

        if (matchService != null) {
            matchService.verificarMatch(candidato, empresa, curtidas)
        }
    }

    List<Curtida> listarCurtidas() {
        return curtidas
    }

    private <T> T executarTransacional(Connection conexaoExterna, Closure<T> operacao) {
        if (conexaoExterna != null && !conexaoExterna.isClosed()) {
            return operacao.call(conexaoExterna)
        }

        if (conexaoProvider == null) {
            throw new IllegalStateException("ConexaoProvider não configurado para transação.")
        }

        Connection conn = conexaoProvider.obterConexao()
        try {
            conn.setAutoCommit(false)
            T resultado = operacao.call(conn)
            conn.commit()
            return resultado
        } catch (Exception e) {
            try {
                conn.rollback()
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx)
            }
            throw e
        } finally {
            try {
                conn.close()
            } catch (Exception ignored) {
            }
        }
    }

    private void tratarExcecaoPersistencia(IllegalStateException e, String operacao) {
        Throwable causa = e.cause
        if (causa instanceof SQLException) {
            SQLException sqlEx = (SQLException) causa
            String sqlState = sqlEx.getSQLState()

            if ("23505" == sqlState) {
                throw new ConflitoDadosException("Curtida já registrada anteriormente.", sqlEx, sqlState)
            }

            if ("23503" == sqlState) {
                String msg = sqlEx.message ?: ""
                if (msg.contains("recrutador") || msg.contains("recrutadores")) {
                    throw new IllegalArgumentException("O recrutador informado não pertence à empresa ou não existe.", sqlEx)
                }
                throw new NoSuchElementException("Recurso não encontrado para registrar curtida.", sqlEx)
            }

            if ("23502" == sqlState) {
                throw new IllegalArgumentException("Campos obrigatórios não informados.", sqlEx)
            }

            throw new IllegalStateException("Falha ao ${operacao}: ${sqlEx.message}", sqlEx)
        }
        throw e
    }
}
