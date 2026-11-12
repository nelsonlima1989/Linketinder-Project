package com.linketinder.controller

import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Recrutador
import com.linketinder.model.RecrutadorResumo
import com.linketinder.model.Vaga
import com.linketinder.service.RecrutadorService

class RecrutadorController {

    private final RecrutadorService recrutadorService

    RecrutadorController(RecrutadorService recrutadorService) {
        this.recrutadorService = recrutadorService
    }

    void cadastrarRecrutador(Recrutador recrutador, Long empresaId) {
        recrutadorService.cadastrarRecrutador(recrutador, empresaId)
    }

    List<RecrutadorResumo> listarRecrutadoresPorEmpresa(Long empresaId) {
        return recrutadorService.listarRecrutadoresPorEmpresa(empresaId)
    }

    List<RecrutadorResumo> listarTodosResumos() {
        return recrutadorService.listarTodosResumos()
    }

    RecrutadorResumo buscarResumoPorId(Long id) {
        return recrutadorService.buscarResumoPorId(id)
    }

    boolean atualizarRecrutador(Recrutador recrutador) {
        return recrutadorService.atualizarRecrutador(recrutador)
    }

    boolean excluirRecrutador(Long id) {
        return recrutadorService.excluirRecrutador(id)
    }

    List<Vaga> listarVagasDaEmpresaDoRecrutador(Long recrutadorId) {
        return recrutadorService.listarVagasDaEmpresaDoRecrutador(recrutadorId)
    }

    List<CandidatoAnonimo> listarCandidatosAnonimosParaRecrutador(Long recrutadorId) {
        return recrutadorService.listarCandidatosAnonimosParaRecrutador(recrutadorId)
    }
}
