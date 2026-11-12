package com.linketinder.repository

import com.linketinder.model.CurtidaDTO

import java.sql.Connection
import java.time.OffsetDateTime

interface CurtidaRepository {

    OffsetDateTime inserirCurtidaCandidatoVaga(Long candidatoId, Long vagaId)

    OffsetDateTime inserirCurtidaCandidatoVaga(Long candidatoId, Long vagaId, Connection conexao)

    OffsetDateTime inserirCurtidaEmpresaCandidato(Long empresaId, Long candidatoId, Long recrutadorId)

    OffsetDateTime inserirCurtidaEmpresaCandidato(Long empresaId, Long candidatoId, Long recrutadorId, Connection conexao)

    boolean existeCurtidaCandidatoVaga(Long candidatoId, Long vagaId)

    boolean existeCurtidaCandidatoVaga(Long candidatoId, Long vagaId, Connection conexao)

    boolean existeCurtidaEmpresaCandidato(Long empresaId, Long candidatoId)

    boolean existeCurtidaEmpresaCandidato(Long empresaId, Long candidatoId, Connection conexao)

    List<CurtidaDTO> listarCurtidasCandidato(Long candidatoId)

    List<CurtidaDTO> listarCurtidasCandidato(Long candidatoId, Connection conexao)

    List<CurtidaDTO> listarCurtidasEmpresa(Long empresaId)

    List<CurtidaDTO> listarCurtidasEmpresa(Long empresaId, Connection conexao)
}
