package com.linketinder.dao

import com.linketinder.model.Skill

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CompetenciaDAO {

    // inserir
    void inserir(Skill skill) {

        String sql = """
            INSERT INTO competencias (nome)
            VALUES (?)
            RETURNING id
        """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setString(1, skill.nome)

            resultado = statement.executeQuery()

            if (resultado.next()) {
                skill.id = resultado.getLong("id")
            }

        } catch (SQLException e) {
            println "Erro ao inserir competência: ${e.message}"

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
    List<Skill> listarTodos() {

        String sql = """
            SELECT id, nome
            FROM competencias
            ORDER BY id
        """

        List<Skill> skills = []

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)
            resultado = statement.executeQuery()

            while (resultado.next()) {

                Skill skill = new Skill(
                        id: resultado.getLong("id"),
                        nome: resultado.getString("nome")
                )

                skills.add(skill)
            }

        } catch (SQLException e) {
            println "Erro ao listar competências: ${e.message}"

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

        return skills
    }

    // buscar por ID
    Skill buscarPorId(Long id) {

        String sql = """
            SELECT id, nome
            FROM competencias
            WHERE id = ?
        """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        Skill skill = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, id)

            resultado = statement.executeQuery()

            if (resultado.next()) {

                skill = new Skill(
                        id: resultado.getLong("id"),
                        nome: resultado.getString("nome")
                )
            }

        } catch (SQLException e) {
            println "Erro ao buscar competência por ID: ${e.message}"

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

        return skill
    }


    // atualizar
    void atualizar(Skill skill) {

        String sql = """
        UPDATE competencias
        SET nome = ?
        WHERE id = ?
    """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setString(1, skill.nome)
            statement.setLong(2, skill.id)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Competência atualizada com sucesso."
            } else {
                println "Nenhuma competência encontrada com o ID ${skill.id}."
            }

        } catch (SQLException e) {
            println "Erro ao atualizar competência: ${e.message}"

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
        DELETE FROM competencias
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
                println "Competência excluída com sucesso."
            } else {
                println "Nenhuma competência encontrada com o ID ${id}."
            }

        } catch (SQLException e) {
            println "Erro ao excluir competência: ${e.message}"

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