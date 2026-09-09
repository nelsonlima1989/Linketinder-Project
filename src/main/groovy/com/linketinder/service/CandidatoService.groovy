package com.linketinder.service

import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.data.DataInicializador

class CandidatoService {

    private List<Candidato> candidatos

    CandidatoService(DataInicializador inicializador) {
        this.candidatos = inicializador.criarCandidatos()
    }

    List<Candidato> listarTodosCandidatos() {
        return candidatos
    }

    void cadastrarCandidato(Candidato candidato){
        candidatos.add(candidato)
    }

/*
    #### Futura implementação ####
    Optional<Candidato> buscarCandidatoPorNome(String nome) {

        def candidato = candidatos.find {
            it.informacoes.nome.equalsIgnoreCase(nome)
        }

        return Optional.ofNullable(candidato)
    }

    #### Futura implementação ####
 */

    List<CandidatoAnonimo> listarCandidatosAnonimos() {

        return candidatos.collect { candidato ->

            new CandidatoAnonimo(
                    id: candidato.id,
                    estado: candidato.estado,
                    descricaoPessoal: candidato.descricaoPessoal,
                    skills: candidato.skills
            )
        }
    }
}