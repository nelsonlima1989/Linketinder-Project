import {
  criarRecrutador,
  editarRecrutador,
  excluirRecrutador,
  obterRecrutadorPorId,
} from "../services/recrutador/recrutadorService";

import {
  bloquearRecrutador,
  liberarBloqueioRecrutador,
  recrutadorEstaBloqueado,
} from "../services/recrutador/edicaoRecrutadorService";

import type { DadosFormularioRecrutador } from "../models/Recrutador";

type ConfiguracaoFormularioRecrutador = {
  empresaId: number;
  recrutadorId?: number;
  navegarParaEmpresa: () => void;
};

export function extrairDadosRecrutador(
  dados: FormData,
): DadosFormularioRecrutador {
  return {
    nome: String(dados.get("nome") ?? ""),
    areaRecrutamento: String(dados.get("areaRecrutamento") ?? ""),
    descricao: String(dados.get("descricao") ?? ""),
    email: String(dados.get("email") ?? ""),
    cpf: String(dados.get("cpf") ?? ""),
    dataNascimento: String(dados.get("dataNascimento") ?? ""),
    senha: String(dados.get("senha") ?? ""),
  };
}

function obterMensagemDeErro(erro: unknown): string {
  if (erro instanceof Error && erro.message) {
    return erro.message;
  }

  return "Não foi possível salvar o recrutador.";
}

export function configurarFormularioRecrutador(
  configuracao: ConfiguracaoFormularioRecrutador,
): void {
  const formulario = document.querySelector<HTMLFormElement>(
    "#formulario-recrutador",
  );

  const botaoCancelar = document.querySelector<HTMLButtonElement>(
    "#botao-cancelar-recrutador",
  );

  if (botaoCancelar) {
    botaoCancelar.addEventListener("click", () => {
      if (configuracao.recrutadorId !== undefined) {
        liberarBloqueioRecrutador();
      }

      configuracao.navegarParaEmpresa();
    });
  }

  if (!formulario) {
    return;
  }

  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();

    const dadosRecrutador = extrairDadosRecrutador(new FormData(formulario));

    try {
      if (configuracao.recrutadorId !== undefined) {
        const atualizou = editarRecrutador(
          configuracao.recrutadorId,
          configuracao.empresaId,
          dadosRecrutador,
        );

        if (!atualizou) {
          alert("Não foi possível atualizar o recrutador.");
          return;
        }

        liberarBloqueioRecrutador();

        alert("Recrutador atualizado com sucesso!");
      } else {
        criarRecrutador(dadosRecrutador, configuracao.empresaId);

        alert("Recrutador cadastrado com sucesso!");
      }
    } catch (erro) {
      // A mensagem do serviço nunca contém CPF, e-mail ou senha.
      alert(obterMensagemDeErro(erro));
      return;
    }

    configuracao.navegarParaEmpresa();
  });
}

type ConfiguracaoExclusaoRecrutador = {
  empresaId: number;
  aoExcluir: () => void;
};

export function configurarExclusaoRecrutador(
  configuracao: ConfiguracaoExclusaoRecrutador,
): void {
  const botoesExcluir = document.querySelectorAll<HTMLButtonElement>(
    ".botao-excluir-recrutador",
  );

  botoesExcluir.forEach((botao) => {
    botao.addEventListener("click", () => {
      const recrutadorId = Number(botao.dataset.recrutadorId);

      if (recrutadorEstaBloqueado(recrutadorId)) {
        alert(
          "Exclusão bloqueada: este recrutador está sendo editado em outra aba.",
        );
        return;
      }

      const confirmou = confirm("Deseja realmente excluir este recrutador?");

      if (!confirmou) {
        return;
      }

      const excluiu = excluirRecrutador(recrutadorId, configuracao.empresaId);

      if (!excluiu) {
        alert("Não foi possível excluir o recrutador.");
        return;
      }

      alert("Recrutador excluído com sucesso!");

      configuracao.aoExcluir();
    });
  });
}

type ConfiguracaoEdicaoRecrutador = {
  empresaId: number;
  abrirFormulario: (recrutadorId: number) => void;
};

export function configurarEdicaoRecrutador(
  configuracao: ConfiguracaoEdicaoRecrutador,
): void {
  const botoesEditar = document.querySelectorAll<HTMLButtonElement>(
    ".botao-editar-recrutador",
  );

  botoesEditar.forEach((botao) => {
    botao.addEventListener("click", () => {
      const recrutadorId = Number(botao.dataset.recrutadorId);

      if (!Number.isInteger(recrutadorId) || recrutadorId <= 0) {
        return;
      }

      const recrutador = obterRecrutadorPorId(
        recrutadorId,
        configuracao.empresaId,
      );

      if (!recrutador) {
        return;
      }

      const bloqueou = bloquearRecrutador(recrutadorId, configuracao.empresaId);

      if (!bloqueou) {
        alert("Este recrutador está sendo editado em outra aba.");
        return;
      }

      configuracao.abrirFormulario(recrutadorId);
    });
  });
}

type ConfiguracaoCriacaoRecrutador = {
  abrirFormulario: () => void;
};

export function configurarCriacaoRecrutador(
  configuracao: ConfiguracaoCriacaoRecrutador,
): void {
  const botaoNovoRecrutador = document.querySelector<HTMLButtonElement>(
    "#botao-novo-recrutador",
  );

  if (!botaoNovoRecrutador) {
    return;
  }

  botaoNovoRecrutador.addEventListener("click", () => {
    configuracao.abrirFormulario();
  });
}
