package br.com.empadasdabia.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Responsável por abrir conexões com o banco MySQL.
 * Os dados de acesso ficam no arquivo database.properties (pasta resources),
 * assim não é preciso mexer no código para trocar usuário ou senha.
 */
public final class ConexaoBD {

    private static final Properties CONFIG = carregarConfiguracao();

    private ConexaoBD() {
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.usuario"),
                CONFIG.getProperty("db.senha"));
    }

    /** Usado na inicialização para avisar o usuário caso o banco esteja fora do ar. */
    public static void testarConexao() throws SQLException {
        try (Connection conexao = getConexao()) {
            conexao.isValid(2);
        }
    }

    private static Properties carregarConfiguracao() {
        Properties propriedades = new Properties();
        try (InputStream entrada = ConexaoBD.class.getResourceAsStream("/database.properties")) {
            if (entrada == null) {
                throw new IllegalStateException("Arquivo database.properties não encontrado.");
            }
            propriedades.load(entrada);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o database.properties.", e);
        }
        return propriedades;
    }
}
