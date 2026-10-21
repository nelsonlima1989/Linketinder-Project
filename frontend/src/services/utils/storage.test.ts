import { beforeEach, describe, expect, it, vi } from "vitest";
import { carregarDados, salvarDados } from "./storage";

describe("storage", () => {

    const dadosArmazenados = new Map<string, string>();

    beforeEach(() => {
        dadosArmazenados.clear();

        vi.stubGlobal("localStorage", {
            setItem: (chave: string, valor: string): void => {
                dadosArmazenados.set(chave, valor);
            },

            getItem: (chave: string): string | null => {
                return dadosArmazenados.get(chave) ?? null;
            }
        });
    });

    it("deve salvar dados no localStorage", () => {
        const candidato = {
            id: 1,
            nome: "Candidato Teste"
        };

        salvarDados("candidato", candidato);

        expect(dadosArmazenados.get("candidato"))
            .toBe(JSON.stringify(candidato));
    });

    it("deve carregar dados armazenados", () => {
        const empresa = {
            id: 1,
            nome: "Empresa Teste"
        };

        dadosArmazenados.set("empresa", JSON.stringify(empresa));

        const resultado = carregarDados<typeof empresa>("empresa");

        expect(resultado).toEqual(empresa);
    });

    it("deve retornar null quando a chave nao existir", () => {
        const resultado = carregarDados("inexistente");

        expect(resultado).toBeNull();
    });

    it("deve sobrescrever dados de uma mesma chave", () => {
        salvarDados("candidato", { id: 1 });
        salvarDados("candidato", { id: 2 });

        const resultado = carregarDados<{ id: number }>("candidato");

        expect(resultado).toEqual({ id: 2 });
    });

    it("deve propagar erro quando o JSON armazenado for invalido", () => {
        dadosArmazenados.set("candidato", "{json invalido");

        expect(() => carregarDados("candidato"))
            .toThrow(SyntaxError);
    });

});