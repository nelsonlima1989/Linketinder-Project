import type { Candidato } from "../../models/Candidato";
import { candidatos } from "../../data/dados";
import { salvarDados, carregarDados } from "../utils/storage";

const CHAVE = "candidatos";

export function listarCandidatos(): Candidato[] {
    const dadosSalvos = carregarDados<Candidato[]>(CHAVE);

    if (dadosSalvos) {
        return dadosSalvos;
    }

    salvarDados(CHAVE, candidatos);

    return candidatos;
}

export function adicionarCandidato(candidato: Candidato): void {
    const candidatosAtuais = listarCandidatos();

    candidatosAtuais.push(candidato);

    salvarDados(CHAVE, candidatosAtuais);
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