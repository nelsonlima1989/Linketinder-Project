
import { describe, expect, it } from "vitest";

import { renderizarCardsVagasEmpresa } from "./renderizarCardsVagasEmpresa";

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

describe("renderizarCardsVagasEmpresa", () => {
    it("deve informar quando não houver vagas cadastradas", () => {
        const html = renderizarCardsVagasEmpresa([]);

        expect(html).toContain("Nenhuma vaga cadastrada.");
        expect(html).not.toContain('class="card-vaga-empresa"');
    });

    it("deve renderizar um cartão para cada vaga", () => {
        const vagas = [
            criarVaga({ id: 1 }),
            criarVaga({ id: 2 })
        ];

        const html = renderizarCardsVagasEmpresa(vagas);

        expect(html.match(/class="card-vaga-empresa"/g))
            .toHaveLength(2);
    });

    it("deve apresentar título, descrição e skills", () => {
        const html = renderizarCardsVagasEmpresa([criarVaga()]);

        expect(html).toContain("<h3>Desenvolvedor Full Stack</h3>");
        expect(html).toContain("Desenvolvimento de aplicações web");
        expect(html).toContain("TypeScript • React");
    });

    it("deve preservar os botões de edição e exclusão", () => {
        const html = renderizarCardsVagasEmpresa([criarVaga()]);

        expect(html).toContain('class="botao-editar-vaga"');
        expect(html).toContain('class="botao-excluir-vaga"');
        expect(html).toContain("Editar");
        expect(html).toContain("Excluir");
    });

    it("deve associar o ID correto aos botões da vaga", () => {
        const html = renderizarCardsVagasEmpresa([
            criarVaga({ id: 42 })
        ]);

        expect(
            html.match(/data-vaga-id="42"/g)
        ).toHaveLength(2);
    });

    it("deve preservar os IDs de diferentes vagas", () => {
        const html = renderizarCardsVagasEmpresa([
            criarVaga({ id: 10 }),
            criarVaga({ id: 20 })
        ]);

        expect(html.match(/data-vaga-id="10"/g)).toHaveLength(2);
        expect(html.match(/data-vaga-id="20"/g)).toHaveLength(2);
    });

    it("deve escapar HTML no título da vaga", () => {
        const vaga = criarVaga({
            titulo: "<script>alert('teste')</script>"
        });

        const html = renderizarCardsVagasEmpresa([vaga]);

        expect(html).toContain(
            "&lt;script&gt;alert(&#39;teste&#39;)&lt;/script&gt;"
        );
        expect(html).not.toContain("<script>");
    });

    it("deve escapar HTML na descrição da vaga", () => {
        const vaga = criarVaga({
            descricao: "<img src=x onerror=alert(1)>"
        });

        const html = renderizarCardsVagasEmpresa([vaga]);

        expect(html).toContain(
            "&lt;img src=x onerror=alert(1)&gt;"
        );
        expect(html).not.toContain("<img");
    });

    it("deve escapar caracteres especiais nas skills", () => {
        const vaga = criarVaga({
            skills: ["C# & .NET", "<React>"]
        });

        const html = renderizarCardsVagasEmpresa([vaga]);

        expect(html).toContain("C# &amp; .NET • &lt;React&gt;");
    });

    it("não deve modificar a lista original de vagas", () => {
        const vagas = [
            criarVaga({ id: 1 }),
            criarVaga({ id: 2 })
        ];

        const dadosOriginais = structuredClone(vagas);

        renderizarCardsVagasEmpresa(vagas);

        expect(vagas).toEqual(dadosOriginais);
    });
});
