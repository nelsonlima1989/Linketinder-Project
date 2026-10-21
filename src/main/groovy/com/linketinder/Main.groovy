package com.linketinder

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.CurtidaController
import com.linketinder.controller.EmpresaController
import com.linketinder.controller.MatchController
import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.dao.CandidatoDAO
import com.linketinder.database.ConexaoProvider
import com.linketinder.database.ConexaoFactory
import com.linketinder.dao.EmpresaDAO
import com.linketinder.dao.VagaCompetenciaDAO
import com.linketinder.dao.VagaDAO
import com.linketinder.repository.CandidatoCompetenciaRepository
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.VagaCompetenciaRepository
import com.linketinder.repository.VagaRepository
import com.linketinder.service.CandidatoService
import com.linketinder.service.CurtidaService
import com.linketinder.service.EmpresaService
import com.linketinder.service.MatchService
import com.linketinder.view.Menu



ConexaoProvider conexaoProvider = ConexaoFactory.criarProvider("postgresql")

CandidatoRepository candidatoDAO = new CandidatoDAO(conexaoProvider)

CandidatoCompetenciaRepository candidatoCompetenciaDAO = new CandidatoCompetenciaDAO(conexaoProvider)

EmpresaRepository empresaDAO = new EmpresaDAO(conexaoProvider)

VagaRepository vagaDAO = new VagaDAO(conexaoProvider, empresaDAO)

VagaCompetenciaRepository vagaCompetenciaDAO = new VagaCompetenciaDAO(conexaoProvider)



CandidatoService candidatoService =
        new CandidatoService(
                candidatoDAO,
                candidatoCompetenciaDAO
        )

CandidatoController candidatoController = new CandidatoController(candidatoService)

EmpresaService empresaService =
        new EmpresaService(
                empresaDAO,
                vagaDAO,
                vagaCompetenciaDAO
        )

EmpresaController empresaController = new EmpresaController(empresaService)

MatchService matchService = new MatchService()

MatchController matchController = new MatchController(matchService)

CurtidaService curtidaService =
        new CurtidaService(
                empresaService,
                matchService
        )

CurtidaController curtidaController = new CurtidaController(curtidaService)



Scanner scanner = new Scanner(System.in)
Menu menu = new Menu(
        scanner,
        candidatoController,
        empresaController,
        curtidaController,
        matchController
)

menu.iniciarPrograma()