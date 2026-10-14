package br.com.empadasdabia.utils;

/**
 * Erro de regra de negócio ou de dado inválido digitado pelo usuário.
 * A mensagem é sempre amigável, pronta para ser exibida na tela.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
