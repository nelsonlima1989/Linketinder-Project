import {
  criarVaga,
  editarVaga,
  excluirVaga,
  obterVagaPorId,
  type DadosFormularioVaga,
} from "../services/vaga/vagaService";

import {
  bloquearVaga,
  liberarBloqueioVaga,
  vagaEstaBloqueada,
} from "../services/vaga/edicaoVagaService";

type ConfiguracaoFormularioVaga = {
  empresaId: number;
  vagaId?: number;
  navegarParaEmpresa: () => void;
};

export function extrairDadosVaga(dados: FormData): DadosFormularioVaga {
  return {
    titulo: String(dados.get("titulo") ?? ""),
    descricao: String(dados.get("descricao") ?? ""),
    skills: String(dados.get("skills") ?? ""),
  };
}

export function configurarFormularioVaga(
  configuracao: ConfiguracaoFormularioVaga,
): void {
  const formulario =
    document.querySelector<HTMLFormElement>("#formulario-vaga");

  const botaoCancelar = document.querySelector<HTMLButtonElement>(
    "#botao-cancelar-vaga",
  );

  if (botaoCancelar) {
    botaoCancelar.addEventListener("click", () => {
      if (configuracao.vagaId !== undefined) {
        liberarBloqueioVaga();
      }

      configuracao.navegarParaEmpresa();
    });
  }

  if (!formulario) {
    return;
  }

  formulario.addEventListener("submit", (evento) => {
    evento.preventDefault();

    const dadosVaga = extrairDadosVaga(new FormData(formulario));

    if (configuracao.vagaId !== undefined) {
      editarVaga(configuracao.vagaId, configuracao.empresaId, dadosVaga);

      liberarBloqueioVaga();

      alert("Vaga atualizada com sucesso!");
    } else {
      criarVaga(dadosVaga, configuracao.empresaId);

      alert("Vaga cadastrada com sucesso!");
    }

    configuracao.navegarParaEmpresa();
  });
}

type ConfiguracaoExclusaoVaga = {
  empresaId: number;
  aoExcluir: () => void;
};

export function configurarExclusaoVaga(
  configuracao: ConfiguracaoExclusaoVaga,
): void {
  const botoesExcluir = document.querySelectorAll<HTMLButtonElement>(
    ".botao-excluir-vaga",
  );

  botoesExcluir.forEach((botao) => {
    botao.addEventListener("click", () => {
      const vagaId = Number(botao.dataset.vagaId);

      if (vagaEstaBloqueada(vagaId)) {
        alert("Exclusão bloqueada: esta vaga está sendo editada em outra aba.");
        return;
      }

      const confirmou = confirm("Deseja realmente excluir esta vaga?");

      if (!confirmou) {
        return;
      }

      const excluiu = excluirVaga(vagaId, configuracao.empresaId);

      if (!excluiu) {
        alert("Não foi possível excluir a vaga.");
        return;
      }

      alert("Vaga excluída com sucesso!");

      configuracao.aoExcluir();
    });
  });
}

type ConfiguracaoEdicaoVaga = {
  empresaId: number;
  abrirFormulario: (vagaId: number) => void;
};

export function configurarEdicaoVaga(
  configuracao: ConfiguracaoEdicaoVaga,
): void {
  const botoesEditar =
    document.querySelectorAll<HTMLButtonElement>(".botao-editar-vaga");

  botoesEditar.forEach((botao) => {
    botao.addEventListener("click", () => {
      const vagaId = Number(botao.dataset.vagaId);

      if (!Number.isInteger(vagaId) || vagaId <= 0) {
        return;
      }

      const vaga = obterVagaPorId(vagaId, configuracao.empresaId);

      if (!vaga) {
        return;
      }

      const bloqueou = bloquearVaga(vagaId, configuracao.empresaId);

      if (!bloqueou) {
        alert("Esta vaga está sendo editada em outra aba.");
        return;
      }

      configuracao.abrirFormulario(vagaId);
    });
  });
}

type ConfiguracaoCriacaoVaga = {
  abrirFormulario: () => void;
};

export function configurarCriacaoVaga(
  configuracao: ConfiguracaoCriacaoVaga,
): void {
  const botaoNovaVaga =
    document.querySelector<HTMLButtonElement>("#botao-nova-vaga");

  if (!botaoNovaVaga) {
    return;
  }

  botaoNovaVaga.addEventListener("click", () => {
    configuracao.abrirFormulario();
  });
}
