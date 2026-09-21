import type { Vaga } from "../../models/Vaga";

export function renderizarFormularioVaga(
    vaga?: Vaga
): string {

    const estaEditando = vaga !== undefined;

    return `
        <section class="cadastro-vaga">

            <h1>
                ${estaEditando
                    ? "Editar vaga"
                    : "Nova vaga"}
            </h1>

            <form id="formulario-vaga">

                <div class="campo">
                    <label for="titulo">
                        Título
                    </label>

                    <input
                        type="text"
                        id="titulo"
                        name="titulo"
                        value="${vaga?.titulo ?? ""}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="descricao">
                        Descrição
                    </label>

                    <textarea
                        id="descricao"
                        name="descricao"
                        rows="4"
                        required
                    >${vaga?.descricao ?? ""}</textarea>
                </div>

                <div class="campo">
                    <label for="skills">
                        Skills
                    </label>

                    <input
                        type="text"
                        id="skills"
                        name="skills"
                        value="${vaga?.skills.join(", ") ?? ""}"
                        placeholder="C#, .NET, SQL Server"
                        required
                    >
                </div>

                <div class="acoes-formulario">

                    <button type="submit">
                        ${estaEditando
                            ? "Salvar alterações"
                            : "Cadastrar vaga"}
                    </button>

                    <button
                        type="button"
                        id="botao-cancelar-vaga"
                    >
                        Cancelar
                    </button>

                </div>

            </form>

        </section>
    `;
}