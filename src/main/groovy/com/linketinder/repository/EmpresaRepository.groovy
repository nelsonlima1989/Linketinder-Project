
package com.linketinder.repository

import com.linketinder.model.Empresa

interface EmpresaRepository {

    void inserir(Empresa empresa)

    List<Empresa> listarTodos()

    Empresa buscarPorId(Long id)

    Map<Long, Empresa> buscarPorIds(List<Long> ids)

    boolean atualizar(Empresa empresa)

    boolean excluir(Long id)
}