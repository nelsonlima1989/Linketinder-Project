import { describe, expect, it } from "vitest";
import { converterSkills } from "./skillUtils";

describe("converterSkills", () => {
    it("deve converter uma string em uma lista de skills", () => {
        expect(converterSkills("Java, React, SQL")).toEqual([
            "Java",
            "React",
            "SQL"
        ]);
    });

    it("deve remover espaços extras", () => {
        expect(converterSkills(" Java , React , SQL ")).toEqual([
            "Java",
            "React",
            "SQL"
        ]);
    });

    it("deve ignorar skills vazias", () => {
        expect(converterSkills("Java, , React,")).toEqual([
            "Java",
            "React"
        ]);
    });

    it("deve retornar uma lista vazia para string vazia", () => {
        expect(converterSkills("")).toEqual([]);
    });
});