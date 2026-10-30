
package com.linketinder.service

import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.VagaRepository

class VagaService {

    private final VagaRepository vagaRepository
    private final EmpresaRepository empresaRepository

    VagaService(
            VagaRepository vagaRepository,
            EmpresaRepository empresaRepository
    ) {
        this.vagaRepository = vagaRepository
        this.empresaRepository = empresaRepository
    }

    void cadastrarVaga(Vaga vaga, Long empresaId) {

        if (vaga == null || empresaId == null || empresaId <= 0) {
            throw new IllegalArgumentException(
                    "Dados da vaga ou empresa inválidos."
            )
        }

        Empresa empresa = empresaRepository.buscarPorId(empresaId)

        if (empresa == null) {
            throw new NoSuchElementException(
                    "Empresa não encontrada."
            )
        }

        vaga.empresa = empresa

        vagaRepository.inserir(vaga)
    }
}
