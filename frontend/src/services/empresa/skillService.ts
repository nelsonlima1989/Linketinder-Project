import type { Candidato } from "../../models/Candidato";

export function contarSkills(candidatos: Candidato[]): Record<string, number> {
  const contagem: Record<string, number> = {};

  for (const candidato of candidatos) {
    for (const skill of candidato.skills) {
      contagem[skill] = (contagem[skill] ?? 0) + 1;
    }
  }

  return contagem;
}

export function ordenarSkills(
  contagemSkills: Record<string, number>,
): Record<string, number> {
  return Object.fromEntries(
    Object.entries(contagemSkills).sort(
      ([, quantidadeA], [, quantidadeB]) => quantidadeB - quantidadeA,
    ),
  );
}
