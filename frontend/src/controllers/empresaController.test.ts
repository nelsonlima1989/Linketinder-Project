import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  configurarEdicaoEmpresa,
  configurarExclusaoEmpresa,
  configurarFormularioEmpresa,
  extrairDadosEmpresa,
} from "./empresaController";

import {
  criarEmpresa,
  editarEmpresa,
} from "../services/empresa/empresaService";

import {
  bloquearEmpresa,
  empresaEstaBloqueada,
  liberarBloqueioEmpresa,
} from "../services/empresa/edicaoEmpresaService";

import { excluirEmpresaComVagas } from "../services/empresa/exclusaoEmpresaService";

vi.mock("../services/empresa/exclusaoEmpresaService", () => ({
  excluirEmpresaComVagas: vi.fn(),
}));

vi.mock("../services/empresa/empresaService", () => ({
  criarEmpresa: vi.fn(),
  editarEmpresa: vi.fn(),
}));

vi.mock("../services/empresa/edicaoEmpresaService", () => ({
  bloquearEmpresa: vi.fn(),
  liberarBloqueioEmpresa: vi.fn(),
  empresaEstaBloqueada: vi.fn(),
}));

describe("empresaController - eventos do formulário", () => {
  let formulario: {
    addEventListener: ReturnType<typeof vi.fn>;
    reset: ReturnType<typeof vi.fn>;
  };

  let botaoCancelar: {
    addEventListener: ReturnType<typeof vi.fn>;
  };

  let navegarParaPerfil: ReturnType<typeof vi.fn<() => void>>;
  let consultarElemento: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    vi.clearAllMocks();

    formulario = {
      addEventListener: vi.fn(),
      reset: vi.fn(),
    };

    botaoCancelar = {
      addEventListener: vi.fn(),
    };

    navegarParaPerfil = vi.fn<() => void>();

    consultarElemento = vi.fn((seletor: string) => {
      if (seletor === "#formulario-empresa") {
        return formulario;
      }

      if (seletor === "#botao-cancelar-empresa") {
        return botaoCancelar;
      }

      return null;
    });

    vi.stubGlobal("document", {
      querySelector: consultarElemento,
    });

    vi.stubGlobal("alert", vi.fn());

    vi.stubGlobal(
      "FormData",
      class {
        constructor(_formulario: unknown) {}

        get(campo: string): string | null {
          const dados: Record<string, string> = {
            nome: "Empresa Teste",
            cnpj: "12345678000190",
            descricao: "Empresa de tecnologia",
            estado: "RJ",
          };

          return dados[campo] ?? null;
        }
      },
    );
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  function dispararEvento(
    elemento: { addEventListener: ReturnType<typeof vi.fn> },
    nomeEvento: string,
  ): void {
    const registro = elemento.addEventListener.mock.calls.find(
      ([evento]) => evento === nomeEvento,
    );

    expect(registro).toBeDefined();

    const callback = registro![1] as (evento: Event) => void;

    callback({
      preventDefault: vi.fn(),
    } as unknown as Event);
  }

  it("deve cadastrar uma nova empresa", () => {
    configurarFormularioEmpresa({ navegarParaPerfil });

    dispararEvento(formulario, "submit");

    expect(criarEmpresa).toHaveBeenCalledWith({
      nome: "Empresa Teste",
      cnpj: "12345678000190",
      descricao: "Empresa de tecnologia",
      estado: "RJ",
    });

    expect(editarEmpresa).not.toHaveBeenCalled();
  });

  it("deve limpar o formulário e informar o cadastro", () => {
    configurarFormularioEmpresa({ navegarParaPerfil });

    dispararEvento(formulario, "submit");

    expect(formulario.reset).toHaveBeenCalledOnce();
    expect(alert).toHaveBeenCalledWith("Empresa cadastrada com sucesso!");
  });

  it("deve editar a empresa utilizando seu ID", () => {
    configurarFormularioEmpresa({
      empresaId: 3,
      navegarParaPerfil,
    });

    dispararEvento(formulario, "submit");

    expect(editarEmpresa).toHaveBeenCalledWith(
      3,
      expect.objectContaining({
        nome: "Empresa Teste",
        cnpj: "12345678000190",
      }),
    );

    expect(criarEmpresa).not.toHaveBeenCalled();
  });

  it("deve liberar o bloqueio e navegar após editar", () => {
    configurarFormularioEmpresa({
      empresaId: 3,
      navegarParaPerfil,
    });

    dispararEvento(formulario, "submit");

    expect(liberarBloqueioEmpresa).toHaveBeenCalledOnce();
    expect(navegarParaPerfil).toHaveBeenCalledOnce();
    expect(alert).toHaveBeenCalledWith("Perfil atualizado com sucesso!");
  });

  it("deve liberar o bloqueio ao cancelar o formulário", () => {
    configurarFormularioEmpresa({
      empresaId: 3,
      navegarParaPerfil,
    });

    dispararEvento(botaoCancelar, "click");

    expect(liberarBloqueioEmpresa).toHaveBeenCalledOnce();
    expect(navegarParaPerfil).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o formulário não existir", () => {
    consultarElemento.mockReturnValue(null);

    expect(() =>
      configurarFormularioEmpresa({ navegarParaPerfil }),
    ).not.toThrow();
  });
});

describe("extrairDadosEmpresa", () => {
  it("deve extrair os dados preenchidos corretamente", () => {
    const dados = new FormData();

    dados.set("nome", "Empresa Teste");
    dados.set("cnpj", "12345678000190");
    dados.set("descricao", "Empresa de tecnologia");
    dados.set("estado", "RJ");

    expect(extrairDadosEmpresa(dados)).toEqual({
      nome: "Empresa Teste",
      cnpj: "12345678000190",
      descricao: "Empresa de tecnologia",
      estado: "RJ",
    });
  });

  it("deve retornar strings vazias para campos ausentes", () => {
    const dados = new FormData();

    expect(extrairDadosEmpresa(dados)).toEqual({
      nome: "",
      cnpj: "",
      descricao: "",
      estado: "",
    });
  });
});

describe("empresaController - exclusão de empresa", () => {
  let botaoExcluir: { addEventListener: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    vi.resetAllMocks();

    botaoExcluir = { addEventListener: vi.fn() };

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoExcluir),
    });

    vi.stubGlobal("alert", vi.fn());
    vi.stubGlobal("confirm", vi.fn(() => true));
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  function clicar(): void {
    const registro = botaoExcluir.addEventListener.mock.calls.find(
      ([evento]) => evento === "click",
    );

    expect(registro).toBeDefined();

    (registro![1] as () => void)();
  }

  it("não deve excluir uma empresa bloqueada", () => {
    vi.mocked(empresaEstaBloqueada).mockReturnValue(true);

    configurarExclusaoEmpresa({ empresaId: 3 });

    clicar();

    expect(empresaEstaBloqueada).toHaveBeenCalledWith(3);
    expect(confirm).not.toHaveBeenCalled();
    expect(excluirEmpresaComVagas).not.toHaveBeenCalled();
    expect(alert).toHaveBeenCalledWith(
      "Exclusão bloqueada: esta empresa está sendo editada em outra aba.",
    );
  });

  it("não deve excluir quando o usuário cancelar a confirmação", () => {
    vi.stubGlobal("confirm", vi.fn(() => false));
    vi.mocked(empresaEstaBloqueada).mockReturnValue(false);

    configurarExclusaoEmpresa({ empresaId: 3 });

    clicar();

    expect(confirm).toHaveBeenCalledWith("Deseja realmente excluir sua conta?");
    expect(excluirEmpresaComVagas).not.toHaveBeenCalled();
  });

  it("deve informar quando a exclusão da empresa falhar", () => {
    const aoExcluir = vi.fn<() => void>();

    vi.mocked(empresaEstaBloqueada).mockReturnValue(false);
    vi.mocked(excluirEmpresaComVagas).mockReturnValue(false);

    configurarExclusaoEmpresa({ empresaId: 3, aoExcluir });

    clicar();

    expect(excluirEmpresaComVagas).toHaveBeenCalledWith(3);
    expect(alert).toHaveBeenCalledWith("Não foi possível excluir a conta.");
    expect(alert).not.toHaveBeenCalledWith("Conta excluída com sucesso!");
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("deve excluir empresa, vagas e recrutadores de forma atômica e informar o sucesso", () => {
    vi.mocked(empresaEstaBloqueada).mockReturnValue(false);
    vi.mocked(excluirEmpresaComVagas).mockReturnValue(true);

    configurarExclusaoEmpresa({ empresaId: 3 });

    clicar();

    expect(excluirEmpresaComVagas).toHaveBeenCalledOnce();
    expect(excluirEmpresaComVagas).toHaveBeenCalledWith(3);
    expect(alert).toHaveBeenCalledWith("Conta excluída com sucesso!");
  });

  it("não deve anunciar sucesso nem navegar se a cascata falhar (estado restaurado pelo serviço)", () => {
    const aoExcluir = vi.fn<() => void>();

    vi.mocked(empresaEstaBloqueada).mockReturnValue(false);
    vi.mocked(excluirEmpresaComVagas).mockImplementation(() => {
      throw new Error("Falha ao remover vagas");
    });

    configurarExclusaoEmpresa({ empresaId: 3, aoExcluir });

    expect(() => clicar()).not.toThrow();

    expect(alert).toHaveBeenCalledWith("Não foi possível excluir a conta.");
    expect(alert).not.toHaveBeenCalledWith("Conta excluída com sucesso!");
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("não deve lançar erro quando o botão de exclusão não existir", () => {
    vi.stubGlobal("document", {
      querySelector: vi.fn(() => null),
    });

    expect(() => configurarExclusaoEmpresa({ empresaId: 3 })).not.toThrow();

    expect(excluirEmpresaComVagas).not.toHaveBeenCalled();
  });

  it("deve executar o callback após excluir a empresa e seus dependentes", () => {
    const aoExcluir = vi.fn<() => void>();

    vi.mocked(empresaEstaBloqueada).mockReturnValue(false);
    vi.mocked(excluirEmpresaComVagas).mockReturnValue(true);

    configurarExclusaoEmpresa({ empresaId: 3, aoExcluir });

    clicar();

    expect(excluirEmpresaComVagas).toHaveBeenCalledWith(3);
    expect(aoExcluir).toHaveBeenCalledOnce();
  });
});


describe("empresaController - abertura da edição", () => {
  let botaoEditar: {
    addEventListener: ReturnType<typeof vi.fn>;
  };

  let abrirFormulario: ReturnType<typeof vi.fn<() => void>>;

  beforeEach(() => {
    vi.clearAllMocks();

    botaoEditar = {
      addEventListener: vi.fn(),
    };

    abrirFormulario = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoEditar),
    });

    vi.stubGlobal("alert", vi.fn());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  function clicarEmEditar(): void {
    const registro = botaoEditar.addEventListener.mock.calls.find(
      ([evento]) => evento === "click",
    );

    expect(registro).toBeDefined();

    const aoClicar = registro![1] as () => void;
    aoClicar();
  }

  it("não deve abrir o formulário quando a empresa estiver bloqueada", () => {
    vi.mocked(bloquearEmpresa).mockReturnValue(false);

    configurarEdicaoEmpresa({
      empresaId: 3,
      abrirFormulario,
    });

    clicarEmEditar();

    expect(bloquearEmpresa).toHaveBeenCalledWith(3);

    expect(abrirFormulario).not.toHaveBeenCalled();

    expect(alert).toHaveBeenCalledWith(
      "Esta empresa está sendo editada em outra aba.",
    );
  });

  it("deve abrir o formulário quando adquirir o bloqueio", () => {
    vi.mocked(bloquearEmpresa).mockReturnValue(true);

    configurarEdicaoEmpresa({
      empresaId: 3,
      abrirFormulario,
    });

    clicarEmEditar();

    expect(bloquearEmpresa).toHaveBeenCalledWith(3);
    expect(abrirFormulario).toHaveBeenCalledOnce();

    expect(alert).not.toHaveBeenCalled();
  });

  it("não deve lançar erro quando o botão de edição não existir", () => {
    vi.stubGlobal("document", {
      querySelector: vi.fn(() => null),
    });

    expect(() =>
      configurarEdicaoEmpresa({
        empresaId: 3,
        abrirFormulario,
      }),
    ).not.toThrow();

    expect(bloquearEmpresa).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });
});
