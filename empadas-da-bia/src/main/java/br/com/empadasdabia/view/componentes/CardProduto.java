package br.com.empadasdabia.view.componentes;

import br.com.empadasdabia.model.Produto;
import br.com.empadasdabia.utils.Formatador;
import br.com.empadasdabia.utils.Imagens;
import br.com.empadasdabia.utils.Tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/** Cartão de um produto na tela "Nosso Cardápio": foto, nome, descrição e preço. */
public class CardProduto extends PainelArredondado {

    private static final Color CINZA_TEXTO = new Color(0x5A524A);
    private final Produto produto;

    public CardProduto(Produto produto, Consumer<Produto> aoClicar, Consumer<Produto> aoDuploClique) {
        super(new BorderLayout(0, 8), produto.isAtivo() ? Tema.BRANCO : new Color(0xFAF7F2), Tema.BORDA, 18);
        this.produto = produto;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 12, 8));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(criarFoto(), BorderLayout.NORTH);
        add(criarInformacoes(), BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                aoClicar.accept(produto);
                if (e.getClickCount() == 2) {
                    aoDuploClique.accept(produto);
                }
            }
        });
    }

    public Produto getProduto() {
        return produto;
    }

    /** Destaca o cartão com borda azul quando estiver selecionado. */
    public void setSelecionado(boolean selecionado) {
        setBorda(selecionado ? Tema.AZUL : Tema.BORDA, selecionado ? 3 : 1);
    }

    private JPanel criarFoto() {
        Color fundo = produto.isAtivo() ? Tema.fundoDaFoto(produto.getCategoria().getNome()) : new Color(0xF3EEE6);
        PainelArredondado foto = new PainelArredondado(new BorderLayout(), fundo, null, 14);
        foto.setPreferredSize(new Dimension(150, 124));
        ImageIcon icone = Imagens.carregar(produto.getImagem(), 140, 118);
        JLabel imagem = new JLabel(produto.isAtivo() ? icone : Imagens.cinza(icone), SwingConstants.CENTER);
        foto.add(imagem, BorderLayout.CENTER);
        if (!produto.isAtivo()) {
            JPanel topo = Tema.painelTransparente(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            topo.add(Componentes.selo("INATIVO", CINZA_TEXTO, Tema.BRANCO));
            foto.add(topo, BorderLayout.NORTH);
        }
        return foto;
    }

    private JPanel criarInformacoes() {
        JPanel info = Tema.painelTransparente(null);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        JLabel nome = new JLabel("<html>" + produto.getNome() + "</html>");
        nome.setFont(Tema.fonteCardapio(Font.BOLD, 15.5f));
        nome.setForeground(produto.isAtivo() ? Tema.TEXTO : CINZA_TEXTO);

        String textoDescricao = produto.getDescricao() == null ? "" : produto.getDescricao();
        JLabel descricao = new JLabel("<html><div style='width:128px'>" + textoDescricao + "</div></html>");
        descricao.setFont(Tema.fonte(Font.PLAIN, 12.5f));
        descricao.setForeground(Tema.TEXTO_SUAVE);

        JLabel preco = Tema.preco(Formatador.moeda(produto.getPreco()), 20);
        if (!produto.isAtivo()) {
            preco.setForeground(new Color(0x7A716A));
        }

        for (JLabel rotulo : new JLabel[]{nome, descricao, preco}) {
            rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        info.add(nome);
        info.add(Box.createVerticalStrut(4));
        info.add(descricao);
        info.add(Box.createVerticalGlue());
        info.add(Box.createVerticalStrut(8));
        info.add(preco);
        return info;
    }
}
