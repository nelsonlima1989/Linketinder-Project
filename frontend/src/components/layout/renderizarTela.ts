import type { Tela } from "../../services/ui/navegacaoService";

import { renderizarPerfilEmpresa } from "../empresas/renderizarPerfilEmpresa";
import { renderizarPerfilCandidato } from "../candidatos/renderizarPerfilCandidato";
import { renderizarFormularioCandidato } from "../candidatos/renderizarFormularioCandidato";
import { renderizarFormularioEmpresa } from "../empresas/renderizarFormularioEmpresa";
import { obterEmpresaPorId } from "../../services/empresa/empresaService";
import { obterCandidatoPorId } from "../../services/candidato/candidatoService";
import { obterVagaPorId } from "../../services/vaga/vagaService";
import { renderizarFormularioVaga } from "../vagas/renderizarFormularioVaga";

export function renderizarTela(
  tela: Tela,
  empresaId?: number,
  candidatoId?: number,
  vagaId?: number,
): string {
  switch (tela) {
    case "empresa": {
      if (empresaId === undefined) {
        return "<p>Nenhuma empresa selecionada.</p>";
      }

      const empresaAtual = obterEmpresaPorId(empresaId);

      if (!empresaAtual) {
        return "<p>Empresa não encontrada.</p>";
      }

      return renderizarPerfilEmpresa(empresaAtual);
    }

    case "candidato": {
      if (candidatoId === undefined) {
        return "<p>Nenhum candidato selecionado.</p>";
      }

      const candidatoAtual = obterCandidatoPorId(candidatoId);

      if (!candidatoAtual) {
        return "<p>Candidato não encontrado.</p>";
      }

      return renderizarPerfilCandidato(candidatoAtual);
    }

    case "cadastro-candidato": {
      if (candidatoId !== undefined) {
        const candidato = obterCandidatoPorId(candidatoId);

        if (!candidato) {
          return "<p>Candidato não encontrado.</p>";
        }

        return renderizarFormularioCandidato(candidato);
      }

      return renderizarFormularioCandidato();
    }

    case "cadastro-empresa": {
      if (empresaId !== undefined) {
        const empresa = obterEmpresaPorId(empresaId);

        if (!empresa) {
          return "<p>Empresa não encontrada.</p>";
        }

        return renderizarFormularioEmpresa(empresa);
      }

      return renderizarFormularioEmpresa();
    }

    case "cadastro-vaga": {
      if (vagaId !== undefined && empresaId !== undefined) {
        const vaga = obterVagaPorId(vagaId, empresaId);

        if (!vaga) {
          return "<p>Vaga não encontrada.</p>";
        }

        return renderizarFormularioVaga(vaga);
      }

      return renderizarFormularioVaga();
    }

    default:
      return "";
  }
}
