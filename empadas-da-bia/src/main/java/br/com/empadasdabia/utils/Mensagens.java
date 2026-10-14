package br.com.empadasdabia.utils;

import javax.swing.JOptionPane;
import java.awt.Component;

/** Atalhos para as caixas de mensagem do sistema (sempre com os mesmos títulos). */
public final class Mensagens {

    private Mensagens() {
    }

    public static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Empadas da Bia", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Executa uma ação da tela tratando os erros em um único lugar:
     * erro de validação vira aviso; erro de banco vira mensagem de erro.
     * Devolve true se a ação terminou sem erro.
     */
    public static boolean tentar(Component pai, Runnable acao) {
        try {
            acao.run();
            return true;
        } catch (ValidacaoException e) {
            aviso(pai, e.getMessage());
        } catch (RuntimeException e) {
            erro(pai, e.getMessage() == null ? "Ocorreu um erro inesperado." : e.getMessage());
        }
        return false;
    }

    public static boolean confirmar(Component pai, String mensagem, String titulo) {
        int resposta = JOptionPane.showConfirmDialog(pai, mensagem, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }
}
