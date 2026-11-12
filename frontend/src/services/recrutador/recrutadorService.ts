import type {
    Recrutador,
    DadosFormularioRecrutador,
} from "../../models/Recrutador";
import { recrutadores } from "../../data/dados";
import { salvarDados, carregarDados } from "../utils/storage";
import { calcularProximoId } from "../utils/idUtils";
import { obterEmpresaPorId } from "../empresa/empresaService";
import { recrutadorEstaBloqueado } from "./edicaoRecrutadorService";

const CHAVE = "recrutadores";

const PADRAO_CPF = /^\d{11}$/;
const PADRAO_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PADRAO_DATA_ISO = /^(\d{4})-(\d{2})-(\d{2})$/;

const TAMANHO_MAX_NOME = 150;
const TAMANHO_MAX_AREA = 100;
const TAMANHO_MAX_EMAIL = 150;
const TAMANHO_MIN_SENHA = 8;
const TAMANHO_MAX_SENHA = 128;
const IDADE_MINIMA = 18;

export type OpcoesValidacaoRecrutador = {
    empresaId: number;
    recrutadorIdIgnorado?: number;
    exigirSenha?: boolean;
};

function sanitizarRecrutador(recrutador: Recrutador): Recrutador {
    return {
        id: recrutador.id,
        empresaId: recrutador.empresaId,
        nome: recrutador.nome,
        areaRecrutamento: recrutador.areaRecrutamento,
        descricao: recrutador.descricao,
        email: recrutador.email,
        cpf: recrutador.cpf,
        dataNascimento: recrutador.dataNascimento,
    };
}

function validarDataNascimentoMaiorIdade(
    dataIso: string,
    dataReferencia: Date = new Date(),
): boolean {
    const correspondencia = PADRAO_DATA_ISO.exec(dataIso);

    if (!correspondencia) {
        return false;
    }

    const ano = Number(correspondencia[1]);
    const mes = Number(correspondencia[2]);
    const dia = Number(correspondencia[3]);

    const dataUTC = new Date(Date.UTC(ano, mes - 1, dia));

    if (
        dataUTC.getUTCFullYear() !== ano ||
        dataUTC.getUTCMonth() !== mes - 1 ||
        dataUTC.getUTCDate() !== dia
    ) {
        return false;
    }

    const anoAtual = dataReferencia.getFullYear();
    const mesAtual = dataReferencia.getMonth() + 1;
    const diaAtual = dataReferencia.getDate();

    const hojeUTC = new Date(Date.UTC(anoAtual, mesAtual - 1, diaAtual));

    if (dataUTC.getTime() >= hojeUTC.getTime()) {
        return false;
    }

    let idade = anoAtual - ano;

    if (mesAtual < mes || (mesAtual === mes && diaAtual < dia)) {
        idade -= 1;
    }

    return idade >= IDADE_MINIMA;
}

export function listarRecrutadores(): Recrutador[] {
    const dadosSalvos = carregarDados<Recrutador[]>(CHAVE);

    if (dadosSalvos) {
        return dadosSalvos.map(sanitizarRecrutador);
    }

    const copiaInicial = recrutadores.map(sanitizarRecrutador);

    salvarDados(CHAVE, copiaInicial);

    return copiaInicial;
}

export function obterProximoId(): number {
    const recrutadoresAtuais = listarRecrutadores();

    return calcularProximoId(recrutadoresAtuais);
}

export function obterRecrutadoresPorEmpresa(empresaId: number): Recrutador[] {
    const recrutadoresAtuais = listarRecrutadores();

    return recrutadoresAtuais.filter(
        (recrutador) => recrutador.empresaId === empresaId,
    );
}

export function obterRecrutadorPorId(
    id: number,
    empresaId?: number,
): Recrutador | undefined {
    const recrutadoresAtuais = listarRecrutadores();

    return recrutadoresAtuais.find(
        (recrutador) =>
            recrutador.id === id &&
            (empresaId === undefined || recrutador.empresaId === empresaId),
    );
}

export function validarDadosRecrutador(
    dados: DadosFormularioRecrutador,
    opcoes: OpcoesValidacaoRecrutador,
): Omit<Recrutador, "id"> {
    const {
        empresaId,
        recrutadorIdIgnorado,
        exigirSenha = true,
    } = opcoes;

    if (!Number.isInteger(empresaId) || empresaId <= 0) {
        throw new Error("Identificador da empresa inválido.");
    }

    const empresa = obterEmpresaPorId(empresaId);

    if (!empresa) {
        throw new Error("Empresa não encontrada.");
    }

    const nome = (dados.nome ?? "").trim();

    if (!nome || nome.length > TAMANHO_MAX_NOME) {
        throw new Error("Nome do recrutador inválido.");
    }

    const areaRecrutamento = (dados.areaRecrutamento ?? "").trim();

    if (!areaRecrutamento || areaRecrutamento.length > TAMANHO_MAX_AREA) {
        throw new Error("Área de recrutamento inválida.");
    }

    const descricao = (dados.descricao ?? "").trim();

    if (!descricao) {
        throw new Error("Descrição do recrutador inválida.");
    }

    const email = (dados.email ?? "").trim();

    if (
        !email ||
        email.length > TAMANHO_MAX_EMAIL ||
        !PADRAO_EMAIL.test(email)
    ) {
        throw new Error("E-mail do recrutador inválido.");
    }

    const cpf = (dados.cpf ?? "").trim();

    if (!PADRAO_CPF.test(cpf)) {
        throw new Error(
            "CPF inválido. Informe exatamente 11 dígitos numéricos.",
        );
    }

    const dataNascimento = (dados.dataNascimento ?? "").trim();

    if (!validarDataNascimentoMaiorIdade(dataNascimento)) {
        throw new Error(
            "Data de nascimento inválida. O recrutador deve ter pelo menos 18 anos.",
        );
    }

    const senhaInformada = dados.senha ?? "";
    const possuiSenhaNaoVazia = senhaInformada.trim().length > 0;

    if (exigirSenha || possuiSenhaNaoVazia) {
        if (
            senhaInformada.trim().length < TAMANHO_MIN_SENHA ||
            senhaInformada.length > TAMANHO_MAX_SENHA
        ) {
            throw new Error(
                "Senha inválida. Deve possuir entre 8 e 128 caracteres.",
            );
        }
    }

    const recrutadoresAtuais = listarRecrutadores();

    const conflitoCpf = recrutadoresAtuais.some(
        (recrutador) =>
            recrutador.id !== recrutadorIdIgnorado &&
            recrutador.cpf === cpf,
    );

    if (conflitoCpf) {
        throw new Error("CPF já cadastrado para outro recrutador.");
    }

    const emailMinusculo = email.toLowerCase();

    const conflitoEmail = recrutadoresAtuais.some(
        (recrutador) =>
            recrutador.id !== recrutadorIdIgnorado &&
            recrutador.email.toLowerCase() === emailMinusculo,
    );

    if (conflitoEmail) {
        throw new Error("E-mail já cadastrado para outro recrutador.");
    }

    return {
        empresaId,
        nome,
        areaRecrutamento,
        descricao,
        email,
        cpf,
        dataNascimento,
    };
}

export function adicionarRecrutador(
    recrutador: Omit<Recrutador, "id">,
): number {
    const recrutadoresAtuais = listarRecrutadores();

    const novoId = obterProximoId();

    const novoRecrutador: Recrutador = sanitizarRecrutador({
        id: novoId,
        ...recrutador,
    });

    recrutadoresAtuais.push(novoRecrutador);

    salvarDados(CHAVE, recrutadoresAtuais);

    return novoId;
}

export function criarRecrutador(
    dados: DadosFormularioRecrutador,
    empresaId: number,
): number {
    const dadosValidados = validarDadosRecrutador(dados, {
        empresaId,
        exigirSenha: true,
    });

    return adicionarRecrutador(dadosValidados);
}

export function editarRecrutador(
    id: number,
    empresaId: number,
    dados: DadosFormularioRecrutador,
): boolean {
    const recrutadoresAtuais = listarRecrutadores();

    const indice = recrutadoresAtuais.findIndex(
        (recrutador) =>
            recrutador.id === id && recrutador.empresaId === empresaId,
    );

    if (indice === -1) {
        return false;
    }

    const registroOriginal = recrutadoresAtuais[indice];

    const dadosValidados = validarDadosRecrutador(dados, {
        empresaId: registroOriginal.empresaId,
        recrutadorIdIgnorado: id,
        exigirSenha: false,
    });

    recrutadoresAtuais[indice] = sanitizarRecrutador({
        id: registroOriginal.id,
        empresaId: registroOriginal.empresaId,
        nome: dadosValidados.nome,
        areaRecrutamento: dadosValidados.areaRecrutamento,
        descricao: dadosValidados.descricao,
        email: dadosValidados.email,
        cpf: dadosValidados.cpf,
        dataNascimento: dadosValidados.dataNascimento,
    });

    salvarDados(CHAVE, recrutadoresAtuais);

    return true;
}

export function excluirRecrutador(
    id: number,
    empresaId: number,
): boolean {
    if (recrutadorEstaBloqueado(id)) {
        return false;
    }

    const recrutadoresAtuais = listarRecrutadores();

    const recrutador = recrutadoresAtuais.find(
        (item) => item.id === id && item.empresaId === empresaId,
    );

    if (!recrutador) {
        return false;
    }

    const recrutadoresRestantes = recrutadoresAtuais.filter(
        (item) => item.id !== id,
    );

    salvarDados(CHAVE, recrutadoresRestantes);

    return true;
}

export function excluirRecrutadoresPorEmpresa(empresaId: number): void {
    const recrutadoresAtuais = listarRecrutadores();

    const recrutadoresRestantes = recrutadoresAtuais.filter(
        (recrutador) => recrutador.empresaId !== empresaId,
    );

    salvarDados(CHAVE, recrutadoresRestantes);
}
