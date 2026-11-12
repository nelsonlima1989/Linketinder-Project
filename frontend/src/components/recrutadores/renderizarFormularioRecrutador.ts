import type { Recrutador } from "../../models/Recrutador";
import { escaparHtml } from "../../services/utils/escapeHtml";

export function renderizarFormularioRecrutador(
    recrutador?: Recrutador,
    mensagemErro?: string
): string {
    const estaEditando = recrutador !== undefined;

    return `
        <section class="cadastro-recrutador">

            <h1>
                ${estaEditando
                    ? "Editar recrutador"
                    : "Novo recrutador"}
            </h1>

            ${mensagemErro
                ? `<p class="mensagem-erro" role="alert">${escaparHtml(mensagemErro)}</p>`
                : ""}

            <form id="formulario-recrutador">

                <div class="campo">
                    <label for="nome">Nome</label>

                    <input
                        type="text"
                        id="nome"
                        name="nome"
                        maxlength="150"
                        value="${escaparHtml(recrutador?.nome ?? "")}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="areaRecrutamento">Área de recrutamento</label>

                    <input
                        type="text"
                        id="areaRecrutamento"
                        name="areaRecrutamento"
                        maxlength="100"
                        value="${escaparHtml(recrutador?.areaRecrutamento ?? "")}"
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
                    >${escaparHtml(recrutador?.descricao ?? "")}</textarea>
                </div>

                <div class="campo">
                    <label for="email">E-mail</label>

                    <input
                        type="email"
                        id="email"
                        name="email"
                        maxlength="150"
                        value="${escaparHtml(recrutador?.email ?? "")}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="cpf">CPF (somente números)</label>

                    <input
                        type="text"
                        id="cpf"
                        name="cpf"
                        maxlength="11"
                        pattern="\\d{11}"
                        inputmode="numeric"
                        value="${escaparHtml(recrutador?.cpf ?? "")}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="dataNascimento">Data de nascimento</label>

                    <input
                        type="date"
                        id="dataNascimento"
                        name="dataNascimento"
                        value="${escaparHtml(recrutador?.dataNascimento ?? "")}"
                        required
                    >
                </div>

                <div class="campo">
                    <label for="senha">
                        ${estaEditando
                            ? "Nova senha (deixe em branco para manter)"
                            : "Senha (mínimo 8 caracteres)"}
                    </label>

                    <input
                        type="password"
                        id="senha"
                        name="senha"
                        minlength="8"
                        maxlength="128"
                        autocomplete="new-password"
                        value=""
                        ${estaEditando ? "" : "required"}
                    >
                </div>

                <div class="acoes-formulario">

                    <button type="submit">
                        ${estaEditando
                            ? "Salvar alterações"
                            : "Cadastrar recrutador"}
                    </button>

                    <button
                        type="button"
                        id="botao-cancelar-recrutador"
                    >
                        Cancelar
                    </button>

                </div>

            </form>

        </section>
    `;
}
