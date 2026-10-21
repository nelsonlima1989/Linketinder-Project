import type { Candidato } from "../../models/Candidato";
import { escaparHtml } from "../../services/utils/escapeHtml";

const NUMERO_INICIAL_CANDIDATO = 1001;

export function renderizarCardsCandidatos(candidatos: Candidato[]): string {
    return candidatos.map((candidato, index) => `
        <article class="card-candidato">
            <h3>Candidato #${index + NUMERO_INICIAL_CANDIDATO}</h3>

            <p>
                <strong>Formação:</strong>
                ${escaparHtml(candidato.formacao)}
            </p>

            <p>
                <strong>Skills:</strong>
                ${candidato.skills.map(escaparHtml).join(" • ")}
            </p>
        </article>
    `).join("");
}