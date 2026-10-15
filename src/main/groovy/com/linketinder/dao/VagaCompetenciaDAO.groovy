
package com.linketinder.dao

import com.linketinder.model.Skill
import com.linketinder.repository.VagaCompetenciaRepository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VagaCompetenciaDAO implements VagaCompetenciaRepository {

    private final ConexaoProvider conexaoProvider

    VagaCompetenciaDAO(ConexaoProvider conexaoProvider) {
        this.conexaoProvider = conexaoProvider
    }

    void adicionarCompetencia(Long vagaId, Long competenciaId) {

        String sql = """
            INSERT INTO vaga_competencias (
                vaga_id,
                competencia_id
            )
            VALUES (?, ?)
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, vagaId)
            statement.setLong(2, competenciaId)

            statement.executeUpdate()

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível associar a competência à vaga.",
                    e
            )
        }
    }

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

        List<Skill> competencias = []

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, vagaId)

            try (ResultSet resultado = statement.executeQuery()) {

                while (resultado.next()) {
                    Skill competencia = new Skill(
                            id: resultado.getLong("id"),
                            nome: resultado.getString("nome")
                    )

                    competencias.add(competencia)
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar as competências da vaga.",
                    e
            )
        }

        return competencias
    }

    void removerCompetencia(Long vagaId, Long competenciaId) {

        String sql = """
            DELETE FROM vaga_competencias
            WHERE vaga_id = ?
              AND competencia_id = ?
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, vagaId)
            statement.setLong(2, competenciaId)

            statement.executeUpdate()

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível remover a competência da vaga.",
                    e
            )
        }
    }
}
