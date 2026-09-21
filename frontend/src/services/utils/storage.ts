export function salvarDados<T>(chave: string, dados: T): void {
    localStorage.setItem(chave, JSON.stringify(dados));
}

export function carregarDados<T>(chave: string): T | null {
    const dados = localStorage.getItem(chave);

    if (!dados) {
        return null;
    }

    return JSON.parse(dados) as T;
}