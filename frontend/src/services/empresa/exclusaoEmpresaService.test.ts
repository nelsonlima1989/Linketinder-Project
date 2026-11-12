import { beforeEach, describe, expect, it, vi } from "vitest";

import { excluirEmpresa, listarEmpresas } from "./empresaService";
import { excluirVagasPorEmpresa, listarVagas } from "../vaga/vagaService";
import {
  excluirRecrutadoresPorEmpresa,
  listarRecrutadores,
} from "../recrutador/recrutadorService";
import { salvarDados } from "../utils/storage";
import { excluirEmpresaComVagas } from "./exclusaoEmpresaService";

vi.mock("./empresaService", () => ({
  excluirEmpresa: vi.fn(),
  listarEmpresas: vi.fn(),
}));

vi.mock("../vaga/vagaService", () => ({
  excluirVagasPorEmpresa: vi.fn(),
  listarVagas: vi.fn(),
}));

vi.mock("../recrutador/recrutadorService", () => ({
  excluirRecrutadoresPorEmpresa: vi.fn(),
  listarRecrutadores: vi.fn(),
}));

vi.mock("../utils/storage", () => ({
  salvarDados: vi.fn(),
}));

const EMPRESAS = [{ id: 3, nome: "Code Factory" }];
const VAGAS = [{ id: 1, empresaId: 3 }];
const RECRUTADORES = [{ id: 1, empresaId: 3 }];

describe("exclusaoEmpresaService", () => {
  beforeEach(() => {
    vi.resetAllMocks();

    vi.mocked(listarEmpresas).mockReturnValue(EMPRESAS as never);
    vi.mocked(listarVagas).mockReturnValue(VAGAS as never);
    vi.mocked(listarRecrutadores).mockReturnValue(RECRUTADORES as never);
  });

  it("deve excluir a empresa, as vagas e os recrutadores quando tudo funcionar", () => {
    vi.mocked(excluirEmpresa).mockReturnValue(true);

    const resultado = excluirEmpresaComVagas(3);

    expect(resultado).toBe(true);
    expect(excluirEmpresa).toHaveBeenCalledWith(3);
    expect(excluirVagasPorEmpresa).toHaveBeenCalledWith(3);
    expect(excluirRecrutadoresPorEmpresa).toHaveBeenCalledWith(3);
    expect(salvarDados).not.toHaveBeenCalled();
  });

  it("não deve excluir vagas nem recrutadores quando a exclusão da empresa falhar", () => {
    vi.mocked(excluirEmpresa).mockReturnValue(false);

    const resultado = excluirEmpresaComVagas(3);

    expect(resultado).toBe(false);
    expect(excluirEmpresa).toHaveBeenCalledWith(3);
    expect(excluirVagasPorEmpresa).not.toHaveBeenCalled();
    expect(excluirRecrutadoresPorEmpresa).not.toHaveBeenCalled();
    expect(salvarDados).not.toHaveBeenCalled();
  });

  it("deve restaurar empresas, vagas e recrutadores quando a exclusão das vagas falhar", () => {
    vi.mocked(excluirEmpresa).mockReturnValue(true);
    vi.mocked(excluirVagasPorEmpresa).mockImplementation(() => {
      throw new Error("Falha ao remover vagas");
    });

    expect(() => excluirEmpresaComVagas(3)).toThrow("Falha ao remover vagas");

    expect(excluirRecrutadoresPorEmpresa).not.toHaveBeenCalled();
    expect(salvarDados).toHaveBeenCalledWith("empresas", EMPRESAS);
    expect(salvarDados).toHaveBeenCalledWith("vagas", VAGAS);
    expect(salvarDados).toHaveBeenCalledWith("recrutadores", RECRUTADORES);
  });

  it("deve restaurar o estado completo quando a exclusão dos recrutadores falhar", () => {
    vi.mocked(excluirEmpresa).mockReturnValue(true);
    vi.mocked(excluirRecrutadoresPorEmpresa).mockImplementation(() => {
      throw new Error("Falha ao remover recrutadores");
    });

    expect(() => excluirEmpresaComVagas(3)).toThrow(
      "Falha ao remover recrutadores",
    );

    expect(excluirVagasPorEmpresa).toHaveBeenCalledWith(3);
    expect(salvarDados).toHaveBeenCalledWith("empresas", EMPRESAS);
    expect(salvarDados).toHaveBeenCalledWith("vagas", VAGAS);
    expect(salvarDados).toHaveBeenCalledWith("recrutadores", RECRUTADORES);
  });

  it("deve restaurar o estado quando a própria exclusão da empresa lançar erro", () => {
    vi.mocked(excluirEmpresa).mockImplementation(() => {
      throw new Error("Falha ao remover empresa");
    });

    expect(() => excluirEmpresaComVagas(3)).toThrow("Falha ao remover empresa");

    expect(excluirVagasPorEmpresa).not.toHaveBeenCalled();
    expect(excluirRecrutadoresPorEmpresa).not.toHaveBeenCalled();
    expect(salvarDados).toHaveBeenCalledWith("empresas", EMPRESAS);
    expect(salvarDados).toHaveBeenCalledWith("vagas", VAGAS);
    expect(salvarDados).toHaveBeenCalledWith("recrutadores", RECRUTADORES);
  });

  it("deve restaurar uma cópia do estado original, não a referência alterada", () => {
    const vagasOriginais = [{ id: 1, empresaId: 3 }];
    vi.mocked(listarVagas).mockReturnValue(vagasOriginais as never);
    vi.mocked(excluirEmpresa).mockReturnValue(true);

    vi.mocked(excluirVagasPorEmpresa).mockImplementation(() => {
      vagasOriginais.length = 0;
      throw new Error("Falha parcial");
    });

    expect(() => excluirEmpresaComVagas(3)).toThrow("Falha parcial");

    expect(salvarDados).toHaveBeenCalledWith("vagas", [
      { id: 1, empresaId: 3 },
    ]);
  });
});
