import { describe, expect, it } from "vitest";

import { obterTelaInicial, obterTelaDoMenu } from "./navegacaoService";

describe("navegacaoService", () => {
  describe("obterTelaInicial", () => {
    it("deve retornar empresa como tela inicial", () => {
      expect(obterTelaInicial()).toBe("empresa");
    });
  });

  describe("obterTelaDoMenu", () => {
    function criarElemento(tela?: string): Pick<HTMLElement, "dataset"> {
      return {
        dataset: {
          tela,
        },
      };
    }

    it.each(["empresa", "candidato", "cadastro-candidato", "cadastro-empresa"])(
      "deve aceitar a tela %s no menu",
      (tela) => {
        const elemento = criarElemento(tela);

        expect(obterTelaDoMenu(elemento)).toBe(tela);
      },
    );

    it("deve rejeitar cadastro-vaga no menu", () => {
      const elemento = criarElemento("cadastro-vaga");

      expect(obterTelaDoMenu(elemento)).toBeNull();
    });

    it("deve rejeitar uma tela desconhecida", () => {
      const elemento = criarElemento("configuracoes");

      expect(obterTelaDoMenu(elemento)).toBeNull();
    });

    it("deve retornar null quando não houver data-tela", () => {
      const elemento = criarElemento();

      expect(obterTelaDoMenu(elemento)).toBeNull();
    });
  });
});
