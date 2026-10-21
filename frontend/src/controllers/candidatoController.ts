
import {
  criarCandidato,
  editarCandidato,
  excluirCandidato,
  type DadosFormularioCandidato,
} from "../services/candidato/candidatoService";

import {
  bloquearCandidato,
  candidatoEstaBloqueado,
  liberarBloqueioCandidato,
} from "../services/candidato/edicaoCandidatoService";

// Configuração do formulário de candidatos

type ConfiguracaoFormularioCandidato = {
  candidatoId?: number;
  navegarParaPerfil: () => void;
  aoCadastrar?: (candidatoId: number) => void;
};

export function extrairDadosCandidato(
  dados: FormData,
): DadosFormularioCandidato {
  return {
    nome: String(dados.get("nome") ?? ""),
    cpf: String(dados.get("cpf") ?? ""),
    idade: String(dados.get("idade") ?? ""),
    formacao: String(dados.get("formacao") ?? ""),
    descricao: String(dados.get("descricao") ?? ""),
    skills: String(dados.get("skills") ?? ""),
    estado: String(dados.get("estado") ?? ""),
  };
}

export function configurarFormularioCandidato(
  configuracao: ConfiguracaoFormularioCandidato,
): void {
  const formulario = document.querySelector<HTMLFormElement>(
    "#formulario-candidato",
  );

  const botaoCancelar = document.querySelector<HTMLButtonElement>(
    "#botao-cancelar-candidato",
  );

  if (botaoCancelar) {
    botaoCancelar.addEventListener("click", () => {
      if (configuracao.candidatoId !== undefined) {
        liberarBloqueioCandidato();
      }

      configuracao.navegarParaPerfil();
    });
  }

  if (!formulario) {
    return;
  }

  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();

    const dados = new FormData(formulario);
    const dadosCandidato = extrairDadosCandidato(dados);

    // Edição de candidato existente
    if (configuracao.candidatoId !== undefined) {
      editarCandidato(
        configuracao.candidatoId,
        dadosCandidato,
      );

      liberarBloqueioCandidato();

      alert("Perfil atualizado com sucesso!");

      configuracao.navegarParaPerfil();
      return;
    }

    // Cadastro de novo candidato
    const novoCandidatoId = criarCandidato(dadosCandidato);

    configuracao.aoCadastrar?.(novoCandidatoId);

    formulario.reset();

    alert("Candidato cadastrado com sucesso!");
  });
}

// Exclusão de candidatos

type ConfiguracaoExclusaoCandidato = {
  candidatoId?: number;
  aoExcluir: () => void;
};

export function configurarExclusaoCandidato(
  configuracao: ConfiguracaoExclusaoCandidato,
): void {
  const botaoExcluir = document.querySelector<HTMLButtonElement>(
    "#botao-excluir-candidato",
  );

  if (!botaoExcluir) {
    return;
  }

  botaoExcluir.addEventListener("click", () => {
    const candidatoId = configuracao.candidatoId;

    if (candidatoId === undefined) {
      return;
    }

    if (candidatoEstaBloqueado(candidatoId)) {
      alert(
        "Exclusão bloqueada: este candidato está sendo editado em outra aba.",
      );
      return;
    }

    const confirmou = confirm(
      "Deseja realmente excluir sua conta?",
    );

    if (!confirmou) {
      return;
    }

    const excluiu = excluirCandidato(candidatoId);

    if (!excluiu) {
      alert("Não foi possível excluir a conta.");
      return;
    }

    alert("Conta excluída com sucesso!");

    configuracao.aoExcluir();
  });
}

// Abertura da edição de candidatos

type ConfiguracaoEdicaoCandidato = {
  candidatoId?: number;
  abrirFormulario: () => void;
};

export function configurarEdicaoCandidato(
  configuracao: ConfiguracaoEdicaoCandidato,
): void {
  const botaoEditar = document.querySelector<HTMLButtonElement>(
    "#botao-editar-candidato",
  );

  if (!botaoEditar) {
    return;
  }

  botaoEditar.addEventListener("click", () => {
    const candidatoId = configuracao.candidatoId;

    if (candidatoId === undefined) {
      return;
    }

    const bloqueou = bloquearCandidato(candidatoId);

    if (!bloqueou) {
      alert("Este candidato está sendo editado em outra aba.");
      return;
    }

    configuracao.abrirFormulario();
  });
}
