import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  bloquearCandidato,
  obterBloqueioCandidato,
  candidatoEstaBloqueado,
  liberarBloqueioCandidato,
} from "./edicaoCandidatoService";

describe("edicaoCandidatoService", () => {
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

    liberarBloqueioCandidato();
  });

  afterEach(() => {
    liberarBloqueioCandidato();
    vi.useRealTimers();
    vi.unstubAllGlobals();
  });

  it("deve bloquear candidato disponível", () => {
    expect(bloquearCandidato(1)).toBe(true);
    expect(candidatoEstaBloqueado(1)).toBe(true);
  });

  it("não deve bloquear candidato já bloqueado", () => {
    bloquearCandidato(1);

    expect(bloquearCandidato(1)).toBe(false);
  });

  it("deve consultar bloqueio existente", () => {
    bloquearCandidato(1);

    const bloqueio = obterBloqueioCandidato(1);

    expect(bloqueio).toEqual({
      candidatoId: 1,
      sessaoId: expect.any(String),
      timestamp: Date.now(),
    });
  });

  it("deve retornar null para candidato não bloqueado", () => {
    expect(obterBloqueioCandidato(99)).toBeNull();
  });

  it("deve identificar bloqueio expirado", () => {
    dados.set(
      "candidato-em-edicao-1",
      JSON.stringify({
        candidatoId: 1,
        timestamp: Date.now() - 6000,
      }),
    );

    expect(obterBloqueioCandidato(1)).toBeNull();
    expect(dados.has("candidato-em-edicao-1")).toBe(false);
  });

  it("deve liberar o bloqueio do candidato atual", () => {
    bloquearCandidato(1);

    liberarBloqueioCandidato();

    expect(candidatoEstaBloqueado(1)).toBe(false);
  });

  it("deve renovar o bloqueio durante o heartbeat", () => {
    bloquearCandidato(1);

    const timestampInicial = Date.now();

    vi.advanceTimersByTime(2000);

    const bloqueio = obterBloqueioCandidato(1);

    expect(bloqueio?.timestamp).toBe(timestampInicial + 2000);
  });

  it("não deve bloquear outro candidato enquanto existe uma edição ativa", () => {
    expect(bloquearCandidato(1)).toBe(true);

    expect(bloquearCandidato(2)).toBe(false);

    expect(candidatoEstaBloqueado(1)).toBe(true);
    expect(candidatoEstaBloqueado(2)).toBe(false);

    liberarBloqueioCandidato();

    expect(candidatoEstaBloqueado(1)).toBe(false);
  });

  it("não deve renovar bloqueio pertencente a outra sessão", () => {
    bloquearCandidato(1);

    const chave = "candidato-em-edicao-1";

    const bloqueioOriginal = JSON.parse(dados.get(chave)!);

    expect(bloqueioOriginal.sessaoId).toBeDefined();

    vi.advanceTimersByTime(1000);

    const timestampOutraAba = Date.now();

    dados.set(
      chave,
      JSON.stringify({
        candidatoId: 1,
        sessaoId: "outra-sessao",
        timestamp: timestampOutraAba,
      }),
    );

    vi.advanceTimersByTime(1000);

    const bloqueioAtual = JSON.parse(dados.get(chave)!);

    expect(bloqueioAtual.sessaoId).toBe("outra-sessao");
    expect(bloqueioAtual.timestamp).toBe(
      new Date("2026-01-01T12:00:01Z").getTime(),
    );
  });

  it("não deve liberar bloqueio pertencente a outra sessão", () => {
    bloquearCandidato(1);

    const chave = "candidato-em-edicao-1";

    dados.set(
      chave,
      JSON.stringify({
        candidatoId: 1,
        sessaoId: "outra-sessao",
        timestamp: Date.now(),
      }),
    );

    liberarBloqueioCandidato();

    expect(obterBloqueioCandidato(1)).toEqual({
      candidatoId: 1,
      sessaoId: "outra-sessao",
      timestamp: Date.now(),
    });
  });
});
