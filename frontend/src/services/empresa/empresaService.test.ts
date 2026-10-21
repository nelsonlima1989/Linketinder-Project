import { beforeEach, describe, expect, it, vi } from "vitest";

import {
  listarEmpresas,
  obterProximoId,
  obterEmpresaPorId,
  adicionarEmpresa,
  criarEmpresa,
  editarEmpresa,
  excluirEmpresa,
} from "./empresaService";

import { carregarDados, salvarDados } from "../utils/storage";
import type { Empresa } from "../../models/Empresa";

vi.mock("../utils/storage", () => ({
  carregarDados: vi.fn(),
  salvarDados: vi.fn(),
}));

const carregarDadosMock = vi.mocked(carregarDados);
const salvarDadosMock = vi.mocked(salvarDados);

function criarEmpresaMock(id: number, nome: string = "Empresa Teste"): Empresa {
  return {
    id,
    nome,
    cnpj: "12345678000199",
    descricao: "Empresa de tecnologia",
    estado: "RJ",
  };
}

describe("empresaService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("deve listar empresas armazenadas", () => {
    const empresas = [criarEmpresaMock(1)];

    carregarDadosMock.mockReturnValue(empresas);

    expect(listarEmpresas()).toEqual(empresas);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve retornar ID 1 quando nao houver empresas", () => {
    carregarDadosMock.mockReturnValue([]);

    expect(obterProximoId()).toBe(1);
  });

  it("deve calcular o proximo ID disponivel", () => {
    carregarDadosMock.mockReturnValue([
      criarEmpresaMock(2),
      criarEmpresaMock(8),
      criarEmpresaMock(5),
    ]);

    expect(obterProximoId()).toBe(9);
  });

  it("deve localizar empresa pelo ID", () => {
    const empresas = [
      criarEmpresaMock(1, "Empresa A"),
      criarEmpresaMock(2, "Empresa B"),
    ];

    carregarDadosMock.mockReturnValue(empresas);

    expect(obterEmpresaPorId(2)).toEqual(empresas[1]);
  });

  it("deve retornar undefined quando a empresa nao existir", () => {
    carregarDadosMock.mockReturnValue([]);

    expect(obterEmpresaPorId(99)).toBeUndefined();
  });

  it("nao deve sobrescrever empresas ja armazenadas", () => {
    carregarDadosMock.mockReturnValue([criarEmpresaMock(1)]);

    listarEmpresas();

    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve adicionar empresa com ID incremental", () => {
    carregarDadosMock.mockReturnValue([]);

    adicionarEmpresa({
      nome: "Empresa Nova",
      cnpj: "12345678000199",
      descricao: "Empresa de tecnologia",
      estado: "RJ",
    });

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "empresas",
      expect.arrayContaining([
        expect.objectContaining({
          id: 1,
          nome: "Empresa Nova",
        }),
      ]),
    );
  });

  it("deve criar empresa com os dados do formulario", () => {
    carregarDadosMock.mockReturnValue([]);

    criarEmpresa({
      nome: "Empresa Teste",
      cnpj: "98765432000100",
      descricao: "Desenvolvimento de software",
      estado: "SP",
    });

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "empresas",
      expect.arrayContaining([
        expect.objectContaining({
          id: 1,
          nome: "Empresa Teste",
          cnpj: "98765432000100",
          estado: "SP",
        }),
      ]),
    );
  });

  it("deve retornar o ID gerado ao adicionar uma empresa", () => {
    carregarDadosMock.mockReturnValue([
      criarEmpresaMock(2),
      criarEmpresaMock(8),
      criarEmpresaMock(5),
    ]);

    const novoId = adicionarEmpresa({
      nome: "Empresa Nova",
      cnpj: "12345678000199",
      descricao: "Empresa de tecnologia",
      estado: "RJ",
    });

    expect(novoId).toBe(9);

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "empresas",
      expect.arrayContaining([
        expect.objectContaining({
          id: 9,
          nome: "Empresa Nova",
        }),
      ]),
    );
  });

  it("deve retornar o ID da empresa criada pelo formulário", () => {
    carregarDadosMock.mockReturnValue([
      criarEmpresaMock(3),
      criarEmpresaMock(6),
    ]);

    const novoId = criarEmpresa({
      nome: "Empresa Full Stack",
      cnpj: "98765432000100",
      descricao: "Desenvolvimento de software",
      estado: "SP",
    });

    expect(novoId).toBe(7);

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "empresas",
      expect.arrayContaining([
        expect.objectContaining({
          id: 7,
          nome: "Empresa Full Stack",
        }),
      ]),
    );
  });

  it("deve editar uma empresa existente", () => {
    carregarDadosMock.mockReturnValue([criarEmpresaMock(1, "Empresa Antiga")]);

    editarEmpresa(1, {
      nome: "Empresa Atualizada",
      cnpj: "12345678000199",
      descricao: "Nova descrição",
      estado: "MG",
    });

    expect(salvarDadosMock).toHaveBeenCalledWith(
      "empresas",
      expect.arrayContaining([
        expect.objectContaining({
          id: 1,
          nome: "Empresa Atualizada",
          descricao: "Nova descrição",
          estado: "MG",
        }),
      ]),
    );
  });

  it("nao deve editar empresa inexistente", () => {
    carregarDadosMock.mockReturnValue([]);

    editarEmpresa(99, {
      nome: "Empresa Teste",
      cnpj: "12345678000199",
      descricao: "Teste",
      estado: "RJ",
    });

    expect(salvarDadosMock).not.toHaveBeenCalled();
  });

  it("deve excluir empresa existente", () => {
    const empresa1 = criarEmpresaMock(1);
    const empresa2 = criarEmpresaMock(2);

    carregarDadosMock.mockReturnValue([empresa1, empresa2]);

    const resultado = excluirEmpresa(1);

    expect(resultado).toBe(true);
    expect(salvarDadosMock).toHaveBeenCalledWith("empresas", [empresa2]);
  });

  it("nao deve excluir empresa inexistente", () => {
    carregarDadosMock.mockReturnValue([]);

    const resultado = excluirEmpresa(99);

    expect(resultado).toBe(false);
    expect(salvarDadosMock).not.toHaveBeenCalled();
  });
});
