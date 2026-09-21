const CHAVE_BASE = "empresa-em-edicao";

const INTERVALO_HEARTBEAT = 2000;
const TEMPO_EXPIRACAO = 5000;

type BloqueioEmpresa = {
    empresaId: number;
    timestamp: number;
};

let intervaloHeartbeat: number | undefined;
let empresaBloqueadaAtual: number | undefined;

function obterChave(empresaId: number): string {
    return `${CHAVE_BASE}-${empresaId}`;
}

export function bloquearEmpresa(
    empresaId: number
): boolean {

    const bloqueioAtual =
        obterBloqueioEmpresa(empresaId);

    if (bloqueioAtual) {
        return false;
    }

    salvarBloqueio(empresaId);

    empresaBloqueadaAtual = empresaId;

    iniciarHeartbeat();

    return true;
}

function salvarBloqueio(
    empresaId: number
): void {

    const bloqueio: BloqueioEmpresa = {
        empresaId,
        timestamp: Date.now()
    };

    localStorage.setItem(
        obterChave(empresaId),
        JSON.stringify(bloqueio)
    );
}

function iniciarHeartbeat(): void {

    if (intervaloHeartbeat !== undefined) {
        return;
    }

    intervaloHeartbeat = window.setInterval(() => {

        if (empresaBloqueadaAtual === undefined) {
            pararHeartbeat();
            return;
        }

        const bloqueio =
            obterBloqueioEmpresa(
                empresaBloqueadaAtual
            );

        if (!bloqueio) {
            empresaBloqueadaAtual = undefined;
            pararHeartbeat();
            return;
        }

        salvarBloqueio(
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

export function obterBloqueioEmpresa(
    empresaId: number
): BloqueioEmpresa | null {

    const dados =
        localStorage.getItem(
            obterChave(empresaId)
        );

    if (!dados) {
        return null;
    }

    const bloqueio =
        JSON.parse(dados) as BloqueioEmpresa;

    const expirou =
        Date.now() - bloqueio.timestamp >
        TEMPO_EXPIRACAO;

    if (expirou) {

        localStorage.removeItem(
            obterChave(empresaId)
        );

        return null;
    }

    return bloqueio;
}

export function empresaEstaBloqueada(
    empresaId: number
): boolean {

    const bloqueio =
        obterBloqueioEmpresa(empresaId);

    return bloqueio !== null;
}

export function liberarBloqueioEmpresa(): void {

    if (empresaBloqueadaAtual !== undefined) {

        localStorage.removeItem(
            obterChave(empresaBloqueadaAtual)
        );

        empresaBloqueadaAtual = undefined;
    }

    pararHeartbeat();
}