package com.linketinder.model

import java.time.OffsetDateTime

class CurtidaResultadoDTO {
    Long candidatoId
    Long vagaId
    Long empresaId
    Long recrutadorId
    OffsetDateTime dataCurtida
    boolean match
    Long matchId
    String mensagem
}
