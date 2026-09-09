package com.linketinder.service

import com.linketinder.data.DataInicializador
import com.linketinder.model.Empresa
import com.linketinder.model.EmpresaAnonima

class EmpresaService {

    private List<Empresa> empresas

    EmpresaService(DataInicializador inicializador) {
        this.empresas = inicializador.criarEmpresas()
    }

    List<Empresa> listarTodasEmpresas() {
        return empresas
    }

    void cadastrarEmpresa(Empresa empresa){
        empresas.add(empresa)
    }

    /*
    #### Futura implementação ####
    Optional<Empresa> buscarEmpresaPorNome(String nome) {
        def empresa = empresas.find {
            it.informacoes.nome.equalsIgnoreCase(nome)
        }

        return Optional.ofNullable(empresa)
    }

    #### Futura implementação ####
     */

    List<EmpresaAnonima> listarEmpresasAnonimas() {

        return empresas.collect { empresa ->

            new EmpresaAnonima(
                    id: empresa.id,
                    pais: empresa.pais,
                    estado: empresa.estado,
                    descricaoEmpresarial: empresa.descricaoEmpresarial,
                    vagas: empresa.vagas
            )
        }
    }


}