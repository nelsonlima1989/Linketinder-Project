package com.linketinder.dao

import com.linketinder.model.Candidato
import com.linketinder.model.Informacoes

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CandidatoDAO {


    void inserir(Candidato candidato) {

        String sql = """
            INSERT INTO candidatos (
                nome,
                sobrenome,
                data_nascimento,
                email,
                cpf,
                pais,
                estado,
                cep,
                descricao_pessoal,
                senha
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """

        try (
                Connection conexao = ConexaoDB.conectar();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            preencherParametrosCandidato(statement, candidato)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    candidato.id = resultado.getLong("id")
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível inserir o candidato.",
                    e
            )
        }

    }


    List<Candidato> listarTodos() {

        String sql = """
            SELECT
                id,
                nome,
                sobrenome,
                data_nascimento,
                email,
                cpf,
                pais,
                estado,
                cep,
                descricao_pessoal,
                senha
            FROM candidatos
            ORDER BY id
        """

        List<Candidato> candidatos = []

        try (
                Connection conexao = ConexaoDB.conectar();
                PreparedStatement statement = conexao.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {
            while (resultado.next()) {
                candidatos.add(construirCandidato(resultado))
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível listar os candidatos.",
                    e
            )
        }

        return candidatos
    }


    Candidato buscarPorId(Long id) {
        String sql = """
        SELECT
            id,
            nome,
            sobrenome,
            data_nascimento,
            email,
            cpf,
            pais,
            estado,
            cep,
            descricao_pessoal,
            senha
        FROM candidatos
        WHERE id = ?
    """

        Candidato candidato = null

        try (
                Connection conexao = ConexaoDB.conectar();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    candidato = construirCandidato(resultado)
                }
            }

        } catch (SQLException e) {
            println "Erro ao buscar candidato por ID: ${e.message}"
        }

        return candidato
    }


    void atualizar(Candidato candidato) {
        String sql = """
        UPDATE candidatos
        SET
            nome = ?,
            sobrenome = ?,
            data_nascimento = ?,
            email = ?,
            cpf = ?,
            pais = ?,
            estado = ?,
            cep = ?,
            descricao_pessoal = ?,
            senha = ?
        WHERE id = ?
    """

        try (
                Connection conexao = ConexaoDB.conectar();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            preencherParametrosCandidato(statement, candidato)
            statement.setLong(11, candidato.id)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Candidato atualizado com sucesso."
            } else {
                println "Nenhum candidato encontrado com o ID ${candidato.id}."
            }

        } catch (SQLException e) {
            println "Erro ao atualizar candidato: ${e.message}"
        }
    }


    void excluir(Long id) {
        String sql = """
        DELETE FROM candidatos
        WHERE id = ?
    """

        try (
                Connection conexao = ConexaoDB.conectar();
                PreparedStatement statement = conexao.prepareStatement(sql)
        ) {
            statement.setLong(1, id)

            int linhasAfetadas = statement.executeUpdate()

            if (linhasAfetadas > 0) {
                println "Candidato excluído com sucesso."
            } else {
                println "Nenhum candidato encontrado com o ID ${id}."
            }

        } catch (SQLException e) {
            println "Erro ao excluir candidato: ${e.message}"
        }
    }


    private void preencherParametrosCandidato(
            PreparedStatement statement,
            Candidato candidato
    ) throws SQLException {
        statement.setString(1, candidato.informacoes.nome)
        statement.setString(2, candidato.informacoes.sobrenome)
        statement.setDate(3, java.sql.Date.valueOf(candidato.dataNascimento))
        statement.setString(4, candidato.informacoes.email)
        statement.setString(5, candidato.cpf)
        statement.setString(6, candidato.informacoes.pais)
        statement.setString(7, candidato.estado)
        statement.setString(8, candidato.informacoes.cep)
        statement.setString(9, candidato.descricaoPessoal)
        statement.setString(10, candidato.senha)
    }


    private Candidato construirCandidato(ResultSet resultado) throws SQLException {
        Informacoes informacoes = new Informacoes(
                nome: resultado.getString("nome"),
                sobrenome: resultado.getString("sobrenome"),
                email: resultado.getString("email"),
                pais: resultado.getString("pais"),
                cep: resultado.getString("cep")
        )

        return new Candidato(
                id: resultado.getLong("id"),
                estado: resultado.getString("estado"),
                informacoes: informacoes,
                cpf: resultado.getString("cpf"),
                dataNascimento: resultado.getDate("data_nascimento").toLocalDate(),
                descricaoPessoal: resultado.getString("descricao_pessoal"),
                senha: resultado.getString("senha")
        )
    }

}