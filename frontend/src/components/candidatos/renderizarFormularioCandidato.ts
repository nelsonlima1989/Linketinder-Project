
import type { Candidato } from "../../models/Candidato";
import { escaparHtml } from "../../services/utils/escapeHtml";

export function renderizarFormularioCandidato(
    candidato?: Candidato
): string {
    const estaEditando = candidato !== undefined;

    return `
        <section class="cadastro-candidato">

            <h1>
                ${estaEditando
                    ? "Editar perfil"
                    : "Novo candidato"}
            </h1>

            <form id="formulario-candidato">

                <div class="campo">
                    <label for="nome">Nome</label>

                    <input
                        type="text"
                        id="nome"
                        name="nome"
                        value="${escaparHtml(candidato?.nome ?? "")}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="cpf">CPF</label>

                    <input
                        type="text"
                        id="cpf"
                        name="cpf"
                        value="${escaparHtml(candidato?.cpf ?? "")}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="idade">Idade</label>

                    <input
                        type="number"
                        id="idade"
                        name="idade"
                        min="1"
                        value="${candidato?.idade ?? ""}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="formacao">Formação</label>

                    <input
                        type="text"
                        id="formacao"
                        name="formacao"
                        value="${escaparHtml(candidato?.formacao ?? "")}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="descricao">Descrição</label>

                    <textarea
                        id="descricao"
                        name="descricao"
                        rows="4"
                        required
                    >${escaparHtml(candidato?.descricao ?? "")}</textarea>
                </div>

                <div class="campo">
                    <label for="skills">Skills</label>

                    <input
                        type="text"
                        id="skills"
                        name="skills"
                        value="${escaparHtml(candidato?.skills.join(", ") ?? "")}"
                        placeholder="Java, Spring, SQL"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="estado">Estado</label>

                    <input
                        type="text"
                        id="estado"
                        name="estado"
                        value="${escaparHtml(candidato?.estado ?? "")}"
                        required
                    >
                </div>

                <div class="acoes-formulario">

                    <button type="submit">
                        ${estaEditando
                            ? "Salvar alterações"
                            : "Cadastrar candidato"}
                    </button>

                    <button
                        type="button"
                        id="botao-cancelar-candidato"
                    >
                        Cancelar
                    </button>

                </div>

            </form>

        </section>
    `;
}
