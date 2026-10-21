
import type { Tela } from "./navegacaoService";

export function obterEmpresaIdParaRenderizacao(
    tela: Tela,
    empresaAtualId: number,
    empresaEmEdicaoId?: number
): number | undefined {
    if (tela === "cadastro-empresa") {
        return empresaEmEdicaoId;
    }

    return empresaAtualId;
}
