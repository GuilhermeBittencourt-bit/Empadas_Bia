package br.com.empadasdabia.view.componentes;

import br.com.empadasdabia.utils.Tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

/** Cartão de número do Dashboard (ex.: "Pedidos hoje: 7"). */
public class CardIndicador extends PainelArredondado {

    private final JLabel valor;
    private final JLabel detalhe;

    public CardIndicador(String titulo, Color corValor) {
        super(null, Tema.BRANCO, Tema.BORDA, 16);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel rotulo = Tema.texto(titulo, Font.BOLD, 13, Tema.TEXTO_SUAVE);
        valor = new JLabel("—");
        valor.setFont(Tema.fonteCardapio(Font.BOLD, 30));
        valor.setForeground(corValor);
        detalhe = Tema.texto(" ", Font.PLAIN, 12, Tema.TEXTO_SUAVE);

        add(rotulo);
        add(Box.createVerticalStrut(4));
        add(valor);
        add(Box.createVerticalStrut(2));
        add(detalhe);
    }

    public void setValores(String valorTexto, String detalheTexto) {
        valor.setText(valorTexto);
        detalhe.setText(detalheTexto);
    }
}
