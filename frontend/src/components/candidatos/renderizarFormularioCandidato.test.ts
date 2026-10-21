
import { describe, expect, it } from "vitest";

import { renderizarFormularioCandidato } from "./renderizarFormularioCandidato";

import type { Candidato } from "../../models/Candidato";

function criarCandidato(
    dados: Partial<Candidato> = {}
): Candidato {
    return {
        id: 1,
        nome: "Candidato Teste",
        cpf: "12345678900",
        idade: 25,
        formacao: "Análise e Desenvolvimento de Sistemas",
        descricao: "Desenvolvedor Full Stack",
        skills: ["TypeScript", "React"],
        estado: "RJ",
        ...dados
    };
}

describe("renderizarFormularioCandidato", () => {
    it("deve renderizar o formulário de novo candidato", () => {
        const html = renderizarFormularioCandidato();

        expect(html).toContain("Novo candidato");
        expect(html).toContain('id="formulario-candidato"');
        expect(html).toContain("Cadastrar candidato");
    });

    it("deve renderizar o formulário de edição", () => {
        const html = renderizarFormularioCandidato(criarCandidato());

        expect(html).toContain("Editar perfil");
        expect(html).toContain("Salvar alterações");
    });

    it("deve preencher os dados do candidato na edição", () => {
        const html = renderizarFormularioCandidato(criarCandidato());

        expect(html).toContain('value="Candidato Teste"');
        expect(html).toContain('value="12345678900"');
        expect(html).toContain('value="25"');
        expect(html).toContain(
            'value="Análise e Desenvolvimento de Sistemas"'
        );
        expect(html).toContain("Desenvolvedor Full Stack");
        expect(html).toContain('value="TypeScript, React"');
        expect(html).toContain('value="RJ"');
    });

    it("deve manter os campos obrigatórios do formulário", () => {
        const html = renderizarFormularioCandidato();

        for (const campo of [
            "nome",
            "cpf",
            "idade",
            "formacao",
            "descricao",
            "skills",
            "estado"
        ]) {
            expect(html).toContain(`id="${campo}"`);
        }

        expect(html.match(/\brequired\b/g)).toHaveLength(7);
    });

    it("deve manter o campo idade como numérico", () => {
        const html = renderizarFormularioCandidato();

        expect(html).toContain('type="number"');
        expect(html).toContain('min="1"');
    });

    it("deve preservar o botão de cancelamento", () => {
        const html = renderizarFormularioCandidato();

        expect(html).toContain('id="botao-cancelar-candidato"');
        expect(html).toContain('type="button"');
    });

    it("deve escapar caracteres HTML no nome e CPF", () => {
        const candidato = criarCandidato({
            nome: 'João" onfocus="alert(1)',
            cpf: "<12345678900>"
        });

        const html = renderizarFormularioCandidato(candidato);

        expect(html).toContain(
            'value="João&quot; onfocus=&quot;alert(1)"'
        );
        expect(html).toContain('value="&lt;12345678900&gt;"');
        expect(html).not.toContain('onfocus="alert(1)"');
    });

    it("deve escapar HTML na formação e no estado", () => {
        const candidato = criarCandidato({
            formacao: "<b>Graduação</b>",
            estado: 'RJ" autofocus'
        });

        const html = renderizarFormularioCandidato(candidato);

        expect(html).toContain(
            'value="&lt;b&gt;Graduação&lt;/b&gt;"'
        );
        expect(html).toContain('value="RJ&quot; autofocus"');
    });

    it("deve escapar caracteres especiais nas skills", () => {
        const candidato = criarCandidato({
            skills: ["C# & .NET", '"React"']
        });

        const html = renderizarFormularioCandidato(candidato);

        expect(html).toContain(
            'value="C# &amp; .NET, &quot;React&quot;"'
        );
    });

    it("deve impedir que a descrição encerre o textarea", () => {
        const candidato = criarCandidato({
            descricao: "</textarea><script>alert(1)</script>"
        });

        const html = renderizarFormularioCandidato(candidato);

        expect(html).toContain(
            "&lt;/textarea&gt;&lt;script&gt;alert(1)&lt;/script&gt;"
        );
        expect(html).not.toContain(
            "</textarea><script>"
        );
    });

    it("não deve modificar os dados do candidato", () => {
        const candidato = criarCandidato();
        const dadosOriginais = structuredClone(candidato);

        renderizarFormularioCandidato(candidato);

        expect(candidato).toEqual(dadosOriginais);
    });
});
