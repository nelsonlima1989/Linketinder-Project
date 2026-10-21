import { describe, expect, it } from "vitest";

import { calcularAfinidade } from "./afinidadeService";

import type { Candidato } from "../../models/Candidato";
import type { Vaga } from "../../models/Vaga";

describe("calcularAfinidade", () => {

    function criarCandidato(skills: string[]): Candidato {
        return {
            skills
        } as Candidato;
    }

    function criarVaga(skills: string[]): Vaga {
        return {
            skills
        } as Vaga;
    }

    it("deve retornar 0 quando a vaga nao possui skills", () => {
        const candidato = criarCandidato(["Java", "Groovy"]);
        const vaga = criarVaga([]);

        expect(calcularAfinidade(candidato, vaga)).toBe(0);
    });

    it("deve retornar 0 quando o candidato nao possui skills", () => {
        const candidato = criarCandidato([]);
        const vaga = criarVaga(["Java"]);

        expect(calcularAfinidade(candidato, vaga)).toBe(0);
    });

    it("deve retornar 0 quando nao existem skills em comum", () => {
        const candidato = criarCandidato(["Python"]);
        const vaga = criarVaga(["Java", "Groovy"]);

        expect(calcularAfinidade(candidato, vaga)).toBe(0);
    });

    it("deve calcular afinidade parcial de 50 por cento", () => {
        const candidato = criarCandidato(["Java", "SQL"]);
        const vaga = criarVaga(["Java", "Groovy"]);

        expect(calcularAfinidade(candidato, vaga)).toBe(50);
    });

    it("deve calcular afinidade total de 100 por cento", () => {
        const candidato = criarCandidato(["Java", "Groovy", "SQL"]);
        const vaga = criarVaga(["Java", "Groovy"]);

        expect(calcularAfinidade(candidato, vaga)).toBe(100);
    });

    it("deve ignorar letras maiusculas e espacos", () => {
        const candidato = criarCandidato([" JAVA ", "groovy"]);
        const vaga = criarVaga(["java", "GROOVY "]);

        expect(calcularAfinidade(candidato, vaga)).toBe(100);
    });

    it("deve arredondar o percentual de afinidade", () => {
        const candidato = criarCandidato(["Java"]);
        const vaga = criarVaga(["Java", "Groovy", "SQL"]);

        expect(calcularAfinidade(candidato, vaga)).toBe(33);
    });

    it("deve desconsiderar skills duplicadas na vaga", () => {
    const candidato = criarCandidato(["Java"]);
    const vaga = criarVaga(["Java", "JAVA", "Groovy"]);

        expect(calcularAfinidade(candidato, vaga)).toBe(50);
    });

});