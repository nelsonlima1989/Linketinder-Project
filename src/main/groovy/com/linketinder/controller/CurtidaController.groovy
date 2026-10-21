package com.linketinder.controller

import com.linketinder.model.Candidato
import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.service.CurtidaService

class CurtidaController {

    private final CurtidaService curtidaService

    CurtidaController(CurtidaService curtidaService) {
        this.curtidaService = curtidaService
    }

    void curtirVaga(Candidato candidato, Vaga vaga) {
        curtidaService.curtirVaga(candidato, vaga)
    }

    void curtirCandidato(Empresa empresa, Candidato candidato) {
        curtidaService.curtirCandidato(empresa, candidato)
    }
}