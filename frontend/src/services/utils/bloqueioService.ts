
type ConfiguracaoBloqueio = {
    chaveBase: string;
    intervaloHeartbeat: number;
    tempoExpiracao: number;
};

type DadosBloqueio = {
    sessaoId: string;
    timestamp: number;
};

export function criarGerenciadorBloqueio<T extends DadosBloqueio>(
    configuracao: ConfiguracaoBloqueio
) {
    const sessaoId = crypto.randomUUID();

    let registroAtualId: number | undefined;
    let intervaloHeartbeat: number | undefined;

    function obterChave(id: number): string {
        return `${configuracao.chaveBase}-${id}`;
    }

    function consultar(id: number): T | null {
        const chave = obterChave(id);
        const dados = localStorage.getItem(chave);

        if (!dados) {
            return null;
        }

        const bloqueio = JSON.parse(dados) as T;

        if (Date.now() - bloqueio.timestamp > configuracao.tempoExpiracao) {
            localStorage.removeItem(chave);
            return null;
        }

        return bloqueio;
    }

    function salvar(id: number, dados: Omit<T, "sessaoId" | "timestamp">): void {
        const bloqueio = {
            ...dados,
            sessaoId,
            timestamp: Date.now()
        } as T;

        localStorage.setItem(
            obterChave(id),
            JSON.stringify(bloqueio)
        );
    }

    function pararHeartbeat(): void {
        if (intervaloHeartbeat === undefined) {
            return;
        }

        window.clearInterval(intervaloHeartbeat);
        intervaloHeartbeat = undefined;
    }

    function liberar(): void {
        if (registroAtualId !== undefined) {
            const bloqueio = consultar(registroAtualId);

            if (bloqueio?.sessaoId === sessaoId) {
                localStorage.removeItem(obterChave(registroAtualId));
            }

            registroAtualId = undefined;
        }

        pararHeartbeat();
    }

    function iniciarHeartbeat(
        id: number,
        dados: Omit<T, "sessaoId" | "timestamp">
    ): void {
        if (intervaloHeartbeat !== undefined) {
            return;
        }

        intervaloHeartbeat = window.setInterval(() => {
            const bloqueio = consultar(id);

            if (!bloqueio || bloqueio.sessaoId !== sessaoId) {
                registroAtualId = undefined;
                pararHeartbeat();
                return;
            }

            salvar(id, dados);
        }, configuracao.intervaloHeartbeat);
    }

    function bloquear(
        id: number,
        dados: Omit<T, "sessaoId" | "timestamp">
    ): boolean {
        if (registroAtualId !== undefined) {
            return false;
        }

        if (consultar(id)) {
            return false;
        }

        salvar(id, dados);
        registroAtualId = id;
        iniciarHeartbeat(id, dados);

        return true;
    }

    function estaBloqueado(id: number): boolean {
        return consultar(id) !== null;
    }

    return {
        bloquear,
        consultar,
        estaBloqueado,
        liberar
    };
}
