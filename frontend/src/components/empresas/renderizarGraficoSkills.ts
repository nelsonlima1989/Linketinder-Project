import { Chart } from "chart.js/auto";

export function renderizarGraficoSkills(): string {
  return `
        <section class="grafico-skills">
            <h2>Skills dos candidatos</h2>

            <canvas id="grafico-skills"></canvas>
        </section>
    `;
}

export function criarGraficoSkills(
  contagemSkills: Record<string, number>,
): void {
  const canvas = document.querySelector<HTMLCanvasElement>("#grafico-skills");

  console.log("Canvas encontrado:", canvas);

  if (!canvas) {
    return;
  }

  new Chart(canvas, {
    type: "bar",

    data: {
      labels: Object.keys(contagemSkills),

      datasets: [
        {
          label: "Quantidade de candidatos",
          data: Object.values(contagemSkills),
        },
      ],
    },

    options: {
      responsive: true,
      indexAxis: "y",

      scales: {
        y: {
          ticks: {
            autoSkip: false,
          },
        },
      },
    },
  });
}
