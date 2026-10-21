import { beforeEach, describe, expect, it, vi } from "vitest";

import { excluirEmpresa } from "./empresaService";
import { excluirVagasPorEmpresa } from "../vaga/vagaService";
import { excluirEmpresaComVagas } from "./exclusaoEmpresaService";


vi.mock("./empresaService", () => ({
  excluirEmpresa: vi.fn(),
}));

vi.mock("../vaga/vagaService", () => ({
  excluirVagasPorEmpresa: vi.fn(),
}));

describe("exclusaoEmpresaService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("deve excluir as vagas e a empresa quando ambas as operações funcionarem", () => {
    vi.mocked(excluirEmpresa).mockReturnValue(true);

    const resultado = excluirEmpresaComVagas(3);

    expect(resultado).toBe(true);
    expect(excluirVagasPorEmpresa).toHaveBeenCalledWith(3);
    expect(excluirEmpresa).toHaveBeenCalledWith(3);
  });

  it("deve retornar false quando a exclusão da empresa falhar", () => {
  vi.mocked(excluirEmpresa).mockReturnValue(false);

  const resultado = excluirEmpresaComVagas(3);

  expect(resultado).toBe(false);
  expect(excluirEmpresa).toHaveBeenCalledWith(3);
});
});