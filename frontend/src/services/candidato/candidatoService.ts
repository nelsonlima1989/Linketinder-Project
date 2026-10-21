import type { Candidato } from "../../models/Candidato";
import { candidatos } from "../../data/dados";
import { candidatoEstaBloqueado } from "./edicaoCandidatoService";
import { salvarDados, carregarDados } from "../utils/storage";
import { calcularProximoId } from "../utils/idUtils";
import { converterSkills } from "../utils/skillUtils";

export type DadosFormularioCandidato = {
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

  return calcularProximoId(candidatosAtuais);
}

export function adicionarCandidato(candidato: Omit<Candidato, "id">): number {
  const candidatosAtuais = listarCandidatos();

  const novoId = obterProximoId();

  const novoCandidato: Candidato = {
    id: novoId,
    ...candidato,
  };

  candidatosAtuais.push(novoCandidato);

  salvarDados(CHAVE, candidatosAtuais);

  return novoId;
}

export function obterCandidatoPorId(id: number): Candidato | undefined {
  const candidatosAtuais = listarCandidatos();

  return candidatosAtuais.find((candidato) => candidato.id === id);
}

export function criarCandidato(dados: DadosFormularioCandidato): number {
  const novoCandidato: Omit<Candidato, "id"> = {
    nome: dados.nome,
    cpf: dados.cpf,
    idade: Number(dados.idade),
    formacao: dados.formacao,
    descricao: dados.descricao,
    skills: converterSkills(dados.skills),
    estado: dados.estado,
  };

  return adicionarCandidato(novoCandidato);
}

export function editarCandidato(
  id: number,
  dados: DadosFormularioCandidato,
): void {
  const candidatosAtuais = listarCandidatos();

  const indiceCandidato = candidatosAtuais.findIndex(
    (candidato) => candidato.id === id,
  );

  if (indiceCandidato === -1) {
    return;
  }

  const skills = converterSkills(dados.skills);

  candidatosAtuais[indiceCandidato] = {
    ...candidatosAtuais[indiceCandidato],
    nome: dados.nome,
    cpf: dados.cpf,
    idade: Number(dados.idade),
    formacao: dados.formacao,
    descricao: dados.descricao,
    skills,
    estado: dados.estado,
  };

  salvarDados(CHAVE, candidatosAtuais);
}

export function excluirCandidato(id: number): boolean {
  const candidatosAtuais = listarCandidatos();

  const candidato = candidatosAtuais.find((candidato) => candidato.id === id);

  if (!candidato) {
    return false;
  }

  if (candidatoEstaBloqueado(id)) {
    return false;
  }

  const novosCandidatos = candidatosAtuais.filter(
    (candidato) => candidato.id !== id,
  );

  salvarDados(CHAVE, novosCandidatos);

  return true;
}
