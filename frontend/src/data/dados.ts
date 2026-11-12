import type { Candidato } from "../models/Candidato";
import type { Empresa } from "../models/Empresa";
import type { Vaga } from "../models/Vaga";
import type { Recrutador } from "../models/Recrutador";

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

export const recrutadores: Recrutador[] = [
    {
        id: 1,
        empresaId: 1,
        nome: "Camila Ferreira",
        areaRecrutamento: "Engenharia de Software Backend",
        descricao: "Recrutadora técnica focada em vagas Java, Spring e bancos de dados relacionais.",
        email: "camila.ferreira@techsolutions.com",
        cpf: "66666666661",
        dataNascimento: "1992-04-15"
    },
    {
        id: 2,
        empresaId: 1,
        nome: "Rafael Martins",
        areaRecrutamento: "Arquitetura e Plataforma",
        descricao: "Especialista em atração de talentos para equipes de plataforma e Node.js.",
        email: "rafael.martins@techsolutions.com",
        cpf: "66666666662",
        dataNascimento: "1989-08-22"
    },
    {
        id: 3,
        empresaId: 2,
        nome: "Juliana Rocha",
        areaRecrutamento: "Desenvolvimento .NET e Cloud",
        descricao: "Responsável por processos seletivos de engenharia de software corporativa.",
        email: "juliana.rocha@digitalsystems.com",
        cpf: "66666666663",
        dataNascimento: "1995-01-10"
    },
    {
        id: 4,
        empresaId: 3,
        nome: "Lucas Mendes",
        areaRecrutamento: "Frontend e Produto Digital",
        descricao: "Recrutador focado em especialistas TypeScript, React e experiência do usuário.",
        email: "lucas.mendes@codefactory.com",
        cpf: "66666666664",
        dataNascimento: "1993-11-05"
    },
    {
        id: 5,
        empresaId: 3,
        nome: "Beatriz Almeida",
        areaRecrutamento: "Full Stack e Agilidade",
        descricao: "Atua na seleção de pessoas desenvolvedoras web e lideranças técnicas.",
        email: "beatriz.almeida@codefactory.com",
        cpf: "66666666665",
        dataNascimento: "1996-06-30"
    }
];
