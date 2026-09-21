import type { Vaga } from "../../models/Vaga";
import { vagaEstaBloqueada } from "./edicaoVagaService";

import { vagas } from "../../data/dados";

import {
    salvarDados,
    carregarDados
} from "../utils/storage";

const CHAVE = "vagas";

type DadosFormularioVaga = {
    titulo: string;
    descricao: string;
    skills: string;
};

export function listarVagas(): Vaga[] {

    const dadosSalvos =
        carregarDados<Vaga[]>(CHAVE);

    if (dadosSalvos) {
        return dadosSalvos;
    }

    salvarDados(CHAVE, vagas);

    return vagas;
}

export function obterProximoId(): number {

    const vagasAtuais = listarVagas();

    if (vagasAtuais.length === 0) {
        return 1;
    }

    const maiorId = Math.max(
        ...vagasAtuais.map(vaga => vaga.id)
    );

    return maiorId + 1;
}

export function adicionarVaga(
    vaga: Omit<Vaga, "id">
): void {

    const vagasAtuais = listarVagas();

    const novoId = obterProximoId();

    const novaVaga: Vaga = {
        id: novoId,
        ...vaga
    };

    vagasAtuais.push(novaVaga);

    salvarDados(CHAVE, vagasAtuais);
}

export function criarVaga(
    dados: DadosFormularioVaga,
    empresaId: number
): void {

    const novaVaga: Omit<Vaga, "id"> = {
        titulo: dados.titulo,
        descricao: dados.descricao,
        skills: dados.skills
            .split(",")
            .map(skill => skill.trim())
            .filter(skill => skill !== ""),
        empresaId: empresaId
    };

    adicionarVaga(novaVaga);
}

export function obterVagasPorEmpresa(
    empresaId: number
): Vaga[] {

    const vagasAtuais = listarVagas();

    return vagasAtuais.filter(
        vaga => vaga.empresaId === empresaId
    );
}

export function excluirVaga(
    id: number,
    empresaId: number
): boolean {

    const vagasAtuais = listarVagas();

    const vaga = vagasAtuais.find(
        vaga => vaga.id === id
    );

    if (!vaga) {
        return false;
    }

    if (vaga.empresaId !== empresaId) {
        return false;
    }

    if (vagaEstaBloqueada(id)) {
        return false;
    }

    const novasVagas = vagasAtuais.filter(
        vaga => vaga.id !== id
    );

    salvarDados(CHAVE, novasVagas);

    return true;
}

export function editarVaga(
    id: number,
    empresaId: number,
    dados: DadosFormularioVaga
): void {

    const vagasAtuais = listarVagas();

    const indiceVaga =
        vagasAtuais.findIndex(
            vaga =>
                vaga.id === id &&
                vaga.empresaId === empresaId
        );

    if (indiceVaga === -1) {
        return;
    }

    const skills = dados.skills
        .split(",")
        .map(skill => skill.trim())
        .filter(skill => skill !== "");

    vagasAtuais[indiceVaga] = {
        ...vagasAtuais[indiceVaga],
        titulo: dados.titulo,
        descricao: dados.descricao,
        skills: skills
    };

    salvarDados(CHAVE, vagasAtuais);
}

export function obterVagaPorId(
    id: number,
    empresaId: number
): Vaga | undefined {

    const vagasAtuais = listarVagas();

    return vagasAtuais.find(
        vaga =>
            vaga.id === id &&
            vaga.empresaId === empresaId
    );
}

export function excluirVagasPorEmpresa(empresaId: number): void {
    const vagasAtuais = listarVagas();

    const novasVagas = vagasAtuais.filter(
        vaga => vaga.empresaId !== empresaId
    );

    salvarDados(CHAVE, novasVagas);
}