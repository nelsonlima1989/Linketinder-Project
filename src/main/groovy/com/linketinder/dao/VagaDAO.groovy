package com.linketinder.dao

import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.VagaRepository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VagaDAO implements VagaRepository{

    private final ConexaoProvider conexaoProvider
    private final EmpresaRepository empresaRepository

    VagaDAO(
            ConexaoProvider conexaoProvider,
            EmpresaRepository empresaRepository
    ) {
        this.conexaoProvider = conexaoProvider
        this.empresaRepository = empresaRepository
    }



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
        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, vaga.empresa.id)
            statement.setString(2, vaga.titulo)
            statement.setString(3, vaga.estado)
            statement.setString(4, vaga.cidade)
            statement.setString(5, vaga.descricaoVaga)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    vaga.id = resultado.getLong("id")
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível inserir a vaga.",
                    e
            )
        }

    }

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
        Map<Long, Long> empresaIdPorVagaId = [:]
        Set<Long> empresaIds = new LinkedHashSet<Long>()

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {
            while (resultado.next()) {

                Long empresaId = resultado.getLong("empresa_id")

                Vaga vaga = new Vaga(
                        id: resultado.getLong("id"),
                        titulo: resultado.getString("nome"),
                        descricaoVaga: resultado.getString("descricao"),
                        estado: resultado.getString("estado"),
                        cidade: resultado.getString("cidade")
                )

                vagas.add(vaga)
                empresaIdPorVagaId[vaga.id] = empresaId
                empresaIds.add(empresaId)
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar as vagas.",
                    e
            )
        }

        if (empresaIds.isEmpty()) {
            return vagas
        }

        Map<Long, Empresa> empresasPorId = empresaRepository.buscarPorIds(
                new ArrayList<Long>(empresaIds)
        )

        for (Vaga vaga : vagas) {
            Long empresaId = empresaIdPorVagaId[vaga.id]
            vaga.empresa = empresasPorId[empresaId]
        }

        return vagas
    }




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

        Vaga vaga = null

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    vaga = construirVaga(resultado)
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível buscar a vaga por ID.",
                    e
            )
        }

        return vaga
    }

    boolean atualizar(Vaga vaga) {

        String sql = """
        UPDATE vagas
        SET
            nome = ?,
            descricao = ?,
            estado = ?,
            cidade = ?
        WHERE id = ?
    """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setString(1, vaga.titulo)
            statement.setString(2, vaga.descricaoVaga)
            statement.setString(3, vaga.estado)
            statement.setString(4, vaga.cidade)
            statement.setLong(5, vaga.id)

            int linhasAfetadas = statement.executeUpdate()

            return linhasAfetadas > 0

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível atualizar a vaga.",
                    e
            )
        }
    }


    boolean excluir(Long id) {

        String sql = """
        DELETE FROM vagas
        WHERE id = ?
    """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            int linhasAfetadas = statement.executeUpdate()

            return linhasAfetadas > 0

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível excluir a vaga.",
                    e
            )
        }
    }

    private Vaga construirVaga(ResultSet resultado) throws SQLException {

        Long empresaId = resultado.getLong("empresa_id")

        Empresa empresa = empresaRepository.buscarPorId(empresaId)

        return new Vaga(
                id: resultado.getLong("id"),
                titulo: resultado.getString("nome"),
                descricaoVaga: resultado.getString("descricao"),
                estado: resultado.getString("estado"),
                cidade: resultado.getString("cidade"),
                empresa: empresa
        )
    }


}
