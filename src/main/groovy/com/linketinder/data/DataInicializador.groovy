package com.linketinder.data

import com.linketinder.model.Candidato
import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes
import  com.linketinder.model.Skill
import com.linketinder.model.Vaga

class DataInicializador {
    List<Skill> skills = [
            new Skill(id:1L, nome:"Java"),
            new Skill(id: 2L, nome: "Groovy"),
            new Skill(id: 3L, nome: "Python"),
            new Skill(id: 4L, nome: "Spring Framework"),
            new Skill(id: 5L, nome: "Angular"),
            new Skill(id: 6L, nome: "JavaScript")

    ]

    Skill buscarPorNome(String nome){
            return skills.find {
                it.nome == nome
            }
    }

    List<Candidato> criarCandidatos() {
        return [
                new Candidato(
                        id: 1L,
                        cpf: "12345678900",
                        idade: 37,
                        estado: "RJ",
                        descricaoPessoal: "Dev FullStack",
                        informacoes: new Informacoes(
                                nome: "Nelson",
                                cep: "20000-000",
                                email: "nelson@email.com",
                                whatsapp: "21999999999",
                        ),
                        skills: [
                                buscarPorNome("Java"),
                                buscarPorNome("Groovy")
                        ]
                ),
                new Candidato(
                        id: 2L,
                        cpf: "121.121.222-12",
                        idade: 22,
                        estado: "SP",
                        descricaoPessoal: "Dev FrontEnd",
                        informacoes:  new Informacoes(
                                nome: "Joana",
                                cep: "21000-000",
                                email: "joaninhadev@dev.com",
                                whatsapp: "629999922233"
                        ),
                        skills: [
                                buscarPorNome("Angular"),
                                buscarPorNome("JavaScript")
                        ]
                ),
                new Candidato(
                        id: 3L,
                        cpf: "121.333.333-12",
                        idade: 19,
                        estado: "MG",
                        descricaoPessoal: "Dev Backend",
                        informacoes:  new Informacoes(
                                nome: "Armando",
                                cep: "11010-000",
                                email: "armando@dev.com",
                                whatsapp: "11988333399"
                        ),
                        skills: [
                                buscarPorNome("Java"),
                                buscarPorNome("Spring Framework")
                        ]
                ),
                new Candidato(
                        id: 4L,
                        cpf: "333.222.333-87",
                        idade: 30,
                        estado: "RJ",
                        descricaoPessoal: "Dev Backend",
                        informacoes:  new Informacoes(
                                nome: "Gustavo",
                                cep: "21000-000",
                                email: "gugu@email.com",
                                whatsapp: "21977334333"
                        ),
                        skills: [
                                buscarPorNome("Python"),
                                buscarPorNome("Angular")
                        ]
                ),
                new Candidato(
                        id: 5L,
                        cpf: "145.223.442-90",
                        idade: 55,
                        estado: "SP",
                        descricaoPessoal: "Dev FrontEnd Sr",
                        informacoes:  new Informacoes(
                                nome: "Roberto",
                                cep: "11292-883",
                                email: "robmaia@gmail.com",
                                whatsapp: "13988344456"
                        ),
                        skills: [
                                buscarPorNome("Angular"),
                                buscarPorNome("JavaScript"),
                                buscarPorNome("Python")
                        ]
                )
        ]
    }

    List<Empresa> criarEmpresas(){
        return [
                new Empresa(
                        id: 1L,
                        cnpj: "10.122.211/0001-21",
                        pais: "Brasil",
                        estado: "RJ",
                        descricaoEmpresarial: "Empresa especializada em soluções de tecnologia.",
                        informacoes: new Informacoes(
                                nome: "PastelSoft",
                                cep: "21000-000",
                                email: "contato@pastelsoft.com.br",
                                whatsapp: "2199993338888"
                        ),
                        vagas: [
                                new Vaga(
                                        id: 1L,
                                        titulo: "Desenvolvedor Java",
                                        descricaoVaga: "Desenvolvimento de aplicações utilizando Java e Spring Framework",
                                        skills: [
                                                buscarPorNome("Java"),
                                                buscarPorNome("Spring Framework")
                                        ]
                                )
                        ]
                ),
                new Empresa(
                        id: 2L,
                        cnpj: "30.344.433/0001-43",
                        pais: "Brasil",
                        estado: "RJ",
                        descricaoEmpresarial: "Empresa especializada em soluções de tecnologia.",
                        informacoes: new Informacoes(
                                nome: "Arroz-Gostoso",
                                cep: "21000-000",
                                email: "contato@Arroz-Gostoso.com.br",
                                whatsapp: "2199993338888"
                        ),
                        vagas: [
                                new Vaga(
                                        id: 2L,
                                        titulo: "Desenvolvedor Python",
                                        descricaoVaga: "Desenvolvimento de aplicações utilizando Python",
                                        skills: [
                                                buscarPorNome("Python"),
                                                buscarPorNome("Java")
                                        ]
                                )
                        ]
                ),
                new Empresa(
                        id: 3L,
                        cnpj: "30.344.433/0001-43",
                        pais: "Brasil",
                        estado: "RJ",
                        descricaoEmpresarial: "Empresa especializada em soluções de tecnologia.",
                        informacoes: new Informacoes(
                                nome: "Império do Boliche",
                                cep: "21000-000",
                                email: "contato@imperiodoboliche.com.br",
                                whatsapp: "2199993338888"
                        ),
                        vagas: [
                                new Vaga(
                                        id: 3L,
                                        titulo: "Desenvolvedor Frontend",
                                        descricaoVaga: "Desenvolvimento de aplicações web utilizando Angular e Groovy",
                                        skills: [
                                                buscarPorNome("Angular"),
                                                buscarPorNome("Groovy")
                                        ]
                                )
                        ]
                ),
                new Empresa(
                        id: 4L,
                        cnpj: "20.233.322/0001-32",
                        pais: "Brasil",
                        estado: "RJ",
                        descricaoEmpresarial: "Empresa especializada em soluções de tecnologia.",
                        informacoes: new Informacoes(
                                nome: "TechPão",
                                cep: "21000-000",
                                email: "contato@techpao.com.br",
                                whatsapp: "2199993338888"
                        ),
                        vagas: [
                                new Vaga(
                                        id: 4L,
                                        titulo: "Desenvolvedor Java",
                                        descricaoVaga: "Desenvolvimento de aplicações utilizando Java e Groovy",
                                        skills: [
                                                buscarPorNome("Java"),
                                                buscarPorNome("Groovy")
                                        ]
                                )
                        ]
                ),
                new Empresa(
                        id: 5L,
                        cnpj: "10.122.211/0001-21",
                        estado: "RJ",
                        pais: "Brasil",
                        descricaoEmpresarial: "Empresa especializada em soluções de tecnologia.",
                        informacoes: new Informacoes(
                                nome: "CodeBurger",
                                cep: "21000-000",
                                email: "contato@coderburguer.com.br",
                                whatsapp: "2199993338888"
                        ),
                        vagas: [
                                new Vaga(
                                        id: 5L,
                                        titulo: "Desenvolvedor Python",
                                        descricaoVaga: "Desenvolvimento de aplicações utilizando Python e Spring Framework",
                                        skills: [
                                                buscarPorNome("Python"),
                                                buscarPorNome("Spring Framework")
                                        ]
                                )
                        ]
                )
        ]
    }



}
