package com.linketinder.ui

import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.service.CandidatoService
import com.linketinder.service.CurtidaService
import com.linketinder.service.EmpresaService
import com.linketinder.service.MatchService

import java.time.LocalDate
import java.time.Period

class Menu {

    private CandidatoService candidatoService
    private EmpresaService empresaService
    private CurtidaService curtidaService
    private MatchService matchService

    Menu(
            CandidatoService candidatoService,
            EmpresaService empresaService,
            CurtidaService curtidaService,
            MatchService matchService
    ) {
        this.candidatoService = candidatoService
        this.empresaService = empresaService
        this.curtidaService = curtidaService
        this.matchService = matchService
    }

    void iniciarPrograma() {

        Scanner scanner = new Scanner(System.in)

        int opcao = -1

        while (opcao != 0) {

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
            print "Escolha uma opção: "

            opcao = scanner.nextInt()

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

        def empresas = empresaService.listarEmpresasAnonimas()

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        empresas.each { empresa ->

            println "País: ${empresa.pais}"
            println "Estado: ${empresa.estado}"

            println "Vagas:"

            if (empresa.vagas) {

                empresa.vagas.each { vaga ->

                    println "${vaga.id} - ${vaga.titulo}"
                    println "  Descrição: ${vaga.descricaoVaga}"
                    println "  Estado: ${vaga.estado}"
                    println "  Cidade: ${vaga.cidade}"

                    println "  Skills:"

                    if (vaga.skills) {
                        vaga.skills.each { skill ->
                            println "  - ${skill.nome}"
                        }
                    } else {
                        println "  - Nenhuma competência cadastrada"
                    }
                }

            } else {
                println "- Nenhuma vaga cadastrada"
            }

            println "--------------------------------"
        }
    }

    private void curtirVaga() {

        Scanner scanner = new Scanner(System.in)

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

        print "Escolha o candidato: "
        int candidatoOpcao = scanner.nextInt()

        if (candidatoOpcao < 1 || candidatoOpcao > candidatos.size()) {
            println "Candidato inválido."
            return
        }

        def candidato = candidatos[candidatoOpcao - 1]

        println "================================"
        println "            VAGAS"
        println "================================"

        def vagas = empresaService.listarTodasEmpresas()
                .collectMany { empresa ->

                    empresa.vagas.collect { vaga ->
                        [
                                vaga   : vaga,
                                empresa: empresa
                        ]
                    }
                }

        if (!vagas) {
            println "Nenhuma vaga cadastrada."
            return
        }

        vagas.eachWithIndex { item, index ->

            def vaga = item.vaga
            def empresa = item.empresa

            println "${index + 1} - ${vaga.titulo}"
            println "Descrição: ${vaga.descricaoVaga}"
            println "Empresa: ${empresa.informacoes.nome}"
            println "País: ${empresa.pais}"
            println "Estado: ${vaga.estado}"
            println "Cidade: ${vaga.cidade}"
            println "Skills:"

            if (vaga.skills) {
                vaga.skills.each { skill ->
                    println "- ${skill.nome}"
                }
            } else {
                println "- Nenhuma competência cadastrada"
            }

            println "--------------------------------"
        }

        print "Escolha a vaga: "
        int vagaOpcao = scanner.nextInt()

        if (vagaOpcao < 1 || vagaOpcao > vagas.size()) {
            println "Vaga inválida."
            return
        }

        def item = vagas[vagaOpcao - 1]
        def vaga = item.vaga

        curtidaService.curtirVaga(candidato, vaga)
    }

    private void curtirCandidato() {

        Scanner scanner = new Scanner(System.in)

        println "================================"
        println "       CURTIR CANDIDATO"
        println "================================"

        def empresas = empresaService.listarTodasEmpresas()

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        println "Empresas:"

        empresas.eachWithIndex { empresa, index ->
            println "${index + 1} - ${empresa.informacoes.nome}"
        }

        print "Escolha a empresa: "
        int empresaOpcao = scanner.nextInt()

        if (empresaOpcao < 1 || empresaOpcao > empresas.size()) {
            println "Empresa inválida."
            return
        }

        def empresa = empresas[empresaOpcao - 1]

        println "================================"
        println "          CANDIDATOS"
        println "================================"

        def candidatos = candidatoService.listarTodosCandidatos()

        if (!candidatos) {
            println "Nenhum candidato cadastrado."
            return
        }

        candidatos.eachWithIndex { candidato, index ->

            println "${index + 1} - Candidato ${candidato.id}"
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

        print "Escolha o candidato: "
        int candidatoOpcao = scanner.nextInt()

        if (candidatoOpcao < 1 || candidatoOpcao > candidatos.size()) {
            println "Candidato inválido."
            return
        }

        def candidato = candidatos[candidatoOpcao - 1]

        curtidaService.curtirCandidato(
                empresa,
                candidato
        )
    }

    private void listarMatches() {

        println "================================"
        println "            MATCHES"
        println "================================"

        def matches = matchService.listarMatches()

        if (matches) {

            matches.each { match ->

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
                    match.candidato.skills.each { skill ->
                        println "- ${skill.nome}"
                    }
                } else {
                    println "- Nenhuma competência cadastrada"
                }

                println "--------------------------------"

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

                    match.empresa.vagas.each { vaga ->

                        println "- ${vaga.titulo}"
                        println "  Descrição: ${vaga.descricaoVaga}"
                    }

                } else {
                    println "- Nenhuma vaga cadastrada"
                }

                println "================================"
            }

        } else {
            println "Nenhum Match encontrado."
        }

        println "================================"
    }
}