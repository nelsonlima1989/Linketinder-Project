import type { Vaga } from "../../models/Vaga";

export function renderizarCardsVagasEmpresa(
    vagas: Vaga[]
): string {

    if (vagas.length === 0) {
        return `
            <p>Nenhuma vaga cadastrada.</p>
        `;
    }

    return vagas.map(vaga => `
        <article class="card-vaga-empresa">

            <h3>${vaga.titulo}</h3>

            <p>
                <strong>Descrição:</strong>
                ${vaga.descricao}
            </p>

            <p>
                <strong>Skills:</strong>
                ${vaga.skills.join(" • ")}
            </p>

            <div class="acoes-vaga">

                <button
                    type="button"
                    class="botao-editar-vaga"
                    data-vaga-id="${vaga.id}"
                >
                    Editar
                </button>

                <button
                    type="button"
                    class="botao-excluir-vaga"
                    data-vaga-id="${vaga.id}"
                >
                    Excluir
                </button>

            </div>

        </article>
    `).join("");
}

