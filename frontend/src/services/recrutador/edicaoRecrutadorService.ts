import { criarGerenciadorBloqueio } from "../utils/bloqueioService";

export type BloqueioRecrutador = {
    recrutadorId: number;
    empresaId: number;
    sessaoId: string;
    timestamp: number;
};

const gerenciadorBloqueio = criarGerenciadorBloqueio<BloqueioRecrutador>({
    chaveBase: "recrutador-em-edicao",
    intervaloHeartbeat: 2000,
    tempoExpiracao: 5000
});

export function bloquearRecrutador(
    recrutadorId: number,
    empresaId: number
): boolean {
    return gerenciadorBloqueio.bloquear(recrutadorId, {
        recrutadorId,
        empresaId
    });
}

export function obterBloqueioRecrutador(
    recrutadorId: number
): BloqueioRecrutador | null {
    return gerenciadorBloqueio.consultar(recrutadorId);
}

export function recrutadorEstaBloqueado(recrutadorId: number): boolean {
    return gerenciadorBloqueio.estaBloqueado(recrutadorId);
}

export function liberarBloqueioRecrutador(): void {
    gerenciadorBloqueio.liberar();
}
