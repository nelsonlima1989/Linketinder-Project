import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { Chart } from "chart.js/auto";

import {
  renderizarGraficoSkills,
  criarGraficoSkills,
} from "./renderizarGraficoSkills";

vi.mock("chart.js/auto", () => {
  const ChartMock = vi.fn();

  Object.assign(ChartMock, {
    getChart: vi.fn(),
  });

  return { Chart: ChartMock };
});

const chartMock = vi.mocked(Chart);

describe("renderizarGraficoSkills", () => {
  it("deve renderizar a seção do gráfico", () => {
    const html = renderizarGraficoSkills();

    expect(html).toContain('class="grafico-skills"');
    expect(html).toContain("Skills dos candidatos");
  });

  it("deve renderizar o canvas com o identificador correto", () => {
    const html = renderizarGraficoSkills();

    expect(html).toContain('<canvas id="grafico-skills"></canvas>');
  });

  it("deve retornar uma string HTML", () => {
    const html = renderizarGraficoSkills();

    expect(typeof html).toBe("string");
  });
});

describe("criarGraficoSkills", () => {
  const canvas = {} as HTMLCanvasElement;

  beforeEach(() => {
    vi.clearAllMocks();

    vi.stubGlobal("document", {
      querySelector: vi.fn().mockReturnValue(canvas),
    });

    vi.mocked(Chart.getChart).mockReturnValue(undefined);
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("não deve criar gráfico quando o canvas não existir", () => {
    vi.mocked(document.querySelector).mockReturnValue(null);

    criarGraficoSkills({ Java: 3 });

    expect(Chart).not.toHaveBeenCalled();
  });

  it("deve buscar o canvas pelo identificador correto", () => {
    criarGraficoSkills({ Java: 3 });

    expect(document.querySelector).toHaveBeenCalledWith("#grafico-skills");
  });

  it("deve criar um gráfico de barras horizontal", () => {
    criarGraficoSkills({ Java: 3 });

    expect(chartMock).toHaveBeenCalledWith(
      canvas,
      expect.objectContaining({
        type: "bar",
        options: expect.objectContaining({
          indexAxis: "y",
          responsive: true,
        }),
      }),
    );
  });

  it("deve utilizar as skills como rótulos", () => {
    criarGraficoSkills({
      Java: 3,
      TypeScript: 5,
    });

    expect(chartMock).toHaveBeenCalledWith(
      canvas,
      expect.objectContaining({
        data: expect.objectContaining({
          labels: ["Java", "TypeScript"],
        }),
      }),
    );
  });

  it("deve utilizar as quantidades de candidatos", () => {
    criarGraficoSkills({
      Java: 3,
      TypeScript: 5,
    });

    expect(chartMock).toHaveBeenCalledWith(
      canvas,
      expect.objectContaining({
        data: expect.objectContaining({
          datasets: [
            expect.objectContaining({
              label: "Quantidade de candidatos",
              data: [3, 5],
            }),
          ],
        }),
      }),
    );
  });

  it("deve permitir criar um gráfico sem skills", () => {
    criarGraficoSkills({});

    expect(chartMock).toHaveBeenCalledWith(
      canvas,
      expect.objectContaining({
        data: expect.objectContaining({
          labels: [],
          datasets: [
            expect.objectContaining({
              data: [],
            }),
          ],
        }),
      }),
    );
  });

  it("não deve ocultar automaticamente os rótulos das skills", () => {
    criarGraficoSkills({ Java: 3 });

    expect(chartMock).toHaveBeenCalledWith(
      canvas,
      expect.objectContaining({
        options: expect.objectContaining({
          scales: expect.objectContaining({
            y: expect.objectContaining({
              ticks: {
                autoSkip: false,
              },
            }),
          }),
        }),
      }),
    );
  });

  it("deve destruir o gráfico existente antes de criar outro", () => {
    const destruirGrafico = vi.fn();

    vi.mocked(Chart.getChart).mockReturnValue({
      destroy: destruirGrafico,
    } as unknown as Chart);

    criarGraficoSkills({ Java: 3 });

    expect(Chart.getChart).toHaveBeenCalledWith(canvas);
    expect(destruirGrafico).toHaveBeenCalledOnce();
    expect(chartMock).toHaveBeenCalledOnce();

    expect(destruirGrafico.mock.invocationCallOrder[0]).toBeLessThan(
      chartMock.mock.invocationCallOrder[0],
    );
  });
});
