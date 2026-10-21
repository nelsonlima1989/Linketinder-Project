package com.linketinder.controller

import com.linketinder.model.Match
import com.linketinder.service.MatchService

class MatchController {

    private final MatchService matchService

    MatchController(MatchService matchService) {
        this.matchService = matchService
    }

    List<Match> listarMatches() {
        return matchService.listarMatches()
    }
}