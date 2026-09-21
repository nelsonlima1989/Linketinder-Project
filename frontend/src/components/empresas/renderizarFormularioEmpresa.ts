import type { Empresa } from "../../models/Empresa";

export function renderizarFormularioEmpresa(
    empresa?: Empresa
): string {

    const estaEditando =
        empresa !== undefined;

    return `
        <section class="cadastro-empresa">

            <h1>
                ${estaEditando
                    ? "Editar perfil"
                    : "Nova empresa"}
            </h1>

            <form id="formulario-empresa">

                <div class="campo">
                    <label for="nome">Nome</label>
                    <input
                        type="text"
                        id="nome"
                        name="nome"
                        value="${empresa?.nome ?? ""}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="cnpj">CNPJ</label>
                    <input
                        type="text"
                        id="cnpj"
                        name="cnpj"
                        value="${empresa?.cnpj ?? ""}"
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
                    >${empresa?.descricao ?? ""}</textarea>
                </div>

                <div class="campo">
                    <label for="estado">Estado</label>
                    <input
                        type="text"
                        id="estado"
                        name="estado"
                        value="${empresa?.estado ?? ""}"
                        required
                    >
                </div>

                <div class="acoes-formulario">

                    <button type="submit">
                        ${estaEditando
                            ? "Salvar alterações"
                            : "Cadastrar empresa"}
                    </button>

                    <button
                        type="button"
                        id="botao-cancelar-empresa"
                    >
                        Cancelar
                    </button>

                </div>

            </form>
        </section>
    `;
}