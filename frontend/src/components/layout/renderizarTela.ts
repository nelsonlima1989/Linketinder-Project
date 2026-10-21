
import type { Tela } from "../../services/utils/navegacaoService";

import { renderizarPerfilEmpresa } from "../empresas/renderizarPerfilEmpresa";
import { renderizarPerfilCandidato } from "../candidatos/renderizarPerfilCandidato";
import { renderizarFormularioCandidato } from "../candidatos/renderizarFormularioCandidato";
import { renderizarFormularioEmpresa } from "../empresas/renderizarFormularioEmpresa";
import { renderizarFormularioVaga } from "../vagas/renderizarFormularioVaga";

import { obterEmpresaPorId } from "../../services/empresa/empresaService";
import { obterCandidatoPorId } from "../../services/candidato/candidatoService";
import { obterVagaPorId } from "../../services/vaga/vagaService";

type ContextoTela = {
    empresaId?: number;
    candidatoId?: number;
    vagaId?: number;
};

type RenderizadorTela = (contexto: ContextoTela) => string;

function renderizarTelaEmpresa({ empresaId }: ContextoTela): string {
    if (empresaId === undefined) {
        return "<p>Nenhuma empresa selecionada.</p>";
    }

    const empresa = obterEmpresaPorId(empresaId);

    if (!empresa) {
        return "<p>Empresa não encontrada.</p>";
    }

    return renderizarPerfilEmpresa(empresa);
}

function renderizarTelaCandidato({ candidatoId }: ContextoTela): string {
    if (candidatoId === undefined) {
        return "<p>Nenhum candidato selecionado.</p>";
    }

    const candidato = obterCandidatoPorId(candidatoId);

    if (!candidato) {
        return "<p>Candidato não encontrado.</p>";
    }

    return renderizarPerfilCandidato(candidato);
}

function renderizarTelaCadastroCandidato({
    candidatoId
}: ContextoTela): string {
    if (candidatoId === undefined) {
        return renderizarFormularioCandidato();
    }

    const candidato = obterCandidatoPorId(candidatoId);

    if (!candidato) {
        return "<p>Candidato não encontrado.</p>";
    }

    return renderizarFormularioCandidato(candidato);
}

function renderizarTelaCadastroEmpresa({
    empresaId
}: ContextoTela): string {
    if (empresaId === undefined) {
        return renderizarFormularioEmpresa();
    }

    const empresa = obterEmpresaPorId(empresaId);

    if (!empresa) {
        return "<p>Empresa não encontrada.</p>";
    }

    return renderizarFormularioEmpresa(empresa);
}

function renderizarTelaCadastroVaga({
    empresaId,
    vagaId
}: ContextoTela): string {
    if (vagaId === undefined || empresaId === undefined) {
        return renderizarFormularioVaga();
    }

    const vaga = obterVagaPorId(vagaId, empresaId);

    if (!vaga) {
        return "<p>Vaga não encontrada.</p>";
    }

    return renderizarFormularioVaga(vaga);
}

const renderizadores: Record<Tela, RenderizadorTela> = {
    empresa: renderizarTelaEmpresa,
    candidato: renderizarTelaCandidato,
    "cadastro-candidato": renderizarTelaCadastroCandidato,
    "cadastro-empresa": renderizarTelaCadastroEmpresa,
    "cadastro-vaga": renderizarTelaCadastroVaga
};

export function renderizarTela(
    tela: Tela,
    empresaId?: number,
    candidatoId?: number,
    vagaId?: number
): string {
    return renderizadores[tela]({
        empresaId,
        candidatoId,
        vagaId
    });
}
