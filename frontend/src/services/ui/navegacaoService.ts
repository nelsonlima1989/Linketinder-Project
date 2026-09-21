export type Tela =
    | "empresa"
    | "candidato"
    | "cadastro-candidato"
    | "cadastro-empresa"
    | "cadastro-vaga";

export function obterTelaInicial(): Tela {
    return "empresa";
}

export function obterTelaDoMenu(
    elemento: HTMLElement
): Tela | null {

    const tela = elemento.dataset.tela;

    if (
        tela === "empresa" ||
        tela === "candidato" ||
        tela === "cadastro-candidato" ||
        tela === "cadastro-empresa"
    ) {
        return tela;
    }

    return null;
}