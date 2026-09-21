import type { Candidato } from "../../models/Candidato";

export function renderizarCardsCandidatos(candidatos: Candidato[]): string {
    return candidatos.map((candidato, index) => `
        <article class="card-candidato">
            <h3>Candidato #${index + 1001}</h3>

            <p>
                <strong>Formação:</strong>
                ${candidato.formacao}
            </p>

            <p>
                <strong>Skills:</strong>
                ${candidato.skills.join(" • ")}
            </p>
        </article>
    `).join("");
}