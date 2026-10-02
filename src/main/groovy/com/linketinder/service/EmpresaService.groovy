package com.linketinder.service

import com.linketinder.dao.EmpresaDAO
import com.linketinder.dao.VagaCompetenciaDAO
import com.linketinder.dao.VagaDAO
import com.linketinder.model.Empresa
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.Vaga

class EmpresaService {

    private EmpresaDAO empresaDAO
    private VagaDAO vagaDAO
    private VagaCompetenciaDAO vagaCompetenciaDAO

    EmpresaService(
            EmpresaDAO empresaDAO,
            VagaDAO vagaDAO,
            VagaCompetenciaDAO vagaCompetenciaDAO
    ) {
        this.empresaDAO = empresaDAO
        this.vagaDAO = vagaDAO
        this.vagaCompetenciaDAO = vagaCompetenciaDAO
    }


    List<Empresa> listarTodasEmpresas() {

        List<Empresa> empresas = empresaDAO.listarTodos()

        List<Vaga> vagas = vagaDAO.listarTodos()

        empresas.each { empresa ->

            empresa.vagas = vagas.findAll { vaga ->
                vaga.empresa?.id == empresa.id
            }

            empresa.vagas.each { vaga ->

                vaga.skills =
                        vagaCompetenciaDAO
                                .listarCompetenciasDaVaga(vaga.id)
            }
        }

        return empresas
    }


    void cadastrarEmpresa(Empresa empresa) {

        empresaDAO.inserir(empresa)

        empresa.vagas.each { vaga ->

            vaga.empresa = empresa

            vagaDAO.inserir(vaga)

            vaga.skills.each { skill ->

                vagaCompetenciaDAO.adicionarCompetencia(
                        vaga.id,
                        skill.id
                )
            }
        }
    }


    Empresa buscarPorId(Long id) {

        Empresa empresa = empresaDAO.buscarPorId(id)

        if (empresa != null) {

            List<Vaga> vagas = vagaDAO.listarTodos()

            empresa.vagas = vagas.findAll { vaga ->
                vaga.empresa?.id == empresa.id
            }

            empresa.vagas.each { vaga ->

                vaga.skills =
                        vagaCompetenciaDAO
                                .listarCompetenciasDaVaga(vaga.id)
            }
        }

        return empresa
    }


    void atualizarEmpresa(Empresa empresa) {
        empresaDAO.atualizar(empresa)
    }


    void excluirEmpresa(Long id) {
        empresaDAO.excluir(id)
    }


    List<EmpresaAnonima> listarEmpresasAnonimas() {

        List<Empresa> empresas = listarTodasEmpresas()

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