import { beforeEach, describe, expect, it, vi } from "vitest";

import {
  listarVagas,
  obterProximoId,
  obterVagasPorEmpresa,
  obterVagaPorId,
  adicionarVaga,
  criarVaga,
  editarVaga,
  excluirVaga,
  excluirVagasPorEmpresa,
} from "./vagaService";

import { carregarDados, salvarDados } from "../utils/storage";
import type { Vaga } from "../../models/Vaga";

import { vagaEstaBloqueada } from "./edicaoVagaService";

vi.mock("../utils/storage", () => ({
  carregarDados: vi.fn(),
  salvarDados: vi.fn(),
}));

vi.mock("./edicaoVagaService", () => ({
  vagaEstaBloqueada: vi.fn(),
}));

const carregarDadosMock = vi.mocked(carregarDados);
const salvarDadosMock = vi.mocked(salvarDados);

const vagaEstaBloqueadaMock = vi.mocked(vagaEstaBloqueada);

function criarVagaMock(id: number, empresaId: number = 1): Vaga {
  return {
    id,
    empresaId,
    titulo: "Desenvolvedor Java",
    descricao: "Desenvolvimento de aplicações",
    skills: ["Java", "SQL"],
  };
}

describe("vagaService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("deve listar vagas armazenadas", () => {
    const vagas = [criarVagaMock(1)];

    carregarDadosMock.mockReturnValue(vagas);

    expect(listarVagas()).toEqual(vagas);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve retornar ID 1 quando nao houver vagas", () => {
    carregarDadosMock.mockReturnValue([]);

    expect(obterProximoId()).toBe(1);
  });

  it("deve calcular o proximo ID disponivel", () => {
    carregarDadosMock.mockReturnValue([
      criarVagaMock(2),
      criarVagaMock(8),
      criarVagaMock(5),
    ]);

    expect(obterProximoId()).toBe(9);
  });

  it("deve listar apenas vagas de uma empresa", () => {
    const vagaEmpresa1 = criarVagaMock(1, 1);
    const vagaEmpresa2 = criarVagaMock(2, 2);

    carregarDadosMock.mockReturnValue([vagaEmpresa1, vagaEmpresa2]);

    expect(obterVagasPorEmpresa(1)).toEqual([vagaEmpresa1]);
  });

  it("deve localizar vaga pelo ID e empresa", () => {
    const vaga = criarVagaMock(1, 2);

    carregarDadosMock.mockReturnValue([vaga]);

    expect(obterVagaPorId(1, 2)).toEqual(vaga);
  });

  it("nao deve localizar vaga pertencente a outra empresa", () => {
    carregarDadosMock.mockReturnValue([criarVagaMock(1, 2)]);

    expect(obterVagaPorId(1, 1)).toBeUndefined();
  });

  it("deve adicionar vaga com ID incremental", () => {
    carregarDadosMock.mockReturnValue([]);

    adicionarVaga({
      empresaId: 1,
      titulo: "Desenvolvedor",
      descricao: "Desenvolvimento de sistemas",
      skills: ["Java"],
    });

    expect(salvarDadosMock).toHaveBeenCalledWith("vagas", [
      expect.objectContaining({
        id: 1,
        empresaId: 1,
        titulo: "Desenvolvedor",
      }),
    ]);
  });

  it("deve criar vaga convertendo as skills do formulário", () => {
    carregarDadosMock.mockReturnValue([]);

    criarVaga(
      {
        titulo: "Desenvolvedor Full Stack",
        descricao: "Desenvolvimento web",
        skills: " Java, TypeScript, SQL ",
      },
      2,
    );

    expect(salvarDadosMock).toHaveBeenCalledWith("vagas", [
      expect.objectContaining({
        id: 1,
        empresaId: 2,
        skills: ["Java", "TypeScript", "SQL"],
      }),
    ]);
  });

  it("deve editar vaga pertencente à empresa", () => {
    carregarDadosMock.mockReturnValue([criarVagaMock(1, 2)]);

    editarVaga(1, 2, {
      titulo: "Desenvolvedor Atualizado",
      descricao: "Nova descrição",
      skills: "React, Node.js",
    });

    expect(salvarDadosMock).toHaveBeenCalledWith("vagas", [
      expect.objectContaining({
        id: 1,
        titulo: "Desenvolvedor Atualizado",
        skills: ["React", "Node.js"],
      }),
    ]);
  });

  it("não deve editar vaga de outra empresa", () => {
    carregarDadosMock.mockReturnValue([criarVagaMock(1, 2)]);

    editarVaga(1, 1, {
      titulo: "Alteração indevida",
      descricao: "Teste",
      skills: "Java",
    });

    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve excluir vaga pertencente à empresa quando não bloqueada", () => {
    const vaga1 = criarVagaMock(1, 1);
    const vaga2 = criarVagaMock(2, 1);

    carregarDadosMock.mockReturnValue([vaga1, vaga2]);
    vagaEstaBloqueadaMock.mockReturnValue(false);

    expect(excluirVaga(1, 1)).toBe(true);

    expect(salvarDadosMock).toHaveBeenCalledWith("vagas", [vaga2]);
  });

  it("não deve excluir vaga pertencente a outra empresa", () => {
    carregarDadosMock.mockReturnValue([criarVagaMock(1, 2)]);

    expect(excluirVaga(1, 1)).toBe(false);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("não deve excluir vaga bloqueada", () => {
    carregarDadosMock.mockReturnValue([criarVagaMock(1, 1)]);
    vagaEstaBloqueadaMock.mockReturnValue(true);

    expect(excluirVaga(1, 1)).toBe(false);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("não deve excluir vaga inexistente", () => {
    carregarDadosMock.mockReturnValue([]);

    expect(excluirVaga(99, 1)).toBe(false);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve excluir somente as vagas da empresa informada", () => {
    const vagaEmpresa1 = criarVagaMock(1, 1);
    const vagaEmpresa2 = criarVagaMock(2, 2);
    const outraVagaEmpresa1 = criarVagaMock(3, 1);

    carregarDadosMock.mockReturnValue([
      vagaEmpresa1,
      vagaEmpresa2,
      outraVagaEmpresa1,
    ]);

    excluirVagasPorEmpresa(1);

    expect(salvarDadosMock).toHaveBeenCalledWith("vagas", [vagaEmpresa2]);
  });
});
