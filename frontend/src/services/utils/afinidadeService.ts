import type { Candidato } from "../../models/Candidato";
import type { Vaga } from "../../models/Vaga";

export function calcularAfinidade(
    candidato: Candidato,
    vaga: Vaga
): number {

    if (vaga.skills.length === 0) {
        return 0;
    }

    const skillsCandidato = candidato.skills.map(skill =>
        skill.trim().toLowerCase()
    );

    const skillsVaga = vaga.skills.map(skill =>
        skill.trim().toLowerCase()
    );

    const skillsEmComum = skillsVaga.filter(skill =>
        skillsCandidato.includes(skill)
    );

    const percentual = (skillsEmComum.length / skillsVaga.length) * 100;

    return Math.round(percentual);
}