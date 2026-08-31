package com.linketinder.service

import com.linketinder.data.DataInicializador
import com.linketinder.model.Empresa

class EmpresaService {

    private List<Empresa> empresas

    EmpresaService(DataInicializador inicializador) {
        this.empresas = inicializador.criarEmpresas()
    }

    List<Empresa> listarTodasEmpresas() {
        return empresas
    }

    Optional<Empresa> buscarEmpresaPorNome(String nome) {
        def empresa = empresas.find {
            it.informacoes.nome.equalsIgnoreCase(nome)
        }

        return Optional.ofNullable(empresa)
    }
}