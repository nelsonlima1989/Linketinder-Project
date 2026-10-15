package com.linketinder.dao

import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes
import com.linketinder.repository.EmpresaRepository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class EmpresaDAO implements EmpresaRepository{

    private final ConexaoProvider conexaoProvider

    EmpresaDAO(ConexaoProvider conexaoProvider) {
        this.conexaoProvider = conexaoProvider
    }

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

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setString(1, empresa.informacoes.nome)
            statement.setString(2, empresa.cnpj)
            statement.setString(3, empresa.informacoes.email)
            statement.setString(4, empresa.descricaoEmpresarial)
            statement.setString(5, empresa.pais)
            statement.setString(6, empresa.estado)
            statement.setString(7, empresa.informacoes.cep)
            statement.setString(8, empresa.senha)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    empresa.id = resultado.getLong("id")
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível inserir a empresa.",
                    e
            )
        }

    }



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

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {
            while (resultado.next()) {
                empresas.add(construirEmpresa(resultado))
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar as empresas.",
                    e
            )
        }

        return empresas
    }

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

        Empresa empresa = null

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    empresa = construirEmpresa(resultado)
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível buscar a empresa por ID.",
                    e
            )
        }

        return empresa
    }

    Map<Long, Empresa> buscarPorIds(List<Long> ids) {

        if (ids.isEmpty()) {
            return [:]
        }

        String placeholders = ids.collect { "?" }.join(", ")

        String sql = """
        SELECT
            id,
            nome,
            email,
            cnpj,
            descricao,
            pais,
            estado,
            cep,
            senha
        FROM empresas
        WHERE id IN (${placeholders})
        ORDER BY id
    """

        Map<Long, Empresa> empresasPorId = [:]

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            ids.eachWithIndex { Long id, int index ->
                statement.setLong(index + 1, id)
            }

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    Empresa empresa = construirEmpresa(resultado)
                    empresasPorId[empresa.id] = empresa
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível buscar as empresas por IDs.",
                    e
            )
        }

        return empresasPorId
    }


    boolean atualizar(Empresa empresa) {

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

        try (
                Connection conexao = conexaoProvider.obterConexao();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
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

            return linhasAfetadas > 0

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível atualizar a empresa.",
                    e
            )
        }
    }


    boolean excluir(Long id) {

        String sql = """
        DELETE FROM empresas
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
                    "Não foi possível excluir a empresa.",
                    e
            )
        }
    }

    private Empresa construirEmpresa(ResultSet resultado) throws SQLException {

        String pais = resultado.getString("pais")

        Informacoes informacoes = new Informacoes(
                nome: resultado.getString("nome"),
                email: resultado.getString("email"),
                cep: resultado.getString("cep"),
                pais: pais
        )

        return new Empresa(
                id: resultado.getLong("id"),
                cnpj: resultado.getString("cnpj"),
                pais: pais,
                estado: resultado.getString("estado"),
                descricaoEmpresarial: resultado.getString("descricao"),
                senha: resultado.getString("senha"),
                informacoes: informacoes
        )
    }
}