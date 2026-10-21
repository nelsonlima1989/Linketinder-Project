import { describe, expect, it } from "vitest";
import type { Candidato } from "../../models/Candidato";
import { contarSkills, ordenarSkills } from "./skillService";

function criarCandidatoMock(id: number, skills: string[]): Candidato {
  return {
    id,
    nome: `Candidato ${id}`,
    cpf: "12345678900",
    idade: 25,
    formacao: "ADS",
    descricao: "Desenvolvedor",
    skills,
    estado: "RJ",
  };
}

describe("skillService", () => {
  describe("contarSkills", () => {
    it("deve contabilizar as skills dos candidatos", () => {
      const candidatos = [
        criarCandidatoMock(1, ["Java", "React"]),
        criarCandidatoMock(2, ["Java", "SQL"]),
      ];

      expect(contarSkills(candidatos)).toEqual({
        Java: 2,
        React: 1,
        SQL: 1,
      });
    });

    it("deve retornar um objeto vazio quando não houver candidatos", () => {
      expect(contarSkills([])).toEqual({});
    });

    it("deve contabilizar candidatos sem skills", () => {
      const candidatos = [
        criarCandidatoMock(1, []),
        criarCandidatoMock(2, ["Java"]),
      ];

      expect(contarSkills(candidatos)).toEqual({
        Java: 1,
      });
    });

    it("deve preservar a diferenciação entre maiúsculas e minúsculas", () => {
      const candidatos = [
        criarCandidatoMock(1, ["Java"]),
        criarCandidatoMock(2, ["java"]),
      ];

      expect(contarSkills(candidatos)).toEqual({
        Java: 1,
        java: 1,
      });
    });
  });

  describe("ordenarSkills", () => {
    it("deve ordenar skills por quantidade decrescente", () => {
      const contagem = {
        React: 2,
        Java: 5,
        SQL: 3,
      };

      expect(Object.keys(ordenarSkills(contagem))).toEqual([
        "Java",
        "SQL",
        "React",
      ]);
    });

    it("deve retornar um objeto vazio quando não houver skills", () => {
      expect(ordenarSkills({})).toEqual({});
    });
  });
});
