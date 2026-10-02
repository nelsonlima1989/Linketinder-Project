package com.linketinder.dao

import com.linketinder.model.Candidato
import com.linketinder.model.Informacoes

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CandidatoDAO {

    // inserir
    void inserir(Candidato candidato) {

        String sql = """
            INSERT INTO candidatos (
                nome,
                sobrenome,
                data_nascimento,
                email,
                cpf,
                pais,
                estado,
                cep,
                descricao_pessoal,
                senha
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setString(1, candidato.informacoes.nome)
            statement.setString(2, candidato.informacoes.sobrenome)

            statement.setDate(
                    3,
                    java.sql.Date.valueOf(candidato.dataNascimento)
            )

            statement.setString(4, candidato.informacoes.email)
            statement.setString(5, candidato.cpf)
            statement.setString(6, candidato.informacoes.pais)
            statement.setString(7, candidato.estado)
            statement.setString(8, candidato.informacoes.cep)
            statement.setString(9, candidato.descricaoPessoal)
            statement.setString(10, candidato.senha)

            resultado = statement.executeQuery()

            if (resultado.next()) {
                candidato.id = resultado.getLong("id")
            }

        } catch (SQLException e) {
            println "Erro ao inserir candidato: ${e.message}"

        } finally {

            if (resultado != null) {
                try {
                    resultado.close()
                } catch (SQLException e) {
                    println "Erro ao fechar ResultSet: ${e.message}"
                }
            }

            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException e) {
                    println "Erro ao fechar PreparedStatement: ${e.message}"
                }
            }

            if (conexao != null) {
                try {
                    conexao.close()
                } catch (SQLException e) {
                    println "Erro ao fechar conexão: ${e.message}"
                }
            }
        }
    }


    // listar todos
    List<Candidato> listarTodos() {

        String sql = """
            SELECT
                id,
                nome,
                sobrenome,
                data_nascimento,
                email,
                cpf,
                pais,
                estado,
                cep,
                descricao_pessoal,
                senha
            FROM candidatos
            ORDER BY id
        """

        List<Candidato> candidatos = []

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)
            resultado = statement.executeQuery()

            while (resultado.next()) {

                Informacoes informacoes = new Informacoes(
                        nome: resultado.getString("nome"),
                        sobrenome: resultado.getString("sobrenome"),
                        email: resultado.getString("email"),
                        pais: resultado.getString("pais"),
                        cep: resultado.getString("cep")
                )

                Candidato candidato = new Candidato(
                        id: resultado.getLong("id"),
                        estado: resultado.getString("estado"),
                        informacoes: informacoes,
                        cpf: resultado.getString("cpf"),
                        dataNascimento: resultado
                                .getDate("data_nascimento")
                                .toLocalDate(),
                        descricaoPessoal: resultado.getString("descricao_pessoal"),
                        senha: resultado.getString("senha")
                )

                candidatos.add(candidato)
            }

        } catch (SQLException e) {
            println "Erro ao listar candidatos: ${e.message}"

        } finally {

            if (resultado != null) {
                try {
                    resultado.close()
                } catch (SQLException e) {
                    println "Erro ao fechar ResultSet: ${e.message}"
                }
            }

            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException e) {
                    println "Erro ao fechar PreparedStatement: ${e.message}"
                }
            }

            if (conexao != null) {
                try {
                    conexao.close()
                } catch (SQLException e) {
                    println "Erro ao fechar conexão: ${e.message}"
                }
            }
        }

        return candidatos
    }


    // buscar por ID
    Candidato buscarPorId(Long id) {

        String sql = """
            SELECT
                id,
                nome,
                sobrenome,
                data_nascimento,
                email,
                cpf,
                pais,
                estado,
                cep,
                descricao_pessoal,
                senha
            FROM candidatos
            WHERE id = ?
        """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        Candidato candidato = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, id)

            resultado = statement.executeQuery()

            if (resultado.next()) {

                Informacoes informacoes = new Informacoes(
                        nome: resultado.getString("nome"),
                        sobrenome: resultado.getString("sobrenome"),
                        email: resultado.getString("email"),
                        pais: resultado.getString("pais"),
                        cep: resultado.getString("cep")
                )

                candidato = new Candidato(
                        id: resultado.getLong("id"),
                        estado: resultado.getString("estado"),
                        informacoes: informacoes,
                        cpf: resultado.getString("cpf"),
                        dataNascimento: resultado
                                .getDate("data_nascimento")
                                .toLocalDate(),
                        descricaoPessoal: resultado.getString("descricao_pessoal"),
                        senha: resultado.getString("senha")
                )
            }

        } catch (SQLException e) {
            println "Erro ao buscar candidato por ID: ${e.message}"

        } finally {

            if (resultado != null) {
                try {
                    resultado.close()
                } catch (SQLException e) {
                    println "Erro ao fechar ResultSet: ${e.message}"
                }
            }

            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException e) {
                    println "Erro ao fechar PreparedStatement: ${e.message}"
                }
            }

            if (conexao != null) {
                try {
                    conexao.close()
                } catch (SQLException e) {
                    println "Erro ao fechar conexão: ${e.message}"
                }
            }
        }

        return candidato
    }


    // atualizar
    void atualizar(Candidato candidato) {

        String sql = """
            UPDATE candidatos
            SET
                nome = ?,
                sobrenome = ?,
                data_nascimento = ?,
                email = ?,
                cpf = ?,
                pais = ?,
                estado = ?,
                cep = ?,
                descricao_pessoal = ?,
                senha = ?
            WHERE id = ?
        """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setString(1, candidato.informacoes.nome)
            statement.setString(2, candidato.informacoes.sobrenome)

            statement.setDate(
                    3,
                    java.sql.Date.valueOf(candidato.dataNascimento)
            )

            statement.setString(4, candidato.informacoes.email)
            statement.setString(5, candidato.cpf)
            statement.setString(6, candidato.informacoes.pais)
            statement.setString(7, candidato.estado)
            statement.setString(8, candidato.informacoes.cep)
            statement.setString(9, candidato.descricaoPessoal)
            statement.setString(10, candidato.senha)
            statement.setLong(11, candidato.id)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Candidato atualizado com sucesso."
            } else {
                println "Nenhum candidato encontrado com o ID ${candidato.id}."
            }

        } catch (SQLException e) {
            println "Erro ao atualizar candidato: ${e.message}"

        } finally {

            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException e) {
                    println "Erro ao fechar PreparedStatement: ${e.message}"
                }
            }

            if (conexao != null) {
                try {
                    conexao.close()
                } catch (SQLException e) {
                    println "Erro ao fechar conexão: ${e.message}"
                }
            }
        }
    }


    // excluir
    void excluir(Long id) {

        String sql = """
            DELETE FROM candidatos
            WHERE id = ?
        """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, id)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Candidato excluído com sucesso."
            } else {
                println "Nenhum candidato encontrado com o ID ${id}."
            }

        } catch (SQLException e) {
            println "Erro ao excluir candidato: ${e.message}"

        } finally {

            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException e) {
                    println "Erro ao fechar PreparedStatement: ${e.message}"
                }
            }

            if (conexao != null) {
                try {
                    conexao.close()
                } catch (SQLException e) {
                    println "Erro ao fechar conexão: ${e.message}"
                }
            }
        }
    }
}