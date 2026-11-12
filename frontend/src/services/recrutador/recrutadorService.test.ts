import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
    listarRecrutadores,
    obterProximoId,
    obterRecrutadoresPorEmpresa,
    obterRecrutadorPorId,
    validarDadosRecrutador,
    criarRecrutador,
    editarRecrutador,
    excluirRecrutador,
    excluirRecrutadoresPorEmpresa,
} from "./recrutadorService";

import {
    bloquearRecrutador,
    liberarBloqueioRecrutador,
} from "./edicaoRecrutadorService";

import { recrutadores as recrutadoresSeed } from "../../data/dados";
import type { DadosFormularioRecrutador, Recrutador } from "../../models/Recrutador";

function criarFormularioValido(
    sobrescritas: Partial<DadosFormularioRecrutador> = {},
): DadosFormularioRecrutador {
    return {
        nome: "Fernanda Oliveira",
        areaRecrutamento: "Engenharia de Software",
        descricao: "Recrutadora técnica focada em vagas backend e arquitetura.",
        email: "fernanda.oliveira@empresa.com",
        cpf: "12345678909",
        dataNascimento: "1995-05-15",
        senha: "SenhaSegura@2026",
        ...sobrescritas,
    };
}

function criarRecrutadorMock(
    id: number,
    empresaId: number = 1,
    cpf: string = `1000000000${id}`.slice(-11),
    email: string = `recrutador${id}@empresa.com`,
): Recrutador {
    return {
        id,
        empresaId,
        nome: `Recrutador ${id}`,
        areaRecrutamento: "Tecnologia",
        descricao: "Atuação em seleção técnica",
        email,
        cpf,
        dataNascimento: "1992-03-10",
    };
}

describe("recrutadorService", () => {
    let armazenamento: Map<string, string>;

    beforeEach(() => {
        vi.useFakeTimers();
        vi.setSystemTime(new Date("2026-06-15T12:00:00Z"));

        armazenamento = new Map<string, string>();

        vi.stubGlobal("localStorage", {
            getItem: vi.fn((chave: string) => armazenamento.get(chave) ?? null),
            setItem: vi.fn((chave: string, valor: string) => {
                armazenamento.set(chave, valor);
            }),
            removeItem: vi.fn((chave: string) => {
                armazenamento.delete(chave);
            }),
            clear: vi.fn(() => {
                armazenamento.clear();
            }),
        });

        vi.stubGlobal("window", {
            setInterval: globalThis.setInterval,
            clearInterval: globalThis.clearInterval,
        });

        liberarBloqueioRecrutador();
    });

    afterEach(() => {
        liberarBloqueioRecrutador();
        vi.useRealTimers();
        vi.unstubAllGlobals();
    });

    it("deve carregar os recrutadores iniciais do seed quando o localStorage estiver vazio", () => {
        const lista = listarRecrutadores();

        expect(lista).toEqual(recrutadoresSeed);
        expect(armazenamento.has("recrutadores")).toBe(true);
    });

    it("deve listar recrutadores já armazenados no localStorage", () => {
        const existentes = [criarRecrutadorMock(1, 1)];
        armazenamento.set("recrutadores", JSON.stringify(existentes));

        expect(listarRecrutadores()).toEqual(existentes);
    });

    it("deve retornar ID 1 quando não houver recrutadores cadastrados", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        expect(obterProximoId()).toBe(1);
    });

    it("deve calcular o próximo ID disponível com base no maior ID existente", () => {
        armazenamento.set(
            "recrutadores",
            JSON.stringify([
                criarRecrutadorMock(2, 1),
                criarRecrutadorMock(7, 2),
                criarRecrutadorMock(4, 3),
            ]),
        );

        expect(obterProximoId()).toBe(8);
    });

    it("deve cadastrar recrutador válido e associá-lo à empresa (CA-01)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        const novoId = criarRecrutador(criarFormularioValido(), 1);

        expect(novoId).toBe(1);

        const recrutadorSalvo = obterRecrutadorPorId(1, 1);

        expect(recrutadorSalvo).toEqual({
            id: 1,
            empresaId: 1,
            nome: "Fernanda Oliveira",
            areaRecrutamento: "Engenharia de Software",
            descricao: "Recrutadora técnica focada em vagas backend e arquitetura.",
            email: "fernanda.oliveira@empresa.com",
            cpf: "12345678909",
            dataNascimento: "1995-05-15",
        });
    });

    it("não deve persistir senha nem hash no localStorage ao criar ou editar recrutador (CA-08 / RN-06)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        const senhaSubmetida = "SegredoSuperForte#2026";

        const id = criarRecrutador(
            criarFormularioValido({ senha: senhaSubmetida }),
            1,
        );

        const jsonAposCriacao = armazenamento.get("recrutadores") ?? "";

        expect(jsonAposCriacao).not.toContain("senha");
        expect(jsonAposCriacao).not.toContain(senhaSubmetida);
        expect(jsonAposCriacao).not.toContain("pbkdf2");

        const objetoSalvo = JSON.parse(jsonAposCriacao)[0];
        expect("senha" in objetoSalvo).toBe(false);

        const novaSenhaEdicao = "OutraSenhaSecreta$2026";
        editarRecrutador(
            id,
            1,
            criarFormularioValido({
                nome: "Fernanda Oliveira Atualizada",
                senha: novaSenhaEdicao,
            }),
        );

        const jsonAposEdicao = armazenamento.get("recrutadores") ?? "";
        expect(jsonAposEdicao).not.toContain("senha");
        expect(jsonAposEdicao).not.toContain(novaSenhaEdicao);
    });

    it("deve permitir múltiplos recrutadores para a mesma empresa e filtrá-los corretamente (CA-02 / 1:N)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        criarRecrutador(
            criarFormularioValido({
                nome: "Recrutador A",
                cpf: "11122233344",
                email: "a@empresa1.com",
            }),
            1,
        );

        criarRecrutador(
            criarFormularioValido({
                nome: "Recrutador B",
                cpf: "55566677788",
                email: "b@empresa1.com",
            }),
            1,
        );

        criarRecrutador(
            criarFormularioValido({
                nome: "Recrutador C",
                cpf: "99988877766",
                email: "c@empresa2.com",
            }),
            2,
        );

        const daEmpresa1 = obterRecrutadoresPorEmpresa(1);
        const daEmpresa2 = obterRecrutadoresPorEmpresa(2);

        expect(daEmpresa1).toHaveLength(2);
        expect(daEmpresa1.map((r) => r.nome)).toEqual([
            "Recrutador A",
            "Recrutador B",
        ]);
        expect(daEmpresa2).toHaveLength(1);
        expect(daEmpresa2[0].nome).toBe("Recrutador C");
    });

    it("deve rejeitar cadastro para empresaId inválido ou empresa inexistente (CA-03 / VAL-01)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        expect(() => criarRecrutador(criarFormularioValido(), 0)).toThrow(
            "Identificador da empresa inválido.",
        );

        expect(() => criarRecrutador(criarFormularioValido(), -5)).toThrow(
            "Identificador da empresa inválido.",
        );

        expect(() => criarRecrutador(criarFormularioValido(), 999)).toThrow(
            "Empresa não encontrada.",
        );

        expect(listarRecrutadores()).toEqual([]);
    });

    it("deve rejeitar CPF duplicado ou e-mail duplicado (case-insensitive) entre recrutadores (CA-04 / VAL-07)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        criarRecrutador(
            criarFormularioValido({
                cpf: "12345678901",
                email: "ana@empresa.com",
            }),
            1,
        );

        expect(() =>
            criarRecrutador(
                criarFormularioValido({
                    cpf: "12345678901",
                    email: "outro@empresa.com",
                }),
                2,
            ),
        ).toThrow("CPF já cadastrado para outro recrutador.");

        expect(() =>
            criarRecrutador(
                criarFormularioValido({
                    cpf: "98765432100",
                    email: "ANA@EMPRESA.COM",
                }),
                2,
            ),
        ).toThrow("E-mail já cadastrado para outro recrutador.");
    });

    it("deve rejeitar CPF com formato inválido e e-mail malformado (CA-05 / VAL-05 / VAL-06)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        expect(() =>
            criarRecrutador(
                criarFormularioValido({ cpf: "123.456.789-01" }),
                1,
            ),
        ).toThrow("CPF inválido. Informe exatamente 11 dígitos numéricos.");

        expect(() =>
            criarRecrutador(criarFormularioValido({ cpf: "1234567890" }), 1),
        ).toThrow("CPF inválido. Informe exatamente 11 dígitos numéricos.");

        expect(() =>
            criarRecrutador(criarFormularioValido({ cpf: "1234567890a" }), 1),
        ).toThrow("CPF inválido. Informe exatamente 11 dígitos numéricos.");

        expect(() =>
            criarRecrutador(
                criarFormularioValido({ email: "email-invalido" }),
                1,
            ),
        ).toThrow("E-mail do recrutador inválido.");
    });

    it("deve rejeitar campos obrigatórios vazios ou acima do limite (VAL-02 / VAL-03 / VAL-04)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        expect(() =>
            validarDadosRecrutador(criarFormularioValido({ nome: "   " }), {
                empresaId: 1,
            }),
        ).toThrow("Nome do recrutador inválido.");

        expect(() =>
            validarDadosRecrutador(
                criarFormularioValido({ nome: "A".repeat(151) }),
                { empresaId: 1 },
            ),
        ).toThrow("Nome do recrutador inválido.");

        expect(() =>
            validarDadosRecrutador(
                criarFormularioValido({ areaRecrutamento: "  " }),
                { empresaId: 1 },
            ),
        ).toThrow("Área de recrutamento inválida.");

        expect(() =>
            validarDadosRecrutador(
                criarFormularioValido({ areaRecrutamento: "B".repeat(101) }),
                { empresaId: 1 },
            ),
        ).toThrow("Área de recrutamento inválida.");

        expect(() =>
            validarDadosRecrutador(
                criarFormularioValido({ descricao: "   " }),
                { empresaId: 1 },
            ),
        ).toThrow("Descrição do recrutador inválida.");
    });

    it("deve validar data de nascimento e exigir idade mínima de 18 anos (CA-06 / VAL-08)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        // Data atual fixada no teste: 2026-06-15
        // Menor de 18 anos (faz 18 apenas amanhã: 2008-06-16)
        expect(() =>
            criarRecrutador(
                criarFormularioValido({ dataNascimento: "2008-06-16" }),
                1,
            ),
        ).toThrow(
            "Data de nascimento inválida. O recrutador deve ter pelo menos 18 anos.",
        );

        // Data no futuro
        expect(() =>
            criarRecrutador(
                criarFormularioValido({ dataNascimento: "2027-01-01" }),
                1,
            ),
        ).toThrow(
            "Data de nascimento inválida. O recrutador deve ter pelo menos 18 anos.",
        );

        // Data calendário inexistente (31 de fevereiro)
        expect(() =>
            criarRecrutador(
                criarFormularioValido({ dataNascimento: "1995-02-31" }),
                1,
            ),
        ).toThrow(
            "Data de nascimento inválida. O recrutador deve ter pelo menos 18 anos.",
        );

        // Exatamente 18 anos hoje (2008-06-15) -> válido
        const id = criarRecrutador(
            criarFormularioValido({ dataNascimento: "2008-06-15" }),
            1,
        );
        expect(id).toBe(1);
    });

    it("deve rejeitar senha curta (< 8 caracteres) no cadastro ou quando preenchida na edição (VAL-09)", () => {
        armazenamento.set("recrutadores", JSON.stringify([]));

        expect(() =>
            criarRecrutador(criarFormularioValido({ senha: "1234567" }), 1),
        ).toThrow("Senha inválida. Deve possuir entre 8 e 128 caracteres.");

        expect(() =>
            criarRecrutador(criarFormularioValido({ senha: "" }), 1),
        ).toThrow("Senha inválida. Deve possuir entre 8 e 128 caracteres.");

        const id = criarRecrutador(criarFormularioValido(), 1);

        // Na edição, senha em branco é permitida
        expect(
            editarRecrutador(id, 1, criarFormularioValido({ senha: "" })),
        ).toBe(true);

        // Na edição, se informar nova senha curta, deve rejeitar
        expect(() =>
            editarRecrutador(id, 1, criarFormularioValido({ senha: "curta" })),
        ).toThrow("Senha inválida. Deve possuir entre 8 e 128 caracteres.");
    });

    it("deve atualizar recrutador mantendo o vínculo com a empresa original (CA-09 / RN-02)", () => {
        armazenamento.set(
            "recrutadores",
            JSON.stringify([criarRecrutadorMock(1, 3, "12345678909", "original@empresa.com")]),
        );

        const atualizou = editarRecrutador(1, 3, {
            nome: "Nome Atualizado",
            areaRecrutamento: "Nova Área",
            descricao: "Nova descrição",
            email: "original@empresa.com",
            cpf: "12345678909",
            dataNascimento: "1990-10-20",
        });

        expect(atualizou).toBe(true);
        expect(obterRecrutadorPorId(1)).toEqual({
            id: 1,
            empresaId: 3,
            nome: "Nome Atualizado",
            areaRecrutamento: "Nova Área",
            descricao: "Nova descrição",
            email: "original@empresa.com",
            cpf: "12345678909",
            dataNascimento: "1990-10-20",
        });
    });

    it("deve rejeitar CPF ou e-mail de outro recrutador na edição (CA-04 / VAL-07)", () => {
        armazenamento.set(
            "recrutadores",
            JSON.stringify([
                criarRecrutadorMock(1, 1, "11111111111", "um@empresa.com"),
                criarRecrutadorMock(2, 1, "22222222222", "dois@empresa.com"),
            ]),
        );

        expect(() =>
            editarRecrutador(
                2,
                1,
                criarFormularioValido({
                    cpf: "11111111111",
                    email: "dois@empresa.com",
                    senha: "",
                }),
            ),
        ).toThrow("CPF já cadastrado para outro recrutador.");

        expect(() =>
            editarRecrutador(
                2,
                1,
                criarFormularioValido({
                    cpf: "22222222222",
                    email: "UM@empresa.com",
                    senha: "",
                }),
            ),
        ).toThrow("E-mail já cadastrado para outro recrutador.");

        expect(obterRecrutadorPorId(2)?.cpf).toBe("22222222222");
        expect(obterRecrutadorPorId(2)?.email).toBe("dois@empresa.com");
    });

    it("deve permitir manter o próprio CPF e e-mail na edição", () => {
        armazenamento.set(
            "recrutadores",
            JSON.stringify([
                criarRecrutadorMock(1, 1, "11111111111", "um@empresa.com"),
                criarRecrutadorMock(2, 1, "22222222222", "dois@empresa.com"),
            ]),
        );

        expect(
            editarRecrutador(
                2,
                1,
                criarFormularioValido({
                    nome: "Novo Nome",
                    cpf: "22222222222",
                    email: "DOIS@empresa.com",
                    senha: "",
                }),
            ),
        ).toBe(true);

        expect(obterRecrutadorPorId(2)?.nome).toBe("Novo Nome");
        expect(obterRecrutadorPorId(2)?.cpf).toBe("22222222222");
    });

    it("não deve editar recrutador pertencente a outra empresa ou inexistente", () => {
        armazenamento.set(
            "recrutadores",
            JSON.stringify([criarRecrutadorMock(1, 2)]),
        );

        expect(editarRecrutador(1, 1, criarFormularioValido())).toBe(false);
        expect(editarRecrutador(99, 2, criarFormularioValido())).toBe(false);
    });

    it("deve excluir recrutador individualmente mantendo os demais registros intactos (CA-10)", () => {
        const rec1 = criarRecrutadorMock(1, 1);
        const rec2 = criarRecrutadorMock(2, 1);
        const rec3 = criarRecrutadorMock(3, 2);

        armazenamento.set(
            "recrutadores",
            JSON.stringify([rec1, rec2, rec3]),
        );

        expect(excluirRecrutador(1, 1)).toBe(true);
        expect(listarRecrutadores()).toEqual([rec2, rec3]);
    });

    it("não deve excluir recrutador bloqueado em edição, inexistente ou de outra empresa (CA-09)", () => {
        const rec1 = criarRecrutadorMock(1, 1);
        armazenamento.set("recrutadores", JSON.stringify([rec1]));

        expect(excluirRecrutador(1, 2)).toBe(false);
        expect(excluirRecrutador(99, 1)).toBe(false);

        bloquearRecrutador(1, 1);
        expect(excluirRecrutador(1, 1)).toBe(false);
        expect(listarRecrutadores()).toEqual([rec1]);
    });

    it("deve excluir em cascata todos os recrutadores da empresa informada (CA-11)", () => {
        const recEmpresa1A = criarRecrutadorMock(1, 1);
        const recEmpresa2 = criarRecrutadorMock(2, 2);
        const recEmpresa1B = criarRecrutadorMock(3, 1);

        armazenamento.set(
            "recrutadores",
            JSON.stringify([recEmpresa1A, recEmpresa2, recEmpresa1B]),
        );

        excluirRecrutadoresPorEmpresa(1);

        expect(listarRecrutadores()).toEqual([recEmpresa2]);
    });
});
