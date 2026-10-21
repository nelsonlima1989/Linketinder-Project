
import { describe, expect, it } from "vitest";
import { obterEmpresaIdParaRenderizacao } from "./contextoTelaService";


describe("obterEmpresaIdParaRenderizacao", () => {
    it("deve manter a empresa selecionada ao exibir seu perfil", () => {
        expect(
            obterEmpresaIdParaRenderizacao("empresa", 3)
        ).toBe(3);
    });

    it("não deve enviar empresaId ao formulário de nova empresa", () => {
        expect(
            obterEmpresaIdParaRenderizacao("cadastro-empresa", 3)
        ).toBeUndefined();
    });

    it("deve enviar empresaId quando estiver editando empresa", () => {
        expect(
            obterEmpresaIdParaRenderizacao("cadastro-empresa", 3, 3)
        ).toBe(3);
    });

    it("deve preservar empresaId ao cadastrar uma vaga", () => {
        expect(
            obterEmpresaIdParaRenderizacao("cadastro-vaga", 3)
        ).toBe(3);
    });
});
