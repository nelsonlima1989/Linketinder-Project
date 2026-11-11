package com.linketinder.service

import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Empresa
import com.linketinder.model.Recrutador
import com.linketinder.model.RecrutadorResumo
import com.linketinder.model.Skill
import com.linketinder.model.Vaga
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.RecrutadorRepository

import java.time.LocalDate
import java.time.Period
import java.util.NoSuchElementException
import java.util.regex.Pattern

class RecrutadorService {

    private static final Pattern PADRAO_CPF = Pattern.compile('^\\d{11}$')
    private static final Pattern PADRAO_EMAIL = Pattern.compile('^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$')
    private static final int TAMANHO_MAX_NOME = 150
    private static final int TAMANHO_MAX_AREA = 100
    private static final int TAMANHO_MAX_EMAIL = 150
    private static final int TAMANHO_MIN_SENHA = 8
    private static final int TAMANHO_MAX_SENHA = 128
    private static final int IDADE_MINIMA = 18

    private final RecrutadorRepository recrutadorRepository
    private final EmpresaRepository empresaRepository
    private final EmpresaService empresaService
    private final CandidatoService candidatoService

    RecrutadorService(
            RecrutadorRepository recrutadorRepository,
            EmpresaRepository empresaRepository,
            EmpresaService empresaService = null,
            CandidatoService candidatoService = null
    ) {
        this.recrutadorRepository = recrutadorRepository
        this.empresaRepository = empresaRepository
        this.empresaService = empresaService
        this.candidatoService = candidatoService
    }

    void cadastrarRecrutador(Recrutador recrutador, Long empresaId) {
        if (recrutador == null || empresaId == null || empresaId <= 0) {
            throw new IllegalArgumentException(
                    "Dados do recrutador ou empresa inválidos."
            )
        }

        normalizarEValidarCampos(recrutador, true)

        Empresa empresa = empresaRepository.buscarPorId(empresaId)
        if (empresa == null) {
            throw new NoSuchElementException("Empresa não encontrada.")
        }

        verificarUnicidade(recrutador.cpf, recrutador.email, null)

        recrutador.empresa = empresa
        recrutador.senha = SenhaHashUtil.gerarHash(recrutador.senha)

        recrutadorRepository.inserir(recrutador)
    }

    List<RecrutadorResumo> listarRecrutadoresPorEmpresa(Long empresaId) {
        if (empresaId == null || empresaId <= 0) {
            throw new IllegalArgumentException("Identificador da empresa inválido.")
        }

        List<Recrutador> recrutadores = recrutadorRepository.listarPorEmpresaId(empresaId)

        return recrutadores.collect { Recrutador recrutador ->
            converterParaResumo(recrutador, empresaId)
        }
    }

    List<RecrutadorResumo> listarTodosResumos() {
        List<Recrutador> recrutadores = recrutadorRepository.listarTodos()

        return recrutadores.collect { Recrutador recrutador ->
            converterParaResumo(recrutador, recrutador.empresa?.id)
        }
    }

    /**
     * Consulta segura para camadas de apresentação, controllers e respostas HTTP.
     * Retorna apenas {@link RecrutadorResumo}, omitindo senha, CPF, data de nascimento
     * e a referência completa para {@link Empresa}.
     */
    RecrutadorResumo buscarResumoPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identificador do recrutador inválido.")
        }

        Recrutador recrutador = recrutadorRepository.buscarPorId(id)
        if (recrutador == null) {
            return null
        }

        return converterParaResumo(recrutador, recrutador.empresa?.id)
    }

    /**
     * Consulta interna de domínio que retorna a entidade {@link Recrutador} completa.
     * Não deve ser exposta diretamente em views, DTOs de listagem ou respostas HTTP.
     */
    Recrutador buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identificador do recrutador inválido.")
        }

        return recrutadorRepository.buscarPorId(id)
    }

    boolean atualizarRecrutador(Recrutador recrutador) {
        if (recrutador == null || recrutador.id == null || recrutador.id <= 0) {
            throw new IllegalArgumentException("Dados do recrutador inválidos.")
        }

        Recrutador existente = recrutadorRepository.buscarPorId(recrutador.id)
        if (existente == null) {
            return false
        }

        boolean informouNovaSenha = recrutador.senha != null &&
                !recrutador.senha.trim().isEmpty()

        normalizarEValidarCampos(recrutador, informouNovaSenha)
        verificarUnicidade(recrutador.cpf, recrutador.email, recrutador.id)

        recrutador.empresa = existente.empresa

        if (informouNovaSenha) {
            recrutador.senha = SenhaHashUtil.gerarHash(recrutador.senha)
        } else {
            recrutador.senha = existente.senha
        }

        return recrutadorRepository.atualizar(recrutador)
    }

    boolean excluirRecrutador(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identificador do recrutador inválido.")
        }

        return recrutadorRepository.excluir(id)
    }

    int excluirPorEmpresa(Long empresaId) {
        if (empresaId == null || empresaId <= 0) {
            throw new IllegalArgumentException("Identificador da empresa inválido.")
        }

        return recrutadorRepository.excluirPorEmpresaId(empresaId)
    }

    List<Vaga> listarVagasDaEmpresaDoRecrutador(Long recrutadorId) {
        if (empresaService == null) {
            throw new IllegalStateException(
                    "Serviço de empresas não configurado para consulta de vagas."
            )
        }

        Recrutador recrutador = obterRecrutadorExistente(recrutadorId)
        Long empresaId = recrutador.empresa?.id

        if (empresaId == null) {
            throw new IllegalStateException(
                    "Recrutador não possui empresa vinculada."
            )
        }

        Empresa empresa = empresaService.buscarPorId(empresaId)
        List<Vaga> vagasDaEmpresa = empresa?.vagas ?: []

        return vagasDaEmpresa.collect { Vaga vaga ->
            sanitizarVagaParaApresentacao(vaga)
        }
    }

    List<CandidatoAnonimo> listarCandidatosAnonimosParaRecrutador(Long recrutadorId) {
        if (candidatoService == null) {
            throw new IllegalStateException(
                    "Serviço de candidatos não configurado para consulta anônima."
            )
        }

        obterRecrutadorExistente(recrutadorId)

        return candidatoService.listarCandidatosAnonimos()
    }

    private Recrutador obterRecrutadorExistente(Long recrutadorId) {
        if (recrutadorId == null || recrutadorId <= 0) {
            throw new IllegalArgumentException("Identificador do recrutador inválido.")
        }

        Recrutador recrutador = recrutadorRepository.buscarPorId(recrutadorId)
        if (recrutador == null) {
            throw new NoSuchElementException("Recrutador não encontrado.")
        }

        return recrutador
    }

    private void normalizarEValidarCampos(
            Recrutador recrutador,
            boolean exigirSenhaEmTextoPuro
    ) {
        String nome = recrutador.nome?.trim()
        if (!nome || nome.length() > TAMANHO_MAX_NOME) {
            throw new IllegalArgumentException("Nome do recrutador inválido.")
        }

        String area = recrutador.areaRecrutamento?.trim()
        if (!area || area.length() > TAMANHO_MAX_AREA) {
            throw new IllegalArgumentException("Área de recrutamento inválida.")
        }

        String descricao = recrutador.descricao?.trim()
        if (!descricao) {
            throw new IllegalArgumentException("Descrição do recrutador inválida.")
        }

        String email = recrutador.email?.trim()
        if (!email || email.length() > TAMANHO_MAX_EMAIL || !PADRAO_EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("E-mail do recrutador inválido.")
        }

        String cpf = recrutador.cpf?.trim()
        if (!cpf || !PADRAO_CPF.matcher(cpf).matches()) {
            throw new IllegalArgumentException(
                    "CPF inválido. Informe exatamente 11 dígitos numéricos."
            )
        }

        LocalDate nascimento = recrutador.dataNascimento
        LocalDate hoje = LocalDate.now()
        if (nascimento == null || !nascimento.isBefore(hoje) ||
                Period.between(nascimento, hoje).years < IDADE_MINIMA) {
            throw new IllegalArgumentException(
                    "Data de nascimento inválida. O recrutador deve ter pelo menos 18 anos."
            )
        }

        if (exigirSenhaEmTextoPuro) {
            String senha = recrutador.senha
            if (senha == null ||
                    senha.trim().length() < TAMANHO_MIN_SENHA ||
                    senha.length() > TAMANHO_MAX_SENHA) {
                throw new IllegalArgumentException(
                        "Senha inválida. Deve possuir entre 8 e 128 caracteres."
                )
            }
        }

        recrutador.nome = nome
        recrutador.areaRecrutamento = area
        recrutador.descricao = descricao
        recrutador.email = email
        recrutador.cpf = cpf
    }

    private void verificarUnicidade(
            String cpf,
            String email,
            Long idIgnorado
    ) {
        Recrutador existentePorCpf = recrutadorRepository.buscarPorCpf(cpf)
        if (existentePorCpf != null && existentePorCpf.id != idIgnorado) {
            throw new ConflitoDadosException(
                    "CPF já cadastrado para outro recrutador."
            )
        }

        Recrutador existentePorEmail = recrutadorRepository.buscarPorEmail(email)
        if (existentePorEmail != null && existentePorEmail.id != idIgnorado) {
            throw new ConflitoDadosException(
                    "E-mail já cadastrado para outro recrutador."
            )
        }
    }

    private static RecrutadorResumo converterParaResumo(
            Recrutador recrutador,
            Long empresaIdPadrao
    ) {
        return new RecrutadorResumo(
                id: recrutador.id,
                empresaId: recrutador.empresa?.id ?: empresaIdPadrao,
                nome: recrutador.nome,
                areaRecrutamento: recrutador.areaRecrutamento,
                descricao: recrutador.descricao,
                email: recrutador.email
        )
    }

    private static Vaga sanitizarVagaParaApresentacao(Vaga vaga) {
        return new Vaga(
                id: vaga.id,
                titulo: vaga.titulo,
                descricaoVaga: vaga.descricaoVaga,
                estado: vaga.estado,
                cidade: vaga.cidade,
                empresa: null,
                skills: vaga.skills ? new ArrayList<Skill>(vaga.skills) : []
        )
    }
}
