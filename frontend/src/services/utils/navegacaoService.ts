export type Tela =
  | "empresa"
  | "candidato"
  | "cadastro-candidato"
  | "cadastro-empresa"
  | "cadastro-vaga";

export const ITENS_MENU = [
  { tela: "empresa", titulo: "Empresa" },
  { tela: "candidato", titulo: "Candidato" },
  { tela: "cadastro-candidato", titulo: "Novo candidato" },
  { tela: "cadastro-empresa", titulo: "Nova empresa" },
] as const satisfies ReadonlyArray<{
  tela: Tela;
  titulo: string;
}>;

const TELAS_MENU: ReadonlySet<string> = new Set(
  ITENS_MENU.map((item) => item.tela),
);

export function obterTelaInicial(): Tela {
  return "empresa";
}

export function obterTelaDoMenu(
  elemento: Pick<HTMLElement, "dataset">,
): Tela | null {
  const tela = elemento.dataset.tela;

  if (tela && TELAS_MENU.has(tela)) {
    return tela as Tela;
  }

  return null;
}
