import type { Candidato } from "../../models/Candidato";
import type { Vaga } from "../../models/Vaga";

export function calcularAfinidade(
    candidato: Candidato,
    vaga: Vaga
): number {

    const skillsVaga = new Set(
        vaga.skills.map(skill => skill.trim().toLowerCase())
    );

    if (skillsVaga.size === 0) {
        return 0;
    }

    const skillsCandidato = new Set(
        candidato.skills.map(skill => skill.trim().toLowerCase())
    );

    const skillsEmComum = [...skillsVaga].filter(skill =>
        skillsCandidato.has(skill)
    );

    const percentual = (skillsEmComum.length / skillsVaga.size) * 100;

    return Math.round(percentual);
}