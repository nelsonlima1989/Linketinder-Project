import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.dao.CandidatoDAO
import com.linketinder.dao.EmpresaDAO
import com.linketinder.dao.VagaCompetenciaDAO
import com.linketinder.dao.VagaDAO
import com.linketinder.service.CandidatoService
import com.linketinder.service.CurtidaService
import com.linketinder.service.EmpresaService
import com.linketinder.service.MatchService
import com.linketinder.ui.Menu


// DAOs
CandidatoDAO candidatoDAO = new CandidatoDAO()

CandidatoCompetenciaDAO candidatoCompetenciaDAO =
        new CandidatoCompetenciaDAO()

EmpresaDAO empresaDAO = new EmpresaDAO()

VagaDAO vagaDAO = new VagaDAO()

VagaCompetenciaDAO vagaCompetenciaDAO =
        new VagaCompetenciaDAO()


// Services
CandidatoService candidatoService =
        new CandidatoService(
                candidatoDAO,
                candidatoCompetenciaDAO
        )

EmpresaService empresaService =
        new EmpresaService(
                empresaDAO,
                vagaDAO,
                vagaCompetenciaDAO
        )

MatchService matchService =
        new MatchService()

CurtidaService curtidaService =
        new CurtidaService(
                empresaService,
                matchService
        )


// Menu
Menu menu = new Menu(
        candidatoService,
        empresaService,
        curtidaService,
        matchService
)

menu.iniciarPrograma()