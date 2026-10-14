package br.com.empadasdabia.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/** Validações reutilizadas pelos Controllers. Todas lançam ValidacaoException com mensagem amigável. */
public final class Validador {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validador() {
    }

    /** Campo obrigatório: devolve o texto sem espaços nas pontas. */
    public static String obrigatorio(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        }
        return tamanhoMaximo(valor.trim(), campo, tamanhoMaximo);
    }

    /** Campo opcional: devolve null quando estiver vazio. */
    public static String opcional(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return tamanhoMaximo(valor.trim(), campo, tamanhoMaximo);
    }

    private static String tamanhoMaximo(String valor, String campo, int tamanhoMaximo) {
        if (valor.length() > tamanhoMaximo) {
            throw new ValidacaoException("O campo \"" + campo + "\" aceita no máximo " + tamanhoMaximo + " caracteres.");
        }
        return valor;
    }

    public static String somenteNumeros(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    /** Telefone com DDD: 10 dígitos (fixo) ou 11 dígitos (celular). Devolve só os números. */
    public static String telefone(String valor) {
        String numeros = somenteNumeros(valor);
        if (numeros.isEmpty()) {
            throw new ValidacaoException("O campo \"Telefone\" é obrigatório.");
        }
        if (numeros.length() != 10 && numeros.length() != 11) {
            throw new ValidacaoException("Telefone inválido. Informe o DDD e o número, ex.: (47) 99812-3344.");
        }
        return numeros;
    }

    /** E-mail é opcional, mas se for informado precisa ter formato válido. */
    public static String email(String valor) {
        String email = opcional(valor, "E-mail", 100);
        if (email != null && !EMAIL.matcher(email).matches()) {
            throw new ValidacaoException("E-mail inválido. Exemplo: nome@email.com");
        }
        return email;
    }

    /** CEP é opcional, mas se for informado precisa ter 8 dígitos. */
    public static String cep(String valor) {
        String numeros = somenteNumeros(valor);
        if (numeros.isEmpty()) {
            return null;
        }
        if (numeros.length() != 8) {
            throw new ValidacaoException("CEP inválido. Informe os 8 números, ex.: 89201-000.");
        }
        return numeros;
    }

    /** Converte o texto digitado (ex.: "9,00" ou "R$ 12,50") em BigDecimal maior que zero. */
    public static BigDecimal preco(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException("O campo \"Preço\" é obrigatório.");
        }
        String texto = valor.replace("R$", "").replace(" ", "").replace(" ", "");
        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(",", ".");
        }
        try {
            BigDecimal preco = new BigDecimal(texto);
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidacaoException("O preço deve ser maior que zero.");
            }
            return preco.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Preço inválido. Use o formato 9,00.");
        }
    }
}
