import type { Vaga } from "../../models/Vaga";

export function renderizarCardsVagas(
    vagas: Vaga[]
): string {

    return vagas.map(vaga => `
        <article class="card-vaga">

            <h3>${vaga.titulo}</h3>

            <p>
                <strong>Descrição:</strong>
                ${vaga.descricao}
            </p>

            <p>
                <strong>Skills:</strong>
                ${vaga.skills.join(" • ")}
            </p>

        </article>
    `).join("");
}