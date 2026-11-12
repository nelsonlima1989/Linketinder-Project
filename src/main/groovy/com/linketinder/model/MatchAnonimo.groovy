package com.linketinder.model

import java.time.OffsetDateTime

class MatchAnonimo {
    Long id
    OffsetDateTime dataMatch
    CandidatoAnonimo candidato
    EmpresaAnonima empresa
    RecrutadorAnonimo recrutador
}
