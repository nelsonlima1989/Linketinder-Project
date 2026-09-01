package com.linketinder.service

import com.linketinder.model.Candidato
import com.linketinder.model.Curtida
import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.model.TipoCurtida

class CurtidaService {

    private List<Curtida> curtidas = []

    private EmpresaService empresaService
    private MatchService matchService

    CurtidaService(
            EmpresaService empresaService,
            MatchService matchService
    ) {
        this.empresaService = empresaService
        this.matchService = matchService
    }

    void curtirVaga(Candidato candidato, Vaga vaga) {

        def empresa = empresaService.listarTodasEmpresas().find { empresa ->
            empresa.vagas.contains(vaga)
        }

        def curtida = new Curtida(
                candidato: candidato,
                vaga: vaga,
                empresa: empresa,
                tipoCurtida: TipoCurtida.CANDIDATO
        )

        curtidas << curtida

        println "Candidato ${candidato.id} curtiu a vaga ${vaga.titulo}."

        def match = matchService.verificarMatch(
                candidato,
                empresa,
                curtidas
        )

        if (match) {
            println "================================"
            println "            MATCH!"
            println "================================"

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

            match.empresa.vagas.each { it ->
                println "- ${it.titulo}"
                println "  Descrição: ${it.descricaoVaga}"
            }

            println "================================"
        }
    }

    void curtirCandidato(Empresa empresa, Candidato candidato) {

        def curtida = new Curtida(
                candidato: candidato,
                empresa: empresa,
                tipoCurtida: TipoCurtida.EMPRESA
        )

        curtidas << curtida

        println "Empresa ${empresa.id} curtiu o candidato ${candidato.id}."

        def match = matchService.verificarMatch(
                candidato,
                empresa,
                curtidas
        )

        if (match) {
            println "================================"
            println "            MATCH!"
            println "================================"

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

            match.empresa.vagas.each { it ->
                println "- ${it.titulo}"
                println "  Descrição: ${it.descricaoVaga}"
            }

            println "================================"
        }
    }

    List<Curtida> listarCurtidas() {
        return curtidas
    }
}