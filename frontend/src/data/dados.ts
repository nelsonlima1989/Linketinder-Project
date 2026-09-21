import type { Candidato } from "../models/Candidato";
import type { Empresa } from "../models/Empresa";
import type { Vaga } from "../models/Vaga";

export const candidatos: Candidato[] = [
    {
        id: 1,
        nome: "Ana Silva",
        cpf: "11111111111",
        idade: 25,
        formacao: "Análise e Desenvolvimento de Sistemas",
        descricao: "Desenvolvedora Full Stack",
        skills: ["Java", "Spring", "SQL", "Git"],
        estado: "RJ"
    },
    {
        id: 2,
        nome: "Carlos Souza",
        cpf: "22222222222",
        idade: 28,
        formacao: "Engenharia de Software",
        descricao: "Desenvolvedor Backend",
        skills: ["C#", ".NET", "SQL Server", "Docker"],
        estado: "SP"
    },
    {
        id: 3,
        nome: "Mariana Costa",
        cpf: "33333333333",
        idade: 24,
        formacao: "Ciência da Computação",
        descricao: "Desenvolvedora Frontend",
        skills: ["TypeScript", "React", "HTML", "CSS"],
        estado: "MG"
    },
    {
        id: 4,
        nome: "João Oliveira",
        cpf: "44444444444",
        idade: 30,
        formacao: "Sistemas de Informação",
        descricao: "Desenvolvedor Full Stack",
        skills: ["JavaScript", "Node.js", "React", "PostgreSQL"],
        estado: "RJ"
    },
    {
        id: 5,
        nome: "Fernanda Lima",
        cpf: "55555555555",
        idade: 27,
        formacao: "Análise e Desenvolvimento de Sistemas",
        descricao: "Desenvolvedora Backend",
        skills: ["Python", "Django", "PostgreSQL", "Docker"],
        estado: "PR"
    }
];

export const empresas: Empresa[] = [
    {
        id: 1,
        nome: "Tech Solutions",
        cnpj: "11111111000111",
        descricao: "Empresa de desenvolvimento de software",
        estado: "RJ"
    },
    {
        id: 2,
        nome: "Digital Systems",
        cnpj: "22222222000122",
        descricao: "Soluções digitais e tecnologia",
        estado: "SP"
    },
    {
        id: 3,
        nome: "Code Factory",
        cnpj: "33333333000133",
        descricao: "Desenvolvimento de aplicações web",
        estado: "MG"
    }
];

export const vagas: Vaga[] = [
    {
        id: 1,
        titulo: "Desenvolvedor Java",
        descricao: "Desenvolvimento de APIs e aplicações backend.",
        skills: ["Java", "Spring", "SQL"],
        empresaId: 1
    },
    {
        id: 2,
        titulo: "Desenvolvedor .NET",
        descricao: "Desenvolvimento de APIs utilizando C# e .NET.",
        skills: ["C#", ".NET", "SQL Server"],
        empresaId: 2
    },
    {
        id: 3,
        titulo: "Desenvolvedor Frontend",
        descricao: "Desenvolvimento de interfaces web modernas.",
        skills: ["TypeScript", "React", "HTML", "CSS"],
        empresaId: 3
    },
    {
        id: 4,
        titulo: "Desenvolvedor Node.js",
        descricao: "Desenvolvimento de APIs utilizando Node.js.",
        skills: ["JavaScript", "Node.js", "PostgreSQL"],
        empresaId: 1
    }
];