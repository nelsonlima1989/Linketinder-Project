package com.linketinder.dao

import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class EmpresaDAO {

    // inserir
    void inserir(Empresa empresa) {

        String sql = """
            INSERT INTO empresas (
                nome,
                cnpj,
                email,
                descricao,
                pais,
                estado,
                cep,
                senha
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setString(1, empresa.informacoes.nome)
            statement.setString(2, empresa.cnpj)
            statement.setString(3, empresa.informacoes.email)
            statement.setString(4, empresa.descricaoEmpresarial)
            statement.setString(5, empresa.pais)
            statement.setString(6, empresa.estado)
            statement.setString(7, empresa.informacoes.cep)
            statement.setString(8, empresa.senha)

            resultado = statement.executeQuery()

            if (resultado.next()) {
                empresa.id = resultado.getLong("id")
            }

        } catch (SQLException e) {
            println "Erro ao inserir empresa: ${e.message}"

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
    List<Empresa> listarTodos() {

        String sql = """
            SELECT
                id,
                nome,
                cnpj,
                email,
                descricao,
                pais,
                estado,
                cep,
                senha
            FROM empresas
            ORDER BY id
        """

        List<Empresa> empresas = []

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
                        email: resultado.getString("email"),
                        cep: resultado.getString("cep"),
                        pais: resultado.getString("pais")
                )

                Empresa empresa = new Empresa(
                        id: resultado.getLong("id"),
                        cnpj: resultado.getString("cnpj"),
                        pais: resultado.getString("pais"),
                        estado: resultado.getString("estado"),
                        descricaoEmpresarial: resultado.getString("descricao"),
                        senha: resultado.getString("senha"),
                        informacoes: informacoes
                )

                empresas.add(empresa)
            }

        } catch (SQLException e) {
            println "Erro ao listar empresas: ${e.message}"

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

        return empresas
    }


    // buscar por ID
    Empresa buscarPorId(Long id) {

        String sql = """
            SELECT
                id,
                nome,
                cnpj,
                email,
                descricao,
                pais,
                estado,
                cep,
                senha
            FROM empresas
            WHERE id = ?
        """

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        Empresa empresa = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, id)

            resultado = statement.executeQuery()

            if (resultado.next()) {

                Informacoes informacoes = new Informacoes(
                        nome: resultado.getString("nome"),
                        email: resultado.getString("email"),
                        cep: resultado.getString("cep"),
                        pais: resultado.getString("pais")
                )

                empresa = new Empresa(
                        id: resultado.getLong("id"),
                        cnpj: resultado.getString("cnpj"),
                        pais: resultado.getString("pais"),
                        estado: resultado.getString("estado"),
                        descricaoEmpresarial: resultado.getString("descricao"),
                        senha: resultado.getString("senha"),
                        informacoes: informacoes
                )
            }

        } catch (SQLException e) {
            println "Erro ao buscar empresa por ID: ${e.message}"

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

        return empresa
    }


    // atualizar
    void atualizar(Empresa empresa) {

        String sql = """
            UPDATE empresas
            SET
                nome = ?,
                cnpj = ?,
                email = ?,
                descricao = ?,
                pais = ?,
                estado = ?,
                cep = ?,
                senha = ?
            WHERE id = ?
        """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setString(1, empresa.informacoes.nome)
            statement.setString(2, empresa.cnpj)
            statement.setString(3, empresa.informacoes.email)
            statement.setString(4, empresa.descricaoEmpresarial)
            statement.setString(5, empresa.pais)
            statement.setString(6, empresa.estado)
            statement.setString(7, empresa.informacoes.cep)
            statement.setString(8, empresa.senha)
            statement.setLong(9, empresa.id)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Empresa atualizada com sucesso."
            } else {
                println "Nenhuma empresa encontrada com o ID ${empresa.id}."
            }

        } catch (SQLException e) {
            println "Erro ao atualizar empresa: ${e.message}"

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
            DELETE FROM empresas
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
                println "Empresa excluída com sucesso."
            } else {
                println "Nenhuma empresa encontrada com o ID ${id}."
            }

        } catch (SQLException e) {
            println "Erro ao excluir empresa: ${e.message}"

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