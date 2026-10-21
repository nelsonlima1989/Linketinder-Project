import { criarGerenciadorBloqueio } from "../utils/bloqueioService";

type BloqueioEmpresa = {
    empresaId: number;
    sessaoId: string;
    timestamp: number;
};

const gerenciadorBloqueio =
    criarGerenciadorBloqueio<BloqueioEmpresa>({
        chaveBase: "empresa-em-edicao",
        intervaloHeartbeat: 2000,
        tempoExpiracao: 5000
    });

export function bloquearEmpresa(empresaId: number): boolean {
    return gerenciadorBloqueio.bloquear(empresaId, {
        empresaId
    });
}

export function obterBloqueioEmpresa(
    empresaId: number
): BloqueioEmpresa | null {
    return gerenciadorBloqueio.consultar(empresaId);
}

export function empresaEstaBloqueada(empresaId: number): boolean {
    return gerenciadorBloqueio.estaBloqueado(empresaId);
}

export function liberarBloqueioEmpresa(): void {
    gerenciadorBloqueio.liberar();
}