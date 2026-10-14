package br.com.empadasdabia.view.componentes;

import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Imagens;
import br.com.empadasdabia.utils.Tema;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

/** Cartão compacto do cardápio no Novo Pedido: foto, nome, preço e botão "+". */
public class CardCardapio extends PainelArredondado {

    private final Produto produto;
    private final JLabel quantidadeNoPedido;

    public CardCardapio(Produto produto, Runnable aoAdicionar) {
        super(new BorderLayout(12, 0), Tema.BRANCO, Tema.BORDA, 14);
        this.produto = produto;
        setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 12));

        PainelArredondado foto = new PainelArredondado(new BorderLayout(),
                Tema.fundoDaFoto(produto.getCategoria().getNome()), null, 10);
        foto.setPreferredSize(new Dimension(76, 66));
        foto.add(new JLabel(Imagens.carregar(produto.getImagem(), 72, 62), SwingConstants.CENTER));
        add(foto, BorderLayout.WEST);

        JPanel textos = Tema.painelTransparente(null);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel("<html>" + produto.getNome().replace("Empada de ", "").replace("Empada ", "") + "</html>");
        nome.setFont(Tema.fonteCardapio(Font.BOLD, 14.5f));
        nome.setForeground(Tema.TEXTO);
        quantidadeNoPedido = Tema.texto(" ", Font.BOLD, 12, Tema.ROSA_ESCURO);
        textos.add(nome);
        textos.add(Tema.preco(Formatador.moeda(produto.getPreco()), 15));
        textos.add(quantidadeNoPedido);
        add(textos, BorderLayout.CENTER);

        JButton adicionar = new JButton("+");
        adicionar.setToolTipText("Adicionar " + produto.getNome());
        adicionar.getAccessibleContext().setAccessibleName("Adicionar " + produto.getNome());
        adicionar.setFont(Tema.fonte(Font.BOLD, 20));
        adicionar.setForeground(Tema.BRANCO);
        adicionar.setBackground(Tema.AZUL);
        adicionar.setFocusPainted(false);
        adicionar.setPreferredSize(new Dimension(40, 40));
        adicionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adicionar.putClientProperty(FlatClientProperties.STYLE,
                "arc:999; margin:0,0,0,0; borderWidth:0; focusWidth:0; hoverBackground:#264E75; pressedBackground:#1F4468");
        adicionar.addActionListener(e -> aoAdicionar.run());
        JPanel lateral = Tema.painelTransparente(new GridBagLayout());
        lateral.add(adicionar);
        add(lateral, BorderLayout.EAST);
    }

    public Produto getProduto() {
        return produto;
    }

    /** Mostra "3 no pedido" e destaca o cartão em rosa quando o produto já está na comanda. */
    public void atualizarQuantidade(int quantidade) {
        quantidadeNoPedido.setText(quantidade > 0 ? quantidade + " no pedido" : " ");
        setBorda(quantidade > 0 ? Tema.ROSA : Tema.BORDA, quantidade > 0 ? 2 : 1);
    }
}
