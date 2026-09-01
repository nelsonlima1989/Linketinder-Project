import com.linketinder.data.DataInicializador
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService
import com.linketinder.service.CurtidaService
import com.linketinder.service.MatchService
import com.linketinder.ui.Menu

// Inicializador
def inicializador = new DataInicializador()

// Services
def candidatoService = new CandidatoService(inicializador)
def empresaService = new EmpresaService(inicializador)

def matchService = new MatchService()

def curtidaService = new CurtidaService(
        empresaService,
        matchService
)

// Menu
def menu = new Menu(
        candidatoService,
        empresaService,
        curtidaService,
        matchService
)
menu.iniciarPrograma()