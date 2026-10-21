
import { describe, expect, it } from "vitest";

import { renderizarPerfilCandidato } from "./renderizarPerfilCandidato";

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

describe("renderizarPerfilCandidato", () => {
    it("deve renderizar a estrutura do perfil", () => {
        const html = renderizarPerfilCandidato(criarCandidato());

        expect(html).toContain('class="perfil-candidato"');
        expect(html).toContain("<h1>Candidato Teste</h1>");
    });

    it("deve apresentar as informações profissionais", () => {
        const html = renderizarPerfilCandidato(criarCandidato());

        expect(html).toContain("Formação:");
        expect(html).toContain("Análise e Desenvolvimento de Sistemas");
        expect(html).toContain("Descrição:");
        expect(html).toContain("Desenvolvedor Full Stack");
        expect(html).toContain("TypeScript • React");
        expect(html).toContain("Estado:");
        expect(html).toContain("RJ");
    });

    it("deve preservar os botões de edição e exclusão", () => {
        const html = renderizarPerfilCandidato(criarCandidato());

        expect(html).toContain('id="botao-editar-candidato"');
        expect(html).toContain('id="botao-excluir-candidato"');
        expect(html).toContain("Editar Perfil");
        expect(html).toContain("Excluir conta");
    });

    it("deve preservar a seção de vagas disponíveis", () => {
        const html = renderizarPerfilCandidato(criarCandidato());

        expect(html).toContain('class="vagas-disponiveis"');
        expect(html).toContain("<h2>Vagas disponíveis</h2>");
        expect(html).toContain('id="lista-vagas"');
    });

    it("não deve exibir CPF e idade no perfil", () => {
        const html = renderizarPerfilCandidato(criarCandidato());

        expect(html).not.toContain("12345678900");
        expect(html).not.toContain("25");
    });

    it("deve escapar HTML no nome do candidato", () => {
        const candidato = criarCandidato({
            nome: "<script>alert('teste')</script>"
        });

        const html = renderizarPerfilCandidato(candidato);

        expect(html).toContain(
            "&lt;script&gt;alert(&#39;teste&#39;)&lt;/script&gt;"
        );
        expect(html).not.toContain("<script>");
    });

    it("deve escapar HTML na formação e descrição", () => {
        const candidato = criarCandidato({
            formacao: "<b>Graduação</b>",
            descricao: "Desenvolvedor & Analista"
        });

        const html = renderizarPerfilCandidato(candidato);

        expect(html).toContain("&lt;b&gt;Graduação&lt;/b&gt;");
        expect(html).toContain("Desenvolvedor &amp; Analista");
        expect(html).not.toContain("<b>Graduação</b>");
    });

    it("deve escapar HTML nas skills e no estado", () => {
        const candidato = criarCandidato({
            skills: ["C# & .NET", "<img src=x onerror=alert(1)>"],
            estado: "<RJ>"
        });

        const html = renderizarPerfilCandidato(candidato);

        expect(html).toContain(
            "C# &amp; .NET • &lt;img src=x onerror=alert(1)&gt;"
        );
        expect(html).toContain("&lt;RJ&gt;");
        expect(html).not.toContain("<img");
    });

    it("não deve modificar os dados originais do candidato", () => {
        const candidato = criarCandidato();
        const skillsOriginais = [...candidato.skills];

        renderizarPerfilCandidato(candidato);

        expect(candidato.skills).toEqual(skillsOriginais);
        expect(candidato.nome).toBe("Candidato Teste");
    });
});
