import { beforeEach, afterEach, describe, expect, it, vi } from "vitest";

import {
  configurarEdicaoCandidato,
  configurarExclusaoCandidato,
  configurarFormularioCandidato,  
  extrairDadosCandidato,
} from "./candidatoController";

import {
  criarCandidato,
  editarCandidato,
  excluirCandidato,
} from "../services/candidato/candidatoService";

import {
  bloquearCandidato,
  candidatoEstaBloqueado,
  liberarBloqueioCandidato,
} from "../services/candidato/edicaoCandidatoService";

vi.mock("../services/candidato/candidatoService", () => ({
  criarCandidato: vi.fn(),
  editarCandidato: vi.fn(),
  excluirCandidato: vi.fn(),
}));

vi.mock("../services/candidato/edicaoCandidatoService", () => ({
  bloquearCandidato: vi.fn(),
  liberarBloqueioCandidato: vi.fn(),
  candidatoEstaBloqueado: vi.fn(),
}));

describe("candidatoController - eventos do formulário", () => {
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
      if (seletor === "#formulario-candidato") {
        return formulario;
      }

      if (seletor === "#botao-cancelar-candidato") {
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
            nome: "Ana Silva",
            cpf: "12345678900",
            idade: "25",
            formacao: "ADS",
            descricao: "Desenvolvedora",
            skills: "Java, SQL",
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

  it("deve cadastrar um novo candidato", () => {
    configurarFormularioCandidato({ navegarParaPerfil });

    dispararEvento(formulario, "submit");

    expect(criarCandidato).toHaveBeenCalledWith({
      nome: "Ana Silva",
      cpf: "12345678900",
      idade: "25",
      formacao: "ADS",
      descricao: "Desenvolvedora",
      skills: "Java, SQL",
      estado: "RJ",
    });

    expect(editarCandidato).not.toHaveBeenCalled();
  });

  it("deve informar o ID do candidato após o cadastro", () => {
  const aoCadastrar = vi.fn<(candidatoId: number) => void>();

  vi.mocked(criarCandidato).mockReturnValue(8);

  configurarFormularioCandidato({
    navegarParaPerfil,
    aoCadastrar,
  });

  dispararEvento(formulario, "submit");

  expect(criarCandidato).toHaveBeenCalledOnce();
  expect(aoCadastrar).toHaveBeenCalledExactlyOnceWith(8);
});

  it("deve limpar o formulário e informar o cadastro", () => {
    configurarFormularioCandidato({ navegarParaPerfil });

    dispararEvento(formulario, "submit");

    expect(formulario.reset).toHaveBeenCalledOnce();

    expect(alert).toHaveBeenCalledWith("Candidato cadastrado com sucesso!");
  });

  it("deve editar o candidato utilizando seu ID", () => {
    configurarFormularioCandidato({
      candidatoId: 3,
      navegarParaPerfil,
    });

    dispararEvento(formulario, "submit");

    expect(editarCandidato).toHaveBeenCalledWith(
      3,
      expect.objectContaining({
        nome: "Ana Silva",
        skills: "Java, SQL",
      }),
    );

    expect(criarCandidato).not.toHaveBeenCalled();
  });

  it("deve liberar o bloqueio e navegar após editar", () => {
    configurarFormularioCandidato({
      candidatoId: 3,
      navegarParaPerfil,
    });

    dispararEvento(formulario, "submit");

    expect(liberarBloqueioCandidato).toHaveBeenCalledOnce();
    expect(navegarParaPerfil).toHaveBeenCalledOnce();

    expect(alert).toHaveBeenCalledWith("Perfil atualizado com sucesso!");
  });

  it("deve liberar o bloqueio ao cancelar uma edição", () => {
    configurarFormularioCandidato({
      candidatoId: 3,
      navegarParaPerfil,
    });

    dispararEvento(botaoCancelar, "click");

    expect(liberarBloqueioCandidato).toHaveBeenCalledOnce();
    expect(navegarParaPerfil).toHaveBeenCalledOnce();
  });

  it("não deve liberar bloqueio ao cancelar novo cadastro", () => {
    configurarFormularioCandidato({ navegarParaPerfil });

    dispararEvento(botaoCancelar, "click");

    expect(liberarBloqueioCandidato).not.toHaveBeenCalled();
    expect(navegarParaPerfil).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o formulário não existir", () => {
    consultarElemento.mockReturnValue(null);

    expect(() =>
      configurarFormularioCandidato({ navegarParaPerfil }),
    ).not.toThrow();
  });
});

describe("extrairDadosCandidato", () => {
  it("deve extrair os dados preenchidos no formulário", () => {
    const dados = new FormData();

    dados.set("nome", "Ana Silva");
    dados.set("cpf", "12345678900");
    dados.set("idade", "25");
    dados.set("formacao", "ADS");
    dados.set("descricao", "Desenvolvedora Full Stack");
    dados.set("skills", "Java, Spring, SQL");
    dados.set("estado", "RJ");

    expect(extrairDadosCandidato(dados)).toEqual({
      nome: "Ana Silva",
      cpf: "12345678900",
      idade: "25",
      formacao: "ADS",
      descricao: "Desenvolvedora Full Stack",
      skills: "Java, Spring, SQL",
      estado: "RJ",
    });
  });

  it("deve retornar strings vazias para campos ausentes", () => {
    const dados = new FormData();

    expect(extrairDadosCandidato(dados)).toEqual({
      nome: "",
      cpf: "",
      idade: "",
      formacao: "",
      descricao: "",
      skills: "",
      estado: "",
    });
  });

  it("deve preservar as skills como texto", () => {
    const dados = new FormData();

    dados.set("skills", "TypeScript, React, Node.js");

    const candidato = extrairDadosCandidato(dados);

    expect(candidato.skills).toBe("TypeScript, React, Node.js");
  });
});

describe("candidatoController - exclusão de candidato", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("não deve excluir um candidato bloqueado", () => {
    const botaoExcluir = {
      addEventListener: vi.fn(),
    };

    const navegarAposExclusao = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoExcluir),
    });

    vi.stubGlobal("alert", vi.fn());
    vi.stubGlobal("confirm", vi.fn());

    vi.mocked(candidatoEstaBloqueado).mockReturnValue(true);

    configurarExclusaoCandidato({
      candidatoId: 3,
      aoExcluir: navegarAposExclusao,
    });

    const registro = botaoExcluir.addEventListener.mock.calls.find(
      ([evento]) => evento === "click",
    );

    expect(registro).toBeDefined();

    const aoClicar = registro![1] as () => void;

    aoClicar();

    expect(candidatoEstaBloqueado).toHaveBeenCalledWith(3);

    expect(excluirCandidato).not.toHaveBeenCalled();

    expect(confirm).not.toHaveBeenCalled();

    expect(alert).toHaveBeenCalledWith(
      "Exclusão bloqueada: este candidato está sendo editado em outra aba.",
    );

    expect(navegarAposExclusao).not.toHaveBeenCalled();

    vi.unstubAllGlobals();
  });

  it("não deve excluir quando o candidato não possuir ID", () => {
    const botaoExcluir = { addEventListener: vi.fn() };
    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoExcluir),
    });

    vi.stubGlobal("confirm", vi.fn());

    configurarExclusaoCandidato({ aoExcluir });

    const aoClicar = botaoExcluir.addEventListener.mock.calls[0][1];
    aoClicar();

    expect(candidatoEstaBloqueado).not.toHaveBeenCalled();
    expect(excluirCandidato).not.toHaveBeenCalled();
    expect(confirm).not.toHaveBeenCalled();
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("não deve excluir quando o usuário cancelar a confirmação", () => {
    const botaoExcluir = { addEventListener: vi.fn() };
    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoExcluir),
    });

    vi.stubGlobal(
      "confirm",
      vi.fn(() => false),
    );

    vi.mocked(candidatoEstaBloqueado).mockReturnValue(false);

    configurarExclusaoCandidato({ candidatoId: 3, aoExcluir });

    const aoClicar = botaoExcluir.addEventListener.mock.calls[0][1];
    aoClicar();

    expect(confirm).toHaveBeenCalledWith("Deseja realmente excluir sua conta?");
    expect(excluirCandidato).not.toHaveBeenCalled();
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("deve informar quando a exclusão falhar", () => {
    const botaoExcluir = { addEventListener: vi.fn() };
    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoExcluir),
    });
    vi.stubGlobal(
      "confirm",
      vi.fn(() => true),
    );
    vi.stubGlobal("alert", vi.fn());

    vi.mocked(candidatoEstaBloqueado).mockReturnValue(false);
    vi.mocked(excluirCandidato).mockReturnValue(false);

    configurarExclusaoCandidato({ candidatoId: 3, aoExcluir });

    const aoClicar = botaoExcluir.addEventListener.mock.calls[0][1];
    aoClicar();

    expect(excluirCandidato).toHaveBeenCalledWith(3);
    expect(alert).toHaveBeenCalledWith("Não foi possível excluir a conta.");
    expect(aoExcluir).not.toHaveBeenCalled();
  });

  it("deve navegar após excluir o candidato com sucesso", () => {
    const botaoExcluir = { addEventListener: vi.fn() };
    const aoExcluir = vi.fn<() => void>();

    vi.stubGlobal("document", {
      querySelector: vi.fn(() => botaoExcluir),
    });
    vi.stubGlobal(
      "confirm",
      vi.fn(() => true),
    );
    vi.stubGlobal("alert", vi.fn());

    vi.mocked(candidatoEstaBloqueado).mockReturnValue(false);
    vi.mocked(excluirCandidato).mockReturnValue(true);

    configurarExclusaoCandidato({ candidatoId: 3, aoExcluir });

    const aoClicar = botaoExcluir.addEventListener.mock.calls[0][1];
    aoClicar();

    expect(excluirCandidato).toHaveBeenCalledWith(3);
    expect(alert).toHaveBeenCalledWith("Conta excluída com sucesso!");
    expect(aoExcluir).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o botão de exclusão não existir", () => {
    vi.stubGlobal("document", {
      querySelector: vi.fn(() => null),
    });

    const aoExcluir = vi.fn<() => void>();

    expect(() =>
      configurarExclusaoCandidato({
        candidatoId: 3,
        aoExcluir,
      }),
    ).not.toThrow();

    expect(excluirCandidato).not.toHaveBeenCalled();
  });
});


describe("candidatoController - abertura da edição", () => {
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

  it("não deve iniciar edição quando o candidato não possuir ID", () => {
    configurarEdicaoCandidato({
      candidatoId: undefined,
      abrirFormulario,
    });

    clicarEmEditar();

    expect(bloquearCandidato).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });

  it("não deve abrir o formulário quando o candidato estiver bloqueado", () => {
    vi.mocked(bloquearCandidato).mockReturnValue(false);

    configurarEdicaoCandidato({
      candidatoId: 3,
      abrirFormulario,
    });

    clicarEmEditar();

    expect(bloquearCandidato).toHaveBeenCalledWith(3);
    expect(abrirFormulario).not.toHaveBeenCalled();

    expect(alert).toHaveBeenCalledWith(
      "Este candidato está sendo editado em outra aba.",
    );
  });

  it("deve abrir o formulário quando adquirir o bloqueio", () => {
    vi.mocked(bloquearCandidato).mockReturnValue(true);

    configurarEdicaoCandidato({
      candidatoId: 3,
      abrirFormulario,
    });

    clicarEmEditar();

    expect(bloquearCandidato).toHaveBeenCalledWith(3);
    expect(abrirFormulario).toHaveBeenCalledOnce();
  });

  it("não deve lançar erro quando o botão de edição não existir", () => {
    vi.stubGlobal("document", {
      querySelector: vi.fn(() => null),
    });

    expect(() =>
      configurarEdicaoCandidato({
        candidatoId: 3,
        abrirFormulario,
      }),
    ).not.toThrow();

    expect(bloquearCandidato).not.toHaveBeenCalled();
    expect(abrirFormulario).not.toHaveBeenCalled();
  });
});

