
package com.linketinder.dao

import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Skill
import com.linketinder.repository.CompetenciaRepository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CompetenciaDAO implements CompetenciaRepository {

    private final ConexaoProvider conexaoProvider

    CompetenciaDAO(ConexaoProvider conexaoProvider) {
        this.conexaoProvider = conexaoProvider
    }

    void inserir(Skill skill) {

        String sql = """
            INSERT INTO competencias (nome)
            VALUES (?)
            RETURNING id
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setString(1, skill.nome)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    skill.id = resultado.getLong("id")
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível inserir a competência.",
                    e
            )
        }
    }

    List<Skill> listarTodos() {

        String sql = """
            SELECT
                id,
                nome
            FROM competencias
            ORDER BY id
        """

        List<Skill> competencias = []

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {
            while (resultado.next()) {
                competencias.add(construirSkill(resultado))
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar as competências.",
                    e
            )
        }

        return competencias
    }

    Skill buscarPorId(Long id) {

        String sql = """
            SELECT
                id,
                nome
            FROM competencias
            WHERE id = ?
        """

        Skill competencia = null

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    competencia = construirSkill(resultado)
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível buscar a competência por ID.",
                    e
            )
        }

        return competencia
    }

    boolean atualizar(Skill skill) {

        String sql = """
            UPDATE competencias
            SET nome = ?
            WHERE id = ?
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setString(1, skill.nome)
            statement.setLong(2, skill.id)

            return statement.executeUpdate() > 0

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível atualizar a competência.",
                    e
            )
        }
    }

    boolean excluir(Long id) {

        String sql = """
            DELETE FROM competencias
            WHERE id = ?
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            return statement.executeUpdate() > 0

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível excluir a competência.",
                    e
            )
        }
    }

    private Skill construirSkill(ResultSet resultado) throws SQLException {

        return new Skill(
                id: resultado.getLong("id"),
                nome: resultado.getString("nome")
        )
    }
}
