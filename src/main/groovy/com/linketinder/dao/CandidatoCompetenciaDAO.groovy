package com.linketinder.dao

import com.linketinder.model.Skill

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CandidatoCompetenciaDAO {

    //adicionar
    void adicionarCompetencia(Long candidatoId, Long competenciaId) {

        String sql = """
            INSERT INTO candidato_competencias (
                candidato_id,
                competencia_id
            )
            VALUES (?, ?)
        """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, candidatoId)
            statement.setLong(2, competenciaId)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Competência associada ao candidato com sucesso."
            }

        } catch (SQLException e) {
            println "Erro ao associar competência ao candidato: ${e.message}"

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
    List<Skill> listarCompetenciasDoCandidato(Long candidatoId) {

        String sql = """
        SELECT
            c.id,
            c.nome
        FROM competencias AS c
        INNER JOIN candidato_competencias AS cc
            ON c.id = cc.competencia_id
        WHERE cc.candidato_id = ?
        ORDER BY c.id
    """

        List<Skill> skills = []

        Connection conexao = null
        PreparedStatement statement = null
        ResultSet resultado = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, candidatoId)

            resultado = statement.executeQuery()

            while (resultado.next()) {

                Skill skill = new Skill(
                        id: resultado.getLong("id"),
                        nome: resultado.getString("nome")
                )

                skills.add(skill)
            }

        } catch (SQLException e) {
            println "Erro ao listar competências do candidato: ${e.message}"

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
    void removerCompetencia(Long candidatoId, Long competenciaId) {

        String sql = """
        DELETE FROM candidato_competencias
        WHERE candidato_id = ?
          AND competencia_id = ?
    """

        Connection conexao = null
        PreparedStatement statement = null

        try {
            conexao = ConexaoDB.conectar()
            statement = conexao.prepareStatement(sql)

            statement.setLong(1, candidatoId)
            statement.setLong(2, competenciaId)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Competência removida do candidato com sucesso."
            } else {
                println "Relacionamento não encontrado."
            }

        } catch (SQLException e) {
            println "Erro ao remover competência do candidato: ${e.message}"

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