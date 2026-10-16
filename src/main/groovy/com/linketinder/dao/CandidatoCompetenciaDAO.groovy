package com.linketinder.dao

import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Skill
import com.linketinder.repository.CandidatoCompetenciaRepository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException



class CandidatoCompetenciaDAO implements CandidatoCompetenciaRepository {

    private final ConexaoProvider conexaoProvider

    CandidatoCompetenciaDAO(ConexaoProvider conexaoProvider) {
        this.conexaoProvider = conexaoProvider
    }


    void adicionarCompetencia(Long candidatoId, Long competenciaId) {

        String sql = """
            INSERT INTO candidato_competencias (
                candidato_id,
                competencia_id
            )
            VALUES (?, ?)
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, candidatoId)
            statement.setLong(2, competenciaId)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Competência associada ao candidato com sucesso."
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível associar a competência ao candidato.",
                    e
            )
        }

    }


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

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, candidatoId)

            try (ResultSet resultado = statement.executeQuery()) {

                while (resultado.next()) {
                    Skill skill = new Skill(
                            id: resultado.getLong("id"),
                            nome: resultado.getString("nome")
                    )

                    skills.add(skill)
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar as competências do candidato.",
                    e
            )
        }


        return skills
    }


    void removerCompetencia(Long candidatoId, Long competenciaId) {

        String sql = """
        DELETE FROM candidato_competencias
        WHERE candidato_id = ?
          AND competencia_id = ?
    """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, candidatoId)
            statement.setLong(2, competenciaId)

            statement.executeUpdate()

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível remover a competência do candidato.",
                    e
            )
        }
    }

}