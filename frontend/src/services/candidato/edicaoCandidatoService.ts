const CHAVE_BASE = "candidato-em-edicao";

const INTERVALO_HEARTBEAT = 2000;
const TEMPO_EXPIRACAO = 5000;

type BloqueioCandidato = {
    candidatoId: number;
    timestamp: number;
};

let intervaloHeartbeat: number | undefined;

let candidatoBloqueadoAtual: number | undefined;

function obterChave(candidatoId: number): string {
    return `${CHAVE_BASE}-${candidatoId}`;
}

export function bloquearCandidato(
    candidatoId: number
): boolean {

    const bloqueioAtual =
        obterBloqueioCandidato(candidatoId);

    if (bloqueioAtual) {
        return false;
    }

    salvarBloqueio(candidatoId);

    candidatoBloqueadoAtual = candidatoId;

    iniciarHeartbeat();

    return true;
}

function salvarBloqueio(
    candidatoId: number
): void {

    const bloqueio: BloqueioCandidato = {
        candidatoId,
        timestamp: Date.now()
    };

    localStorage.setItem(
        obterChave(candidatoId),
        JSON.stringify(bloqueio)
    );
}

function iniciarHeartbeat(): void {

    if (intervaloHeartbeat !== undefined) {
        return;
    }

    intervaloHeartbeat = window.setInterval(() => {

        if (candidatoBloqueadoAtual === undefined) {
            pararHeartbeat();
            return;
        }

        const bloqueio =
            obterBloqueioCandidato(
                candidatoBloqueadoAtual
            );

        if (!bloqueio) {
            candidatoBloqueadoAtual = undefined;
            pararHeartbeat();
            return;
        }

        salvarBloqueio(
            bloqueio.candidatoId
        );

    }, INTERVALO_HEARTBEAT);
}

function pararHeartbeat(): void {

    if (intervaloHeartbeat === undefined) {
        return;
    }

    window.clearInterval(
        intervaloHeartbeat
    );

    intervaloHeartbeat = undefined;
}

export function obterBloqueioCandidato(
    candidatoId: number
): BloqueioCandidato | null {

    const dados =
        localStorage.getItem(
            obterChave(candidatoId)
        );

    if (!dados) {
        return null;
    }

    const bloqueio =
        JSON.parse(dados) as BloqueioCandidato;

    const expirou =
        Date.now() - bloqueio.timestamp >
        TEMPO_EXPIRACAO;

    if (expirou) {

        localStorage.removeItem(
            obterChave(candidatoId)
        );

        return null;
    }

    return bloqueio;
}

export function candidatoEstaBloqueado(
    candidatoId: number
): boolean {

    const bloqueio =
        obterBloqueioCandidato(candidatoId);

    return bloqueio !== null;
}

export function liberarBloqueioCandidato(): void {

    if (candidatoBloqueadoAtual !== undefined) {

        localStorage.removeItem(
            obterChave(candidatoBloqueadoAtual)
        );

        candidatoBloqueadoAtual = undefined;
    }

    pararHeartbeat();
}