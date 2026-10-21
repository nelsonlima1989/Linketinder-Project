
import { criarGerenciadorBloqueio } from "../utils/bloqueioService";

type BloqueioCandidato = {
    candidatoId: number;
    sessaoId: string;
    timestamp: number;
};

const gerenciadorBloqueio = criarGerenciadorBloqueio<BloqueioCandidato>({
    chaveBase: "candidato-em-edicao",
    intervaloHeartbeat: 2000,
    tempoExpiracao: 5000
});

export function bloquearCandidato(candidatoId: number): boolean {
    return gerenciadorBloqueio.bloquear(candidatoId, {
        candidatoId
    });
}

export function obterBloqueioCandidato(
    candidatoId: number
): BloqueioCandidato | null {
    return gerenciadorBloqueio.consultar(candidatoId);
}

export function candidatoEstaBloqueado(candidatoId: number): boolean {
    return gerenciadorBloqueio.estaBloqueado(candidatoId);
}

export function liberarBloqueioCandidato(): void {
    gerenciadorBloqueio.liberar();
}
