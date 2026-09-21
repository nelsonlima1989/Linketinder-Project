import type { Candidato } from "../../models/Candidato";

export function contarSkills(candidatos: Candidato[]): Record<string, number> {

    const contagem: Record<string, number> = {};

    for (const candidato of candidatos) {

        for (const skill of candidato.skills) {

            if (contagem[skill]) {
                contagem[skill]++;
            } else {
                contagem[skill] = 1;
            }
        }
    }

    return contagem;
}

export function ordenarSkills(
    contagemSkills: Record<string, number>
): Record<string, number> {

    return Object.fromEntries(
        Object.entries(contagemSkills)
            .sort(([, quantidadeA], [, quantidadeB]) => quantidadeB - quantidadeA)
    );
}