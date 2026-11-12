import type { Recrutador } from "../../models/Recrutador";
import { escaparHtml } from "../../services/utils/escapeHtml";

export function renderizarCardsRecrutadoresEmpresa(
    recrutadores: Recrutador[]
): string {

    if (recrutadores.length === 0) {
        return `
            <p>Nenhum recrutador cadastrado.</p>
        `;
    }

    return recrutadores.map(recrutador => `
        <article class="card-recrutador-empresa">

            <h3>${escaparHtml(recrutador.nome)}</h3>

            <p>
                <strong>Área de recrutamento:</strong>
                ${escaparHtml(recrutador.areaRecrutamento)}
            </p>

            <p>
                <strong>Descrição:</strong>
                ${escaparHtml(recrutador.descricao)}
            </p>

            <p>
                <strong>E-mail:</strong>
                ${escaparHtml(recrutador.email)}
            </p>

            <div class="acoes-recrutador">

                <button
                    type="button"
                    class="botao-editar-recrutador"
                    data-recrutador-id="${recrutador.id}"
                >
                    Editar
                </button>

                <button
                    type="button"
                    class="botao-excluir-recrutador"
                    data-recrutador-id="${recrutador.id}"
                >
                    Excluir
                </button>

            </div>

        </article>
    `).join("");
}
