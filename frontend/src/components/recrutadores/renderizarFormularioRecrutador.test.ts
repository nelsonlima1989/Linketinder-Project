import { describe, expect, it } from "vitest";

import { renderizarFormularioRecrutador } from "./renderizarFormularioRecrutador";

import type { Recrutador } from "../../models/Recrutador";

function criarRecrutador(dados: Partial<Recrutador> = {}): Recrutador {
    return {
        id: 7,
        empresaId: 3,
        nome: "Camila Ferreira",
        areaRecrutamento: "Engenharia de Software",
        descricao: "Recrutadora técnica",
        email: "camila@empresa.com",
        cpf: "12345678909",
        dataNascimento: "1992-04-15",
        ...dados
    };
}

describe("renderizarFormularioRecrutador", () => {
    it("deve renderizar o formulário de cadastro", () => {
        const html = renderizarFormularioRecrutador();

        expect(html).toContain("Novo recrutador");
        expect(html).toContain('id="formulario-recrutador"');
        expect(html).toContain("Cadastrar recrutador");
    });

    it("deve renderizar o formulário de edição", () => {
        const html = renderizarFormularioRecrutador(criarRecrutador());

        expect(html).toContain("Editar recrutador");
        expect(html).toContain("Salvar alterações");
    });

    it("deve renderizar todos os campos do recrutador", () => {
        const html = renderizarFormularioRecrutador();

        for (const campo of [
            "nome",
            "areaRecrutamento",
            "descricao",
            "email",
            "cpf",
            "dataNascimento",
            "senha"
        ]) {
            expect(html).toContain(`id="${campo}"`);
            expect(html).toContain(`name="${campo}"`);
        }
    });

    it("deve exigir todos os 7 campos no cadastro", () => {
        const html = renderizarFormularioRecrutador();

        expect(html.match(/\brequired\b/g)).toHaveLength(7);
    });

    it("deve declarar restrições de validação HTML", () => {
        const html = renderizarFormularioRecrutador();

        expect(html).toContain('type="email"');
        expect(html).toContain('type="date"');
        expect(html).toContain('type="password"');
        expect(html).toContain('pattern="\\d{11}"');
        expect(html).toContain('maxlength="11"');
        expect(html).toContain('maxlength="150"');
        expect(html).toContain('maxlength="100"');
        expect(html).toContain('minlength="8"');
        expect(html).toContain('maxlength="128"');
    });

    it("deve preencher os dados do recrutador na edição, exceto a senha", () => {
        const html = renderizarFormularioRecrutador(criarRecrutador());

        expect(html).toContain('value="Camila Ferreira"');
        expect(html).toContain('value="Engenharia de Software"');
        expect(html).toContain("Recrutadora técnica");
        expect(html).toContain('value="camila@empresa.com"');
        expect(html).toContain('value="12345678909"');
        expect(html).toContain('value="1992-04-15"');
    });

    it("nunca deve preencher o campo senha, nem na edição", () => {
        const recrutadorComSenha = {
            ...criarRecrutador(),
            senha: "SenhaSecreta123"
        } as Recrutador;

        const htmlEdicao = renderizarFormularioRecrutador(recrutadorComSenha);
        const htmlCadastro = renderizarFormularioRecrutador();

        expect(htmlEdicao).not.toContain("SenhaSecreta123");

        for (const html of [htmlEdicao, htmlCadastro]) {
            expect(html).toContain('autocomplete="new-password"');
            expect(html).toMatch(/name="senha"[\s\S]*?value=""/);
        }
    });

    it("deve tornar a senha opcional na edição", () => {
        const html = renderizarFormularioRecrutador(criarRecrutador());

        expect(html.match(/\brequired\b/g)).toHaveLength(6);
        expect(html).toContain("deixe em branco para manter");
    });

    it("deve preservar o botão de cancelamento", () => {
        const html = renderizarFormularioRecrutador();

        expect(html).toContain('id="botao-cancelar-recrutador"');
        expect(html).toContain('type="button"');
        expect(html).toContain("Cancelar");
    });

    it("deve exibir mensagem de erro escapada quando informada", () => {
        const html = renderizarFormularioRecrutador(
            undefined,
            "CPF inválido. <script>alert(1)</script>"
        );

        expect(html).toContain('class="mensagem-erro"');
        expect(html).toContain('role="alert"');
        expect(html).toContain("CPF inválido.");
        expect(html).not.toContain("<script>");
    });

    it("não deve exibir mensagem de erro por padrão", () => {
        expect(renderizarFormularioRecrutador()).not.toContain("mensagem-erro");
    });

    it("deve escapar HTML nos campos preenchidos", () => {
        const html = renderizarFormularioRecrutador(
            criarRecrutador({
                nome: 'Ana" autofocus onfocus="alert(1)',
                descricao: "</textarea><script>alert(1)</script>"
            })
        );

        expect(html).not.toContain('onfocus="alert(1)"');
        expect(html).not.toContain("</textarea><script>");
        expect(html).toContain("&quot;");
    });

    it("não deve modificar os dados originais", () => {
        const recrutador = criarRecrutador();
        const copia = structuredClone(recrutador);

        renderizarFormularioRecrutador(recrutador);

        expect(recrutador).toEqual(copia);
    });
});
