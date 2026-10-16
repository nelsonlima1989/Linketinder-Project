package databaseTest

import com.linketinder.database.ConexaoFactory
import com.linketinder.database.ConexaoPostgresProvider
import com.linketinder.database.ConexaoProvider
import spock.lang.Specification

class ConexaoFactorySpec extends Specification {

    def "deve criar um provider PostgreSQL"() {
        when:
        ConexaoProvider provider =
                ConexaoFactory.criarProvider("postgresql")

        then:
        provider instanceof ConexaoPostgresProvider
    }

    def "deve aceitar PostgreSQL com letras maiusculas e espacos"() {
        when:
        ConexaoProvider provider =
                ConexaoFactory.criarProvider(" PostgreSQL ")

        then:
        provider instanceof ConexaoPostgresProvider
    }

    def "deve rejeitar banco de dados nao suportado"() {
        when:
        ConexaoFactory.criarProvider("mysql")

        then:
        IllegalArgumentException erro =
                thrown(IllegalArgumentException)

        erro.message == "Banco de dados não suportado: mysql"
    }

    def "deve rejeitar tipo de banco nulo"() {
        when:
        ConexaoFactory.criarProvider(null)

        then:
        thrown(IllegalArgumentException)
    }
}