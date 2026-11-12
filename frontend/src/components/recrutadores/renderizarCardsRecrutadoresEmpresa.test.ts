import { describe, expect, it } from "vitest";

import { renderizarCardsRecrutadoresEmpresa } from "./renderizarCardsRecrutadoresEmpresa";

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

describe("renderizarCardsRecrutadoresEmpresa", () => {
    it("deve exibir estado vazio quando não houver recrutadores", () => {
        const html = renderizarCardsRecrutadoresEmpresa([]);

        expect(html).toContain("Nenhum recrutador cadastrado.");
        expect(html).not.toContain("card-recrutador-empresa");
    });

    it("deve renderizar nome, área, descrição e e-mail", () => {
        const html = renderizarCardsRecrutadoresEmpresa([criarRecrutador()]);

        expect(html).toContain("Camila Ferreira");
        expect(html).toContain("Engenharia de Software");
        expect(html).toContain("Recrutadora técnica");
        expect(html).toContain("camila@empresa.com");
    });

    it("deve renderizar um card por recrutador", () => {
        const html = renderizarCardsRecrutadoresEmpresa([
            criarRecrutador({ id: 1 }),
            criarRecrutador({ id: 2 })
        ]);

        expect(html.match(/class="card-recrutador-empresa"/g)).toHaveLength(2);
    });

    it("deve renderizar botões de editar e excluir com data-recrutador-id", () => {
        const html = renderizarCardsRecrutadoresEmpresa([criarRecrutador()]);

        expect(html).toContain('class="botao-editar-recrutador"');
        expect(html).toContain('class="botao-excluir-recrutador"');
        expect(html.match(/data-recrutador-id="7"/g)).toHaveLength(2);
    });

    it("não deve exibir CPF, data de nascimento nem senha (anonimato/RN-06)", () => {
        const recrutador = {
            ...criarRecrutador(),
            senha: "SenhaSecreta123",
            hashSenha: "pbkdf2_sha256$65536$x$y"
        } as Recrutador;

        const html = renderizarCardsRecrutadoresEmpresa([recrutador]);

        expect(html).not.toContain("12345678909");
        expect(html).not.toContain("1992-04-15");
        expect(html).not.toContain("SenhaSecreta123");
        expect(html).not.toContain("pbkdf2");
        expect(html.toLowerCase()).not.toContain("senha");
        expect(html).not.toContain("CPF");
    });

    it("deve escapar HTML em todos os campos textuais", () => {
        const html = renderizarCardsRecrutadoresEmpresa([
            criarRecrutador({
                nome: "<script>alert(1)</script>",
                areaRecrutamento: "<img src=x onerror=alert(1)>",
                descricao: "<b>negrito</b>",
                email: 'a"<i>@x.com'
            })
        ]);

        expect(html).not.toContain("<script>");
        expect(html).not.toContain("<img");
        expect(html).not.toContain("<b>");
        expect(html).not.toContain("<i>");
        expect(html).toContain("&lt;script&gt;");
    });

    it("não deve modificar os dados originais", () => {
        const lista = [criarRecrutador()];
        const copia = structuredClone(lista);

        renderizarCardsRecrutadoresEmpresa(lista);

        expect(lista).toEqual(copia);
    });
});
