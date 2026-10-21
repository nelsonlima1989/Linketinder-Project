import { beforeEach, describe, expect, it, vi } from "vitest";

import {
  adicionarCandidato,
  criarCandidato,
  editarCandidato,
  excluirCandidato,
  listarCandidatos,
  obterCandidatoPorId,
  obterProximoId,
} from "./candidatoService";

import { candidatoEstaBloqueado } from "./edicaoCandidatoService";
import { carregarDados, salvarDados } from "../utils/storage";

import type { Candidato } from "../../models/Candidato";

vi.mock("../utils/storage", () => ({
  carregarDados: vi.fn(),
  salvarDados: vi.fn(),
}));

vi.mock("./edicaoCandidatoService", () => ({
  candidatoEstaBloqueado: vi.fn(),
}));

const carregarDadosMock = vi.mocked(carregarDados);
const salvarDadosMock = vi.mocked(salvarDados);
const candidatoEstaBloqueadoMock = vi.mocked(candidatoEstaBloqueado);

function criarCandidatoMock(
  id: number,
  nome: string = "Candidato Teste",
): Candidato {
  return {
    id,
    nome,
    cpf: "12345678900",
    idade: 25,
    formacao: "ADS",
    descricao: "Desenvolvedor",
    skills: ["Java"],
    estado: "RJ",
  };
}

describe("candidatoService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("deve listar candidatos armazenados", () => {
    const candidatos: Candidato[] = [criarCandidatoMock(1)];

    carregarDadosMock.mockReturnValue(candidatos);

    const resultado = listarCandidatos();

    expect(resultado).toEqual(candidatos);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve retornar o proximo ID disponivel", () => {
    carregarDadosMock.mockReturnValue([
      criarCandidatoMock(2),
      criarCandidatoMock(7),
      criarCandidatoMock(4),
    ]);

    expect(obterProximoId()).toBe(8);
  });

  it("deve retornar ID 1 quando nao houver candidatos", () => {
    carregarDadosMock.mockReturnValue([]);

    expect(obterProximoId()).toBe(1);
  });

  it("deve localizar um candidato pelo ID", () => {
    const candidatos: Candidato[] = [
      criarCandidatoMock(1, "Ana"),
      criarCandidatoMock(2, "Carlos"),
    ];

    carregarDadosMock.mockReturnValue(candidatos);

    expect(obterCandidatoPorId(2)).toEqual(candidatos[1]);
  });

  it("deve retornar undefined quando o candidato nao existir", () => {
    carregarDadosMock.mockReturnValue([]);

    expect(obterCandidatoPorId(99)).toBeUndefined();
  });

  it("deve adicionar candidato com ID incremental", () => {
    carregarDadosMock.mockReturnValue([]);

    adicionarCandidato({
      nome: "Ana",
      cpf: "123",
      idade: 25,
      formacao: "ADS",
      descricao: "Desenvolvedora",
      skills: ["Java"],
      estado: "RJ",
    });

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "candidatos",
      expect.arrayContaining([
        expect.objectContaining({
          id: 1,
          nome: "Ana",
        }),
      ]),
    );
  });

  it("deve criar candidato convertendo os dados do formulario", () => {
    carregarDadosMock.mockReturnValue([]);

    criarCandidato({
      nome: "Carlos",
      cpf: "456",
      idade: "30",
      formacao: "ADS",
      descricao: "Desenvolvedor",
      skills: "Java, Groovy, ",
      estado: "RJ",
    });

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "candidatos",
      expect.arrayContaining([
        expect.objectContaining({
          id: 1,
          nome: "Carlos",
          idade: 30,
          skills: ["Java", "Groovy"],
        }),
      ]),
    );
  });

  it("deve retornar o ID ao adicionar um candidato", () => {
    carregarDadosMock.mockReturnValue([
      criarCandidatoMock(2),
      criarCandidatoMock(7),
    ]);

    const novoId = adicionarCandidato({
      nome: "Mariana",
      cpf: "789",
      idade: 28,
      formacao: "ADS",
      descricao: "Desenvolvedora Front-end",
      skills: ["React"],
      estado: "RJ",
    });

    expect(novoId).toBe(8);

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "candidatos",
      expect.arrayContaining([
        expect.objectContaining({
          id: 8,
          nome: "Mariana",
        }),
      ]),
    );
  });

  it("deve retornar o ID do candidato criado pelo formulário", () => {
    carregarDadosMock.mockReturnValue([
      criarCandidatoMock(3),
      criarCandidatoMock(5),
    ]);

    const novoId = criarCandidato({
      nome: "Carlos",
      cpf: "456",
      idade: "30",
      formacao: "ADS",
      descricao: "Desenvolvedor",
      skills: "Java, Groovy",
      estado: "RJ",
    });

    expect(novoId).toBe(6);

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "candidatos",
      expect.arrayContaining([
        expect.objectContaining({
          id: 6,
          nome: "Carlos",
        }),
      ]),
    );
  });

  it("deve editar um candidato existente", () => {
    carregarDadosMock.mockReturnValue([criarCandidatoMock(1, "Ana")]);

    editarCandidato(1, {
      nome: "Ana Maria",
      cpf: "123",
      idade: "26",
      formacao: "ADS",
      descricao: "Perfil atualizado",
      skills: "TypeScript, React",
      estado: "RJ",
    });

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "candidatos",
      expect.arrayContaining([
        expect.objectContaining({
          id: 1,
          nome: "Ana Maria",
          idade: 26,
          skills: ["TypeScript", "React"],
        }),
      ]),
    );
  });

  it("nao deve alterar candidato inexistente", () => {
    carregarDadosMock.mockReturnValue([]);

    editarCandidato(99, {
      nome: "Teste",
      cpf: "123",
      idade: "25",
      formacao: "ADS",
      descricao: "Teste",
      skills: "Java",
      estado: "RJ",
    });

    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve excluir candidato quando nao estiver bloqueado", () => {
    const candidato1 = criarCandidatoMock(1);
    const candidato2 = criarCandidatoMock(2);

    carregarDadosMock.mockReturnValue([candidato1, candidato2]);

    candidatoEstaBloqueadoMock.mockReturnValue(false);

    const resultado = excluirCandidato(1);

    expect(resultado).toBe(true);

    expect(salvarDadosMock).toHaveBeenCalledWith("candidatos", [candidato2]);
  });

  it("nao deve excluir candidato bloqueado", () => {
    carregarDadosMock.mockReturnValue([criarCandidatoMock(1)]);

    candidatoEstaBloqueadoMock.mockReturnValue(true);

    const resultado = excluirCandidato(1);

    expect(resultado).toBe(false);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve retornar false ao excluir candidato inexistente", () => {
    carregarDadosMock.mockReturnValue([]);

    const resultado = excluirCandidato(99);

    expect(resultado).toBe(false);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });
});
