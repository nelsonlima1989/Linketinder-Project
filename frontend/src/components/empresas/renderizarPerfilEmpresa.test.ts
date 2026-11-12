
import { describe, expect, it, vi } from "vitest";

import { renderizarPerfilEmpresa } from "./renderizarPerfilEmpresa";
import { renderizarGraficoSkills } from "./renderizarGraficoSkills";

import type { Empresa } from "../../models/Empresa";

vi.mock("./renderizarGraficoSkills", () => ({
    renderizarGraficoSkills: vi.fn(() => '<div id="grafico-teste"></div>')
}));

function criarEmpresa(dados: Partial<Empresa> = {}): Empresa {
    return {
        id: 1,
        nome: "Empresa Teste",
        cnpj: "12345678000199",
        descricao: "Empresa de tecnologia",
        estado: "RJ",
        ...dados
    };
}

describe("renderizarPerfilEmpresa", () => {
    it("deve renderizar a estrutura do perfil", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).toContain('class="perfil-empresa"');
        expect(html).toContain('class="cabecalho-empresa"');
        expect(html).toContain("<h1>Empresa Teste</h1>");
    });

    it("deve apresentar as informações da empresa", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).toContain("Empresa de tecnologia");
        expect(html).toContain("<strong>Estado:</strong>");
        expect(html).toContain("RJ");
    });

    it("deve preservar os botões de ação", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).toContain('id="botao-nova-vaga"');
        expect(html).toContain('id="botao-editar-empresa"');
        expect(html).toContain('id="botao-excluir-empresa"');

        expect(html).toContain("Cadastrar nova vaga");
        expect(html).toContain("Editar Perfil");
        expect(html).toContain("Excluir conta");
    });

    it("deve preservar a área de candidatos disponíveis", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).toContain('class="candidatos"');
        expect(html).toContain('id="lista-candidatos"');
        expect(html).toContain("Candidatos disponíveis");
    });

    it("deve preservar a área de vagas da empresa", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).toContain('class="vagas-empresa"');
        expect(html).toContain('id="lista-vagas-empresa"');
        expect(html).toContain("Minhas vagas");
    });

    it("deve incorporar o HTML do gráfico de skills", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(renderizarGraficoSkills).toHaveBeenCalled();
        expect(html).toContain('<div id="grafico-teste"></div>');
    });

    it("deve renderizar o botão de novo recrutador", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).toContain('id="botao-novo-recrutador"');
        expect(html).toContain("Cadastrar novo recrutador");
    });

    it("deve renderizar a seção Meus recrutadores", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).toContain('class="recrutadores-empresa"');
        expect(html).toContain("Meus recrutadores");
        expect(html).toContain('id="lista-recrutadores-empresa"');
    });

    it("deve renderizar apenas o contêiner de recrutadores, sem dados pessoais", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).not.toContain("card-recrutador-empresa");
        expect(html).not.toContain("cpf");
    });

    it("não deve exibir o CNPJ da empresa", () => {
        const html = renderizarPerfilEmpresa(criarEmpresa());

        expect(html).not.toContain("12345678000199");
    });

    it("deve escapar HTML no nome da empresa", () => {
        const empresa = criarEmpresa({
            nome: "<script>alert('teste')</script>"
        });

        const html = renderizarPerfilEmpresa(empresa);

        expect(html).toContain(
            "&lt;script&gt;alert(&#39;teste&#39;)&lt;/script&gt;"
        );

        expect(html).not.toContain("<script>");
    });

    it("deve escapar HTML na descrição da empresa", () => {
        const empresa = criarEmpresa({
            descricao: "<img src=x onerror=alert(1)>"
        });

        const html = renderizarPerfilEmpresa(empresa);

        expect(html).toContain(
            "&lt;img src=x onerror=alert(1)&gt;"
        );

        expect(html).not.toContain("<img");
    });

    it("deve escapar HTML no estado da empresa", () => {
        const empresa = criarEmpresa({
            estado: "RJ & SP"
        });

        const html = renderizarPerfilEmpresa(empresa);

        expect(html).toContain("RJ &amp; SP");
    });

    it("não deve modificar os dados originais da empresa", () => {
        const empresa = criarEmpresa();
        const dadosOriginais = structuredClone(empresa);

        renderizarPerfilEmpresa(empresa);

        expect(empresa).toEqual(dadosOriginais);
    });
});
