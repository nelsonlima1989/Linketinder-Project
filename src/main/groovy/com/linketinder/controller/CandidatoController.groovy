package com.linketinder.controller

import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.service.CandidatoService

class CandidatoController {

    private final CandidatoService candidatoService

    CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService
    }

    List<CandidatoAnonimo> listarCandidatosAnonimos() {
        return candidatoService.listarCandidatosAnonimos()
    }

    List<Candidato> listarTodosCandidatos() {
        return candidatoService.listarTodosCandidatos()
    }

}