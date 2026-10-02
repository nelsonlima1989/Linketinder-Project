package com.linketinder.dao

import com.linketinder.model.Skill

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VagaCompetenciaDAO {


    //adicionar
    void adicionarCompetencia(Long vagaId, Long competenciaId) {

        String sql = """
            INSERT INTO vaga_competencias (
                vaga_id,
                competencia_id
            )
            VALUES (?, ?)
        """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, vagaId)
            statement.setLong(2, competenciaId)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Competência associada à vaga com sucesso."
            }

        } catch (SQLException e) {
            println "Erro ao associar competência à vaga: ${e.message}"

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


    //listar
    List<Skill> listarCompetenciasDaVaga(Long vagaId) {

        String sql = """
        SELECT
            c.id,
            c.nome
        FROM competencias AS c
        INNER JOIN vaga_competencias AS vc
            ON c.id = vc.competencia_id
        WHERE vc.vaga_id = ?
        ORDER BY c.id
    """

        List<Skill> skills = []

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, vagaId)

            resultado = statement.executeQuery()

            while (resultado.next()) {

                Skill skill = new Skill(
                        id: resultado.getLong("id"),
                        nome: resultado.getString("nome")
                )

                skills.add(skill)
            }

        } catch (SQLException e) {
            println "Erro ao listar competências da vaga: ${e.message}"

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


    //remover
    void removerCompetencia(Long vagaId, Long competenciaId) {

        String sql = """
        DELETE FROM vaga_competencias
        WHERE vaga_id = ?
          AND competencia_id = ?
    """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, vagaId)
            statement.setLong(2, competenciaId)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Competência removida da vaga com sucesso."
            } else {
                println "Relacionamento não encontrado."
            }

        } catch (SQLException e) {
            println "Erro ao remover competência da vaga: ${e.message}"

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