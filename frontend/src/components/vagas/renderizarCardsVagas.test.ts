
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderizarCardsVagas } from "./renderizarCardsVagas";
import { calcularAfinidade } from "../../services/utils/afinidadeService";

import type { Vaga } from "../../models/Vaga";
import type { Candidato } from "../../models/Candidato";

vi.mock("../../services/utils/afinidadeService", () => ({
    calcularAfinidade: vi.fn()
}));

const calcularAfinidadeMock = vi.mocked(calcularAfinidade);

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

function criarCandidato(dados: Partial<Candidato> = {}): Candidato {
    return {
        id: 1,
        nome: "Candidato Teste",
        cpf: "12345678900",
        idade: 25,
        formacao: "ADS",
        descricao: "Desenvolvedor",
        skills: ["TypeScript"],
        estado: "RJ",
        ...dados
    };
}

describe("renderizarCardsVagas", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        calcularAfinidadeMock.mockReturnValue(50);
    });

    it("deve retornar uma string vazia quando não houver vagas", () => {
        expect(renderizarCardsVagas([], criarCandidato())).toBe("");

        expect(calcularAfinidadeMock).not.toHaveBeenCalled();
    });

    it("deve renderizar um cartão para cada vaga", () => {
        const vagas = [
            criarVaga({ id: 1 }),
            criarVaga({ id: 2 })
        ];

        const html = renderizarCardsVagas(vagas, criarCandidato());

        expect(html.match(/class="card-vaga"/g)).toHaveLength(2);
    });

    it("deve apresentar o título e a descrição da vaga", () => {
        const html = renderizarCardsVagas(
            [criarVaga()],
            criarCandidato()
        );

        expect(html).toContain("<h3>Desenvolvedor Full Stack</h3>");
        expect(html).toContain("Desenvolvimento de aplicações web");
    });

    it("deve apresentar as skills separadas por ponto", () => {
        const vaga = criarVaga({
            skills: ["Java", "Groovy", "PostgreSQL"]
        });

        const html = renderizarCardsVagas([vaga], criarCandidato());

        expect(html).toContain("Java • Groovy • PostgreSQL");
    });

    it("deve apresentar o percentual de afinidade calculado", () => {
        calcularAfinidadeMock.mockReturnValue(75);

        const html = renderizarCardsVagas(
            [criarVaga()],
            criarCandidato()
        );

        expect(html).toContain("75%");
    });

    it("deve calcular a afinidade utilizando o candidato e a vaga", () => {
        const candidato = criarCandidato();
        const vaga = criarVaga();

        renderizarCardsVagas([vaga], candidato);

        expect(calcularAfinidadeMock)
            .toHaveBeenCalledExactlyOnceWith(candidato, vaga);
    });

    it("deve escapar HTML no título da vaga", () => {
        const vaga = criarVaga({
            titulo: "<script>alert('teste')</script>"
        });

        const html = renderizarCardsVagas([vaga], criarCandidato());

        expect(html).toContain(
            "&lt;script&gt;alert(&#39;teste&#39;)&lt;/script&gt;"
        );

        expect(html).not.toContain("<script>");
    });

    it("deve escapar HTML na descrição da vaga", () => {
        const vaga = criarVaga({
            descricao: "<img src=x onerror=alert(1)>"
        });

        const html = renderizarCardsVagas([vaga], criarCandidato());

        expect(html).toContain(
            "&lt;img src=x onerror=alert(1)&gt;"
        );

        expect(html).not.toContain("<img");
    });

    it("deve escapar caracteres especiais nas skills", () => {
        const vaga = criarVaga({
            skills: ["C# & .NET", "<React>"]
        });

        const html = renderizarCardsVagas([vaga], criarCandidato());

        expect(html).toContain("C# &amp; .NET • &lt;React&gt;");
    });

    it("não deve modificar os dados recebidos", () => {
        const vaga = criarVaga();
        const candidato = criarCandidato();

        const vagaOriginal = structuredClone(vaga);
        const candidatoOriginal = structuredClone(candidato);

        renderizarCardsVagas([vaga], candidato);

        expect(vaga).toEqual(vagaOriginal);
        expect(candidato).toEqual(candidatoOriginal);
    });
});
