import type { Candidato } from "../../models/Candidato";

export function renderizarPerfilCandidato(candidato: Candidato): string {
  return `
        <section class="perfil-candidato">

            <h1>${candidato.nome}</h1>

            <p>
                <strong>Formação:</strong>
                ${candidato.formacao}
            </p>

            <p>
                <strong>Descrição:</strong>
                ${candidato.descricao}
            </p>

            <p>
                <strong>Skills:</strong>
                ${candidato.skills.join(" • ")}
            </p>

            <p>
                <strong>Estado:</strong>
                ${candidato.estado}
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
