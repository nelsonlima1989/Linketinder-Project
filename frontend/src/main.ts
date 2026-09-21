import "./styles/style.css";

import {
  listarCandidatos,
  criarCandidato,
  editarCandidato,
  excluirCandidato,
} from "./services/candidato/candidatoService";

import {
  listarVagas,
  criarVaga,
  obterVagasPorEmpresa,
  excluirVaga,
  editarVaga,
  obterVagaPorId,
  excluirVagasPorEmpresa,
} from "./services/vaga/vagaService";

import { renderizarCardsVagasEmpresa } from "./components/vagas/renderizarCardsVagasEmpresa";
import { renderizarCardsVagas } from "./components/vagas/renderizarCardsVagas";
import { renderizarCardsCandidatos } from "./components/candidatos/renderizarCardsCandidatos";
import { renderizarSidebar } from "./components/layout/renderizarSidebar";
import { renderizarTela } from "./components/layout/renderizarTela";
import { contarSkills, ordenarSkills } from "./services/empresa/skillService";
import { criarGraficoSkills } from "./components/empresas/renderizarGraficoSkills";
import {
  criarEmpresa,
  editarEmpresa,
  excluirEmpresa,
} from "./services/empresa/empresaService";

import {
  bloquearCandidato,
  liberarBloqueioCandidato,
  candidatoEstaBloqueado,
} from "./services/candidato/edicaoCandidatoService";

import {
  bloquearVaga,
  liberarBloqueioVaga,
  vagaEstaBloqueada,
} from "./services/vaga/edicaoVagaService";

import {
  bloquearEmpresa,
  empresaEstaBloqueada,
  liberarBloqueioEmpresa,
} from "./services/empresa/edicaoEmpresaService";

import {
  obterTelaInicial,
  obterTelaDoMenu,
  type Tela,
} from "./services/ui/navegacaoService";

const candidatos = listarCandidatos();

const vagas = listarVagas();

const skills = ordenarSkills(contarSkills(candidatos));

const app = document.querySelector<HTMLDivElement>("#app");

let telaAtual: Tela = obterTelaInicial();

let empresaAtualId = 3;

let candidatoAtualId: number | undefined = 3;

function atualizarConteudo(
  tela: Tela,
  empresaId?: number,
  vagaId?: number,
): void {
  if (empresaId !== undefined) {
    empresaAtualId = empresaId;
  }

  const conteudo = document.querySelector<HTMLElement>(".conteudo");

  if (!conteudo) {
    return;
  }

  conteudo.innerHTML = renderizarTela(
    tela,
    empresaAtualId,
    candidatoAtualId,
    vagaId,
  );

  // cadastro e edição de candidato

  if (tela === "cadastro-candidato") {
    const formulario = document.querySelector<HTMLFormElement>(
      "#formulario-candidato",
    );

    const botaoCancelar = document.querySelector<HTMLButtonElement>(
      "#botao-cancelar-candidato",
    );

    if (botaoCancelar) {
      botaoCancelar.addEventListener("click", () => {
        if (candidatoAtualId !== undefined) {
          liberarBloqueioCandidato();
        }

        atualizarConteudo("candidato");
      });
    }

    if (formulario) {
      formulario.addEventListener("submit", (evento) => {
        evento.preventDefault();

        const dados = new FormData(formulario);

        const dadosCandidato = {
          nome: String(dados.get("nome")),
          cpf: String(dados.get("cpf")),
          idade: String(dados.get("idade")),
          formacao: String(dados.get("formacao")),
          descricao: String(dados.get("descricao")),
          skills: String(dados.get("skills")),
          estado: String(dados.get("estado")),
        };

        if (candidatoAtualId !== undefined) {
          editarCandidato(candidatoAtualId, dadosCandidato);

          liberarBloqueioCandidato();

          alert("Perfil atualizado com sucesso!");

          atualizarConteudo("candidato");

          return;
        }

        criarCandidato(dadosCandidato);

        formulario.reset();

        alert("Candidato cadastrado com sucesso!");
      });
    }
  }

  // perfil da empresa

  if (tela === "empresa") {
    const botaoNovaVaga =
      document.querySelector<HTMLButtonElement>("#botao-nova-vaga");

    if (botaoNovaVaga) {
      botaoNovaVaga.addEventListener("click", () => {
        atualizarConteudo("cadastro-vaga", empresaAtualId);
      });
    }

    const botaoEditarEmpresa = document.querySelector<HTMLButtonElement>(
      "#botao-editar-empresa",
    );

    if (botaoEditarEmpresa) {
      botaoEditarEmpresa.addEventListener("click", () => {
        const bloqueou = bloquearEmpresa(empresaAtualId);

        if (!bloqueou) {
          alert("Esta empresa está sendo editada em outra aba.");

          return;
        }

        atualizarConteudo("cadastro-empresa", empresaAtualId);
      });
    }

    const botaoExcluirEmpresa = document.querySelector<HTMLButtonElement>(
      "#botao-excluir-empresa",
    );

    if (botaoExcluirEmpresa) {
      botaoExcluirEmpresa.addEventListener("click", () => {
        if (empresaAtualId === undefined) return;

        if (empresaEstaBloqueada(empresaAtualId)) {
          alert(
            "Exclusão bloqueada: esta empresa está sendo editada em outra aba.",
          );
          return;
        }

        const confirmar = confirm("Deseja realmente excluir sua conta?");

        if (!confirmar) return;

        const excluiu = excluirEmpresa(empresaAtualId);

        if (!excluiu) {
          alert("Não foi possível excluir a conta.");
          return;
        }

        excluirVagasPorEmpresa(empresaAtualId);

        alert("Conta excluída com sucesso!");
      });
    }

    const listaCandidatos =
      document.querySelector<HTMLDivElement>("#lista-candidatos");

    if (listaCandidatos) {
      listaCandidatos.innerHTML = renderizarCardsCandidatos(candidatos);
    }

    const listaVagasEmpresa = document.querySelector<HTMLDivElement>(
      "#lista-vagas-empresa",
    );

    if (listaVagasEmpresa) {
      const vagasDaEmpresa = obterVagasPorEmpresa(empresaAtualId);

      listaVagasEmpresa.innerHTML = renderizarCardsVagasEmpresa(vagasDaEmpresa);
    }

    // excluir vaga

    const botoesExcluir = document.querySelectorAll<HTMLButtonElement>(
      ".botao-excluir-vaga",
    );

    botoesExcluir.forEach((botao) => {
      botao.addEventListener("click", () => {
        const vagaId = Number(botao.dataset.vagaId);

        if (vagaEstaBloqueada(vagaId)) {
          alert(
            "Exclusão bloqueada: esta vaga está sendo editada em outra aba.",
          );

          return;
        }

        const confirmar = confirm("Deseja realmente excluir esta vaga?");

        if (!confirmar) {
          return;
        }

        const excluiu = excluirVaga(vagaId, empresaAtualId);

        if (!excluiu) {
          alert("Não foi possível excluir a vaga.");

          return;
        }

        alert("Vaga excluída com sucesso!");

        atualizarConteudo("empresa", empresaAtualId);
      });
    });

    //editar vaga

    const botoesEditar =
      document.querySelectorAll<HTMLButtonElement>(".botao-editar-vaga");

    botoesEditar.forEach((botao) => {
      botao.addEventListener("click", () => {
        const vagaId = Number(botao.dataset.vagaId);

        const vaga = obterVagaPorId(vagaId, empresaAtualId);

        if (!vaga) {
          return;
        }

        const bloqueou = bloquearVaga(vagaId, empresaAtualId);

        if (!bloqueou) {
          alert("Esta vaga está sendo editada em outra aba.");

          return;
        }

        atualizarConteudo("cadastro-vaga", empresaAtualId, vagaId);
      });
    });

    criarGraficoSkills(skills);
  }

  // perfil do candidato

  if (tela === "candidato") {
    // editar perfil

    const botaoEditar = document.querySelector<HTMLButtonElement>(
      "#botao-editar-candidato",
    );

    if (botaoEditar) {
      botaoEditar.addEventListener("click", () => {
        if (candidatoAtualId === undefined) {
          return;
        }

        const bloqueou = bloquearCandidato(candidatoAtualId);

        if (!bloqueou) {
          alert("Este candidato está sendo editado em outra aba.");

          return;
        }

        atualizarConteudo("cadastro-candidato");
      });
    }

    // excluir conta

    const botaoExcluir = document.querySelector<HTMLButtonElement>(
      "#botao-excluir-candidato",
    );

    if (botaoExcluir) {
      botaoExcluir.addEventListener("click", () => {
        if (candidatoAtualId === undefined) {
          return;
        }

        if (candidatoEstaBloqueado(candidatoAtualId)) {
          alert(
            "Exclusão bloqueada: este candidato está sendo editado em outra aba.",
          );

          return;
        }

        const confirmar = confirm("Deseja realmente excluir sua conta?");

        if (!confirmar) {
          return;
        }

        const excluiu = excluirCandidato(candidatoAtualId);

        if (!excluiu) {
          alert("Não foi possível excluir a conta.");

          return;
        }

        candidatoAtualId = undefined;

        alert("Conta excluída com sucesso!");

        atualizarConteudo("candidato");
      });
    }

    // lista de vagas

    const listaVagas = document.querySelector<HTMLDivElement>("#lista-vagas");

    if (listaVagas) {
      listaVagas.innerHTML = renderizarCardsVagas(vagas);
    }
  }

  //cadastro empresas

  if (tela === "cadastro-empresa") {
    const formulario = document.querySelector<HTMLFormElement>(
      "#formulario-empresa",
    );

    const botaoCancelar = document.querySelector<HTMLButtonElement>(
      "#botao-cancelar-empresa",
    );

    if (botaoCancelar) {
      botaoCancelar.addEventListener("click", () => {
        liberarBloqueioEmpresa();

        atualizarConteudo("empresa", empresaAtualId);
      });
    }

    if (formulario) {
      formulario.addEventListener("submit", (evento) => {
        evento.preventDefault();

        const dados = new FormData(formulario);

        const dadosEmpresa = {
          nome: String(dados.get("nome")),
          cnpj: String(dados.get("cnpj")),
          descricao: String(dados.get("descricao")),
          estado: String(dados.get("estado")),
        };

        if (empresaId !== undefined) {
          editarEmpresa(empresaId, dadosEmpresa);

          liberarBloqueioEmpresa();

          alert("Perfil atualizado com sucesso!");

          atualizarConteudo("empresa", empresaId);

          return;
        }

        criarEmpresa(dadosEmpresa);

        formulario.reset();

        alert("Empresa cadastrada com sucesso!");
      });
    }
  }

  //cadastro e edição das vagas
  if (tela === "cadastro-vaga") {
    const formulario =
      document.querySelector<HTMLFormElement>("#formulario-vaga");

    //botao para cancelar edição/criação de vagas

    const botaoCancelar = document.querySelector<HTMLButtonElement>(
      "#botao-cancelar-vaga",
    );

    if (botaoCancelar) {
      botaoCancelar.addEventListener("click", () => {
        //bloquer caso esteja editando um vagaId válido

        if (vagaId !== undefined) {
          liberarBloqueioVaga();
        }

        //volta para o perfil da empresa

        atualizarConteudo("empresa", empresaAtualId);
      });
    }

    //submit do form

    if (formulario) {
      formulario.addEventListener("submit", (evento) => {
        evento.preventDefault();

        const dados = new FormData(formulario);

        const dadosVaga = {
          titulo: String(dados.get("titulo")),

          descricao: String(dados.get("descricao")),

          skills: String(dados.get("skills")),
        };

        //se existe vagaId edita

        if (vagaId !== undefined) {
          editarVaga(vagaId, empresaAtualId, dadosVaga);

          //libera bloquei após salvamento da vaga

          liberarBloqueioVaga();

          alert("Vaga atualizada com sucesso!");
        } else {
          //criar nova vaga sem vagaId

          criarVaga(dadosVaga, empresaAtualId);

          alert("Vaga cadastrada com sucesso!");
        }

        //vikta oara empresa após salvar

        atualizarConteudo("empresa", empresaAtualId);
      });
    }
  }
}

// INICIALIZAÇÃO DA APLICAÇÃO

if (app) {
  app.innerHTML = `
    ${renderizarSidebar()}

    <main class="conteudo"></main>
  `;

  atualizarConteudo(telaAtual);

  const itensMenu = document.querySelectorAll<HTMLElement>(".menu-item");

  itensMenu.forEach((item) => {
    item.addEventListener("click", () => {
      const tela = obterTelaDoMenu(item);

      if (!tela) {
        return;
      }

      telaAtual = tela;

      itensMenu.forEach((menuItem) => {
        menuItem.classList.remove("ativo");
      });

      item.classList.add("ativo");

      if (telaAtual === "cadastro-candidato") {
        candidatoAtualId = undefined;
      }

      atualizarConteudo(telaAtual);
    });
  });
}
