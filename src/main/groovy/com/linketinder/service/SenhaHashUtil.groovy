package com.linketinder.service

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.HexFormat

class SenhaHashUtil {

    static final String ALGORITMO_PREFIXO = "pbkdf2_sha256"
    static final String ALGORITMO_JCE = "PBKDF2WithHmacSHA256"
    static final int ITERACOES = 65536
    static final int TAMANHO_SALT_BYTES = 16
    static final int TAMANHO_CHAVE_BITS = 256

    private static final SecureRandom SECURE_RANDOM = new SecureRandom()

    static String gerarHash(String senha) {
        if (senha == null || senha.trim().length() < 8) {
            throw new IllegalArgumentException(
                    "Senha deve possuir pelo menos 8 caracteres."
            )
        }

        byte[] salt = new byte[TAMANHO_SALT_BYTES]
        SECURE_RANDOM.nextBytes(salt)

        byte[] hash = derivarChave(senha.toCharArray(), salt, ITERACOES)

        HexFormat hex = HexFormat.of()
        return "${ALGORITMO_PREFIXO}\$${ITERACOES}\$${hex.formatHex(salt)}\$${hex.formatHex(hash)}"
    }

    static boolean verificarSenha(String senhaCandidata, String hashArmazenado) {
        if (senhaCandidata == null || hashArmazenado == null) {
            return false
        }

        String[] partes = hashArmazenado.split('\\$')
        if (partes.length != 4 || partes[0] != ALGORITMO_PREFIXO) {
            return false
        }

        try {
            int iteracoes = Integer.parseInt(partes[1])
            HexFormat hex = HexFormat.of()
            byte[] salt = hex.parseHex(partes[2])
            byte[] hashEsperado = hex.parseHex(partes[3])

            byte[] hashCalculado = derivarChave(
                    senhaCandidata.toCharArray(),
                    salt,
                    iteracoes
            )

            return MessageDigest.isEqual(hashEsperado, hashCalculado)
        } catch (IllegalArgumentException ignored) {
            return false
        }
    }

    static boolean ehHashValido(String valor) {
        if (valor == null) {
            return false
        }

        String[] partes = valor.split('\\$')
        return partes.length == 4 &&
                partes[0] == ALGORITMO_PREFIXO &&
                partes[2].length() == TAMANHO_SALT_BYTES * 2 &&
                partes[3].length() == (TAMANHO_CHAVE_BITS / 8) * 2
    }

    private static byte[] derivarChave(
            char[] senhaChars,
            byte[] salt,
            int iteracoes
    ) {
        PBEKeySpec spec = new PBEKeySpec(
                senhaChars,
                salt,
                iteracoes,
                TAMANHO_CHAVE_BITS
        )
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO_JCE)
            return factory.generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
