import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  configurarCriacaoVaga,
  configurarEdicaoVaga,
  configurarExclusaoVaga,
  configurarFormularioVaga,
  extrairDadosVaga,
} from "./vagaController";

import {
  criarVaga,
  editarVaga,
  excluirVaga,
  obterVagaPorId,
} from "../services/vaga/vagaService";

import {
  bloquearVaga,
  liberarBloqueioVaga,
  vagaEstaBloqueada,
} from "../services/vaga/edicaoVagaService";

vi.mock("../services/vaga/vagaService", () => ({
  criarVaga: vi.fn(),
  editarVaga: vi.fn(),
  excluirVaga: vi.fn(),
  obterVagaPorId: vi.fn(),
}));

vi.mock("../services/vaga/edicaoVagaService", () => ({
  bloquearVaga: vi.fn(),
  liberarBloqueioVaga: vi.fn(),
  vagaEstaBloqueada: vi.fn(),
}));

describe("vagaController - eventos do formulário", () => {
  let formulario: {
    addEventListener: ReturnType<typeof vi.fn>;
  };

  let botaoCancelar: {
    addEventListener: ReturnType<typeof vi.fn>;
  };

  let navegarParaEmpresa: ReturnType<typeof vi.fn<() => void>>;
  let consultarElemento: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    vi.clearAllMocks();

    formulario = {
      addEventListener: vi.fn(),
    };

    botaoCancelar = {
      addEventListener: vi.fn(),
    };

    navegarParaEmpresa = vi.fn<() => void>();

    consultarElemento = vi.fn((seletor: string) => {
      if (seletor === "#formulario-vaga") {
        return formulario;
      }

      if (seletor === "#botao-cancelar-vaga") {
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
            titulo: "Desenvolvedor Full Stack",
            descricao: "Desenvolvimento de aplicações web",
            skills: "TypeScript, React, Node.js",
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

  it("deve cadastrar uma vaga vinculada à empresa correta", () => {
    configurarFormularioVaga({
      empresaId: 3,
      navegarParaEmpresa,
    });

    dispararEvento(formulario, "submit");

    expect(criarVaga).toHaveBeenCalledWith(
      {
        titulo: "Desenvolvedor Full Stack",
        descricao: "Desenvolvimento de aplicações web",
        skills: "TypeScript, React, Node.js",
      },
      3,
    );

    expect(editarVaga).not.toHaveBeenCalled();
  });

  it("deve informar o cadastro e navegar para a empresa", () => {
    configurarFormularioVaga({
      empresaId: 3,
      navegarParaEmpresa,
    });

    dispararEvento(formulario, "submit");

    expect(alert).toHaveBeenCalledWith("Vaga cadastrada com sucesso!");

    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
    expect(liberarBloqueioVaga).not.toHaveBeenCalled();
  });

  it("deve editar a vaga utilizando os IDs corretos", () => {
    configurarFormularioVaga({
      empresaId: 3,
      vagaId: 8,
      navegarParaEmpresa,
    });

    dispararEvento(formulario, "submit");

    expect(editarVaga).toHaveBeenCalledWith(8, 3, {
      titulo: "Desenvolvedor Full Stack",
      descricao: "Desenvolvimento de aplicações web",
      skills: "TypeScript, React, Node.js",
    });

    expect(criarVaga).not.toHaveBeenCalled();
  });

  it("deve liberar o bloqueio e navegar após editar", () => {
    configurarFormularioVaga({
      empresaId: 3,
      vagaId: 8,
      navegarParaEmpresa,
    });

    dispararEvento(formulario, "submit");

    expect(liberarBloqueioVaga).toHaveBeenCalledOnce();

    expect(alert).toHaveBeenCalledWith("Vaga atualizada com sucesso!");

    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
  });

  it("deve liberar o bloqueio ao cancelar uma edição", () => {
    configurarFormularioVaga({
      empresaId: 3,
      vagaId: 8,
      navegarParaEmpresa,
    });

    dispararEvento(botaoCancelar, "click");

    expect(liberarBloqueioVaga).toHaveBeenCalledOnce();
    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
  });

  it("não deve liberar bloqueio ao cancelar novo cadastro", () => {
    configurarFormularioVaga({
      empresaId: 3,
      navegarParaEmpresa,
    });

    dispararEvento(botaoCancelar, "click");

    expect(liberarBloqueioVaga).not.toHaveBeenCalled();
    expect(navegarParaEmpresa).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o formulário não existir", () => {
    consultarElemento.mockReturnValue(null);

    expect(() =>
      configurarFormularioVaga({
        empresaId: 3,
        navegarParaEmpresa,
      }),
    ).not.toThrow();
  });
});

describe("extrairDadosVaga", () => {
  it("deve extrair os dados preenchidos corretamente", () => {
    const dados = new FormData();

    dados.set("titulo", "Desenvolvedor Back-end");
    dados.set("descricao", "Desenvolvimento de APIs");
    dados.set("skills", "Java, Spring, PostgreSQL");

    expect(extrairDadosVaga(dados)).toEqual({
      titulo: "Desenvolvedor Back-end",
      descricao: "Desenvolvimento de APIs",
      skills: "Java, Spring, PostgreSQL",
    });
  });

  it("deve retornar strings vazias para campos ausentes", () => {
    const dados = new FormData();

    expect(extrairDadosVaga(dados)).toEqual({
      titulo: "",
      descricao: "",
      skills: "",
    });
  });
});

describe("vagaController - exclusão de vagas", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("não deve excluir uma vaga bloqueada", () => {
    const botaoExcluir = {
      dataset: { vagaId: "8" },
      addEventListener: vi.fn(),
    };

    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [botaoExcluir]),
    });

    vi.stubGlobal("alert", vi.fn());
    vi.stubGlobal("confirm", vi.fn());

    vi.mocked(vagaEstaBloqueada).mockReturnValue(true);

    configurarExclusaoVaga({
      empresaId: 3,
      aoExcluir,
    });

    const registro = botaoExcluir.addEventListener.mock.calls.find(
      ([evento]) => evento === "click",
    );

    expect(registro).toBeDefined();

    const aoClicar = registro![1] as () => void;
    aoClicar();

    expect(vagaEstaBloqueada).toHaveBeenCalledWith(8);

    expect(confirm).not.toHaveBeenCalled();
    expect(excluirVaga).not.toHaveBeenCalled();
    expect(aoExcluir).not.toHaveBeenCalled();

    expect(alert).toHaveBeenCalledWith(
      "Exclusão bloqueada: esta vaga está sendo editada em outra aba.",
    );
  });

  it("não deve excluir quando o usuário cancelar a confirmação", () => {
    const botaoExcluir = {
      dataset: { vagaId: "8" },
      addEventListener: vi.fn(),
    };

    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [botaoExcluir]),
    });
    vi.stubGlobal(
      "confirm",
      vi.fn(() => false),
    );

    vi.mocked(vagaEstaBloqueada).mockReturnValue(false);

    configurarExclusaoVaga({ empresaId: 3, aoExcluir });

    const aoClicar = botaoExcluir.addEventListener.mock.calls[0][1];
    aoClicar();

    expect(confirm).toHaveBeenCalledWith("Deseja realmente excluir esta vaga?");
    expect(excluirVaga).not.toHaveBeenCalled();
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("deve informar quando a exclusão falhar", () => {
    const botaoExcluir = {
      dataset: { vagaId: "8" },
      addEventListener: vi.fn(),
    };

    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [botaoExcluir]),
    });
    vi.stubGlobal(
      "confirm",
      vi.fn(() => true),
    );
    vi.stubGlobal("alert", vi.fn());

    vi.mocked(vagaEstaBloqueada).mockReturnValue(false);
    vi.mocked(excluirVaga).mockReturnValue(false);

    configurarExclusaoVaga({ empresaId: 3, aoExcluir });

    const aoClicar = botaoExcluir.addEventListener.mock.calls[0][1];
    aoClicar();

    expect(excluirVaga).toHaveBeenCalledWith(8, 3);
    expect(alert).toHaveBeenCalledWith("Não foi possível excluir a vaga.");
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("deve atualizar a interface após excluir com sucesso", () => {
    const botaoExcluir = {
      dataset: { vagaId: "8" },
      addEventListener: vi.fn(),
    };

    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [botaoExcluir]),
    });
    vi.stubGlobal(
      "confirm",
      vi.fn(() => true),
    );
    vi.stubGlobal("alert", vi.fn());

    vi.mocked(vagaEstaBloqueada).mockReturnValue(false);
    vi.mocked(excluirVaga).mockReturnValue(true);

    configurarExclusaoVaga({ empresaId: 3, aoExcluir });

    const aoClicar = botaoExcluir.addEventListener.mock.calls[0][1];
    aoClicar();

    expect(excluirVaga).toHaveBeenCalledWith(8, 3);
    expect(alert).toHaveBeenCalledWith("Vaga excluída com sucesso!");
    expect(aoExcluir).toHaveBeenCalledOnce();
  });

  it("deve configurar a exclusão para múltiplas vagas", () => {
    const primeiroBotao = {
      dataset: { vagaId: "8" },
      addEventListener: vi.fn(),
    };

    const segundoBotao = {
      dataset: { vagaId: "9" },
      addEventListener: vi.fn(),
    };

    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [primeiroBotao, segundoBotao]),
    });
    vi.stubGlobal(
      "confirm",
      vi.fn(() => true),
    );
    vi.stubGlobal("alert", vi.fn());

    vi.mocked(vagaEstaBloqueada).mockReturnValue(false);
    vi.mocked(excluirVaga).mockReturnValue(true);

    configurarExclusaoVaga({ empresaId: 3, aoExcluir });

    const clicarPrimeiro = primeiroBotao.addEventListener.mock.calls[0][1];
    const clicarSegundo = segundoBotao.addEventListener.mock.calls[0][1];

    clicarPrimeiro();
    clicarSegundo();

    expect(excluirVaga).toHaveBeenNthCalledWith(1, 8, 3);
    expect(excluirVaga).toHaveBeenNthCalledWith(2, 9, 3);
    expect(aoExcluir).toHaveBeenCalledTimes(2);
  });

  it("não deve lançar erro quando não existirem botões de exclusão", () => {
    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => []),
    });

    const aoExcluir = vi.fn<() => void>();

    expect(() =>
      configurarExclusaoVaga({
        empresaId: 3,
        aoExcluir,
      }),
    ).not.toThrow();

    expect(excluirVaga).not.toHaveBeenCalled();
  });
});

describe("vagaController - abertura da edição", () => {
  type BotaoEditar = {
    dataset: { vagaId: string };
    addEventListener: ReturnType<typeof vi.fn>;
  };

  let botaoEditar: BotaoEditar;
  let abrirFormulario: ReturnType<typeof vi.fn<(vagaId: number) => void>>;

  function criarBotao(vagaId: string): BotaoEditar {
    return {
      dataset: { vagaId },
      addEventListener: vi.fn(),
    };
  }

  function clicarEmEditar(botao: BotaoEditar): void {
    const registro = botao.addEventListener.mock.calls.find(
      ([evento]) => evento === "click",
    );

    expect(registro).toBeDefined();

    const aoClicar = registro![1] as () => void;
    aoClicar();
  }

  beforeEach(() => {
    vi.clearAllMocks();

    botaoEditar = criarBotao("8");
    abrirFormulario = vi.fn<(vagaId: number) => void>();

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [botaoEditar]),
    });

    vi.stubGlobal("alert", vi.fn());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("deve abrir o formulário quando a vaga existir e adquirir o bloqueio", () => {
    vi.mocked(obterVagaPorId).mockReturnValue({
      id: 8,
      empresaId: 3,
      titulo: "Desenvolvedor Full Stack",
      descricao: "Desenvolvimento de aplicações web",
      skills: ["TypeScript", "React"],
    });

    vi.mocked(bloquearVaga).mockReturnValue(true);

    configurarEdicaoVaga({
      empresaId: 3,
      abrirFormulario,
    });

    clicarEmEditar(botaoEditar);

    expect(obterVagaPorId).toHaveBeenCalledWith(8, 3);
    expect(bloquearVaga).toHaveBeenCalledWith(8, 3);
    expect(abrirFormulario).toHaveBeenCalledExactlyOnceWith(8);
    expect(alert).not.toHaveBeenCalled();
  });

  it("não deve iniciar edição quando a vaga não existir", () => {
    vi.mocked(obterVagaPorId).mockReturnValue(undefined);

    configurarEdicaoVaga({
      empresaId: 3,
      abrirFormulario,
    });

    clicarEmEditar(botaoEditar);

    expect(obterVagaPorId).toHaveBeenCalledWith(8, 3);
    expect(bloquearVaga).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });

  it("não deve abrir o formulário quando a vaga estiver bloqueada", () => {
    vi.mocked(obterVagaPorId).mockReturnValue({
      id: 8,
      empresaId: 3,
      titulo: "Desenvolvedor Full Stack",
      descricao: "Desenvolvimento de aplicações web",
      skills: ["TypeScript", "React"],
    });

    vi.mocked(bloquearVaga).mockReturnValue(false);

    configurarEdicaoVaga({
      empresaId: 3,
      abrirFormulario,
    });

    clicarEmEditar(botaoEditar);

    expect(bloquearVaga).toHaveBeenCalledWith(8, 3);
    expect(abrirFormulario).not.toHaveBeenCalled();

    expect(alert).toHaveBeenCalledWith(
      "Esta vaga está sendo editada em outra aba.",
    );
  });

  it("não deve consultar vaga quando o ID for inválido", () => {
    botaoEditar.dataset.vagaId = "invalido";

    configurarEdicaoVaga({
      empresaId: 3,
      abrirFormulario,
    });

    clicarEmEditar(botaoEditar);

    expect(obterVagaPorId).not.toHaveBeenCalled();
    expect(bloquearVaga).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });

  it("deve configurar a edição para múltiplas vagas", () => {
    const primeiroBotao = criarBotao("8");
    const segundoBotao = criarBotao("9");

    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => [primeiroBotao, segundoBotao]),
    });

    vi.mocked(obterVagaPorId).mockImplementation((id, empresaId) => ({
      id,
      empresaId,
      titulo: `Vaga ${id}`,
      descricao: "Descrição da vaga",
      skills: ["TypeScript"],
    }));

    vi.mocked(bloquearVaga).mockReturnValue(true);

    configurarEdicaoVaga({
      empresaId: 3,
      abrirFormulario,
    });

    clicarEmEditar(primeiroBotao);
    clicarEmEditar(segundoBotao);

    expect(obterVagaPorId).toHaveBeenNthCalledWith(1, 8, 3);
    expect(obterVagaPorId).toHaveBeenNthCalledWith(2, 9, 3);

    expect(abrirFormulario).toHaveBeenNthCalledWith(1, 8);
    expect(abrirFormulario).toHaveBeenNthCalledWith(2, 9);
  });

  it("não deve lançar erro quando não existirem botões de edição", () => {
    vi.stubGlobal("document", {
      querySelectorAll: vi.fn(() => []),
    });

    expect(() =>
      configurarEdicaoVaga({
        empresaId: 3,
        abrirFormulario,
      }),
    ).not.toThrow();

    expect(obterVagaPorId).not.toHaveBeenCalled();
    expect(bloquearVaga).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });
});

describe("vagaController - abertura do cadastro", () => {
  let botaoNovaVaga: {
    addEventListener: ReturnType<typeof vi.fn>;
  };

  let abrirFormulario: ReturnType<typeof vi.fn<() => void>>;

  beforeEach(() => {
    vi.clearAllMocks();

    botaoNovaVaga = {
      addEventListener: vi.fn(),
    };

    abrirFormulario = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoNovaVaga),
    });
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("deve abrir o formulário ao clicar em Nova Vaga", () => {
    configurarCriacaoVaga({
      abrirFormulario,
    });

    expect(botaoNovaVaga.addEventListener).toHaveBeenCalledWith(
      "click",
      expect.any(Function),
    );

    const aoClicar = botaoNovaVaga.addEventListener.mock
      .calls[0][1] as () => void;

    aoClicar();

    expect(abrirFormulario).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o botão Nova Vaga não existir", () => {
    vi.stubGlobal("document", {
      querySelector: vi.fn(() => null),
    });

    expect(() =>
      configurarCriacaoVaga({
        abrirFormulario,
      }),
    ).not.toThrow();

    expect(abrirFormulario).not.toHaveBeenCalled();
  });
});
