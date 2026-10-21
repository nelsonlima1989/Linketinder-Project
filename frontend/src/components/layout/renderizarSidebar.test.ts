
import { describe, expect, it } from "vitest";

import { renderizarSidebar } from "./renderizarSidebar";

describe("renderizarSidebar", () => {
    it("deve renderizar a estrutura da sidebar", () => {
        const html = renderizarSidebar();

        expect(html).toContain('<aside class="sidebar">');
        expect(html).toContain('<nav class="sidebar-menu">');
        expect(html).toContain("LINKETINDER");
        expect(html).toContain("Plataforma de talentos");
    });

    it("deve renderizar as quatro opções de navegação", () => {
        const html = renderizarSidebar();

        expect(html).toContain('data-tela="empresa"');
        expect(html).toContain('data-tela="candidato"');
        expect(html).toContain('data-tela="cadastro-candidato"');
        expect(html).toContain('data-tela="cadastro-empresa"');
    });

    it("deve marcar empresa como opção inicial ativa", () => {
        const html = renderizarSidebar();

        expect(html).toMatch(
            /class="menu-item ativo"\s+data-tela="empresa"/
        );
    });

    it("deve renderizar os títulos das opções", () => {
        const html = renderizarSidebar();

        expect(html).toContain("<span>Empresa</span>");
        expect(html).toContain("<span>Candidato</span>");
        expect(html).toContain("<span>Novo candidato</span>");
        expect(html).toContain("<span>Nova empresa</span>");
    });

    it("deve preservar a separação visual dos cadastros", () => {
        const html = renderizarSidebar();

        expect(html).toContain('class="menu-separador"');
        expect(html).toContain('class="menu-titulo"');
        expect(html).toContain("Cadastros");
    });

    it("não deve exibir cadastro de vaga no menu lateral", () => {
        const html = renderizarSidebar();

        expect(html).not.toContain('data-tela="cadastro-vaga"');
    });

    it("deve renderizar exatamente quatro botões de menu", () => {
        const html = renderizarSidebar();

        const botoes = html.match(/class="menu-item(?: ativo)?"/g) ?? [];

        expect(botoes).toHaveLength(4);
    });
});
