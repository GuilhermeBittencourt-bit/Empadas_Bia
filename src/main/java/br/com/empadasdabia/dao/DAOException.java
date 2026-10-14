package br.com.empadasdabia.dao;

/**
 * Erro ao acessar o banco de dados.
 * Os DAOs convertem a SQLException nesta exceção com uma mensagem que pode ser mostrada ao usuário.
 */
public class DAOException extends RuntimeException {

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    /** SQLState iniciado por "23" = violação de restrição (chave estrangeira ou valor único). */
    static boolean violouRestricao(java.sql.SQLException e) {
        return e.getSQLState() != null && e.getSQLState().startsWith("23");
    }
}
