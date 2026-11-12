import { excluirEmpresa } from "./empresaService";
import { excluirVagasPorEmpresa } from "../vaga/vagaService";
import { excluirRecrutadoresPorEmpresa } from "../recrutador/recrutadorService";

export function excluirEmpresaComVagas(
  empresaId: number,
): boolean {
  excluirVagasPorEmpresa(empresaId);
  excluirRecrutadoresPorEmpresa(empresaId);

  return excluirEmpresa(empresaId);
}