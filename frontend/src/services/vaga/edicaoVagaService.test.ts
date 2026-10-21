import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  bloquearVaga,
  obterBloqueioVaga,
  vagaEstaBloqueada,
  liberarBloqueioVaga,
} from "./edicaoVagaService";

describe("edicaoVagaService", () => {
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

    liberarBloqueioVaga();
  });

  afterEach(() => {
    liberarBloqueioVaga();
    vi.useRealTimers();
    vi.unstubAllGlobals();
  });

  it("deve bloquear uma vaga disponível", () => {
    expect(bloquearVaga(1, 10)).toBe(true);
    expect(vagaEstaBloqueada(1)).toBe(true);
  });

  it("não deve bloquear uma vaga já bloqueada", () => {
    bloquearVaga(1, 10);

    expect(bloquearVaga(1, 20)).toBe(false);
  });

  it("deve consultar os dados do bloqueio", () => {
    bloquearVaga(1, 10);

    expect(obterBloqueioVaga(1)).toEqual({
      vagaId: 1,
      empresaId: 10,
      sessaoId: expect.any(String),
      timestamp: Date.now(),
    });
  });

  it("deve retornar null para uma vaga sem bloqueio", () => {
    expect(obterBloqueioVaga(99)).toBeNull();
  });

  it("deve remover um bloqueio expirado", () => {
    dados.set(
      "vaga-em-edicao-1",
      JSON.stringify({
        vagaId: 1,
        empresaId: 10,
        sessaoId: expect.any(String),
        timestamp: Date.now() - 6000,
      }),
    );

    expect(obterBloqueioVaga(1)).toBeNull();
    expect(dados.has("vaga-em-edicao-1")).toBe(false);
  });

  it("deve liberar o bloqueio da vaga atual", () => {
    bloquearVaga(1, 10);

    liberarBloqueioVaga();

    expect(vagaEstaBloqueada(1)).toBe(false);
  });

  it("deve renovar o bloqueio pelo heartbeat", () => {
    bloquearVaga(1, 10);

    const timestampInicial = Date.now();

    vi.advanceTimersByTime(2000);

    expect(obterBloqueioVaga(1)).toEqual({
      vagaId: 1,
      empresaId: 10,
      sessaoId: expect.any(String),
      timestamp: timestampInicial + 2000,
    });
  });

  it("não deve bloquear outra vaga enquanto existe uma edição ativa", () => {
    expect(bloquearVaga(1, 10)).toBe(true);

    expect(bloquearVaga(2, 10)).toBe(false);

    expect(vagaEstaBloqueada(1)).toBe(true);
    expect(vagaEstaBloqueada(2)).toBe(false);

    liberarBloqueioVaga();

    expect(vagaEstaBloqueada(1)).toBe(false);
  });

  it("não deve renovar bloqueio pertencente a outra sessão", () => {
    bloquearVaga(1, 10);

    const chave = "vaga-em-edicao-1";
    const bloqueioOriginal = JSON.parse(dados.get(chave)!);

    expect(bloqueioOriginal.sessaoId).toBeDefined();

    vi.advanceTimersByTime(1000);

    const timestampOutraSessao = Date.now();

    dados.set(
      chave,
      JSON.stringify({
        vagaId: 1,
        empresaId: 10,
        sessaoId: "outra-sessao",
        timestamp: timestampOutraSessao,
      }),
    );

    vi.advanceTimersByTime(1000);

    expect(obterBloqueioVaga(1)).toEqual({
      vagaId: 1,
      empresaId: 10,
      sessaoId: expect.any(String),
      timestamp: timestampOutraSessao,
    });
  });

  it("não deve liberar bloqueio pertencente a outra sessão", () => {
    bloquearVaga(1, 10);

    const chave = "vaga-em-edicao-1";
    const timestampOutraSessao = Date.now();

    dados.set(
        chave,
        JSON.stringify({
            vagaId: 1,
            empresaId: 10,
            sessaoId: "outra-sessao",
            timestamp: timestampOutraSessao
        })
    );

    liberarBloqueioVaga();

    expect(obterBloqueioVaga(1)).toEqual({
        vagaId: 1,
        empresaId: 10,
        sessaoId: "outra-sessao",
        timestamp: timestampOutraSessao
    });
});


});
