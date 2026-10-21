package com.linketinder.controller

import com.linketinder.model.Empresa
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.Vaga
import com.linketinder.service.EmpresaService

class EmpresaController {

    private final EmpresaService empresaService

    EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService
    }

    List<EmpresaAnonima> listarEmpresasAnonimas() {
        return empresaService.listarEmpresasAnonimas()
    }

    List<Empresa> listarTodasEmpresas() {
        return empresaService.listarTodasEmpresas()
    }

}