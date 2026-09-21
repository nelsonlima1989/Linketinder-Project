import type { Candidato } from "../../models/Candidato";
import { candidatos } from "../../data/dados";
import { candidatoEstaBloqueado } from "./edicaoCandidatoService";
import { salvarDados, carregarDados } from "../utils/storage";

type DadosFormularioCandidato = {
    nome: string;
    cpf: string;
    idade: string;
    formacao: string;
    descricao: string;
    skills: string;
    estado: string;
};

const CHAVE = "candidatos";

export function listarCandidatos(): Candidato[] {
    const dadosSalvos = carregarDados<Candidato[]>(CHAVE);

    if (dadosSalvos) {
        return dadosSalvos;
    }

    salvarDados(CHAVE, candidatos);

    return candidatos;
}

export function obterProximoId(): number {

    const candidatosAtuais = listarCandidatos();

    if (candidatosAtuais.length === 0) {
        return 1;
    }

    const maiorId = Math.max(
        ...candidatosAtuais.map(candidato => candidato.id)
    );

    return maiorId + 1;
}

export function adicionarCandidato(
    candidato: Omit<Candidato, "id">
): void {

    const candidatosAtuais = listarCandidatos();

    const novoId = obterProximoId();

    const novoCandidato: Candidato = {
        id: novoId,
        ...candidato
    };

    candidatosAtuais.push(novoCandidato);

    salvarDados(CHAVE, candidatosAtuais);
}

export function obterCandidatoPorId(
    id: number
): Candidato | undefined {

    const candidatosAtuais = listarCandidatos();

    return candidatosAtuais.find(
        candidato => candidato.id === id
    );
}

export function criarCandidato(
    dados: DadosFormularioCandidato
): void {

    const novoCandidato: Omit<Candidato, "id"> = {
        nome: dados.nome,
        cpf: dados.cpf,
        idade: Number(dados.idade),
        formacao: dados.formacao,
        descricao: dados.descricao,
        skills: dados.skills
            .split(",")
            .map(skill => skill.trim())
            .filter(skill => skill !== ""),
        estado: dados.estado
    };

    adicionarCandidato(novoCandidato);
}

export function editarCandidato(
    id: number,
    dados: DadosFormularioCandidato
): void {

    const candidatosAtuais = listarCandidatos();

    const indiceCandidato =
        candidatosAtuais.findIndex(
            candidato => candidato.id === id
        );

    if (indiceCandidato === -1) {
        return;
    }

    const skills = dados.skills
        .split(",")
        .map(skill => skill.trim())
        .filter(skill => skill !== "");

    candidatosAtuais[indiceCandidato] = {
        ...candidatosAtuais[indiceCandidato],
        nome: dados.nome,
        cpf: dados.cpf,
        idade: Number(dados.idade),
        formacao: dados.formacao,
        descricao: dados.descricao,
        skills,
        estado: dados.estado
    };

    salvarDados(
        CHAVE,
        candidatosAtuais
    );
}

export function excluirCandidato(
    id: number
): boolean {

    const candidatosAtuais = listarCandidatos();

    const candidato = candidatosAtuais.find(
        candidato => candidato.id === id
    );

    if (!candidato) {
        return false;
    }

    if (candidatoEstaBloqueado(id)) {
        return false;
    }

    const novosCandidatos =
        candidatosAtuais.filter(
            candidato => candidato.id !== id
        );

    salvarDados(
        CHAVE,
        novosCandidatos
    );

    return true;
}