import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderizarTela } from "./renderizarTela";

import { obterEmpresaPorId } from "../../services/empresa/empresaService";
import { obterCandidatoPorId } from "../../services/candidato/candidatoService";
import { obterVagaPorId } from "../../services/vaga/vagaService";

import { renderizarPerfilEmpresa } from "../empresas/renderizarPerfilEmpresa";
import { renderizarPerfilCandidato } from "../candidatos/renderizarPerfilCandidato";
import { renderizarFormularioCandidato } from "../candidatos/renderizarFormularioCandidato";
import { renderizarFormularioEmpresa } from "../empresas/renderizarFormularioEmpresa";
import { renderizarFormularioVaga } from "../vagas/renderizarFormularioVaga";

import type { Empresa } from "../../models/Empresa";
import type { Candidato } from "../../models/Candidato";
import type { Vaga } from "../../models/Vaga";

vi.mock("../../services/empresa/empresaService", () => ({
  obterEmpresaPorId: vi.fn(),
}));

vi.mock("../../services/candidato/candidatoService", () => ({
  obterCandidatoPorId: vi.fn(),
}));

vi.mock("../empresas/renderizarPerfilEmpresa", () => ({
  renderizarPerfilEmpresa: vi.fn(),
}));

vi.mock("../candidatos/renderizarPerfilCandidato", () => ({
  renderizarPerfilCandidato: vi.fn(),
}));

vi.mock("../candidatos/renderizarFormularioCandidato", () => ({
  renderizarFormularioCandidato: vi.fn(),
}));

vi.mock("../empresas/renderizarFormularioEmpresa", () => ({
  renderizarFormularioEmpresa: vi.fn(),
}));
vi.mock("../../services/vaga/vagaService", () => ({
  obterVagaPorId: vi.fn(),
}));

vi.mock("../vagas/renderizarFormularioVaga", () => ({
  renderizarFormularioVaga: vi.fn(),
}));

const obterEmpresaPorIdMock = vi.mocked(obterEmpresaPorId);
const obterCandidatoPorIdMock = vi.mocked(obterCandidatoPorId);
const obterVagaPorIdMock = vi.mocked(obterVagaPorId);

const renderizarPerfilEmpresaMock = vi.mocked(renderizarPerfilEmpresa);
const renderizarPerfilCandidatoMock = vi.mocked(renderizarPerfilCandidato);

const renderizarFormularioCandidatoMock = vi.mocked(
  renderizarFormularioCandidato,
);

const renderizarFormularioEmpresaMock = vi.mocked(renderizarFormularioEmpresa);

const renderizarFormularioVagaMock = vi.mocked(renderizarFormularioVaga);

function criarEmpresaMock(): Empresa {
  return {
    id: 1,
    nome: "Empresa Teste",
    cnpj: "12345678000199",
    descricao: "Empresa de tecnologia",
    estado: "RJ",
  };
}

function criarCandidatoMock(): Candidato {
  return {
    id: 1,
    nome: "Candidato Teste",
    cpf: "12345678900",
    idade: 25,
    formacao: "ADS",
    descricao: "Desenvolvedor",
    skills: ["Java"],
    estado: "RJ",
  };
}

function criarVagaMock(): Vaga {
  return {
    id: 1,
    empresaId: 2,
    titulo: "Desenvolvedor Full Stack",
    descricao: "Desenvolvimento de aplicações web",
    skills: ["TypeScript", "React"],
  };
}

describe("renderizarTela", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("deve informar quando nenhuma empresa estiver selecionada", () => {
    expect(renderizarTela("empresa")).toBe(
      "<p>Nenhuma empresa selecionada.</p>",
    );
  });

  it("deve informar quando a empresa não existir", () => {
    obterEmpresaPorIdMock.mockReturnValue(undefined);

    expect(renderizarTela("empresa", 99)).toBe(
      "<p>Empresa não encontrada.</p>",
    );
  });

  it("deve renderizar o perfil da empresa existente", () => {
    const empresa = criarEmpresaMock();

    obterEmpresaPorIdMock.mockReturnValue(empresa);
    renderizarPerfilEmpresaMock.mockReturnValue("<p>Perfil empresa</p>");

    expect(renderizarTela("empresa", 1)).toBe("<p>Perfil empresa</p>");

    expect(obterEmpresaPorIdMock).toHaveBeenCalledWith(1);
    expect(renderizarPerfilEmpresaMock).toHaveBeenCalledWith(empresa);
  });

  it("deve informar quando nenhum candidato estiver selecionado", () => {
    expect(renderizarTela("candidato")).toBe(
      "<p>Nenhum candidato selecionado.</p>",
    );
  });

  it("deve informar quando o candidato não existir", () => {
    obterCandidatoPorIdMock.mockReturnValue(undefined);

    expect(renderizarTela("candidato", undefined, 99)).toBe(
      "<p>Candidato não encontrado.</p>",
    );
  });

  it("deve renderizar o perfil do candidato existente", () => {
    const candidato = criarCandidatoMock();

    obterCandidatoPorIdMock.mockReturnValue(candidato);
    renderizarPerfilCandidatoMock.mockReturnValue("<p>Perfil candidato</p>");

    expect(renderizarTela("candidato", undefined, 1)).toBe(
      "<p>Perfil candidato</p>",
    );

    expect(obterCandidatoPorIdMock).toHaveBeenCalledWith(1);
    expect(renderizarPerfilCandidatoMock).toHaveBeenCalledWith(candidato);
  });

  it("deve renderizar formulário de cadastro de candidato", () => {
    renderizarFormularioCandidatoMock.mockReturnValue("<form>Candidato</form>");

    expect(renderizarTela("cadastro-candidato")).toBe("<form>Candidato</form>");

    expect(renderizarFormularioCandidatoMock).toHaveBeenCalledWith();
  });

  it("deve renderizar formulário de cadastro de empresa", () => {
    renderizarFormularioEmpresaMock.mockReturnValue("<form>Empresa</form>");

    expect(renderizarTela("cadastro-empresa")).toBe("<form>Empresa</form>");

    expect(renderizarFormularioEmpresaMock).toHaveBeenCalledWith();
  });

  it("deve renderizar formulário de edição de candidato", () => {
    const candidato = criarCandidatoMock();

    obterCandidatoPorIdMock.mockReturnValue(candidato);
    renderizarFormularioCandidatoMock.mockReturnValue(
      "<form>Editar candidato</form>",
    );

    expect(renderizarTela("cadastro-candidato", undefined, 1)).toBe(
      "<form>Editar candidato</form>",
    );

    expect(renderizarFormularioCandidatoMock).toHaveBeenCalledWith(candidato);
  });

  it("deve informar quando o candidato para edição não existir", () => {
    obterCandidatoPorIdMock.mockReturnValue(undefined);

    expect(renderizarTela("cadastro-candidato", undefined, 99)).toBe(
      "<p>Candidato não encontrado.</p>",
    );

    expect(renderizarFormularioCandidatoMock).not.toHaveBeenCalled();
  });

  it("deve renderizar formulário de edição de empresa", () => {
    const empresa = criarEmpresaMock();

    obterEmpresaPorIdMock.mockReturnValue(empresa);
    renderizarFormularioEmpresaMock.mockReturnValue(
      "<form>Editar empresa</form>",
    );

    expect(renderizarTela("cadastro-empresa", 1)).toBe(
      "<form>Editar empresa</form>",
    );

    expect(renderizarFormularioEmpresaMock).toHaveBeenCalledWith(empresa);
  });

  it("deve informar quando a empresa para edição não existir", () => {
    obterEmpresaPorIdMock.mockReturnValue(undefined);

    expect(renderizarTela("cadastro-empresa", 99)).toBe(
      "<p>Empresa não encontrada.</p>",
    );

    expect(renderizarFormularioEmpresaMock).not.toHaveBeenCalled();
  });

  it("deve renderizar formulário de cadastro de vaga", () => {
    renderizarFormularioVagaMock.mockReturnValue("<form>Nova vaga</form>");

    expect(renderizarTela("cadastro-vaga", 2)).toBe("<form>Nova vaga</form>");

    expect(renderizarFormularioVagaMock).toHaveBeenCalledWith();
  });

  it("deve renderizar formulário de edição de vaga", () => {
    const vaga = criarVagaMock();

    obterVagaPorIdMock.mockReturnValue(vaga);
    renderizarFormularioVagaMock.mockReturnValue("<form>Editar vaga</form>");

    expect(renderizarTela("cadastro-vaga", 2, undefined, 1)).toBe(
      "<form>Editar vaga</form>",
    );

    expect(obterVagaPorIdMock).toHaveBeenCalledWith(1, 2);
    expect(renderizarFormularioVagaMock).toHaveBeenCalledWith(vaga);
  });

  it("deve informar quando a vaga para edição não existir", () => {
    obterVagaPorIdMock.mockReturnValue(undefined);

    expect(renderizarTela("cadastro-vaga", 2, undefined, 99)).toBe(
      "<p>Vaga não encontrada.</p>",
    );

    expect(renderizarFormularioVagaMock).not.toHaveBeenCalled();
  });

  it("deve renderizar formulário vazio quando faltar o ID da empresa na edição da vaga", () => {
    renderizarFormularioVagaMock.mockReturnValue("<form>Nova vaga</form>");

    expect(renderizarTela("cadastro-vaga", undefined, undefined, 1)).toBe(
      "<form>Nova vaga</form>",
    );

    expect(obterVagaPorIdMock).not.toHaveBeenCalled();
    expect(renderizarFormularioVagaMock).toHaveBeenCalledWith();
  });
});
