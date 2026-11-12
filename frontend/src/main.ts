import "./styles/style.css";

import {
  listarCandidatos,
  obterCandidatoPorId,
} from "./services/candidato/candidatoService";

import { listarVagas, obterVagasPorEmpresa } from "./services/vaga/vagaService";

import { renderizarCardsVagasEmpresa } from "./components/vagas/renderizarCardsVagasEmpresa";
import { renderizarCardsVagas } from "./components/vagas/renderizarCardsVagas";
import { renderizarCardsCandidatos } from "./components/candidatos/renderizarCardsCandidatos";
import { renderizarSidebar } from "./components/layout/renderizarSidebar";
import { renderizarTela } from "./components/layout/renderizarTela";

import { contarSkills, ordenarSkills } from "./services/empresa/skillService";

import { criarGraficoSkills } from "./components/empresas/renderizarGraficoSkills";

import { liberarBloqueioCandidato } from "./services/candidato/edicaoCandidatoService";

import { liberarBloqueioVaga } from "./services/vaga/edicaoVagaService";

import { liberarBloqueioEmpresa } from "./services/empresa/edicaoEmpresaService";

import {
  obterTelaInicial,
  obterTelaDoMenu,
  type Tela,
} from "./services/utils/navegacaoService";

import { obterEmpresaIdParaRenderizacao } from "./services/utils/contextoTelaService";

import {
  configurarFormularioCandidato,
  configurarExclusaoCandidato,
  configurarEdicaoCandidato,
} from "./controllers/candidatoController";

import {
  configurarEdicaoEmpresa,
  configurarExclusaoEmpresa,
  configurarFormularioEmpresa,
} from "./controllers/empresaController";
import {
  configurarCriacaoVaga,
  configurarEdicaoVaga,
  configurarExclusaoVaga,
  configurarFormularioVaga,
} from "./controllers/vagaController";
import { listarEmpresas } from "./services/empresa/empresaService";

import { obterRecrutadoresPorEmpresa } from "./services/recrutador/recrutadorService";
import { liberarBloqueioRecrutador } from "./services/recrutador/edicaoRecrutadorService";
import { renderizarCardsRecrutadoresEmpresa } from "./components/recrutadores/renderizarCardsRecrutadoresEmpresa";
import {
  configurarCriacaoRecrutador,
  configurarEdicaoRecrutador,
  configurarExclusaoRecrutador,
  configurarFormularioRecrutador,
} from "./controllers/recrutadorController";

const app = document.querySelector<HTMLDivElement>("#app");

let telaAtual: Tela = obterTelaInicial();

let empresaAtualId = 3;

let candidatoAtualId: number | undefined = 3;

function atualizarConteudo(
  tela: Tela,
  empresaId?: number,
  vagaId?: number,
  recrutadorId?: number,
): void {
  if (empresaId !== undefined) {
    empresaAtualId = empresaId;
  }

  const conteudo = document.querySelector<HTMLElement>(".conteudo");

  if (!conteudo) {
    return;
  }

  const empresaIdParaRenderizacao = obterEmpresaIdParaRenderizacao(
    tela,
    empresaAtualId,
    empresaId,
  );

  conteudo.innerHTML = renderizarTela(
    tela,
    empresaIdParaRenderizacao,
    candidatoAtualId,
    vagaId,
    recrutadorId,
  );

  // Cadastro e edição de candidatos

  if (tela === "cadastro-candidato") {
    configurarFormularioCandidato({
      candidatoId: candidatoAtualId,
      navegarParaPerfil: () => atualizarConteudo("candidato"),
      aoCadastrar: (candidatoId: number) => {
        candidatoAtualId = candidatoId;
      },
    });
  }

  // Perfil da empresa

  if (tela === "empresa") {
    configurarCriacaoVaga({
      abrirFormulario: () => atualizarConteudo("cadastro-vaga", empresaAtualId),
    });

    configurarEdicaoEmpresa({
      empresaId: empresaAtualId,
      abrirFormulario: () =>
        atualizarConteudo("cadastro-empresa", empresaAtualId),
    });

    // Excluir empresa

    configurarExclusaoEmpresa({
      empresaId: empresaAtualId,
      aoExcluir: () => {
        const empresasRestantes = listarEmpresas();

        empresaAtualId = empresasRestantes[0]?.id;

        atualizarConteudo("empresa");
      },
    });

    // Lista de candidatos

    const listaCandidatos =
      document.querySelector<HTMLDivElement>("#lista-candidatos");

    if (listaCandidatos) {
      listaCandidatos.innerHTML = renderizarCardsCandidatos(listarCandidatos());
    }

    // Vagas da empresa

    const listaVagasEmpresa = document.querySelector<HTMLDivElement>(
      "#lista-vagas-empresa",
    );

    if (listaVagasEmpresa) {
      const vagasDaEmpresa = obterVagasPorEmpresa(empresaAtualId);

      listaVagasEmpresa.innerHTML = renderizarCardsVagasEmpresa(vagasDaEmpresa);
    }

    // Excluir vaga

    configurarExclusaoVaga({
      empresaId: empresaAtualId,
      aoExcluir: () => atualizarConteudo("empresa", empresaAtualId),
    });

    // Editar vaga
    configurarEdicaoVaga({
      empresaId: empresaAtualId,
      abrirFormulario: (vagaId: number) =>
        atualizarConteudo("cadastro-vaga", empresaAtualId, vagaId),
    });

    // Recrutadores da empresa

    configurarCriacaoRecrutador({
      abrirFormulario: () =>
        atualizarConteudo("cadastro-recrutador", empresaAtualId),
    });

    const listaRecrutadoresEmpresa = document.querySelector<HTMLDivElement>(
      "#lista-recrutadores-empresa",
    );

    if (listaRecrutadoresEmpresa) {
      listaRecrutadoresEmpresa.innerHTML = renderizarCardsRecrutadoresEmpresa(
        obterRecrutadoresPorEmpresa(empresaAtualId),
      );
    }

    configurarEdicaoRecrutador({
      empresaId: empresaAtualId,
      abrirFormulario: (recrutadorId: number) =>
        atualizarConteudo(
          "cadastro-recrutador",
          empresaAtualId,
          undefined,
          recrutadorId,
        ),
    });

    configurarExclusaoRecrutador({
      empresaId: empresaAtualId,
      aoExcluir: () => atualizarConteudo("empresa", empresaAtualId),
    });

    // Gráfico de skills

    const skillsAtualizadas = ordenarSkills(contarSkills(listarCandidatos()));

    criarGraficoSkills(skillsAtualizadas);
  }

  // Perfil do candidato

  if (tela === "candidato") {
    // Editar perfil

    configurarEdicaoCandidato({
      candidatoId: candidatoAtualId,
      abrirFormulario: () => atualizarConteudo("cadastro-candidato"),
    });

    // Excluir candidato — responsabilidade do controlador

    configurarExclusaoCandidato({
      candidatoId: candidatoAtualId,
      aoExcluir: () => {
        candidatoAtualId = undefined;
        atualizarConteudo("candidato");
      },
    });

    // Lista de vagas

    const listaVagas = document.querySelector<HTMLDivElement>("#lista-vagas");

    if (listaVagas && candidatoAtualId !== undefined) {
      const candidato = obterCandidatoPorId(candidatoAtualId);

      if (candidato) {
        listaVagas.innerHTML = renderizarCardsVagas(listarVagas(), candidato);
      }
    }
  }

  // Cadastro e edição de empresas

  if (tela === "cadastro-empresa") {
    configurarFormularioEmpresa({
      empresaId,
      navegarParaPerfil: () => atualizarConteudo("empresa", empresaAtualId),
      aoCadastrar: (novoEmpresaId: number) => {
        empresaAtualId = novoEmpresaId;
      },
    });
  }

  // Cadastro e edição de vagas

  if (tela === "cadastro-vaga") {
    configurarFormularioVaga({
      empresaId: empresaAtualId,
      vagaId,
      navegarParaEmpresa: () => atualizarConteudo("empresa", empresaAtualId),
    });
  }

  // Cadastro e edição de recrutadores

  if (tela === "cadastro-recrutador") {
    configurarFormularioRecrutador({
      empresaId: empresaAtualId,
      recrutadorId,
      navegarParaEmpresa: () => atualizarConteudo("empresa", empresaAtualId),
    });
  }
}

// Inicialização da aplicação

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

      liberarBloqueioCandidato();
      liberarBloqueioVaga();
      liberarBloqueioEmpresa();
      liberarBloqueioRecrutador();

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
