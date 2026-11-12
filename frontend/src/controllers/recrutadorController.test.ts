import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  configurarCriacaoRecrutador,
  configurarEdicaoRecrutador,
  configurarExclusaoRecrutador,
  configurarFormularioRecrutador,
  extrairDadosRecrutador,
} from "./recrutadorController";

import {
  criarRecrutador,
  editarRecrutador,
  excluirRecrutador,
  obterRecrutadorPorId,
} from "../services/recrutador/recrutadorService";

import {
  bloquearRecrutador,
  liberarBloqueioRecrutador,
  recrutadorEstaBloqueado,
} from "../services/recrutador/edicaoRecrutadorService";

import type { Recrutador } from "../models/Recrutador";

vi.mock("../services/recrutador/recrutadorService", () => ({
  criarRecrutador: vi.fn(),
  editarRecrutador: vi.fn(),
  excluirRecrutador: vi.fn(),
  obterRecrutadorPorId: vi.fn(),
}));

vi.mock("../services/recrutador/edicaoRecrutadorService", () => ({
  bloquearRecrutador: vi.fn(),
  liberarBloqueioRecrutador: vi.fn(),
  recrutadorEstaBloqueado: vi.fn(),
}));

type ElementoFalso = { addEventListener: ReturnType<typeof vi.fn> };

function obterCallback(
  elemento: ElementoFalso,
  nomeEvento: string,
): (evento?: Event) => void {
  const registro = elemento.addEventListener.mock.calls.find(
    ([evento]) => evento === nomeEvento,
  );

  expect(registro).toBeDefined();

  return registro![1] as (evento?: Event) => void;
}

const DADOS_FORMULARIO: Record<string, string> = {
  nome: "Camila Ferreira",
  areaRecrutamento: "Engenharia",
  descricao: "Recrutadora técnica",
  email: "camila@empresa.com",
  cpf: "12345678909",
  dataNascimento: "1992-04-15",
  senha: "SenhaSegura@2026",
};

describe("extrairDadosRecrutador", () => {
  it("deve extrair todos os campos do formulário", () => {
    const dados = new FormData();

    for (const [campo, valor] of Object.entries(DADOS_FORMULARIO)) {
      dados.set(campo, valor);
    }

    expect(extrairDadosRecrutador(dados)).toEqual(DADOS_FORMULARIO);
  });

  it("deve retornar strings vazias para campos ausentes", () => {
    expect(extrairDadosRecrutador(new FormData())).toEqual({
      nome: "",
      areaRecrutamento: "",
      descricao: "",
      email: "",
      cpf: "",
      dataNascimento: "",
      senha: "",
    });
  });
});

describe("recrutadorController - formulário", () => {
  let formulario: ElementoFalso;
  let botaoCancelar: ElementoFalso;
  let navegarParaEmpresa: ReturnType<typeof vi.fn<() => void>>;

  beforeEach(() => {
    vi.resetAllMocks();

    formulario = { addEventListener: vi.fn() };
    botaoCancelar = { addEventListener: vi.fn() };
    navegarParaEmpresa = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn((seletor: string) => {
        if (seletor === "#formulario-recrutador") return formulario;
        if (seletor === "#botao-cancelar-recrutador") return botaoCancelar;
        return null;
      }),
    });

    vi.stubGlobal("alert", vi.fn());

    vi.stubGlobal(
      "FormData",
      class {
        constructor(_formulario: unknown) {}

        get(campo: string): string | null {
          return DADOS_FORMULARIO[campo] ?? null;
        }
      },
    );
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  function submeter(): void {
    obterCallback(formulario, "submit")({
      preventDefault: vi.fn(),
    } as unknown as Event);
  }

  it("deve cadastrar recrutador na empresa informada e navegar", () => {
    vi.mocked(criarRecrutador).mockReturnValue(1);

    configurarFormularioRecrutador({ empresaId: 3, navegarParaEmpresa });

    submeter();

    expect(criarRecrutador).toHaveBeenCalledWith(DADOS_FORMULARIO, 3);
    expect(editarRecrutador).not.toHaveBeenCalled();
    expect(alert).toHaveBeenCalledWith("Recrutador cadastrado com sucesso!");
    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
  });

  it("deve editar recrutador, liberar o bloqueio e navegar", () => {
    vi.mocked(editarRecrutador).mockReturnValue(true);

    configurarFormularioRecrutador({
      empresaId: 3,
      recrutadorId: 7,
      navegarParaEmpresa,
    });

    submeter();

    expect(editarRecrutador).toHaveBeenCalledWith(7, 3, DADOS_FORMULARIO);
    expect(criarRecrutador).not.toHaveBeenCalled();
    expect(liberarBloqueioRecrutador).toHaveBeenCalledOnce();
    expect(alert).toHaveBeenCalledWith("Recrutador atualizado com sucesso!");
    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
  });

  it("deve exibir a mensagem de validação e permanecer no formulário no cadastro", () => {
    vi.mocked(criarRecrutador).mockImplementation(() => {
      throw new Error("CPF inválido. Informe exatamente 11 dígitos numéricos.");
    });

    configurarFormularioRecrutador({ empresaId: 3, navegarParaEmpresa });

    submeter();

    expect(alert).toHaveBeenCalledWith(
      "CPF inválido. Informe exatamente 11 dígitos numéricos.",
    );
    expect(alert).not.toHaveBeenCalledWith("Recrutador cadastrado com sucesso!");
    expect(navegarParaEmpresa).not.toHaveBeenCalled();
  });

  it("deve manter o bloqueio e permanecer no formulário quando a edição for inválida", () => {
    vi.mocked(editarRecrutador).mockImplementation(() => {
      throw new Error("E-mail já cadastrado para outro recrutador.");
    });

    configurarFormularioRecrutador({
      empresaId: 3,
      recrutadorId: 7,
      navegarParaEmpresa,
    });

    submeter();

    expect(alert).toHaveBeenCalledWith(
      "E-mail já cadastrado para outro recrutador.",
    );
    expect(liberarBloqueioRecrutador).not.toHaveBeenCalled();
    expect(navegarParaEmpresa).not.toHaveBeenCalled();
  });

  it("deve informar falha quando o recrutador a editar não existir", () => {
    vi.mocked(editarRecrutador).mockReturnValue(false);

    configurarFormularioRecrutador({
      empresaId: 3,
      recrutadorId: 99,
      navegarParaEmpresa,
    });

    submeter();

    expect(alert).toHaveBeenCalledWith(
      "Não foi possível atualizar o recrutador.",
    );
    expect(navegarParaEmpresa).not.toHaveBeenCalled();
  });

  it("deve usar mensagem genérica para erros que não sejam Error", () => {
    vi.mocked(criarRecrutador).mockImplementation(() => {
      throw "falha";
    });

    configurarFormularioRecrutador({ empresaId: 3, navegarParaEmpresa });

    submeter();

    expect(alert).toHaveBeenCalledWith("Não foi possível salvar o recrutador.");
  });

  it("não deve exibir a senha em nenhuma mensagem", () => {
    vi.mocked(criarRecrutador).mockImplementation(() => {
      throw new Error("Senha inválida. Deve possuir entre 8 e 128 caracteres.");
    });

    configurarFormularioRecrutador({ empresaId: 3, navegarParaEmpresa });

    submeter();

    for (const chamada of vi.mocked(alert).mock.calls) {
      expect(String(chamada[0])).not.toContain(DADOS_FORMULARIO.senha);
    }
  });

  it("deve liberar o bloqueio ao cancelar a edição", () => {
    configurarFormularioRecrutador({
      empresaId: 3,
      recrutadorId: 7,
      navegarParaEmpresa,
    });

    obterCallback(botaoCancelar, "click")();

    expect(liberarBloqueioRecrutador).toHaveBeenCalledOnce();
    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
  });

  it("não deve liberar bloqueio ao cancelar um novo cadastro", () => {
    configurarFormularioRecrutador({ empresaId: 3, navegarParaEmpresa });

    obterCallback(botaoCancelar, "click")();

    expect(liberarBloqueioRecrutador).not.toHaveBeenCalled();
    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o formulário não existir", () => {
    vi.stubGlobal("document", { querySelector: vi.fn(() => null) });

    expect(() =>
      configurarFormularioRecrutador({ empresaId: 3, navegarParaEmpresa }),
    ).not.toThrow();
  });
});

describe("recrutadorController - exclusão", () => {
  let botao: ElementoFalso & { dataset: { recrutadorId: string } };
  let aoExcluir: ReturnType<typeof vi.fn<() => void>>;

  beforeEach(() => {
    vi.resetAllMocks();

    botao = { addEventListener: vi.fn(), dataset: { recrutadorId: "7" } };
    aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [botao]),
    });

    vi.stubGlobal("alert", vi.fn());
    vi.stubGlobal("confirm", vi.fn(() => true));
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  function clicar(): void {
    obterCallback(botao, "click")();
  }

  it("deve excluir o recrutador da empresa após confirmação", () => {
    vi.mocked(recrutadorEstaBloqueado).mockReturnValue(false);
    vi.mocked(excluirRecrutador).mockReturnValue(true);

    configurarExclusaoRecrutador({ empresaId: 3, aoExcluir });

    clicar();

    expect(confirm).toHaveBeenCalledWith(
      "Deseja realmente excluir este recrutador?",
    );
    expect(excluirRecrutador).toHaveBeenCalledWith(7, 3);
    expect(alert).toHaveBeenCalledWith("Recrutador excluído com sucesso!");
    expect(aoExcluir).toHaveBeenCalledOnce();
  });

  it("não deve excluir recrutador bloqueado em outra aba", () => {
    vi.mocked(recrutadorEstaBloqueado).mockReturnValue(true);

    configurarExclusaoRecrutador({ empresaId: 3, aoExcluir });

    clicar();

    expect(confirm).not.toHaveBeenCalled();
    expect(excluirRecrutador).not.toHaveBeenCalled();
    expect(alert).toHaveBeenCalledWith(
      "Exclusão bloqueada: este recrutador está sendo editado em outra aba.",
    );
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("não deve excluir quando o usuário cancelar a confirmação", () => {
    vi.mocked(recrutadorEstaBloqueado).mockReturnValue(false);
    vi.stubGlobal("confirm", vi.fn(() => false));

    configurarExclusaoRecrutador({ empresaId: 3, aoExcluir });

    clicar();

    expect(excluirRecrutador).not.toHaveBeenCalled();
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("deve informar falha quando a exclusão não for realizada", () => {
    vi.mocked(recrutadorEstaBloqueado).mockReturnValue(false);
    vi.mocked(excluirRecrutador).mockReturnValue(false);

    configurarExclusaoRecrutador({ empresaId: 3, aoExcluir });

    clicar();

    expect(alert).toHaveBeenCalledWith("Não foi possível excluir o recrutador.");
    expect(aoExcluir).not.toHaveBeenCalled();
  });
});

describe("recrutadorController - edição e criação", () => {
  let botao: ElementoFalso & { dataset: { recrutadorId: string } };
  let abrirFormulario: ReturnType<typeof vi.fn<(id: number) => void>>;

  function criarRecrutadorMock(): Recrutador {
    return {
      id: 7,
      empresaId: 3,
      nome: "Camila Ferreira",
      areaRecrutamento: "Engenharia",
      descricao: "Recrutadora técnica",
      email: "camila@empresa.com",
      cpf: "12345678909",
      dataNascimento: "1992-04-15",
    };
  }

  beforeEach(() => {
    vi.resetAllMocks();

    botao = { addEventListener: vi.fn(), dataset: { recrutadorId: "7" } };
    abrirFormulario = vi.fn<(id: number) => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [botao]),
      querySelector: vi.fn(() => botao),
    });

    vi.stubGlobal("alert", vi.fn());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("deve abrir o formulário de edição ao adquirir o bloqueio", () => {
    vi.mocked(obterRecrutadorPorId).mockReturnValue(criarRecrutadorMock());
    vi.mocked(bloquearRecrutador).mockReturnValue(true);

    configurarEdicaoRecrutador({ empresaId: 3, abrirFormulario });

    obterCallback(botao, "click")();

    expect(obterRecrutadorPorId).toHaveBeenCalledWith(7, 3);
    expect(bloquearRecrutador).toHaveBeenCalledWith(7, 3);
    expect(abrirFormulario).toHaveBeenCalledWith(7);
  });

  it("não deve abrir o formulário quando bloqueado em outra aba", () => {
    vi.mocked(obterRecrutadorPorId).mockReturnValue(criarRecrutadorMock());
    vi.mocked(bloquearRecrutador).mockReturnValue(false);

    configurarEdicaoRecrutador({ empresaId: 3, abrirFormulario });

    obterCallback(botao, "click")();

    expect(abrirFormulario).not.toHaveBeenCalled();
    expect(alert).toHaveBeenCalledWith(
      "Este recrutador está sendo editado em outra aba.",
    );
  });

  it("não deve bloquear nem abrir quando o recrutador não pertence à empresa", () => {
    vi.mocked(obterRecrutadorPorId).mockReturnValue(undefined);

    configurarEdicaoRecrutador({ empresaId: 3, abrirFormulario });

    obterCallback(botao, "click")();

    expect(bloquearRecrutador).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });

  it("deve ignorar identificador inválido", () => {
    botao.dataset.recrutadorId = "abc";

    configurarEdicaoRecrutador({ empresaId: 3, abrirFormulario });

    obterCallback(botao, "click")();

    expect(obterRecrutadorPorId).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });

  it("deve abrir o formulário de criação ao clicar em novo recrutador", () => {
    const abrirCriacao = vi.fn<() => void>();

    configurarCriacaoRecrutador({ abrirFormulario: abrirCriacao });

    obterCallback(botao, "click")();

    expect(abrirCriacao).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o botão de novo recrutador não existir", () => {
    vi.stubGlobal("document", { querySelector: vi.fn(() => null) });

    expect(() =>
      configurarCriacaoRecrutador({ abrirFormulario: vi.fn() }),
    ).not.toThrow();
  });
});
