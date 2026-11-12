import {
  criarEmpresa,
  editarEmpresa,
  type DadosFormularioEmpresa,
} from "../services/empresa/empresaService";

import {
  bloquearEmpresa,
  empresaEstaBloqueada,
  liberarBloqueioEmpresa,
} from "../services/empresa/edicaoEmpresaService";
import { excluirEmpresaComVagas } from "../services/empresa/exclusaoEmpresaService";

type ConfiguracaoFormularioEmpresa = {
  empresaId?: number;
  navegarParaPerfil: () => void;
  aoCadastrar?: (empresaId: number) => void;
};

export function extrairDadosEmpresa(dados: FormData): DadosFormularioEmpresa {
  return {
    nome: String(dados.get("nome") ?? ""),
    cnpj: String(dados.get("cnpj") ?? ""),
    descricao: String(dados.get("descricao") ?? ""),
    estado: String(dados.get("estado") ?? ""),
  };
}

export function configurarFormularioEmpresa(
  configuracao: ConfiguracaoFormularioEmpresa,
): void {
  const formulario = document.querySelector<HTMLFormElement>(
    "#formulario-empresa",
  );

  const botaoCancelar = document.querySelector<HTMLButtonElement>(
    "#botao-cancelar-empresa",
  );

  if (botaoCancelar) {
    botaoCancelar.addEventListener("click", () => {
      liberarBloqueioEmpresa();
      configuracao.navegarParaPerfil();
    });
  }

  if (!formulario) {
    return;
  }

  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();

    const dadosEmpresa = extrairDadosEmpresa(new FormData(formulario));

    if (configuracao.empresaId !== undefined) {
      editarEmpresa(configuracao.empresaId, dadosEmpresa);

      liberarBloqueioEmpresa();

      alert("Perfil atualizado com sucesso!");

      configuracao.navegarParaPerfil();
      return;
    }

    const novaEmpresaId = criarEmpresa(dadosEmpresa);

    configuracao.aoCadastrar?.(novaEmpresaId);

    formulario.reset();

    alert("Empresa cadastrada com sucesso!");
  });
}

type ConfiguracaoExclusaoEmpresa = {
  empresaId: number;
  aoExcluir?: () => void;
};

export function configurarExclusaoEmpresa(
  configuracao: ConfiguracaoExclusaoEmpresa,
): void {
  const botaoExcluir = document.querySelector<HTMLButtonElement>(
    "#botao-excluir-empresa",
  );

  if (!botaoExcluir) {
    return;
  }

  botaoExcluir.addEventListener("click", () => {
    const empresaId = configuracao.empresaId;

    if (empresaEstaBloqueada(empresaId)) {
      alert(
        "Exclusão bloqueada: esta empresa está sendo editada em outra aba.",
      );
      return;
    }

    const confirmou = confirm("Deseja realmente excluir sua conta?");

    if (!confirmou) {
      return;
    }

    // Exclusão atômica: empresa, vagas e recrutadores são removidos juntos
    // ou o estado anterior é restaurado.
    let excluiu = false;

    try {
      excluiu = excluirEmpresaComVagas(empresaId);
    } catch {
      alert("Não foi possível excluir a conta.");
      return;
    }

    if (!excluiu) {
      alert("Não foi possível excluir a conta.");
      return;
    }

    alert("Conta excluída com sucesso!");

    configuracao.aoExcluir?.();
  });
}

type ConfiguracaoEdicaoEmpresa = {
  empresaId: number;
  abrirFormulario: () => void;
};

export function configurarEdicaoEmpresa(
  configuracao: ConfiguracaoEdicaoEmpresa,
): void {
  const botaoEditar = document.querySelector<HTMLButtonElement>(
    "#botao-editar-empresa",
  );

  if (!botaoEditar) {
    return;
  }

  botaoEditar.addEventListener("click", () => {
    const bloqueou = bloquearEmpresa(configuracao.empresaId);

    if (!bloqueou) {
      alert("Esta empresa está sendo editada em outra aba.");
      return;
    }

    configuracao.abrirFormulario();
  });
}
