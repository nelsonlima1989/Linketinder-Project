import type { Empresa } from "../../models/Empresa";
import { empresas } from "../../data/dados";
import { salvarDados, carregarDados } from "../ui/storage";

const CHAVE = "empresas";

type DadosFormularioEmpresa = {
    nome: string;
    cnpj: string;
    descricao: string;
    estado: string;
};

export function listarEmpresas(): Empresa[] {

    const dadosSalvos =
        carregarDados<Empresa[]>(CHAVE);

    if (dadosSalvos) {
        return dadosSalvos;
    }

    salvarDados(CHAVE, empresas);

    return empresas;
}

export function obterProximoId(): number {

    const empresasAtuais = listarEmpresas();

    if (empresasAtuais.length === 0) {
        return 1;
    }

    const maiorId = Math.max(
        ...empresasAtuais.map(empresa => empresa.id)
    );

    return maiorId + 1;
}

export function adicionarEmpresa(
    empresa: Omit<Empresa, "id">
): void {

    const empresasAtuais = listarEmpresas();

    const novoId = obterProximoId();

    const novaEmpresa: Empresa = {
        id: novoId,
        ...empresa
    };

    empresasAtuais.push(novaEmpresa);

    salvarDados(CHAVE, empresasAtuais);
}

export function criarEmpresa(
    dados: DadosFormularioEmpresa
): void {

    const novaEmpresa: Omit<Empresa, "id"> = {
        nome: dados.nome,
        cnpj: dados.cnpj,
        descricao: dados.descricao,
        estado: dados.estado
    };

    adicionarEmpresa(novaEmpresa);
}

export function obterEmpresaPorId(
    id: number
): Empresa | undefined {

    const empresasAtuais = listarEmpresas();

    return empresasAtuais.find(
        empresa => empresa.id === id
    );
}

export function editarEmpresa(
    id: number,
    dados: {
        nome: string;
        cnpj: string;
        descricao: string;
        estado: string;
    }
): void {

    const empresasAtuais = listarEmpresas();

    const indiceEmpresa =
        empresasAtuais.findIndex(
            empresa => empresa.id === id
        );

    if (indiceEmpresa === -1) {
        return;
    }

    empresasAtuais[indiceEmpresa] = {
        ...empresasAtuais[indiceEmpresa],
        nome: dados.nome,
        cnpj: dados.cnpj,
        descricao: dados.descricao,
        estado: dados.estado
    };

    salvarDados(
        CHAVE,
        empresasAtuais
    );
}

export function excluirEmpresa(id: number): boolean {
    const empresasAtuais = listarEmpresas();

    const empresa = empresasAtuais.find(empresa => empresa.id === id);

    if (!empresa) return false;

    const novasEmpresas = empresasAtuais.filter(
        empresa => empresa.id !== id
    );

    salvarDados(CHAVE, novasEmpresas);

    return true;
}