package com.linketinder.service

import com.linketinder.model.Candidato
import com.linketinder.model.Empresa
import com.linketinder.model.Curtida
import com.linketinder.model.Match
import com.linketinder.model.TipoCurtida

class MatchService {

    private List<Match> matches = []

    Match verificarMatch(
            Candidato candidato,
            Empresa empresa,
            List<Curtida> curtidas
    ) {

        def curtidaCandidato = curtidas.find { curtida ->
            curtida.candidato == candidato &&
                    curtida.empresa == empresa &&
                    curtida.tipoCurtida == TipoCurtida.CANDIDATO
        }

        def curtidaEmpresa = curtidas.find { curtida ->
            curtida.candidato == candidato &&
                    curtida.empresa == empresa &&
                    curtida.tipoCurtida == TipoCurtida.EMPRESA
        }

        if (curtidaCandidato && curtidaEmpresa) {

            def matchExistente = matches.find { match ->
                match.candidato == candidato &&
                        match.empresa == empresa
            }

            if (matchExistente) {
                return matchExistente
            }

            def match = new Match(
                    candidato: candidato,
                    empresa: empresa
            )

            matches << match

            return match
        }

        return null
    }

    List<Match> listarMatches() {
        return matches
    }
}