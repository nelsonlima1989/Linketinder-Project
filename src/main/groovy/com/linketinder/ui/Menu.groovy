package com.linketinder.ui

import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.Match
import com.linketinder.model.Skill
import com.linketinder.model.Vaga
import com.linketinder.service.CandidatoService
import com.linketinder.service.CurtidaService
import com.linketinder.service.EmpresaService
import com.linketinder.model.Empresa
import com.linketinder.service.MatchService

import java.time.LocalDate
import java.time.Period

class Menu {

    private final CandidatoService candidatoService
    private final EmpresaService empresaService
    private final CurtidaService curtidaService
    private final MatchService matchService
    private final Scanner scanner

    Menu(
            CandidatoService candidatoService,
            EmpresaService empresaService,
            CurtidaService curtidaService,
            MatchService matchService,
            Scanner scanner
    ) {
        this.candidatoService = candidatoService
        this.empresaService = empresaService
        this.curtidaService = curtidaService
        this.matchService = matchService
        this.scanner = scanner
    }

    private void exibirMenuPrincipal() {

        println "================================"
        println "          LINKETINDER"
        println "================================"
        println "1 - Listar candidatos"
        println "2 - Listar vagas"
        println "3 - Curtir vaga"
        println "4 - Curtir candidato"
        println "5 - Listar matches"
        println "0 - Sair"
        println "================================"
    }

    void iniciarPrograma() {

        int opcao = -1

        while (opcao != 0) {

            exibirMenuPrincipal()

            opcao = lerOpcao("Escolha uma opção: ")

            switch (opcao) {

                case 1:
                    listarCandidatos()
                    break

                case 2:
                    listarVagas()
                    break

                case 3:
                    curtirVaga()
                    break

                case 4:
                    curtirCandidato()
                    break

                case 5:
                    listarMatches()
                    break

                case 0:
                    println "Encerrando o Linketinder..."
                    break

                default:
                    println "Opção inválida."
            }
        }
    }

    private void listarCandidatos() {

        println "================================"
        println "          CANDIDATOS"
        println "================================"

        List<CandidatoAnonimo> candidatos

        try {
           candidatos = candidatoService.listarCandidatosAnonimos()
        } catch (IllegalStateException e) {
            println "Erro ao consultar candidatos: ${e.message}"
            return
        }

        if (candidatos.isEmpty()) {
            println "Nenhum candidato cadastrado."
            return
        }

        candidatos.each { candidato ->

            println "ID: ${candidato.id}"
            println "Estado: ${candidato.estado}"
            println "Descrição: ${candidato.descricaoPessoal}"

            println "Skills:"

            if (candidato.skills) {
                candidato.skills.each { skill ->
                    println "- ${skill.nome}"
                }
            } else {
                println "- Nenhuma competência cadastrada"
            }

            println "--------------------------------"
        }
    }

    private void listarVagas() {

        println "================================"
        println "           VAGAS"
        println "================================"

        List<EmpresaAnonima> empresas

        try {
            empresas = empresaService.listarEmpresasAnonimas()

        } catch (IllegalStateException e) {
            println "Erro ao consultar vagas: ${e.message}"
            return
        }

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        empresas.each { EmpresaAnonima empresa ->

            println "Empresa:"
            println "  País: ${empresa.pais}"
            println "  Estado: ${empresa.estado}"
            println ""

            println "Vagas:"

            if (empresa.vagas) {

                empresa.vagas.each { Vaga vaga ->

                    println "  ${vaga.id} - ${vaga.titulo}"
                    println "    Descrição: ${vaga.descricaoVaga}"
                    println "    Estado: ${vaga.estado}"
                    println "    Cidade: ${vaga.cidade}"
                    println "    Skills:"

                    if (vaga.skills) {
                        vaga.skills.each { Skill skill ->
                            println "      - ${skill.nome}"
                        }
                    } else {
                        println "      - Nenhuma competência cadastrada"
                    }

                    println ""
                }

            } else {
                println "  Nenhuma vaga cadastrada"
            }

            println "--------------------------------"
        }
    }

    private void exibirCandidatosParaSelecao(List<Candidato> candidatos) {

        candidatos.eachWithIndex { Candidato candidato, int index ->

            println "${index + 1} - Candidato ${candidato.id}"
            println "Estado: ${candidato.estado}"
            println "Descrição: ${candidato.descricaoPessoal}"
            println "Skills:"

            if (candidato.skills) {
                candidato.skills.each { Skill skill ->
                    println "- ${skill.nome}"
                }
            } else {
                println "- Nenhuma competência cadastrada"
            }

            println "--------------------------------"
        }
    }


    private void exibirVagasParaSelecao(List<Map<String, Object>> vagas) {

        vagas.eachWithIndex { Map<String, Object> item, int index ->

            Vaga vaga = (Vaga) item.vaga
            Empresa empresa = (Empresa) item.empresa

            println "${index + 1} - ${vaga.titulo}"
            println "Descrição: ${vaga.descricaoVaga}"
            println "Empresa: ${empresa.informacoes.nome}"
            println "País: ${empresa.pais}"
            println "Estado: ${vaga.estado}"
            println "Cidade: ${vaga.cidade}"
            println "Skills:"

            if (vaga.skills) {
                vaga.skills.each { Skill skill ->
                    println "- ${skill.nome}"
                }
            } else {
                println "- Nenhuma competência cadastrada"
            }

            println "--------------------------------"
        }
    }


    private List<Map<String, Object>> obterVagasParaSelecao() {

        List<Empresa> empresas = empresaService.listarTodasEmpresas()

        return empresas.collectMany { Empresa empresa ->

            List<Map<String, Object>> vagasDaEmpresa = empresa.vagas.collect { Vaga vaga ->

                Map<String, Object> item = [
                        vaga: vaga,
                        empresa: empresa
                ]

                return item
            }

            return vagasDaEmpresa
        }
    }

    private void curtirVaga() {

        println "================================"
        println "          CURTIR VAGA"
        println "================================"

        List<Candidato> candidatos

        try {
           candidatos = candidatoService.listarTodosCandidatos()
        } catch (IllegalStateException e) {
            println "Erro ao consultar candidatos: ${e.message}"
            return
        }

        if (candidatos.isEmpty()) {
            println "Nenhum candidato cadastrado."
            return
        }

        println "Candidatos:"

        candidatos.eachWithIndex { candidato, index ->
            println "${index + 1} - Candidato ${candidato.id}"
        }

        int candidatoOpcao = lerOpcao("Escolha o candidato: ")

        if (candidatoOpcao < 1 || candidatoOpcao > candidatos.size()) {
            println "Candidato inválido."
            return
        }

        Candidato candidato = candidatos[candidatoOpcao - 1]

        println "================================"
        println "            VAGAS"
        println "================================"

        List<Map<String, Object>> vagas

        try {
            vagas = obterVagasParaSelecao()

        } catch (IllegalStateException e) {
            println "Erro ao consultar vagas: ${e.message}"
            return
        }

        if (!vagas) {
            println "Nenhuma vaga cadastrada."
            return
        }

        exibirVagasParaSelecao(vagas)

        int vagaOpcao = lerOpcao("Escolha a vaga: ")

        if (vagaOpcao < 1 || vagaOpcao > vagas.size()) {
            println "Vaga inválida."
            return
        }

        Map<String, Object> item = vagas[vagaOpcao - 1]

        Vaga vaga = (Vaga) item.vaga

        curtidaService.curtirVaga(candidato, vaga)
    }



    private void curtirCandidato() {

        println "================================"
        println "       CURTIR CANDIDATO"
        println "================================"

        List<Empresa> empresas

        try {
            empresas = empresaService.listarTodasEmpresas()

        } catch (IllegalStateException e) {
            println "Erro ao consultar empresas: ${e.message}"
            return
        }

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        println "Empresas:"

        empresas.eachWithIndex { empresa, index ->
            println "${index + 1} - ${empresa.informacoes.nome}"
        }

        int empresaOpcao = lerOpcao("Escolha a empresa: ")

        if (empresaOpcao < 1 || empresaOpcao > empresas.size()) {
            println "Empresa inválida."
            return
        }

        Empresa empresa = empresas[empresaOpcao - 1]

        println "================================"
        println "          CANDIDATOS"
        println "================================"

        List<Candidato> candidatos

        try {
            candidatos = candidatoService.listarTodosCandidatos()

        } catch (IllegalStateException e) {
            println "Erro ao consultar candidatos: ${e.message}"
            return
        }

        if (!candidatos) {
            println "Nenhum candidato cadastrado."
            return
        }

        exibirCandidatosParaSelecao(candidatos)

        int candidatoOpcao = lerOpcao("Escolha o candidato: ")

        if (candidatoOpcao < 1 || candidatoOpcao > candidatos.size()) {
            println "Candidato inválido."
            return
        }

        Candidato candidato = candidatos[candidatoOpcao - 1]

        curtidaService.curtirCandidato(
                empresa,
                candidato
        )
    }


    private void exibirCandidatoDoMatch(Match match) {

        println "Candidato:"
        println "Nome: ${match.candidato.informacoes.nome}"
        println "CPF: ${match.candidato.cpf}"

        if (match.candidato.dataNascimento != null) {

            int idade = Period.between(
                    match.candidato.dataNascimento,
                    LocalDate.now()
            ).years

            println "Idade: ${idade}"
        }

        println "Estado: ${match.candidato.estado}"
        println "Descrição: ${match.candidato.descricaoPessoal}"
        println "CEP: ${match.candidato.informacoes.cep}"
        println "Email: ${match.candidato.informacoes.email}"

        println "Skills:"

        if (match.candidato.skills) {
            match.candidato.skills.each { Skill skill ->
                println "- ${skill.nome}"
            }
        } else {
            println "- Nenhuma competência cadastrada"
        }
    }

    private void exibirEmpresaDoMatch(Match match) {

        println "Empresa:"
        println "Nome: ${match.empresa.informacoes.nome}"
        println "CNPJ: ${match.empresa.cnpj}"
        println "País: ${match.empresa.pais}"
        println "Estado: ${match.empresa.estado}"
        println "Descrição: ${match.empresa.descricaoEmpresarial}"
        println "CEP: ${match.empresa.informacoes.cep}"
        println "Email: ${match.empresa.informacoes.email}"

        println "Vagas:"

        if (match.empresa.vagas) {

            match.empresa.vagas.each { Vaga vaga ->
                println "- ${vaga.titulo}"
                println "  Descrição: ${vaga.descricaoVaga}"
            }

        } else {
            println "- Nenhuma vaga cadastrada"
        }
    }

    private void listarMatches() {

        println "================================"
        println "            MATCHES"
        println "================================"

        List<Match> matches = matchService.listarMatches()

        if (!matches) {
            println "Nenhum Match encontrado."
            println "================================"
            return
        }

        matches.each { Match match ->

            exibirCandidatoDoMatch(match)

            println "--------------------------------"

            exibirEmpresaDoMatch(match)

            println "================================"
        }

        println "================================"
    }


    private int lerOpcao(String mensagem) {

        while (true) {
            print mensagem

            String entrada = scanner.nextLine().trim()

            if (entrada ==~ /\d+/) {
                try {
                    return Integer.parseInt(entrada)
                } catch (NumberFormatException e) {
                    // O valor excede o limite de Integer
                }
            }

            println "Opção inválida. Digite um número válido."
        }
    }



}