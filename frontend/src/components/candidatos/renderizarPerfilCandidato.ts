
import type { Candidato } from "../../models/Candidato";
import { escaparHtml } from "../../services/utils/escapeHtml";

export function renderizarPerfilCandidato(candidato: Candidato): string {
    return `
        <section class="perfil-candidato">

            <h1>${escaparHtml(candidato.nome)}</h1>

            <p>
                <strong>Formação:</strong>
                ${escaparHtml(candidato.formacao)}
            </p>

            <p>
                <strong>Descrição:</strong>
                ${escaparHtml(candidato.descricao)}
            </p>

            <p>
                <strong>Skills:</strong>
                ${candidato.skills.map(escaparHtml).join(" • ")}
            </p>

            <p>
                <strong>Estado:</strong>
                ${escaparHtml(candidato.estado)}
            </p>

            <div class="acoes-perfil">
                <button
                    type="button"
                    id="botao-editar-candidato"
                >
                    Editar Perfil
                </button>

                <button
                    type="button"
                    id="botao-excluir-candidato"
                >
                    Excluir conta
                </button>
            </div>

        </section>

        <section class="vagas-disponiveis">

            <h2>Vagas disponíveis</h2>

            <div id="lista-vagas"></div>

        </section>
    `;
}
