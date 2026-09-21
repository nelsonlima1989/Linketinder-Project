const CHAVE_BASE = "vaga-em-edicao";

const INTERVALO_HEARTBEAT = 2000;
const TEMPO_EXPIRACAO = 5000;

type BloqueioVaga = {
    vagaId: number;
    empresaId: number;
    timestamp: number;
};

let intervaloHeartbeat: number | undefined;
let vagaBloqueadaAtual: number | undefined;

function obterChave(vagaId: number): string {
    return `${CHAVE_BASE}-${vagaId}`;
}

export function bloquearVaga(
    vagaId: number,
    empresaId: number
): boolean {

    const bloqueioAtual =
        obterBloqueioVaga(vagaId);

    if (bloqueioAtual) {
        return false;
    }

    salvarBloqueio(
        vagaId,
        empresaId
    );

    vagaBloqueadaAtual = vagaId;

    iniciarHeartbeat();

    return true;
}

function salvarBloqueio(
    vagaId: number,
    empresaId: number
): void {

    const bloqueio: BloqueioVaga = {
        vagaId,
        empresaId,
        timestamp: Date.now()
    };

    localStorage.setItem(
        obterChave(vagaId),
        JSON.stringify(bloqueio)
    );
}

function iniciarHeartbeat(): void {

    if (intervaloHeartbeat !== undefined) {
        return;
    }

    intervaloHeartbeat = window.setInterval(() => {

        if (vagaBloqueadaAtual === undefined) {
            pararHeartbeat();
            return;
        }

        const bloqueio =
            obterBloqueioVaga(
                vagaBloqueadaAtual
            );

        if (!bloqueio) {
            vagaBloqueadaAtual = undefined;
            pararHeartbeat();
            return;
        }

        salvarBloqueio(
            bloqueio.vagaId,
            bloqueio.empresaId
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

export function obterBloqueioVaga(
    vagaId: number
): BloqueioVaga | null {

    const dados =
        localStorage.getItem(
            obterChave(vagaId)
        );

    if (!dados) {
        return null;
    }

    const bloqueio =
        JSON.parse(dados) as BloqueioVaga;

    const expirou =
        Date.now() - bloqueio.timestamp >
        TEMPO_EXPIRACAO;

    if (expirou) {

        localStorage.removeItem(
            obterChave(vagaId)
        );

        return null;
    }

    return bloqueio;
}

export function vagaEstaBloqueada(
    vagaId: number
): boolean {

    const bloqueio =
        obterBloqueioVaga(vagaId);

    return bloqueio !== null;
}

export function liberarBloqueioVaga(): void {

    if (vagaBloqueadaAtual !== undefined) {

        localStorage.removeItem(
            obterChave(vagaBloqueadaAtual)
        );

        vagaBloqueadaAtual = undefined;
    }

    pararHeartbeat();
}