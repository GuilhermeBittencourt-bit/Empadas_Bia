package br.com.empadasdabia.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Testa as validações e formatações usadas nos formulários. */
class ValidadorTest {

    @Test
    void converteTextoDePrecoNoFormatoBrasileiro() {
        assertEquals(new BigDecimal("9.00"), Validador.preco("9"));
        assertEquals(new BigDecimal("12.50"), Validador.preco("12,50"));
        assertEquals(new BigDecimal("1250.00"), Validador.preco("R$ 1.250,00"));
    }

    @Test
    void rejeitaPrecoInvalido() {
        assertThrows(ValidacaoException.class, () -> Validador.preco(""));
        assertThrows(ValidacaoException.class, () -> Validador.preco("abc"));
        assertThrows(ValidacaoException.class, () -> Validador.preco("0"));
        assertThrows(ValidacaoException.class, () -> Validador.preco("-5"));
    }

    @Test
    void telefoneGuardaSomenteNumeros() {
        assertEquals("47998123344", Validador.telefone("(47) 99812-3344"));
        assertEquals("4734221234", Validador.telefone("47 3422-1234"));
        assertThrows(ValidacaoException.class, () -> Validador.telefone("99812-3344"));
        assertThrows(ValidacaoException.class, () -> Validador.telefone(""));
    }

    @Test
    void emailECepSaoOpcionaisMasPrecisamSerValidos() {
        assertNull(Validador.email("  "));
        assertEquals("ana@email.com", Validador.email(" ana@email.com "));
        assertThrows(ValidacaoException.class, () -> Validador.email("ana@"));
        assertNull(Validador.cep(""));
        assertEquals("89201000", Validador.cep("89201-000"));
        assertThrows(ValidacaoException.class, () -> Validador.cep("8920"));
    }

    @Test
    void campoObrigatorio() {
        assertEquals("Frango", Validador.obrigatorio("  Frango ", "Nome", 100));
        assertThrows(ValidacaoException.class, () -> Validador.obrigatorio(" ", "Nome", 100));
        assertThrows(ValidacaoException.class, () -> Validador.obrigatorio("abcdef", "Nome", 3));
    }

    @Test
    void formataValoresParaExibicao() {
        assertEquals("R$ 59,00", Formatador.moeda(new BigDecimal("59")));
        assertEquals("(47) 99812-3344", Formatador.telefone("47998123344"));
        assertEquals("89201-000", Formatador.cep("89201000"));
        assertEquals("#015", Formatador.numeroPedido(15));
    }
}
