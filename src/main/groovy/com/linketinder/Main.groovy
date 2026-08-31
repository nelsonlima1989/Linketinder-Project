package com.linketinder

import com.linketinder.data.DataInicializador
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService
import com.linketinder.ui.Menu

def inicializador = new DataInicializador()

def candidatoService = new CandidatoService(inicializador)
def empresaService = new EmpresaService(inicializador)

def menu = new Menu(candidatoService, empresaService)

menu.iniciarPrograma()