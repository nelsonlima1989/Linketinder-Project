package com.linketinder.dao

import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Empresa
import com.linketinder.model.Recrutador
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.RecrutadorRepository

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class RecrutadorDAO implements RecrutadorRepository {

    private final ConexaoProvider conexaoProvider
    private final EmpresaRepository empresaRepository

    RecrutadorDAO(
            ConexaoProvider conexaoProvider,
            EmpresaRepository empresaRepository
    ) {
        this.conexaoProvider = conexaoProvider
        this.empresaRepository = empresaRepository
    }

    @Override
    void inserir(Recrutador recrutador) {
        String sql = """
            INSERT INTO recrutadores (
                empresa_id,
                nome,
                area_recrutamento,
                descricao,
                email,
                cpf,
                data_nascimento,
                senha
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, recrutador.empresa.id)
            statement.setString(2, recrutador.nome)
            statement.setString(3, recrutador.areaRecrutamento)
            statement.setString(4, recrutador.descricao)
            statement.setString(5, recrutador.email)
            statement.setString(6, recrutador.cpf)
            statement.setDate(7, Date.valueOf(recrutador.dataNascimento))
            statement.setString(8, recrutador.senha)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    recrutador.id = resultado.getLong("id")
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível inserir o recrutador.",
                    e
            )
        }
    }

    @Override
    List<Recrutador> listarTodos() {
        String sql = """
            SELECT
                id,
                empresa_id,
                nome,
                area_recrutamento,
                descricao,
                email,
                cpf,
                data_nascimento,
                senha
            FROM recrutadores
            ORDER BY id
        """

        List<Recrutador> recrutadores = []
        Map<Long, Long> empresaIdPorRecrutadorId = [:]
        Set<Long> empresaIds = new LinkedHashSet<Long>()

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {
            while (resultado.next()) {
                Long empresaId = resultado.getLong("empresa_id")
                Recrutador recrutador = construirRecrutadorSemEmpresa(resultado)

                recrutadores.add(recrutador)
                empresaIdPorRecrutadorId[recrutador.id] = empresaId
                empresaIds.add(empresaId)
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar os recrutadores.",
                    e
            )
        }

        if (empresaIds.isEmpty()) {
            return recrutadores
        }

        Map<Long, Empresa> empresasPorId = empresaRepository.buscarPorIds(
                new ArrayList<Long>(empresaIds)
        )

        for (Recrutador recrutador : recrutadores) {
            Long empresaId = empresaIdPorRecrutadorId[recrutador.id]
            recrutador.empresa = empresasPorId[empresaId]
        }

        return recrutadores
    }

    @Override
    List<Recrutador> listarPorEmpresaId(Long empresaId) {
        String sql = """
            SELECT
                id,
                empresa_id,
                nome,
                area_recrutamento,
                descricao,
                email,
                cpf,
                data_nascimento,
                senha
            FROM recrutadores
            WHERE empresa_id = ?
            ORDER BY id
        """

        List<Recrutador> recrutadores = []

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, empresaId)

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    recrutadores.add(construirRecrutadorSemEmpresa(resultado))
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar os recrutadores da empresa.",
                    e
            )
        }

        if (recrutadores.isEmpty()) {
            return recrutadores
        }

        Empresa empresa = empresaRepository.buscarPorId(empresaId)
        for (Recrutador recrutador : recrutadores) {
            recrutador.empresa = empresa
        }

        return recrutadores
    }

    @Override
    Recrutador buscarPorId(Long id) {
        String sql = """
            SELECT
                id,
                empresa_id,
                nome,
                area_recrutamento,
                descricao,
                email,
                cpf,
                data_nascimento,
                senha
            FROM recrutadores
            WHERE id = ?
        """

        Recrutador recrutador = null

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    recrutador = construirRecrutador(resultado)
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível buscar o recrutador por ID.",
                    e
            )
        }

        return recrutador
    }

    @Override
    Recrutador buscarPorCpf(String cpf) {
        String sql = """
            SELECT
                id,
                empresa_id,
                nome,
                area_recrutamento,
                descricao,
                email,
                cpf,
                data_nascimento,
                senha
            FROM recrutadores
            WHERE cpf = ?
        """

        Recrutador recrutador = null

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setString(1, cpf)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    recrutador = construirRecrutador(resultado)
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível buscar o recrutador por CPF.",
                    e
            )
        }

        return recrutador
    }

    @Override
    Recrutador buscarPorEmail(String email) {
        String sql = """
            SELECT
                id,
                empresa_id,
                nome,
                area_recrutamento,
                descricao,
                email,
                cpf,
                data_nascimento,
                senha
            FROM recrutadores
            WHERE LOWER(email) = LOWER(?)
        """

        Recrutador recrutador = null

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setString(1, email)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    recrutador = construirRecrutador(resultado)
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível buscar o recrutador por e-mail.",
                    e
            )
        }

        return recrutador
    }

    @Override
    boolean atualizar(Recrutador recrutador) {
        String sql = """
            UPDATE recrutadores
            SET
                nome = ?,
                area_recrutamento = ?,
                descricao = ?,
                email = ?,
                cpf = ?,
                data_nascimento = ?,
                senha = ?
            WHERE id = ?
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setString(1, recrutador.nome)
            statement.setString(2, recrutador.areaRecrutamento)
            statement.setString(3, recrutador.descricao)
            statement.setString(4, recrutador.email)
            statement.setString(5, recrutador.cpf)
            statement.setDate(6, Date.valueOf(recrutador.dataNascimento))
            statement.setString(7, recrutador.senha)
            statement.setLong(8, recrutador.id)

            int linhasAfetadas = statement.executeUpdate()

            return linhasAfetadas > 0

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível atualizar o recrutador.",
                    e
            )
        }
    }

    @Override
    boolean excluir(Long id) {
        String sql = """
            DELETE FROM recrutadores
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
                    "Não foi possível excluir o recrutador.",
                    e
            )
        }
    }

    @Override
    int excluirPorEmpresaId(Long empresaId) {
        String sql = """
            DELETE FROM recrutadores
            WHERE empresa_id = ?
        """

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, empresaId)

            return statement.executeUpdate()

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível excluir os recrutadores da empresa.",
                    e
            )
        }
    }

    private Recrutador construirRecrutadorSemEmpresa(
            ResultSet resultado
    ) throws SQLException {
        return new Recrutador(
                id: resultado.getLong("id"),
                nome: resultado.getString("nome"),
                areaRecrutamento: resultado.getString("area_recrutamento"),
                descricao: resultado.getString("descricao"),
                email: resultado.getString("email"),
                cpf: resultado.getString("cpf"),
                dataNascimento: resultado.getDate("data_nascimento").toLocalDate(),
                senha: resultado.getString("senha")
        )
    }

    private Recrutador construirRecrutador(
            ResultSet resultado
    ) throws SQLException {
        Long empresaId = resultado.getLong("empresa_id")
        Empresa empresa = empresaRepository.buscarPorId(empresaId)

        Recrutador recrutador = construirRecrutadorSemEmpresa(resultado)
        recrutador.empresa = empresa

        return recrutador
    }
}
