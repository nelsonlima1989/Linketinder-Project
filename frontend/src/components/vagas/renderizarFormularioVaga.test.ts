
import { describe, expect, it } from "vitest";

import { renderizarFormularioVaga } from "./renderizarFormularioVaga";

import type { Vaga } from "../../models/Vaga";

function criarVaga(dados: Partial<Vaga> = {}): Vaga {
    return {
        id: 1,
        empresaId: 2,
        titulo: "Desenvolvedor Full Stack",
        descricao: "Desenvolvimento de aplicações web",
        skills: ["TypeScript", "React"],
        ...dados
    };
}

describe("renderizarFormularioVaga", () => {
    it("deve renderizar o formulário de cadastro", () => {
        const html = renderizarFormularioVaga();

        expect(html).toContain("Nova vaga");
        expect(html).toContain('id="formulario-vaga"');
        expect(html).toContain("Cadastrar vaga");
    });

    it("deve renderizar o formulário de edição", () => {
        const html = renderizarFormularioVaga(criarVaga());

        expect(html).toContain("Editar vaga");
        expect(html).toContain("Salvar alterações");
    });

    it("deve preencher os dados da vaga na edição", () => {
        const html = renderizarFormularioVaga(criarVaga());

        expect(html).toContain('value="Desenvolvedor Full Stack"');
        expect(html).toContain("Desenvolvimento de aplicações web");
        expect(html).toContain('value="TypeScript, React"');
    });

    it("deve preservar os três campos obrigatórios", () => {
        const html = renderizarFormularioVaga();

        for (const campo of ["titulo", "descricao", "skills"]) {
            expect(html).toContain(`id="${campo}"`);
            expect(html).toContain(`name="${campo}"`);
        }

        expect(html.match(/\brequired\b/g)).toHaveLength(3);
    });

    it("deve preservar o botão de cancelamento", () => {
        const html = renderizarFormularioVaga();

        expect(html).toContain('id="botao-cancelar-vaga"');
        expect(html).toContain('type="button"');
        expect(html).toContain("Cancelar");
    });

    it("deve preservar o placeholder das skills", () => {
        const html = renderizarFormularioVaga();

        expect(html).toContain(
            'placeholder="C#, .NET, SQL Server"'
        );
    });

    it("deve escapar HTML no título da vaga", () => {
        const vaga = criarVaga({
            titulo: 'Desenvolvedor" autofocus onfocus="alert(1)'
        });

        const html = renderizarFormularioVaga(vaga);

        expect(html).toContain(
            'value="Desenvolvedor&quot; autofocus onfocus=&quot;alert(1)"'
        );

        expect(html).not.toContain('onfocus="alert(1)"');
    });

    it("deve impedir que a descrição encerre o textarea", () => {
        const vaga = criarVaga({
            descricao: "</textarea><script>alert(1)</script>"
        });

        const html = renderizarFormularioVaga(vaga);

        expect(html).toContain(
            "&lt;/textarea&gt;&lt;script&gt;alert(1)&lt;/script&gt;"
        );

        expect(html).not.toContain("</textarea><script>");
    });

    it("deve escapar caracteres especiais nas skills", () => {
        const vaga = criarVaga({
            skills: ["C# & .NET", '"React"']
        });

        const html = renderizarFormularioVaga(vaga);

        expect(html).toContain(
            'value="C# &amp; .NET, &quot;React&quot;"'
        );
    });

    it("não deve modificar os dados originais da vaga", () => {
        const vaga = criarVaga();
        const dadosOriginais = structuredClone(vaga);

        renderizarFormularioVaga(vaga);

        expect(vaga).toEqual(dadosOriginais);
    });
});
