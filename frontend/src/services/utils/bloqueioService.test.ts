import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { criarGerenciadorBloqueio } from "./bloqueioService";

type BloqueioTeste = {
  registroId: number;
  sessaoId: string;
  timestamp: number;
};

describe("criarGerenciadorBloqueio", () => {
  let dados: Map<string, string>;

  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2026-01-01T12:00:00Z"));

    dados = new Map<string, string>();

    vi.stubGlobal("localStorage", {
      getItem: vi.fn((chave: string) => dados.get(chave) ?? null),
      setItem: vi.fn((chave: string, valor: string) => {
        dados.set(chave, valor);
      }),
      removeItem: vi.fn((chave: string) => {
        dados.delete(chave);
      }),
    });

    vi.stubGlobal("window", {
      setInterval: globalThis.setInterval,
      clearInterval: globalThis.clearInterval,
    });
  });

  afterEach(() => {
    vi.clearAllTimers();
    vi.useRealTimers();
    vi.unstubAllGlobals();
  });

  function criarGerenciador() {
    return criarGerenciadorBloqueio<BloqueioTeste>({
      chaveBase: "teste-em-edicao",
      intervaloHeartbeat: 2000,
      tempoExpiracao: 5000,
    });
  }

  it("deve bloquear um registro disponível", () => {
    const gerenciador = criarGerenciador();

    expect(gerenciador.bloquear(1, { registroId: 1 })).toBe(true);
    expect(gerenciador.estaBloqueado(1)).toBe(true);
  });

  it("não deve bloquear um registro já bloqueado", () => {
    const gerenciador = criarGerenciador();

    gerenciador.bloquear(1, { registroId: 1 });

    expect(gerenciador.bloquear(1, { registroId: 1 })).toBe(false);
  });

  it("deve renovar o bloqueio pelo heartbeat", () => {
    const gerenciador = criarGerenciador();

    gerenciador.bloquear(1, { registroId: 1 });

    const timestampInicial = Date.now();

    vi.advanceTimersByTime(2000);

    expect(gerenciador.consultar(1)?.timestamp).toBe(timestampInicial + 2000);
  });

  it("deve liberar um bloqueio próprio", () => {
    const gerenciador = criarGerenciador();

    gerenciador.bloquear(1, { registroId: 1 });
    gerenciador.liberar();

    expect(gerenciador.estaBloqueado(1)).toBe(false);
  });

  it("deve manter estados independentes entre gerenciadores", () => {
    const gerenciadorCandidato = criarGerenciadorBloqueio<BloqueioTeste>({
      chaveBase: "candidato-em-edicao",
      intervaloHeartbeat: 2000,
      tempoExpiracao: 5000,
    });

    const gerenciadorVaga = criarGerenciadorBloqueio<BloqueioTeste>({
      chaveBase: "vaga-em-edicao",
      intervaloHeartbeat: 2000,
      tempoExpiracao: 5000,
    });

    expect(gerenciadorCandidato.bloquear(1, { registroId: 1 })).toBe(true);

    expect(gerenciadorVaga.bloquear(1, { registroId: 1 })).toBe(true);

    gerenciadorCandidato.liberar();

    expect(gerenciadorCandidato.estaBloqueado(1)).toBe(false);
    expect(gerenciadorVaga.estaBloqueado(1)).toBe(true);
  });

  it("deve remover um bloqueio expirado", () => {
    const gerenciador = criarGerenciador();

    dados.set(
      "teste-em-edicao-1",
      JSON.stringify({
        registroId: 1,
        sessaoId: "outra-sessao",
        timestamp: Date.now() - 6000,
      }),
    );

    expect(gerenciador.consultar(1)).toBeNull();
    expect(dados.has("teste-em-edicao-1")).toBe(false);
  });

  it("não deve bloquear outro registro enquanto existir uma edição ativa", () => {
    const gerenciador = criarGerenciador();

    expect(gerenciador.bloquear(1, { registroId: 1 })).toBe(true);
    expect(gerenciador.bloquear(2, { registroId: 2 })).toBe(false);

    expect(gerenciador.estaBloqueado(1)).toBe(true);
    expect(gerenciador.estaBloqueado(2)).toBe(false);
  });

  it("não deve renovar bloqueio pertencente a outra sessão", () => {
    const gerenciador = criarGerenciador();

    gerenciador.bloquear(1, { registroId: 1 });

    vi.advanceTimersByTime(1000);

    const timestampOutraSessao = Date.now();

    dados.set(
      "teste-em-edicao-1",
      JSON.stringify({
        registroId: 1,
        sessaoId: "outra-sessao",
        timestamp: timestampOutraSessao,
      }),
    );

    vi.advanceTimersByTime(1000);

    expect(gerenciador.consultar(1)).toEqual({
      registroId: 1,
      sessaoId: "outra-sessao",
      timestamp: timestampOutraSessao,
    });
  });

  it("não deve liberar bloqueio pertencente a outra sessão", () => {
    const gerenciador = criarGerenciador();

    gerenciador.bloquear(1, { registroId: 1 });

    const timestampOutraSessao = Date.now();

    dados.set(
      "teste-em-edicao-1",
      JSON.stringify({
        registroId: 1,
        sessaoId: "outra-sessao",
        timestamp: timestampOutraSessao,
      }),
    );

    gerenciador.liberar();

    expect(gerenciador.consultar(1)).toEqual({
      registroId: 1,
      sessaoId: "outra-sessao",
      timestamp: timestampOutraSessao,
    });
  });
});
