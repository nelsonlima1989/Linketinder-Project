package com.linketinder.dao

import com.linketinder.model.Empresa
import com.linketinder.model.Vaga

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VagaDAO {

    // inserir
    void inserir(Vaga vaga) {

        String sql = """
        INSERT INTO vagas (
            empresa_id,
            nome,
            estado,
            cidade,
            descricao
        )
        VALUES (?, ?, ?, ?, ?)
        RETURNING id
    """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, vaga.empresa.id)
            statement.setString(2, vaga.titulo)
            statement.setString(3, vaga.descricaoVaga)
            statement.setString(4, vaga.estado)
            statement.setString(5, vaga.cidade)

            resultado = statement.executeQuery()

            if (resultado.next()) {
                vaga.id = resultado.getLong("id")
            }

        } catch (SQLException e) {
            println "Erro ao inserir vaga: ${e.message}"

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
    List<Vaga> listarTodos() {

        String sql = """
        SELECT
            id,
            empresa_id,
            nome,
            descricao,
            estado,
            cidade
        FROM vagas
        ORDER BY id
    """

        List<Vaga> vagas = []

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)
            resultado = statement.executeQuery()

            EmpresaDAO empresaDAO = new EmpresaDAO()

            while (resultado.next()) {

                Long empresaId = resultado.getLong("empresa_id")

                Empresa empresa = empresaDAO.buscarPorId(empresaId)

                Vaga vaga = new Vaga(
                        id: resultado.getLong("id"),
                        titulo: resultado.getString("nome"),
                        descricaoVaga: resultado.getString("descricao"),
                        estado: resultado.getString("estado"),
                        cidade: resultado.getString("cidade"),
                        empresa: empresa
                )

                vagas.add(vaga)
            }

        } catch (SQLException e) {
            println "Erro ao listar vagas: ${e.message}"

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

        return vagas
    }



    // buscar por ID
    Vaga buscarPorId(Long id) {

        String sql = """
        SELECT
            id,
            empresa_id,
            nome,
            descricao,
            estado,
            cidade
        FROM vagas
        WHERE id = ?
    """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        Vaga vaga = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, id)

            resultado = statement.executeQuery()

            if (resultado.next()) {

                Long empresaId = resultado.getLong("empresa_id")

                EmpresaDAO empresaDAO = new EmpresaDAO()
                Empresa empresa = empresaDAO.buscarPorId(empresaId)

                vaga = new Vaga(
                        id: resultado.getLong("id"),
                        titulo: resultado.getString("nome"),
                        descricaoVaga: resultado.getString("descricao"),
                        estado: resultado.getString("estado"),
                        cidade: resultado.getString("cidade"),
                        empresa: empresa
                )
            }

        } catch (SQLException e) {
            println "Erro ao buscar vaga por ID: ${e.message}"

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

        return vaga
    }

    void atualizar(Vaga vaga) {

        String sql = """
        UPDATE vagas
        SET
            nome = ?,
            descricao = ?,
            estado = ?,
            cidade = ?
        WHERE id = ?
    """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setString(1, vaga.titulo)
            statement.setString(2, vaga.descricaoVaga)
            statement.setString(3, vaga.estado)
            statement.setString(4, vaga.cidade)
            statement.setLong(5, vaga.id)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Vaga atualizada com sucesso."
            } else {
                println "Nenhuma vaga encontrada com o ID ${vaga.id}."
            }

        } catch (SQLException e) {
            println "Erro ao atualizar vaga: ${e.message}"

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
        DELETE FROM vagas
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
                println "Vaga excluída com sucesso."
            } else {
                println "Nenhuma vaga encontrada com o ID ${id}."
            }

        } catch (SQLException e) {
            println "Erro ao excluir vaga: ${e.message}"

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
