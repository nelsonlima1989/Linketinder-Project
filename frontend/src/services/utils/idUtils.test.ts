import { describe, expect, it } from "vitest";
import { calcularProximoId } from "./idUtils";

describe("calcularProximoId", () => {
    it("deve retornar 1 para uma lista vazia", () => {
        expect(calcularProximoId([])).toBe(1);
    });

    it("deve retornar o maior ID mais 1", () => {
        const registros = [
            { id: 2 },
            { id: 8 },
            { id: 5 }
        ];

        expect(calcularProximoId(registros)).toBe(9);
    });

    it("deve funcionar com objetos de diferentes estruturas", () => {
        const registros = [
            { id: 3, nome: "Empresa A" },
            { id: 7, nome: "Empresa B" }
        ];

        expect(calcularProximoId(registros)).toBe(8);
    });
});