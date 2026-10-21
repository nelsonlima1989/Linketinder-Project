
import { criarGerenciadorBloqueio } from "../utils/bloqueioService";

type BloqueioVaga = {
    vagaId: number;
    empresaId: number;
    sessaoId: string;
    timestamp: number;
};

const gerenciadorBloqueio = criarGerenciadorBloqueio<BloqueioVaga>({
    chaveBase: "vaga-em-edicao",
    intervaloHeartbeat: 2000,
    tempoExpiracao: 5000
});

export function bloquearVaga(
    vagaId: number,
    empresaId: number
): boolean {
    return gerenciadorBloqueio.bloquear(vagaId, {
        vagaId,
        empresaId
    });
}

export function obterBloqueioVaga(
    vagaId: number
): BloqueioVaga | null {
    return gerenciadorBloqueio.consultar(vagaId);
}

export function vagaEstaBloqueada(vagaId: number): boolean {
    return gerenciadorBloqueio.estaBloqueado(vagaId);
}

export function liberarBloqueioVaga(): void {
    gerenciadorBloqueio.liberar();
}
