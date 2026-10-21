
export function escaparHtml(texto: string): string {
    const caracteresEspeciais: Record<string, string> = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    };

    return texto.replace(
        /[&<>"']/g,
        caractere => caracteresEspeciais[caractere]
    );
}
