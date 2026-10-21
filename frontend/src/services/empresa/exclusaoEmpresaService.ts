import { excluirEmpresa } from "./empresaService";
import { excluirVagasPorEmpresa } from "../vaga/vagaService";

export function excluirEmpresaComVagas(
  empresaId: number,
): boolean {
  excluirVagasPorEmpresa(empresaId);

  return excluirEmpresa(empresaId);
}