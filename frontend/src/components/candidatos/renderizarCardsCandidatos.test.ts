import { describe, expect, it } from "vitest";

import { renderizarCardsCandidatos } from "./renderizarCardsCandidatos";

import type { Candidato } from "../../models/Candidato";

function criarCandidato(dados: Partial<Candidato> = {}): Candidato {
  return {
    id: 1,
    nome: "Candidato Teste",
    cpf: "12345678900",
    idade: 25,
    formacao: "Análise e Desenvolvimento de Sistemas",
    descricao: "Desenvolvedor",
    skills: ["TypeScript", "React"],
    estado: "RJ",
    ...dados,
  };
}

describe("renderizarCardsCandidatos", () => {
  it("deve retornar uma string vazia quando não houver candidatos", () => {
    expect(renderizarCardsCandidatos([])).toBe("");
  });

  it("deve renderizar um cartão para cada candidato", () => {
    const candidatos = [criarCandidato({ id: 10 }), criarCandidato({ id: 20 })];

    const html = renderizarCardsCandidatos(candidatos);

    expect(html.match(/class="card-candidato"/g)).toHaveLength(2);
  });

  it("deve numerar os candidatos a partir de 1001", () => {
    const candidatos = [criarCandidato({ id: 10 }), criarCandidato({ id: 20 })];

    const html = renderizarCardsCandidatos(candidatos);

    expect(html).toContain("<h3>Candidato #1001</h3>");
    expect(html).toContain("<h3>Candidato #1002</h3>");
    expect(html).not.toContain("<h3>Candidato #10</h3>");
  });

  it("deve apresentar a formação do candidato", () => {
    const candidato = criarCandidato({
      formacao: "Engenharia de Software",
    });

    const html = renderizarCardsCandidatos([candidato]);

    expect(html).toContain("Formação:");
    expect(html).toContain("Engenharia de Software");
  });

  it("deve apresentar as skills separadas por ponto", () => {
    const candidato = criarCandidato({
      skills: ["Java", "Groovy", "PostgreSQL"],
    });

    const html = renderizarCardsCandidatos([candidato]);

    expect(html).toContain("Java • Groovy • PostgreSQL");
  });

  it("não deve divulgar dados pessoais do candidato", () => {
    const candidato = criarCandidato({
      nome: "Nome Confidencial",
      cpf: "98765432100",
    });

    const html = renderizarCardsCandidatos([candidato]);

    expect(html).not.toContain("Nome Confidencial");
    expect(html).not.toContain("98765432100");
  });

  it("não deve modificar a lista original de candidatos", () => {
    const candidatos = [criarCandidato({ id: 2 }), criarCandidato({ id: 1 })];

    const idsOriginais = candidatos.map((candidato) => candidato.id);

    renderizarCardsCandidatos(candidatos);

    expect(candidatos.map((candidato) => candidato.id)).toEqual(idsOriginais);
  });


it("deve escapar tags HTML presentes na formação", () => {
    const candidato = criarCandidato({
        formacao: "<script>alert('teste')</script>"
    });

    const html = renderizarCardsCandidatos([candidato]);

    expect(html).toContain(
        "&lt;script&gt;alert(&#39;teste&#39;)&lt;/script&gt;"
    );

    expect(html).not.toContain("<script>");
});

it("deve escapar caracteres especiais presentes nas skills", () => {
    const candidato = criarCandidato({
        skills: ["C# & .NET", "<img src=x onerror=alert(1)>"]
    });

    const html = renderizarCardsCandidatos([candidato]);

    expect(html).toContain("C# &amp; .NET");
    expect(html).toContain("&lt;img src=x onerror=alert(1)&gt;");
    expect(html).not.toContain("<img");
});

it("deve preservar a separação entre as skills após escapar o HTML", () => {
    const candidato = criarCandidato({
        skills: ["React", "Node.js", "C# & .NET"]
    });

    const html = renderizarCardsCandidatos([candidato]);

    expect(html).toContain("React • Node.js • C# &amp; .NET");
});


});
