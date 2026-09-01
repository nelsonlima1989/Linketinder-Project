package com.linketinder.ui

import com.linketinder.service.CandidatoService
import com.linketinder.service.CurtidaService
import com.linketinder.service.EmpresaService
import com.linketinder.service.MatchService

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

        def candidatos = candidatoService.listarCandidatosAnonimos()

        candidatos.each { candidato ->

            println "ID: ${candidato.id}"
            println "Estado: ${candidato.estado}"
            println "Descrição: ${candidato.descricaoPessoal}"

            println "Skills:"

            candidato.skills.each { skill ->
                println "- ${skill.nome}"
            }

            println "--------------------------------"
        }
    }

    private void listarVagas() {

        println "================================"
        println "           VAGAS"
        println "================================"

        def empresas = empresaService.listarEmpresasAnonimas()

        empresas.each { empresa ->

            println "País: ${empresa.pais}"
            println "Estado: ${empresa.estado}"

            println "Vagas:"

            empresa.vagas.each { vaga ->
                println "${vaga.id} - ${vaga.titulo}"
                println "  Descrição: ${vaga.descricaoVaga}"
                println "  Skills:"

                vaga.skills.each { skill ->
                    println "  - ${skill.nome}"
                }
            }

            println "--------------------------------"
        }
    }

    private void curtirVaga() {

        Scanner scanner = new Scanner(System.in)

        println "================================"
        println "          CURTIR VAGA"
        println "================================"

        println "Candidatos:"

        def candidatos = candidatoService.listarTodosCandidatos()

        candidatos.eachWithIndex { candidato, index ->
            println "${index + 1} - Candidato ${candidato.id}"
        }

        print "Escolha o candidato: "
        int candidatoOpcao = scanner.nextInt()

        def candidato = candidatos[candidatoOpcao - 1]

        println "================================"
        println "            VAGAS"
        println "================================"

        def vagas = empresaService.listarTodasEmpresas()
                .collectMany { empresa ->
                    empresa.vagas.collect { vaga ->
                        [vaga: vaga, empresa: empresa]
                    }
                }

        vagas.eachWithIndex { item, index ->

            def vaga = item.vaga
            def empresa = item.empresa

            println "${index + 1} - ${vaga.titulo}"
            println "Descrição: ${vaga.descricaoVaga}"
            println "País: ${empresa.pais}"
            println "Estado: ${empresa.estado}"
            println "Skills:"

            vaga.skills.each { skill ->
                println "- ${skill.nome}"
            }


            println "--------------------------------"
        }

        print "Escolha a vaga: "
        int vagaOpcao = scanner.nextInt()

        def item = vagas[vagaOpcao - 1]

        def vaga = item.vaga

        curtidaService.curtirVaga(candidato, vaga)
    }

    private void curtirCandidato() {

        Scanner scanner = new Scanner(System.in)

        println "================================"
        println "       CURTIR CANDIDATO"
        println "================================"

        println "Empresas:"

        def empresas = empresaService.listarTodasEmpresas()

        empresas.eachWithIndex { empresa, index ->
            println "${index + 1} - Empresa ${empresa.id}"
        }

        print "Escolha a empresa: "
        int empresaOpcao = scanner.nextInt()

        def empresa = empresas[empresaOpcao - 1]

        println "================================"
        println "          CANDIDATOS"
        println "================================"

        def candidatos = candidatoService.listarTodosCandidatos()

        candidatos.eachWithIndex { candidato, index ->
            println "${index + 1} - Candidato ${candidato.id}"
            println "Estado: ${candidato.estado}"
            println "Descrição: ${candidato.descricaoPessoal}"
            println "Skills:"

            candidato.skills.each { skill ->
                println "- ${skill.nome}"
            }

            println "--------------------------------"
        }

        print "Escolha o candidato: "
        int candidatoOpcao = scanner.nextInt()

        def candidato = candidatos[candidatoOpcao - 1]

        curtidaService.curtirCandidato(empresa, candidato)
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
                println "Idade: ${match.candidato.idade}"
                println "Estado: ${match.candidato.estado}"
                println "Descrição: ${match.candidato.descricaoPessoal}"
                println "CEP: ${match.candidato.informacoes.cep}"
                println "Email: ${match.candidato.informacoes.email}"
                println "WhatsApp: ${match.candidato.informacoes.whatsapp}"

                println "Skills:"
                match.candidato.skills.each { skill ->
                    println "- ${skill.nome}"
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
                println "WhatsApp: ${match.empresa.informacoes.whatsapp}"

                println "Vagas:"

                match.empresa.vagas.each { vaga ->
                    println "- ${vaga.titulo}"
                    println "  Descrição: ${vaga.descricaoVaga}"
                }

                println "================================"
            }

        } else {
            println "Nenhum Match encontrado."
        }

        println "================================"
    }
}