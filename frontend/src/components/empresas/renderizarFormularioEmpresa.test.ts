
import { describe, expect, it } from "vitest";

import { renderizarFormularioEmpresa } from "./renderizarFormularioEmpresa";

import type { Empresa } from "../../models/Empresa";

function criarEmpresa(
    dados: Partial<Empresa> = {}
): Empresa {
    return {
        id: 1,
        nome: "Empresa Teste",
        cnpj: "12345678000199",
        descricao: "Empresa de tecnologia",
        estado: "RJ",
        ...dados
    };
}

describe("renderizarFormularioEmpresa", () => {
    it("deve renderizar o formulário de cadastro", () => {
        const html = renderizarFormularioEmpresa();

        expect(html).toContain("Nova empresa");
        expect(html).toContain('id="formulario-empresa"');
        expect(html).toContain("Cadastrar empresa");
    });

    it("deve renderizar o formulário de edição", () => {
        const html = renderizarFormularioEmpresa(criarEmpresa());

        expect(html).toContain("Editar perfil");
        expect(html).toContain("Salvar alterações");
    });

    it("deve preencher os dados na edição", () => {
        const html = renderizarFormularioEmpresa(criarEmpresa());

        expect(html).toContain('value="Empresa Teste"');
        expect(html).toContain('value="12345678000199"');
        expect(html).toContain("Empresa de tecnologia");
        expect(html).toContain('value="RJ"');
    });

    it("deve preservar os campos obrigatórios", () => {
        const html = renderizarFormularioEmpresa();

        for (const campo of [
            "nome",
            "cnpj",
            "descricao",
            "estado"
        ]) {
            expect(html).toContain(`id="${campo}"`);
        }

        expect(html.match(/\brequired\b/g)).toHaveLength(4);
    });

    it("deve preservar o botão de cancelamento", () => {
        const html = renderizarFormularioEmpresa();

        expect(html).toContain('id="botao-cancelar-empresa"');
        expect(html).toContain('type="button"');
        expect(html).toContain("Cancelar");
    });

    it("deve escapar HTML no nome da empresa", () => {
        const empresa = criarEmpresa({
            nome: 'Empresa" onfocus="alert(1)'
        });

        const html = renderizarFormularioEmpresa(empresa);

        expect(html).toContain(
            'value="Empresa&quot; onfocus=&quot;alert(1)"'
        );

        expect(html).not.toContain('onfocus="alert(1)"');
    });

    it("deve escapar HTML no CNPJ", () => {
        const empresa = criarEmpresa({
            cnpj: '<12345678000199>'
        });

        const html = renderizarFormularioEmpresa(empresa);

        expect(html).toContain(
            'value="&lt;12345678000199&gt;"'
        );
    });

    it("deve escapar HTML no estado", () => {
        const empresa = criarEmpresa({
            estado: 'RJ" autofocus'
        });

        const html = renderizarFormularioEmpresa(empresa);

        expect(html).toContain(
            'value="RJ&quot; autofocus"'
        );
    });

    it("deve impedir que a descrição encerre o textarea", () => {
        const empresa = criarEmpresa({
            descricao: "</textarea><script>alert(1)</script>"
        });

        const html = renderizarFormularioEmpresa(empresa);

        expect(html).toContain(
            "&lt;/textarea&gt;&lt;script&gt;alert(1)&lt;/script&gt;"
        );

        expect(html).not.toContain("</textarea><script>");
    });

    it("não deve modificar os dados originais da empresa", () => {
        const empresa = criarEmpresa();
        const dadosOriginais = structuredClone(empresa);

        renderizarFormularioEmpresa(empresa);

        expect(empresa).toEqual(dadosOriginais);
    });
});
