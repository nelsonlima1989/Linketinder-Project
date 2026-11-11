package com.linketinder.view

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.CurtidaController
import com.linketinder.controller.EmpresaController
import com.linketinder.controller.MatchController
import com.linketinder.controller.RecrutadorController
import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.Match
import com.linketinder.model.Recrutador
import com.linketinder.model.RecrutadorResumo
import com.linketinder.model.Skill
import com.linketinder.model.Vaga
import com.linketinder.model.Empresa
import com.linketinder.service.ConflitoDadosException


import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException
import java.util.NoSuchElementException

class Menu {
    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final CurtidaController curtidaController
    private final MatchController matchController
    private final RecrutadorController recrutadorController
    private final Scanner scanner

    Menu(
            Scanner scanner,
            CandidatoController candidatoController,
            EmpresaController empresaController,
            CurtidaController curtidaController,
            MatchController matchController,
            RecrutadorController recrutadorController = null
    ) {
        this.scanner = scanner
        this.candidatoController = candidatoController
        this.empresaController = empresaController
        this.curtidaController = curtidaController
        this.matchController = matchController
        this.recrutadorController = recrutadorController
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
        println "6 - Listar recrutadores por empresa"
        println "7 - Analisar vagas e candidatos (Recrutador)"
        println "8 - Gerenciar recrutadores"
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

                case 6:
                    listarRecrutadoresPorEmpresa()
                    break

                case 7:
                    analisarVagasECandidatosComoRecrutador()
                    break

                case 8:
                    gerenciarRecrutadores()
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
           candidatos = candidatoController.listarCandidatosAnonimos()
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
            empresas = empresaController.listarEmpresasAnonimas()

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

        List<Empresa> empresas = empresaController.listarTodasEmpresas()

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
           candidatos = candidatoController.listarTodosCandidatos()
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

        curtidaController.curtirVaga(candidato, vaga)
    }



    private void curtirCandidato() {

        println "================================"
        println "       CURTIR CANDIDATO"
        println "================================"

        List<Empresa> empresas

        try {
            empresas = empresaController.listarTodasEmpresas()

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
            candidatos = candidatoController.listarTodosCandidatos()

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

        curtidaController.curtirCandidato(
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

        List<Match> matches = matchController.listarMatches()

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

    private void listarRecrutadoresPorEmpresa() {

        println "================================"
        println "   RECRUTADORES POR EMPRESA"
        println "================================"

        if (recrutadorController == null) {
            println "Funcionalidade de recrutador não configurada."
            return
        }

        List<Empresa> empresas

        try {
            empresas = empresaController.listarTodasEmpresas()
        } catch (IllegalStateException e) {
            println "Erro ao consultar empresas: ${e.message}"
            return
        }

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        println "Empresas:"
        empresas.eachWithIndex { Empresa empresa, int index ->
            println "${index + 1} - ${empresa.informacoes?.nome ?: ('Empresa ' + empresa.id)}"
        }

        int empresaOpcao = lerOpcao("Escolha a empresa: ")

        if (empresaOpcao < 1 || empresaOpcao > empresas.size()) {
            println "Empresa inválida."
            return
        }

        Empresa empresaSelecionada = empresas[empresaOpcao - 1]
        List<RecrutadorResumo> recrutadores

        try {
            recrutadores = recrutadorController.listarRecrutadoresPorEmpresa(
                    empresaSelecionada.id
            )
        } catch (IllegalStateException | IllegalArgumentException e) {
            println "Erro ao consultar recrutadores: ${e.message}"
            return
        }

        if (!recrutadores) {
            println "Nenhum recrutador cadastrado para esta empresa."
            return
        }

        recrutadores.each { RecrutadorResumo recrutador ->
            println "ID: ${recrutador.id}"
            println "Nome: ${recrutador.nome}"
            println "Área de recrutamento: ${recrutador.areaRecrutamento}"
            println "Descrição: ${recrutador.descricao}"
            println "E-mail: ${recrutador.email}"
            println "--------------------------------"
        }
    }

    private void analisarVagasECandidatosComoRecrutador() {

        println "================================"
        println "   PAINEL DO RECRUTADOR"
        println "================================"

        if (recrutadorController == null) {
            println "Funcionalidade de recrutador não configurada."
            return
        }

        List<RecrutadorResumo> recrutadores

        try {
            recrutadores = recrutadorController.listarTodosResumos()
        } catch (IllegalStateException e) {
            println "Erro ao consultar recrutadores: ${e.message}"
            return
        }

        if (!recrutadores) {
            println "Nenhum recrutador cadastrado."
            return
        }

        println "Recrutadores:"
        recrutadores.eachWithIndex { RecrutadorResumo recrutador, int index ->
            println "${index + 1} - ${recrutador.nome} (${recrutador.areaRecrutamento})"
        }

        int recrutadorOpcao = lerOpcao("Escolha o recrutador: ")

        if (recrutadorOpcao < 1 || recrutadorOpcao > recrutadores.size()) {
            println "Recrutador inválido."
            return
        }

        RecrutadorResumo recrutadorSelecionado = recrutadores[recrutadorOpcao - 1]

        List<Vaga> vagasDaEmpresa
        try {
            vagasDaEmpresa = recrutadorController.listarVagasDaEmpresaDoRecrutador(
                    recrutadorSelecionado.id
            )
        } catch (IllegalStateException | IllegalArgumentException | NoSuchElementException e) {
            println "Erro ao consultar vagas da empresa: ${e.message}"
            return
        }

        println "Vagas da empresa do recrutador:"
        if (!vagasDaEmpresa) {
            println "Nenhuma vaga cadastrada para a empresa."
        } else {
            vagasDaEmpresa.each { Vaga vaga ->
                println "ID: ${vaga.id} - ${vaga.titulo}"
                println "Descrição: ${vaga.descricaoVaga}"
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

        List<CandidatoAnonimo> candidatosAnonimos
        try {
            candidatosAnonimos = recrutadorController.listarCandidatosAnonimosParaRecrutador(
                    recrutadorSelecionado.id
            )
        } catch (IllegalStateException | IllegalArgumentException | NoSuchElementException e) {
            println "Erro ao consultar candidatos: ${e.message}"
            return
        }

        println "Candidatos disponíveis (anônimos):"
        if (!candidatosAnonimos) {
            println "Nenhum candidato cadastrado."
        } else {
            candidatosAnonimos.each { CandidatoAnonimo candidato ->
                println "ID: ${candidato.id}"
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
    }

    private void gerenciarRecrutadores() {

        if (recrutadorController == null) {
            println "Funcionalidade de recrutador não configurada."
            return
        }

        int opcao = -1

        while (opcao != 0) {
            println "================================"
            println "    GERENCIAR RECRUTADORES"
            println "================================"
            println "1 - Cadastrar recrutador"
            println "2 - Buscar recrutador por ID"
            println "3 - Atualizar recrutador"
            println "4 - Excluir recrutador"
            println "0 - Voltar"
            println "================================"

            opcao = lerOpcao("Escolha uma opção: ")

            switch (opcao) {
                case 1:
                    cadastrarRecrutadorNoMenu()
                    break
                case 2:
                    buscarRecrutadorPorIdNoMenu()
                    break
                case 3:
                    atualizarRecrutadorNoMenu()
                    break
                case 4:
                    excluirRecrutadorNoMenu()
                    break
                case 0:
                    break
                default:
                    println "Opção inválida."
            }
        }
    }

    private void cadastrarRecrutadorNoMenu() {
        int empresaId = lerOpcao("ID da empresa: ")
        String nome = lerTexto("Nome: ")
        String area = lerTexto("Área de recrutamento: ")
        String descricao = lerTexto("Descrição: ")
        String email = lerTexto("E-mail: ")
        String cpf = lerTexto("CPF (11 dígitos): ")
        String dataTexto = lerTexto("Data de nascimento (AAAA-MM-DD): ")
        String senha = lerTexto("Senha: ")

        LocalDate dataNascimento
        try {
            dataNascimento = LocalDate.parse(dataTexto)
        } catch (DateTimeParseException ignored) {
            println "Erro ao cadastrar recrutador: Data de nascimento em formato inválido. Use AAAA-MM-DD."
            return
        }

        Recrutador novoRecrutador = new Recrutador(
                nome: nome,
                areaRecrutamento: area,
                descricao: descricao,
                email: email,
                cpf: cpf,
                dataNascimento: dataNascimento,
                senha: senha
        )

        try {
            recrutadorController.cadastrarRecrutador(novoRecrutador, (Long) empresaId)
            println "Recrutador cadastrado com sucesso (ID: ${novoRecrutador.id})."
        } catch (ConflitoDadosException e) {
            println "Conflito de dados: ${e.message}"
        } catch (NoSuchElementException | IllegalArgumentException | IllegalStateException e) {
            println "Erro ao cadastrar recrutador: ${e.message}"
        }
    }

    private void buscarRecrutadorPorIdNoMenu() {
        int id = lerOpcao("ID do recrutador: ")

        try {
            RecrutadorResumo resumo = recrutadorController.buscarResumoPorId((Long) id)
            if (resumo == null) {
                println "Recrutador não encontrado."
                return
            }

            println "ID: ${resumo.id}"
            println "Empresa ID: ${resumo.empresaId}"
            println "Nome: ${resumo.nome}"
            println "Área de recrutamento: ${resumo.areaRecrutamento}"
            println "Descrição: ${resumo.descricao}"
            println "E-mail: ${resumo.email}"
            println "--------------------------------"
        } catch (IllegalArgumentException | IllegalStateException e) {
            println "Erro ao buscar recrutador: ${e.message}"
        }
    }

    private void atualizarRecrutadorNoMenu() {
        int id = lerOpcao("ID do recrutador a atualizar: ")
        String nome = lerTexto("Novo nome: ")
        String area = lerTexto("Nova área de recrutamento: ")
        String descricao = lerTexto("Nova descrição: ")
        String email = lerTexto("Novo e-mail: ")
        String cpf = lerTexto("Novo CPF (11 dígitos): ")
        String dataTexto = lerTexto("Nova data de nascimento (AAAA-MM-DD): ")
        String senha = lerTexto("Nova senha (deixe em branco para manter a atual): ")

        LocalDate dataNascimento
        try {
            dataNascimento = LocalDate.parse(dataTexto)
        } catch (DateTimeParseException ignored) {
            println "Erro ao atualizar recrutador: Data de nascimento em formato inválido. Use AAAA-MM-DD."
            return
        }

        Recrutador recrutadorAtualizado = new Recrutador(
                id: (Long) id,
                nome: nome,
                areaRecrutamento: area,
                descricao: descricao,
                email: email,
                cpf: cpf,
                dataNascimento: dataNascimento,
                senha: senha
        )

        try {
            boolean atualizado = recrutadorController.atualizarRecrutador(recrutadorAtualizado)
            if (atualizado) {
                println "Recrutador atualizado com sucesso."
            } else {
                println "Recrutador não encontrado."
            }
        } catch (ConflitoDadosException e) {
            println "Conflito de dados: ${e.message}"
        } catch (IllegalArgumentException | IllegalStateException e) {
            println "Erro ao atualizar recrutador: ${e.message}"
        }
    }

    private void excluirRecrutadorNoMenu() {
        int id = lerOpcao("ID do recrutador a excluir: ")

        try {
            boolean excluido = recrutadorController.excluirRecrutador((Long) id)
            if (excluido) {
                println "Recrutador excluído com sucesso."
            } else {
                println "Recrutador não encontrado."
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            println "Erro ao excluir recrutador: ${e.message}"
        }
    }

    private String lerTexto(String mensagem) {
        print mensagem
        return scanner.nextLine().trim()
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