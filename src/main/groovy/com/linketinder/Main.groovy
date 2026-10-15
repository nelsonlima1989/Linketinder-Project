package com.linketinder

import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.dao.CandidatoDAO
import com.linketinder.dao.ConexaoPostgresProvider
import com.linketinder.dao.ConexaoProvider
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
import com.linketinder.ui.Menu



ConexaoProvider conexaoProvider = new ConexaoPostgresProvider()

CandidatoRepository candidatoDAO = new CandidatoDAO(conexaoProvider)

CandidatoCompetenciaRepository candidatoCompetenciaDAO =
        new CandidatoCompetenciaDAO(conexaoProvider)

EmpresaRepository empresaDAO = new EmpresaDAO(conexaoProvider)

VagaRepository vagaDAO = new VagaDAO(conexaoProvider, empresaDAO)

VagaCompetenciaRepository vagaCompetenciaDAO = new VagaCompetenciaDAO(conexaoProvider)



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



Scanner scanner = new Scanner(System.in)
Menu menu = new Menu(
        candidatoService,
        empresaService,
        curtidaService,
        matchService,
        scanner
)

menu.iniciarPrograma()