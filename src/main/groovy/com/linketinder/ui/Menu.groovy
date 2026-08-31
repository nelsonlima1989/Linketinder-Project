package com.linketinder.ui

import java.util.Scanner
import com.linketinder.service.EmpresaService
import com.linketinder.service.CandidatoService

class Menu {

    private CandidatoService candidatoService
    private EmpresaService empresaService
    private Scanner scanner = new Scanner(System.in)

    Menu(CandidatoService candidatoService, EmpresaService empresaService){
        this.candidatoService = candidatoService
        this.empresaService = empresaService
    }

    void iniciarPrograma() {

        int opcao = -1

        while (opcao != 0) {

            exibirOpcoes()

            print "Escolha uma opção: "
            opcao = scanner.nextInt()

            switch (opcao) {
                case 1:
                    listarCandidatos()
                    break

                case 2:
                    listarEmpresas()
                    break

                case 0:
                    println "\nEncerrando o Linketinder..."
                    break

                default:
                    println "\nOpção inválida!"
            }
        }
    }

    void exibirOpcoes(){

        println "================================"
        println "          LINKETINDER"
        println "================================"
        println "1 - Listar candidatos"
        println "2 - Listar empresas"
        println "0 - Sair"
        println "================================"
    }

    void listarCandidatos() {
        def candidatos = candidatoService.listarTodosCandidatos()

        println "\n================================"
        println "          CANDIDATOS"
        println "================================"

        candidatos.each { candidato ->
            println "\nID: ${candidato.id}"
            println "Nome: ${candidato.informacoes.nome}"
            println "Estado: ${candidato.estado}"
            println "Idade: ${candidato.idade}"

            println "Skills:"
            candidato.skills.each { skill ->
                println "- ${skill.nome}"
            }
        }
    }

    void listarEmpresas() {
        def empresas = empresaService.listarTodasEmpresas()

        println "\n================================"
        println "           EMPRESAS"
        println "================================"

        empresas.each { empresa ->
            println "\nID: ${empresa.id}"
            println "Nome: ${empresa.informacoes.nome}"
            println "País: ${empresa.pais}"
            println "Estado: ${empresa.estado}"
            println "Descrição: ${empresa.descricaoEmpresarial}"

            println "Vagas:"

            empresa.vagas.each { vaga ->
                println "- ${vaga.titulo}"
            }
        }
    }

}

