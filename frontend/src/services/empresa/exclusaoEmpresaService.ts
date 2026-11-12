import { listarEmpresas, excluirEmpresa } from "./empresaService";
import { listarVagas, excluirVagasPorEmpresa } from "../vaga/vagaService";
import {
  listarRecrutadores,
  excluirRecrutadoresPorEmpresa,
} from "../recrutador/recrutadorService";
import { salvarDados } from "../utils/storage";

const CHAVE_EMPRESAS = "empresas";
const CHAVE_VAGAS = "vagas";
const CHAVE_RECRUTADORES = "recrutadores";

/**
 * Exclui a empresa e, em cascata, suas vagas e seus recrutadores.
 *
 * A operação é tudo-ou-nada: se a exclusão da empresa não ocorrer, nada é
 * removido; se qualquer etapa da cascata falhar, o estado anterior das três
 * coleções é restaurado e o erro original é propagado, evitando registros
 * órfãos (vagas/recrutadores sem empresa) ou empresas sem filhos.
 */
export function excluirEmpresaComVagas(
  empresaId: number,
): boolean {
  const copiaEmpresas = structuredClone(listarEmpresas());
  const copiaVagas = structuredClone(listarVagas());
  const copiaRecrutadores = structuredClone(listarRecrutadores());

  try {
    if (!excluirEmpresa(empresaId)) {
      return false;
    }

    excluirVagasPorEmpresa(empresaId);
    excluirRecrutadoresPorEmpresa(empresaId);

    return true;
  } catch (erro) {
    restaurarEstado(copiaEmpresas, copiaVagas, copiaRecrutadores);

    throw erro;
  }
}

function restaurarEstado(
  empresas: unknown,
  vagas: unknown,
  recrutadores: unknown,
): void {
  salvarDados(CHAVE_EMPRESAS, empresas);
  salvarDados(CHAVE_VAGAS, vagas);
  salvarDados(CHAVE_RECRUTADORES, recrutadores);
}
