
package serviceTest

import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.VagaRepository
import com.linketinder.service.VagaService
import spock.lang.Specification

class VagaServiceSpec extends Specification {

    private VagaRepository vagaRepository
    private EmpresaRepository empresaRepository
    private VagaService vagaService

    def setup() {
        vagaRepository = Mock(VagaRepository)
        empresaRepository = Mock(EmpresaRepository)

        vagaService = new VagaService(
                vagaRepository,
                empresaRepository
        )
    }

    def "deve cadastrar vaga vinculada a empresa existente"() {
        given:
        Long empresaId = 6L

        Empresa empresa = new Empresa(id: empresaId)

        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Backend",
                estado: "RJ",
                cidade: "Rio de Janeiro",
                descricaoVaga: "Desenvolvimento de APIs REST"
        )

        when:
        vagaService.cadastrarVaga(vaga, empresaId)

        then:
        1 * empresaRepository.buscarPorId(empresaId) >> empresa

        1 * vagaRepository.inserir({ Vaga vagaRecebida ->
            vagaRecebida.is(vaga) &&
                    vagaRecebida.empresa.is(empresa)
        }) >> { Vaga vagaRecebida ->
            vagaRecebida.id = 10L
        }

        vaga.id == 10L
        vaga.empresa.is(empresa)
    }

    def "deve lançar exceção quando empresa não existir"() {
        given:
        Long empresaId = 999L
        Vaga vaga = new Vaga(titulo: "Desenvolvedor")

        when:
        vagaService.cadastrarVaga(vaga, empresaId)

        then:
        1 * empresaRepository.buscarPorId(empresaId) >> null
        0 * vagaRepository._

        NoSuchElementException e =
                thrown(NoSuchElementException)

        e.message == "Empresa não encontrada."
    }

    def "deve rejeitar vaga nula"() {
        when:
        vagaService.cadastrarVaga(null, 6L)

        then:
        IllegalArgumentException e =
                thrown(IllegalArgumentException)

        e.message == "Dados da vaga ou empresa inválidos."

        0 * empresaRepository._
        0 * vagaRepository._
    }

    def "deve rejeitar identificadores invalidos"() {
        given:
        Vaga vaga = new Vaga(titulo: "Desenvolvedor")

        when:
        vagaService.cadastrarVaga(vaga, empresaId)

        then:
        thrown(IllegalArgumentException)

        0 * empresaRepository._
        0 * vagaRepository._

        where:
        empresaId << [null, 0L, -1L]
    }

    def "deve propagar erro quando persistencia falhar"() {
        given:
        Long empresaId = 6L
        Empresa empresa = new Empresa(id: empresaId)
        Vaga vaga = new Vaga(titulo: "Desenvolvedor")

        when:
        vagaService.cadastrarVaga(vaga, empresaId)

        then:
        1 * empresaRepository.buscarPorId(empresaId) >> empresa

        1 * vagaRepository.inserir(_ as Vaga) >> {
            throw new IllegalStateException(
                    "Não foi possível inserir a vaga."
            )
        }

        IllegalStateException e =
                thrown(IllegalStateException)

        e.message == "Não foi possível inserir a vaga."
    }
}
