package com.linketinder.service


import com.linketinder.model.Empresa
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.Vaga
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.VagaCompetenciaRepository
import com.linketinder.repository.VagaRepository

class EmpresaService {

    private final EmpresaRepository empresaDAO
    private final VagaRepository vagaDAO
    private final VagaCompetenciaRepository vagaCompetenciaDAO

    EmpresaService(
            EmpresaRepository empresaDAO,
            VagaRepository vagaDAO,
            VagaCompetenciaRepository vagaCompetenciaDAO
    ) {
        this.empresaDAO = empresaDAO
        this.vagaDAO = vagaDAO
        this.vagaCompetenciaDAO = vagaCompetenciaDAO
    }


    List<Empresa> listarTodasEmpresas() {

        List<Empresa> empresas = empresaDAO.listarTodos()
        List<Vaga> vagas = vagaDAO.listarTodos()

        empresas.each { Empresa empresa ->
            carregarVagasECompetencias(empresa, vagas)
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

        if (empresa) {
            List<Vaga> vagas = vagaDAO.listarTodos()
            carregarVagasECompetencias(empresa, vagas)
        }

        return empresa
    }


    boolean atualizarEmpresa(Empresa empresa) {
       return empresaDAO.atualizar(empresa)
    }


    boolean excluirEmpresa(Long id) {
       return empresaDAO.excluir(id)
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


    private void carregarVagasECompetencias(
            Empresa empresa,
            List<Vaga> vagas
    ) {
        empresa.vagas = vagas.findAll { Vaga vaga ->
            vaga.empresa?.id == empresa.id
        }

        empresa.vagas.each { Vaga vaga ->
            vaga.skills =
                    vagaCompetenciaDAO
                            .listarCompetenciasDaVaga(vaga.id)
        }
    }


}