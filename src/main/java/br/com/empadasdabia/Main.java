package br.com.empadasdabia;

import br.com.empadasdabia.database.ConexaoBD;
import br.com.empadasdabia.utils.Mensagens;
import br.com.empadasdabia.utils.Tema;
import br.com.empadasdabia.view.TelaPrincipal;

import javax.swing.SwingUtilities;
import java.sql.SQLException;
import java.util.Locale;

/** Ponto de entrada do sistema Empadas da Bia. */
public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.of("pt", "BR"));
        SwingUtilities.invokeLater(() -> {
            Tema.aplicar();
            try {
                ConexaoBD.testarConexao();
            } catch (SQLException e) {
                Mensagens.erro(null, "Não foi possível conectar ao banco de dados.\n\n"
                        + "Verifique se o MySQL está ligado, se os scripts da pasta \"database\" foram executados\n"
                        + "e se o usuário/senha em src/main/resources/database.properties estão corretos.\n\n"
                        + "Detalhe: " + e.getMessage());
                System.exit(1);
            }
            new TelaPrincipal().setVisible(true);
        });
    }
}
