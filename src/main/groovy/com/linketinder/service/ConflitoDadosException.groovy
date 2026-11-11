package com.linketinder.service

/**
 * Exceção de domínio lançada quando há violação de unicidade cadastral
 * (como CPF ou e-mail já pertencente a outro recrutador).
 *
 * Contrato com a camada HTTP (T-05): {@code RecrutadorHttpHandler} deve capturar
 * explicitamente {@link ConflitoDadosException} antes de handlers genéricos de
 * {@link RuntimeException} e responder com HTTP {@code 409 Conflict}.
 */
class ConflitoDadosException extends RuntimeException {

    ConflitoDadosException(String mensagem) {
        super(mensagem)
    }
}
