export interface Recrutador {
    id: number;
    empresaId: number;
    nome: string;
    areaRecrutamento: string;
    descricao: string;
    email: string;
    cpf: string;
    dataNascimento: string;
}

export type DadosFormularioRecrutador = {
    nome: string;
    areaRecrutamento: string;
    descricao: string;
    email: string;
    cpf: string;
    dataNascimento: string;
    senha?: string;
};
