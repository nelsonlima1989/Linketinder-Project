import { beforeEach, describe, expect, it, vi } from "vitest";

import { excluirEmpresa } from "./empresaService";
import { excluirVagasPorEmpresa } from "../vaga/vagaService";
import { excluirRecrutadoresPorEmpresa } from "../recrutador/recrutadorService";
import { excluirEmpresaComVagas } from "./exclusaoEmpresaService";


vi.mock("./empresaService", () => ({
  excluirEmpresa: vi.fn(),
}));

vi.mock("../vaga/vagaService", () => ({
  excluirVagasPorEmpresa: vi.fn(),
}));

vi.mock("../recrutador/recrutadorService", () => ({
  excluirRecrutadoresPorEmpresa: vi.fn(),
}));

describe("exclusaoEmpresaService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("deve excluir as vagas, os recrutadores e a empresa quando as operações funcionarem", () => {
    vi.mocked(excluirEmpresa).mockReturnValue(true);

    const resultado = excluirEmpresaComVagas(3);

    expect(resultado).toBe(true);
    expect(excluirVagasPorEmpresa).toHaveBeenCalledWith(3);
    expect(excluirRecrutadoresPorEmpresa).toHaveBeenCalledWith(3);
    expect(excluirEmpresa).toHaveBeenCalledWith(3);
  });

  it("deve retornar false quando a exclusão da empresa falhar", () => {
    vi.mocked(excluirEmpresa).mockReturnValue(false);

    const resultado = excluirEmpresaComVagas(3);

    expect(resultado).toBe(false);
    expect(excluirVagasPorEmpresa).toHaveBeenCalledWith(3);
    expect(excluirRecrutadoresPorEmpresa).toHaveBeenCalledWith(3);
    expect(excluirEmpresa).toHaveBeenCalledWith(3);
  });
});