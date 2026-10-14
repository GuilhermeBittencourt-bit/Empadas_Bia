package br.com.empadasdabia.view;

import br.com.empadasdabia.model.Pedido;
import br.com.empadasdabia.utils.Imagens;
import br.com.empadasdabia.utils.Tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Janela principal: menu lateral à esquerda e, à direita, a tela escolhida.
 * As telas são trocadas com CardLayout (como cartas de um baralho).
 */
public class TelaPrincipal extends JFrame {

    public static final String INICIO = "Início";
    public static final String PEDIDOS = "Pedidos";
    public static final String PRODUTOS = "Produtos";
    public static final String CATEGORIAS = "Categorias";
    public static final String CLIENTES = "Clientes";

    private static final Color MENU_ATIVO = new Color(0x4A76A3);

    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);
    private final Map<String, JPanel> telas = new LinkedHashMap<>();
    private final Map<String, JButton> botoesMenu = new LinkedHashMap<>();
    private String telaAtual;

    public TelaPrincipal() {
        super("Empadas da Bia - Sistema de Gerenciamento");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1120, 720));
        setLocationRelativeTo(null);
        setIconImage(Imagens.carregar("logo.png", 64, 64).getImage());

        telas.put(INICIO, new PainelDashboard(this));
        telas.put(PEDIDOS, new PainelPedidos(this));
        telas.put(PRODUTOS, new PainelProdutos(this));
        telas.put(CATEGORIAS, new PainelCategorias());
        telas.put(CLIENTES, new PainelClientes());
        telas.forEach((nome, tela) -> conteudo.add(tela, nome));

        setLayout(new BorderLayout());
        add(criarMenuLateral(), BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);

        mostrar(INICIO);
    }

    /** Troca a tela exibida e recarrega os dados dela. */
    public void mostrar(String nomeTela) {
        telaAtual = nomeTela;
        cartoes.show(conteudo, nomeTela);
        botoesMenu.forEach((nome, botao) -> {
            boolean ativo = nome.equals(nomeTela);
            botao.setBackground(ativo ? MENU_ATIVO : Tema.AZUL);
            botao.setFont(Tema.fonte(ativo ? Font.BOLD : Font.PLAIN, 14));
        });
        ((Atualizavel) telas.get(nomeTela)).atualizar();
    }

    /** Abre o Novo Pedido; se o pedido for salvo, mostra os detalhes dele em seguida. */
    public void abrirNovoPedido() {
        DialogNovoPedido dialogo = new DialogNovoPedido(this);
        dialogo.setVisible(true);
        if (dialogo.getPedidoSalvo() != null) {
            new DialogDetalhesPedido(this, dialogo.getPedidoSalvo()).setVisible(true);
        }
        mostrar(telaAtual);
    }

    public void abrirDetalhesPedido(Pedido pedido) {
        new DialogDetalhesPedido(this, pedido).setVisible(true);
        mostrar(telaAtual);
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.AZUL);
        menu.setPreferredSize(new Dimension(220, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));

        menu.add(criarLogo());
        menu.add(Box.createVerticalStrut(22));
        for (String nome : telas.keySet()) {
            JButton botao = criarBotaoMenu(nome);
            botoesMenu.put(nome, botao);
            menu.add(botao);
            menu.add(Box.createVerticalStrut(6));
        }
        menu.add(Box.createVerticalGlue());
        JLabel versao = Tema.texto("Versão 1.0", Font.PLAIN, 12, new Color(0xC9D6E3));
        versao.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(versao);
        return menu;
    }

    private JPanel criarLogo() {
        JPanel logo = new JPanel(new BorderLayout(10, 0));
        logo.setOpaque(false);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setMaximumSize(new Dimension(220, 52));
        JLabel icone = new JLabel(Imagens.carregar("logo.png", 38, 38), SwingConstants.CENTER);
        logo.add(icone, BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel("Empadas da Bia");
        nome.setFont(Tema.fonteCardapio(Font.BOLD, 15));
        nome.setForeground(Tema.BRANCO);
        textos.add(Box.createVerticalStrut(6));
        textos.add(nome);
        textos.add(Tema.texto("Gerenciamento", Font.PLAIN, 12, new Color(0xDCE6F0)));
        logo.add(textos, BorderLayout.CENTER);
        return logo;
    }

    private JButton criarBotaoMenu(String nome) {
        JButton botao = new JButton(nome);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setMaximumSize(new Dimension(220, 42));
        botao.setForeground(Tema.BRANCO);
        botao.setBackground(Tema.AZUL);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.putClientProperty("FlatLaf.style",
                "arc:10; borderWidth:0; focusWidth:0; innerFocusWidth:0; margin:10,14,10,14; hoverBackground:#3D6A97; pressedBackground:#4A76A3");
        botao.addActionListener(e -> mostrar(nome));
        return botao;
    }
}
