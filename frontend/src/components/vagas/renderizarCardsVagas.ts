import type { Vaga } from "../../models/Vaga";
import type { Candidato } from "../../models/Candidato";
import { calcularAfinidade } from "../../services/utils/afinidadeService";

export function renderizarCardsVagas(
    vagas: Vaga[],
    candidato: Candidato
): string {

    return vagas.map(vaga => {
        const afinidade = calcularAfinidade(candidato, vaga);

        return `
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

                <p>
                    <strong>Afinidade:</strong>
                    ${afinidade}%
                </p>

            </article>
        `;
    }).join("");
}