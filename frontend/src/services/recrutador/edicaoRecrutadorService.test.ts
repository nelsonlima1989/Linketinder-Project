import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
    bloquearRecrutador,
    obterBloqueioRecrutador,
    recrutadorEstaBloqueado,
    liberarBloqueioRecrutador,
} from "./edicaoRecrutadorService";

describe("edicaoRecrutadorService", () => {
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

        liberarBloqueioRecrutador();
    });

    afterEach(() => {
        liberarBloqueioRecrutador();
        vi.useRealTimers();
        vi.unstubAllGlobals();
    });

    it("deve bloquear um recrutador disponível", () => {
        expect(bloquearRecrutador(1, 3)).toBe(true);
        expect(recrutadorEstaBloqueado(1)).toBe(true);
    });

    it("não deve bloquear um recrutador já bloqueado", () => {
        bloquearRecrutador(1, 3);

        expect(bloquearRecrutador(1, 3)).toBe(false);
    });

    it("deve consultar os dados do bloqueio", () => {
        bloquearRecrutador(1, 3);

        expect(obterBloqueioRecrutador(1)).toEqual({
            recrutadorId: 1,
            empresaId: 3,
            sessaoId: expect.any(String),
            timestamp: Date.now(),
        });
    });

    it("deve retornar null para um recrutador sem bloqueio", () => {
        expect(obterBloqueioRecrutador(99)).toBeNull();
    });

    it("deve remover um bloqueio expirado", () => {
        dados.set(
            "recrutador-em-edicao-1",
            JSON.stringify({
                recrutadorId: 1,
                empresaId: 3,
                sessaoId: "sessao-expirada",
                timestamp: Date.now() - 6000,
            }),
        );

        expect(obterBloqueioRecrutador(1)).toBeNull();
        expect(dados.has("recrutador-em-edicao-1")).toBe(false);
    });

    it("deve liberar o bloqueio do recrutador atual", () => {
        bloquearRecrutador(1, 3);

        liberarBloqueioRecrutador();

        expect(recrutadorEstaBloqueado(1)).toBe(false);
    });

    it("deve renovar o bloqueio pelo heartbeat", () => {
        bloquearRecrutador(1, 3);

        const timestampInicial = Date.now();

        vi.advanceTimersByTime(2000);

        expect(obterBloqueioRecrutador(1)).toEqual({
            recrutadorId: 1,
            empresaId: 3,
            sessaoId: expect.any(String),
            timestamp: timestampInicial + 2000,
        });
    });

    it("não deve bloquear outro recrutador enquanto existe uma edição ativa", () => {
        expect(bloquearRecrutador(1, 3)).toBe(true);

        expect(bloquearRecrutador(2, 3)).toBe(false);

        expect(recrutadorEstaBloqueado(1)).toBe(true);
        expect(recrutadorEstaBloqueado(2)).toBe(false);

        liberarBloqueioRecrutador();

        expect(recrutadorEstaBloqueado(1)).toBe(false);
    });

    it("não deve renovar bloqueio pertencente a outra sessão", () => {
        bloquearRecrutador(1, 3);

        const chave = "recrutador-em-edicao-1";
        const bloqueioOriginal = JSON.parse(dados.get(chave)!);

        expect(bloqueioOriginal.sessaoId).toBeDefined();

        vi.advanceTimersByTime(1000);

        const timestampOutraSessao = Date.now();

        dados.set(
            chave,
            JSON.stringify({
                recrutadorId: 1,
                empresaId: 3,
                sessaoId: "outra-sessao",
                timestamp: timestampOutraSessao,
            }),
        );

        vi.advanceTimersByTime(1000);

        expect(obterBloqueioRecrutador(1)).toEqual({
            recrutadorId: 1,
            empresaId: 3,
            sessaoId: expect.any(String),
            timestamp: timestampOutraSessao,
        });
    });

    it("não deve liberar bloqueio pertencente a outra sessão", () => {
        bloquearRecrutador(1, 3);

        const chave = "recrutador-em-edicao-1";
        const timestampOutraSessao = Date.now();

        dados.set(
            chave,
            JSON.stringify({
                recrutadorId: 1,
                empresaId: 3,
                sessaoId: "outra-sessao",
                timestamp: timestampOutraSessao,
            }),
        );

        liberarBloqueioRecrutador();

        expect(obterBloqueioRecrutador(1)).toEqual({
            recrutadorId: 1,
            empresaId: 3,
            sessaoId: "outra-sessao",
            timestamp: timestampOutraSessao,
        });
    });
});
