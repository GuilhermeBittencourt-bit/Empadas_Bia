package br.com.empadasdabia.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formata valores para exibição nas telas (moeda, datas, telefone, CEP). */
public final class Formatador {

    private static final Locale BRASIL = Locale.of("pt", "BR");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATA_CURTA = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter DATA_EXTENSO =
            DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", BRASIL);

    private Formatador() {
    }

    /** Ex.: 9.5 -> "R$ 9,50" */
    public static String moeda(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(BRASIL);
        return formato.format(valor == null ? BigDecimal.ZERO : valor).replace(' ', ' ');
    }

    /** Ex.: 9.5 -> "9,50" (usado para preencher campos de edição) */
    public static String numeroDecimal(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getNumberInstance(BRASIL);
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        formato.setGroupingUsed(false);
        return valor == null ? "" : formato.format(valor);
    }

    public static String dataHora(LocalDateTime data) {
        return data == null ? "" : data.format(DATA_HORA);
    }

    public static String dataCurta(LocalDateTime data) {
        return data == null ? "" : data.format(DATA_CURTA);
    }

    public static String dataPorExtenso(LocalDateTime data) {
        return data.format(DATA_EXTENSO);
    }

    /** Ex.: 15 -> "#015" */
    public static String numeroPedido(Integer id) {
        return id == null ? "Novo" : String.format("#%03d", id);
    }

    /** Ex.: 47998123344 -> "(47) 99812-3344" */
    public static String telefone(String numeros) {
        if (numeros == null) {
            return "";
        }
        if (numeros.length() == 11) {
            return "(" + numeros.substring(0, 2) + ") " + numeros.substring(2, 7) + "-" + numeros.substring(7);
        }
        if (numeros.length() == 10) {
            return "(" + numeros.substring(0, 2) + ") " + numeros.substring(2, 6) + "-" + numeros.substring(6);
        }
        return numeros;
    }

    /** Ex.: 89201000 -> "89201-000" */
    public static String cep(String numeros) {
        if (numeros == null || numeros.length() != 8) {
            return numeros == null ? "" : numeros;
        }
        return numeros.substring(0, 5) + "-" + numeros.substring(5);
    }

    public static String textoOuTraco(String valor) {
        return valor == null || valor.isBlank() ? "—" : valor;
    }
}
