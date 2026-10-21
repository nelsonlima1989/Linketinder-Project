import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  bloquearEmpresa,
  obterBloqueioEmpresa,
  empresaEstaBloqueada,
  liberarBloqueioEmpresa,
} from "./edicaoEmpresaService";

describe("edicaoEmpresaService", () => {
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

    liberarBloqueioEmpresa();
  });

  afterEach(() => {
    liberarBloqueioEmpresa();
    vi.useRealTimers();
    vi.unstubAllGlobals();
  });

  it("deve bloquear uma empresa disponível", () => {
    expect(bloquearEmpresa(1)).toBe(true);
    expect(empresaEstaBloqueada(1)).toBe(true);
  });

  it("não deve bloquear uma empresa já bloqueada", () => {
    bloquearEmpresa(1);

    expect(bloquearEmpresa(1)).toBe(false);
  });

  it("deve consultar os dados do bloqueio", () => {
    bloquearEmpresa(1);

    expect(obterBloqueioEmpresa(1)).toEqual({
      empresaId: 1,
      sessaoId: expect.any(String),
      timestamp: Date.now(),
    });
  });

  it("deve retornar null para empresa sem bloqueio", () => {
    expect(obterBloqueioEmpresa(99)).toBeNull();
  });

  it("deve remover um bloqueio expirado", () => {
    dados.set(
      "empresa-em-edicao-1",
      JSON.stringify({
        empresaId: 1,
        timestamp: Date.now() - 6000,
      }),
    );

    expect(obterBloqueioEmpresa(1)).toBeNull();
    expect(dados.has("empresa-em-edicao-1")).toBe(false);
  });

  it("deve liberar o bloqueio da empresa atual", () => {
    bloquearEmpresa(1);

    liberarBloqueioEmpresa();

    expect(empresaEstaBloqueada(1)).toBe(false);
  });

  it("deve renovar o bloqueio pelo heartbeat", () => {
    bloquearEmpresa(1);

    const timestampInicial = Date.now();

    vi.advanceTimersByTime(2000);

    expect(obterBloqueioEmpresa(1)?.timestamp).toBe(timestampInicial + 2000);
  });

  it("não deve bloquear outra empresa enquanto existe uma edição ativa", () => {
    expect(bloquearEmpresa(1)).toBe(true);

    expect(bloquearEmpresa(2)).toBe(false);

    expect(empresaEstaBloqueada(1)).toBe(true);
    expect(empresaEstaBloqueada(2)).toBe(false);
  });

  it("não deve renovar bloqueio pertencente a outra sessão", () => {
    bloquearEmpresa(1);

    vi.advanceTimersByTime(1000);

    const timestampOutraSessao = Date.now();

    dados.set(
      "empresa-em-edicao-1",
      JSON.stringify({
        empresaId: 1,
        sessaoId: "outra-sessao",
        timestamp: timestampOutraSessao,
      }),
    );

    vi.advanceTimersByTime(1000);

    expect(obterBloqueioEmpresa(1)).toEqual({
      empresaId: 1,
      sessaoId: "outra-sessao",
      timestamp: timestampOutraSessao,
    });
  });

  it("não deve liberar bloqueio pertencente a outra sessão", () => {
    bloquearEmpresa(1);

    const timestampOutraSessao = Date.now();

    dados.set(
      "empresa-em-edicao-1",
      JSON.stringify({
        empresaId: 1,
        sessaoId: "outra-sessao",
        timestamp: timestampOutraSessao,
      }),
    );

    liberarBloqueioEmpresa();

    expect(obterBloqueioEmpresa(1)).toEqual({
      empresaId: 1,
      sessaoId: "outra-sessao",
      timestamp: timestampOutraSessao,
    });
  });
});
