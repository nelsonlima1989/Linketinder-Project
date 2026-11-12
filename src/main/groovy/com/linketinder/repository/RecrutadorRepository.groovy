package com.linketinder.repository

import com.linketinder.model.Recrutador

interface RecrutadorRepository {

    void inserir(Recrutador recrutador)

    List<Recrutador> listarTodos()

    List<Recrutador> listarPorEmpresaId(Long empresaId)

    Recrutador buscarPorId(Long id)

    Recrutador buscarPorCpf(String cpf)

    Recrutador buscarPorEmail(String email)

    boolean atualizar(Recrutador recrutador)

    boolean excluir(Long id)

    int excluirPorEmpresaId(Long empresaId)
}
